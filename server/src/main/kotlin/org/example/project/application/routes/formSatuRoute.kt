package org.example.project.application.routes


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.successResponse
import org.example.project.domain.services.inmemory.FormSatuService
import org.example.project.model.request.FormSatuRequest
import org.koin.ktor.ext.inject

fun Application.formSatuRoute() {
    val formSatuService: FormSatuService by inject()
    routing {
        route("/api/ketua/form-1") {
            // GET semua data FormSatu dengan Report
            get {
                val allFormsWithReport = formSatuService.getAllWithReport()
                call.respond(HttpStatusCode.OK, successResponse(
                    allFormsWithReport,
                    message = "Berhasil mengambil semua data form 1"
                ))
            }

            // GET FormSatu berdasarkan form1id dan reportId
            get("/{form1id}/{reportId}") {
                val form1id = call.parameters["form1id"]?.toIntOrNull()
                val reportId = call.parameters["reportId"]?.toIntOrNull()

                if (form1id == null || reportId == null) {
                    call.respond(HttpStatusCode.BadRequest, successResponse(
                        null,
                        message = "form1id dan reportId harus berupa angka"
                    ))
                    return@get
                }

                val result = formSatuService.getByIdWithReport(Pair(form1id, reportId))
                if (result == null) {
                    call.respond(HttpStatusCode.NotFound, successResponse(
                        null,
                        message = "Data Form1 tidak ditemukan"
                    ))
                } else {
                    call.respond(HttpStatusCode.OK, successResponse(
                        result,
                        message = "Berhasil mengambil data form 1"
                    ))
                }
            }

            // POST buat Form1 baru
            post {
                val request = call.receive<FormSatuRequest>()
                if (request.ciriFisik.isBlank() || request.ceritaSingkat.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, successResponse(
                        null,
                        message = "Ciri fisik dan cerita singkat tidak boleh kosong"
                    ))
                    return@post
                }
                val savedForm = formSatuService.create(request)
                call.respond(HttpStatusCode.Created, successResponse(
                    savedForm,
                    message = "Form1 berhasil dibuat"
                ))
            }

            // PUT update Form1
            put("/{form1id}/{reportId}") {
                val form1id = call.parameters["form1id"]?.toIntOrNull()
                val reportId = call.parameters["reportId"]?.toIntOrNull()

                if (form1id == null || reportId == null) {
                    call.respond(HttpStatusCode.BadRequest, successResponse(
                        null,
                        message = "form1id dan reportId harus berupa angka"
                    ))
                    return@put
                }

                val request = call.receive<FormSatuRequest>()
                if (request.ciriFisik.isBlank() || request.ceritaSingkat.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, successResponse(
                        null,
                        message = "Ciri fisik dan cerita singkat tidak boleh kosong"
                    ))
                    return@put
                }
                val updatedForm = formSatuService.update(Pair(form1id, reportId), request)
                call.respond(HttpStatusCode.OK, successResponse(
                    updatedForm,
                    message = "Form1 berhasil diperbarui"
                ))
            }

            // DELETE Form1
            delete("/{form1id}/{reportId}") {
                val form1id = call.parameters["form1id"]?.toIntOrNull()
                val reportId = call.parameters["reportId"]?.toIntOrNull()

                if (form1id == null || reportId == null) {
                    call.respond(HttpStatusCode.BadRequest, successResponse(
                        null,
                        message = "form1id dan reportId harus berupa angka"
                    ))
                    return@delete
                }

                val deleted = formSatuService.delete(Pair(form1id, reportId))
                if (deleted) {
                    call.respond(HttpStatusCode.OK, successResponse(
                        null,
                        message = "Form1 berhasil dihapus"
                    ))
                } else {
                    call.respond(HttpStatusCode.NotFound, successResponse(
                        null,
                        message = "Data Form1 tidak ditemukan"
                    ))
                }
            }
        }
    }
}