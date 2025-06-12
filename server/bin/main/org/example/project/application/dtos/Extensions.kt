package org.example.project.application.dtos

import org.example.project.model.entities.Report
import org.example.project.model.entities.Evidence
import org.example.project.model.entities.Product
import org.example.project.model.request.EvidenceRequest
import org.example.project.model.request.ProductRequest
import org.example.project.model.request.ReportRequest
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
    val now = LocalDateTime.now()
    return Evidence(
        reportId = this.reportId,
        buktiKe = buktiKe,
        filePath = this.filePath,
        createdAt = now,
        updatedAt = now
    )
}