package org.example.project.application.plugins

import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.application.dtos.errorResponse
import org.example.project.application.dtos.successResponse
import org.example.project.application.routes.idParam
import org.example.project.application.routes.productRoute
import org.example.project.domain.services.inmemory.EvidenceService
import org.example.project.domain.services.inmemory.FormSatuService
import org.example.project.domain.services.inmemory.ReportService
import org.example.project.firebase.NotificationService
import org.example.project.model.request.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                errorResponse(cause.message ?: "Input data tidak valid")
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                errorResponse("Terjadi kesalahan pada server")
            )
        }
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                errorResponse(cause.message ?: "Data tidak ditemukan")
            )
        }
    }
    val reportService: ReportService by inject()
    val notificationService: NotificationService by inject()
    val formSatuService: FormSatuService by inject()
    val evidenceService: EvidenceService by inject()

    routing {
        //endpoint aplikasi (/api)
        route("/api") {
            //endpoint api report
            route("/reports") {
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
                                    errorResponse("Tidak ada file yang diunggah")
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
                                errorResponse("Gagal mengunggah file: ${e.message}")
                            )
                        }
                    }
                    patch("/call") {
                        //todo (implementasi penentuan pemanggilan oleh user)
                    }
                }

                //report ketua
                route("/ketua") {
                    get {
                        val response = reportService.getAll()
                        call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
                    }
                    // Endpoint untuk FormSatu dengan unggah file

                    // POST untuk membuat FormSatu dengan reportId sebagai query parameter
                    post("/form-1") {
                        val uploadsDir = File("uploads")
                        if (!uploadsDir.exists()) {
                            uploadsDir.mkdirs()
                        }

                        val filePaths = mutableListOf<String>()
                        var formRequest: FormSatuRequest? = null
                        val multipartData = call.receiveMultipart()
                        val reportId = call.request.queryParameters["reportId"]?.toIntOrNull()
                            ?: throw IllegalArgumentException("Report ID harus disediakan sebagai query parameter")

                        try {
                            multipartData.forEachPart { part ->
                                when (part) {
                                    is PartData.FileItem -> {
                                        val originalFileName = part.originalFileName ?: "form1_${UUID.randomUUID()}"
                                        val fileExtension = originalFileName.substringAfterLast('.', "")
                                        val uniqueFileName = "${UUID.randomUUID()}.${fileExtension}"
                                        val filePath = "uploads/$uniqueFileName"
                                        val file = File(filePath)

                                        file.outputStream().use { output ->
                                            part.provider().copyTo(output)
                                        }

                                        filePaths.add(filePath)
                                    }

                                    is PartData.FormItem -> {
                                        if (part.name == "formData") {
                                            formRequest = Json.decodeFromString<FormSatuRequest>(part.value)
                                        }
                                    }

                                    else -> {}
                                }
                                part.dispose()
                            }

                            if (formRequest == null) {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    errorResponse("Data FormSatu tidak ditemukan")
                                )
                                return@post
                            }

                            if (formRequest!!.ciriFisik.isBlank() || formRequest!!.ceritaSingkat.isBlank()) {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    errorResponse("Ciri fisik dan cerita singkat tidak boleh kosong")
                                )
                                return@post
                            }

                            if (filePaths.isEmpty()) {
                                call.respond(
                                    HttpStatusCode.BadRequest,
                                    errorResponse("Tidak ada file yang diunggah")
                                )
                                return@post
                            }

                            val savedForm = formSatuService.create(formRequest!!, reportId)
                            val report = reportService.getById(reportId)
                                ?: throw NotFoundException("Report dengan ID $reportId tidak ditemukan")

                            call.respond(
                                HttpStatusCode.Created,
                                successResponse(savedForm, "FormSatu berhasil dibuat")
                            )
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                errorResponse("Terjadi kesalahan pada server: ${e.message}")
                            )
                        }
                    }

                    // GET FormSatu berdasarkan form1id dan reportId
                    get("/form-1/{form1id}/{reportId}") {
                        val form1id = call.parameters["form1id"]?.toIntOrNull()
                            ?: throw IllegalArgumentException("Form1 ID tidak valid")
                        val reportId = call.parameters["reportId"]?.toIntOrNull()
                            ?: throw IllegalArgumentException("Report ID tidak valid")
                        val form = formSatuService.getById(Pair(form1id, reportId))
                        if (form != null) {
                            val report = reportService.getById(reportId)
                                ?: throw NotFoundException("Report dengan ID $reportId tidak ditemukan")
                            call.respond(
                                HttpStatusCode.OK,
                                successResponse(form, "Berhasil mengambil FormSatu")
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                successResponse(
                                    null,
                                    "FormSatu dengan ID $form1id dan Report ID $reportId tidak ditemukan"
                                )
                            )
                        }
                    }

                    patch("{id}") {
                        val authHeader = call.request.headers["Authorization"]
                            ?: return@patch call.respond(HttpStatusCode.Unauthorized, "Missing Authorization header")

                        val idToken = authHeader.removePrefix("Bearer ").trim()

                        val decodedToken = try {
                            FirebaseAuth.getInstance().verifyIdToken(idToken)
                        } catch (e: Exception) {
                            return@patch call.respond(
                                HttpStatusCode.Unauthorized,
                                "Invalid or expired ID token: ${e.message}"
                            )
                        }
                        val userId = decodedToken.uid

                        val id = call.parameters["id"]?.toIntOrNull()
                            ?: return@patch call.respond(HttpStatusCode.BadRequest, "ID tidak valid")

                        val request = call.receive<TokenRequest<StatusLaporanRequest>>()
                        val statusRequest = request.data!!
                        val response = reportService.updateStatusLaporan(id, statusRequest)
                        call.respond(HttpStatusCode.OK, successResponse(response, "Status laporan berhasil diupdate"))
                        try {
                            withContext(Dispatchers.IO) {
                                notificationService.notifyUserStatusUpdated(
                                    userId,
                                    request.fcmToken,
                                    statusRequest.statusLaporan
                                )
                            }
                        } catch (e: Exception) {
                            call.application.log.error("Failed to send notification: ${e.message}")
                        }
                    }

                }

                // report bukti
                route("/evidences") {
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
                        call.respond(
                            HttpStatusCode.OK,
                            successResponse(response, "Bukti ke-$buktiKe berhasil diperbarui")
                        )
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
            staticResources("/static", "static")
            staticFiles("/uploads", File("uploads"))
        }
        productRoute()
    }
}