package org.example.project.application.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*
import org.example.project.Config
import org.example.project.application.dtos.errorResponse
import org.example.project.application.dtos.successResponse
import org.example.project.application.routes.*
import org.example.project.domain.services.inmemory.EvidenceService
import org.example.project.domain.services.inmemory.ReportService
import org.example.project.firebase.NotificationService
import org.example.project.model.Response
import org.example.project.model.request.EvidenceRequest
import org.example.project.model.request.ReportRequest
import org.example.project.model.request.StatusLaporanRequest
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                Response(false, cause.message ?: "Input data tidak valid", null)
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                Response(false, cause.message ?: "Terjadi kesalahan pada server", null)
            )
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, errorResponse(cause.message ?: "Data tidak ditemukan"))
        }
    }
    routing {
        //endpoint aplikasi
        route(Config.apiEndpoint) {

            //endpoint api report
            route("/reports") {
            val reportService: ReportService by inject()
            val notificationService: NotificationService by inject()
                //report user
                route("/user") {
                    post {
                        val reportRequest = call.receive<ReportRequest>()
                        val response = reportService.create(reportRequest)
                        call.respond(HttpStatusCode.Created, successResponse(response, "Laporan berhasil dibuat"))
                    }
                    get {
                        val response = reportService.getAllForUser()
                        call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
                    }
                    route("/{id}") {
                        get {
                            val id =
                                call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                            val report = reportService.getByIdForUser(id)
                            if (report != null) {
                                call.respond(
                                    HttpStatusCode.OK,
                                    successResponse(report, "Berhasil mengambil laporan dengan ID $id")
                                )
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    successResponse(null, "Laporan dengan ID $id tidak ditemukan")
                                )
                            }
                        }
                        put {
                            val id =
                                call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                            val reportRequest = call.receive<ReportRequest>()
                            val response = reportService.update(id, reportRequest)
                            call.respond(HttpStatusCode.OK, successResponse(response, "Laporan berhasil diupdate"))
                        }
                        delete {
                            val id =
                                call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                            reportService.delete(id)
                            call.respond(
                                HttpStatusCode.OK,
                                successResponse(null, "Berhasil menghapus laporan dengan ID $id")
                            )
                        }
                    }
                    post("/upload-evidence") {
                        val uploadsDir = File("uploads")
                        if (!uploadsDir.exists()) {
                            uploadsDir.mkdirs()
                        }

                        val filePaths = mutableListOf<String>()
                        val multipartData = call.receiveMultipart()

                        try {
                            multipartData.forEachPart { part ->
                                if (part is PartData.FileItem) {
                                    val originalFileName = part.originalFileName ?: "evidence_${UUID.randomUUID()}"
                                    val fileExtension = originalFileName.substringAfterLast('.', "")
                                    val uniqueFileName = "${UUID.randomUUID()}.${fileExtension}"
                                    val filePath = "uploads/$uniqueFileName"
                                    val file = File(filePath)

                                    file.outputStream().use { output ->
                                        part.provider().copyTo(output)
                                    }

                                    filePaths.add(filePath)
                                }
                                part.dispose()
                            }

                            if (filePaths.isEmpty()) {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    successResponse(null, "Tidak ada file yang diunggah")
                                )
                                return@post
                            }

                            call.respond(
                                HttpStatusCode.OK,
                                successResponse(filePaths, "File berhasil diunggah")
                            )
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                successResponse(null, "Gagal mengunggah file: ${e.message}")
                            )
                        }
                    }
                }
                //report ketua
                route("/ketua") {
                    get {
                        val response = reportService.getAll()
                        call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
                    }
                    patch("{id}") {
                        val id =
                            call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                        val statusRequest = call.receive<StatusLaporanRequest>()
                        val response = reportService.updateStatusLaporan(id, statusRequest)
                        call.respond(HttpStatusCode.OK, successResponse(response, "Status laporan berhasil diupdate"))
                        try {
                            val userId = "user123"
                            notificationService.notifyUserStatusUpdated(userId, statusRequest.statusLaporan)
                        } catch (e: Exception) {
                            call.application.log.error("Failed to send notification: ${e.message}")
                        }
                    }
                }
            }

            // report bukti
            route("/evidences") {
                val evidenceService: EvidenceService by inject()
                post("/{reportId}") {
                    val reportId = call.parameters["reportId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Report ID tidak valid")

                    val evidenceRequest = call.receive<EvidenceRequest>()
                    val response = evidenceService.create(evidenceRequest.copy(reportId = reportId))
                    call.respond(HttpStatusCode.Created, successResponse(response, "Bukti berhasil ditambahkan"))
                }

                get("/{reportId}") {
                    val reportId = call.parameters["reportId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Report ID tidak valid")

                    val response = evidenceService.getByReportId(reportId)
                    call.respond(
                        HttpStatusCode.OK,
                        successResponse(response, "Berhasil mengambil bukti untuk laporan dengan ID $reportId")
                    )
                }

                get("/{reportId}/{buktiKe}") {
                    val reportId = call.parameters["reportId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Report ID tidak valid")
                    val buktiKe = call.parameters["buktiKe"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("BuktiKe tidak valid")

                    val evidence = evidenceService.getById(Pair(reportId, buktiKe))
                    call.respond(
                        HttpStatusCode.OK,
                        successResponse(evidence, "Berhasil mengambil bukti ke-$buktiKe untuk laporan ID $reportId")
                    )
                }

                put("/{reportId}/{buktiKe}") {
                    val reportId = call.parameters["reportId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Report ID tidak valid")
                    val buktiKe = call.parameters["buktiKe"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("BuktiKe tidak valid")

                    val evidenceRequest = call.receive<EvidenceRequest>()
                    val response = evidenceService.update(Pair(reportId, buktiKe), evidenceRequest)
                    call.respond(HttpStatusCode.OK, successResponse(response, "Bukti ke-$buktiKe berhasil diperbarui"))
                }

                delete("/{reportId}/{buktiKe}") {
                    val reportId = call.parameters["reportId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Report ID tidak valid")
                    val buktiKe = call.parameters["buktiKe"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("BuktiKe tidak valid")

                    evidenceService.delete(Pair(reportId, buktiKe))
                    call.respond(
                        HttpStatusCode.OK,
                        successResponse(null, "Bukti ke-$buktiKe untuk laporan ID $reportId berhasil dihapus")
                    )
                }
            }
        }
        staticResources("static", "static")
        static("uploads") {
            files("uploads")
        }
        productRoute()
        uploadRoute()
        firebaseRoute()
        formSatuRoute()
    }
}
