 package org.example.project.application.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.successResponse
import org.example.project.domain.services.inmemory.ReportService
import org.example.project.firebase.FirebaseService
import org.example.project.firebase.NotificationService
import org.example.project.model.request.ReportRequest
import org.example.project.model.request.StatusLaporanRequest
import org.koin.ktor.ext.inject

fun Application.reportRoute() {
    val reportService: ReportService by inject()
    val notificationService: NotificationService by inject()
    routing {
        route("/api") {
            route("/user/reports") {
                post {
                    val reportRequest = call.receive<ReportRequest>()
                    val response = reportService.create(reportRequest)
                    call.respond(HttpStatusCode.Created, successResponse(response, "Laporan berhasil dibuat"))
                }
                get {
                    val response = reportService.getAll()
                    call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
                }
                route("/{id}") {
                    get {
                        val id =
                            call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                        val report = reportService.getById(id)
                        call.respond(
                            HttpStatusCode.OK,
                            successResponse(report, "Berhasil mengambil laporan dengan ID $id")
                        )
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
            }
            route("/ketua/reports") {
                get {
                    val response = reportService.getAll()
                    call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
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
        }
    }
}
