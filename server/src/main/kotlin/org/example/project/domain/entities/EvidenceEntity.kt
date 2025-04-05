package org.example.project.domain.entities

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Evidences : Table("Evidences") {
//    val id = integer("id").autoIncrement()
    val reportId = integer("report_id").references(Reports.id, onDelete = ReferenceOption.CASCADE)
    val buktiKe = integer("bukti_ke")
    val filePath = text("file_path")
    val createdAt = varchar("created_at", 25)
    val updatedAt = varchar("updated_at", 25)

    override val primaryKey = PrimaryKey(reportId, buktiKe)
}