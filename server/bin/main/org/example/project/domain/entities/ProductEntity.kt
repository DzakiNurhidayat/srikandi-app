package org.example.project.domain.entities

import org.jetbrains.exposed.sql.Table

object Products : Table("Products") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description")
    val price = double("price")
    val imageUrl = varchar("image_url", 512).nullable()
    val createdAt = varchar("created_at", 512)
    val updatedAt = varchar("updated_at", 51)

    override val primaryKey = PrimaryKey(id)
}
