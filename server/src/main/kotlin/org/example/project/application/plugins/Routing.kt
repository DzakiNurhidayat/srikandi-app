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
import org.example.project.application.routes.idParam
import org.example.project.application.routes.productRoute
import org.example.project.domain.services.inmemory.EvidenceService
import org.example.project.domain.services.inmemory.FormSatuService
import org.example.project.domain.services.inmemory.ReportService
import org.example.project.firebase.FirebaseService
import org.example.project.firebase.NotificationService
import org.example.project.model.Response
import org.example.project.model.request.EvidenceRequest
import org.example.project.model.request.FormSatuRequest
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
        //endpoint aplikasi (/api)
        route(Config.apiEndpoint) {
            //endpoint api report
            route("/reports") {
                val reportService: ReportService by inject()
                val notificationService: NotificationService by inject()
                //report user
                route("/user") {
                    get {
                        val response = reportService.getAllForUser()
                        call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
                    }
                    post {
                        val reportRequest = call.receive<ReportRequest>()
                        val response = reportService.create(reportRequest)
                        call.respond(HttpStatusCode.Created, successResponse(response, "Laporan berhasil dibuat"))
                    }
                    route("/{id}") {
                        get {
                            val id = call.idParam()
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
                            val id = call.idParam()
                            val reportRequest = call.receive<ReportRequest>()
                            val response = reportService.update(id, reportRequest)
                            call.respond(HttpStatusCode.OK, successResponse(response, "Laporan berhasil diupdate"))
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
                                HttpStatusCode.NoContent,
                                successResponse(filePaths, "File berhasil diunggah")
                            )
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                successResponse(null, "Gagal mengunggah file: ${e.message}")
                            )
                        }
                    }
                    patch("/call") {
                        //todo (implementasi penentuan pemanggilan oleh user)
                    }
                }

                //report ketua
                route("/ketua") {
                    val formSatuService: FormSatuService by inject()
                    get {
                        val response = reportService.getAll()
                        call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
                    }
                    // POST buat Form1 baru
                    post {
                        val request = call.receive<FormSatuRequest>()
                        if (request.ciriFisik.isBlank() || request.ceritaSingkat.isBlank()) {
                            call.respond(
                                HttpStatusCode.BadRequest, successResponse(
                                    null,
                                    message = "Ciri fisik dan cerita singkat tidak boleh kosong"
                                )
                            )
                            return@post
                        }
                        val savedForm = formSatuService.create(request)
                        call.respond(
                            HttpStatusCode.Created, successResponse(
                                savedForm,
                                message = "Form1 berhasil dibuat"
                            )
                        )
                    }
                }

                patch("{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
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

        route("/firebase") {
            val firebaseService: FirebaseService by inject()
            post("/register-token") {
                val params = call.receiveParameters()
                val userId = params["userId"] ?: return@post call.respondText(
                    "userId required",
                    status = HttpStatusCode.BadRequest
                )
                val token = params["token"] ?: return@post call.respondText(
                    "token required",
                    status = HttpStatusCode.BadRequest
                )
                try {
                    firebaseService.saveToken(userId, token)
                    call.respondText("Token registered")
                } catch (e: Exception) {
                    call.respondText(
                        "Failed to register token: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
        staticResources("/static", "static")
        staticFiles("/uploads", File("uploads"))
    }
    productRoute()
}