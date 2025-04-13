package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.successResponse
import org.example.project.domain.services.inmemory.FormSatuService
import org.example.project.domain.services.inmemory.ReportService
import org.example.project.domain.services.interfaces.IFormSatuService
import org.example.project.model.request.FormSatuRequest
import org.koin.core.logger.Logger
import org.koin.ktor.ext.inject

fun Application.formSatuRoutes() {
    val formSatuService: FormSatuService by inject()
    routing {

        route("/api/ketua/form-1") {

            // GET Semua data form1
            get {
                val allForms = formSatuService.getAll()
                call.respond(HttpStatusCode.OK, successResponse(
                    allForms,
                    message = "berhasil mengambil semua data"
                ))
            }

            // GET form1 berdasarkan form1id dan reportId
            get("/{form1id}/{reportId}") {
                val form1id = call.parameters["form1id"]?.toIntOrNull()
                val reportId = call.parameters["reportId"]?.toIntOrNull()

                if (form1id == null || reportId == null) {
                    call.respond(HttpStatusCode.BadRequest, "form1id dan reportId harus berupa angka")
                    return@get
                }

                val result = formSatuService.getById(Pair(form1id, reportId))
                if (result == null) {
                    call.respond(HttpStatusCode.NotFound, "Data Form1 tidak ditemukan")
                } else {
                    call.respond(HttpStatusCode.OK, result)
                }
            }

            // POST buat Form1 baru
            post {
                val request = call.receive<FormSatuRequest>()
                val savedForm = formSatuService.create(request)
                call.respond(HttpStatusCode.Created, savedForm)
            }

            // PUT update Form1
            put("/{form1id}/{reportId}") {
                val form1id = call.parameters["form1id"]?.toIntOrNull()
                val reportId = call.parameters["reportId"]?.toIntOrNull()

                if (form1id == null || reportId == null) {
                    call.respond(HttpStatusCode.BadRequest, "form1id dan reportId harus berupa angka")
                    return@put
                }

                val request = call.receive<FormSatuRequest>()
                val updatedForm = formSatuService.update(Pair(form1id, reportId), request)
                call.respond(HttpStatusCode.OK, updatedForm)
            }

            // DELETE Form1
            delete("/{form1id}/{reportId}") {
                val form1id = call.parameters["form1id"]?.toIntOrNull()
                val reportId = call.parameters["reportId"]?.toIntOrNull()

                if (form1id == null || reportId == null) {
                    call.respond(HttpStatusCode.BadRequest, "form1id dan reportId harus berupa angka")
                    return@delete
                }

                val deleted = formSatuService.delete(Pair(form1id, reportId))
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Form1 berhasil dihapus")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Gagal menghapus Form1")
                }
            }
        }
    }
}
