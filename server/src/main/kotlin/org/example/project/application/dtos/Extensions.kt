package org.example.project.application.dtos

import org.example.project.application.dtos.requests.EvidenceRequest
import org.example.project.application.dtos.requests.ProductRequest
import org.example.project.application.dtos.requests.ReportRequest
import org.example.project.model.Evidence
import org.example.project.model.Product
import org.example.project.model.Report
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ProductRequest.toEntity(): Product {
    return Product(
        id = 0,
        name = this.name,
        description = this.description,
        price = this.price,
        imageUrl = this.imageUrl
    )
}

fun ReportRequest.toEntity(): Report {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return Report(
        id = null,
        isKorban = this.isKorban,
        deskripsi = this.deskripsi,
        jenisKekerasan = this.jenisKekerasan,
        tempatKejadian = this.tempatKejadian,
        tanggalKejadian = LocalDate.parse(this.tanggalKejadian, dateFormatter),
        statusLaporan = this.statusLaporan,
        createdAt = this.createdAt?.let { LocalDateTime.parse(it, dateTimeFormatter) },
        updatedAt = LocalDateTime.now()
    )
}

fun EvidenceRequest.toEntity(buktiKe: Int): Evidence {

    return Evidence(
        reportId = this.reportId,
        buktiKe = buktiKe,
        filePath = this.filePath,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}