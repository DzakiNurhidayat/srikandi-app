package org.example.project.application.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.requests.EvidenceRequest
import org.example.project.application.dtos.successResponse
import org.example.project.domain.services.inmemory.EvidenceService
import org.koin.ktor.ext.inject

fun Application.evidenceRoute() {
    val evidenceService: EvidenceService by inject()

    routing {
        route("/api/evidences") {
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
                call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil bukti untuk laporan dengan ID $reportId"))
            }

            get("/{reportId}/{buktiKe}") {
                val reportId = call.parameters["reportId"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("Report ID tidak valid")
                val buktiKe = call.parameters["buktiKe"]?.toIntOrNull()
                    ?: throw IllegalArgumentException("BuktiKe tidak valid")

                val evidence = evidenceService.getById(Pair(reportId, buktiKe))
                call.respond(HttpStatusCode.OK, successResponse(evidence, "Berhasil mengambil bukti ke-$buktiKe untuk laporan ID $reportId"))
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
                call.respond(HttpStatusCode.OK, successResponse(null, "Bukti ke-$buktiKe untuk laporan ID $reportId berhasil dihapus"))
            }
        }
    }
}
