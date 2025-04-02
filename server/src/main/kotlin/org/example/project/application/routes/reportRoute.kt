package org.example.project.application.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.requests.ReportRequest
import org.example.project.application.dtos.successResponse
import org.example.project.domain.services.inmemory.ReportService
import org.koin.ktor.ext.inject

fun Application.reportRoute() {
    val reportService: ReportService by inject()

    routing {
        route("/api/user/reports") {
            post {
                val reportRequest = call.receive<ReportRequest>()
                val response = reportService.create(reportRequest)
                call.respond(HttpStatusCode.Created, successResponse(response, "Laporan berhasil dibuat"))
            }
            get {
                val response = reportService.getAll()
                call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua laporan"))
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                val report = reportService.getById(id)
                call.respond(HttpStatusCode.OK, successResponse(report, "Berhasil mengambil laporan dengan ID $id"))
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                val reportRequest = call.receive<ReportRequest>()
                val response = reportService.update(id, reportRequest)
                call.respond(HttpStatusCode.OK, successResponse(response, "Laporan berhasil diupdate"))
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                reportService.delete(id)
                call.respond(HttpStatusCode.OK, successResponse(null, "Berhasil menghapus laporan dengan ID $id"))
            }
            get("/status/{status}") {
                val status = call.parameters["status"] ?: throw IllegalArgumentException("Status tidak valid")
                val reports = reportService.getByStatus(status)
                call.respond(HttpStatusCode.OK, successResponse(reports, "Berhasil mengambil laporan dengan status $status"))
            }
            get("/date-range") {
                val startDate = call.request.queryParameters["startDate"] ?: throw IllegalArgumentException("Tanggal mulai tidak valid")
                val endDate = call.request.queryParameters["endDate"] ?: throw IllegalArgumentException("Tanggal akhir tidak valid")
                val reports = reportService.getByDateRange(startDate, endDate)
                call.respond(HttpStatusCode.OK, successResponse(reports, "Berhasil mengambil laporan dalam rentang tanggal $startDate - $endDate"))
            }
        }
    }
}
