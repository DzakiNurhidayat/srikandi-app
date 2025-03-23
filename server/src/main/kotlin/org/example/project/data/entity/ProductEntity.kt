package org.example.project.data.entity

import org.jetbrains.exposed.dao.id.IntIdTable

object ProductTable : IntIdTable("products") {
    val name = varchar("name", 255)
    val description = text("description")
    val price = double("price")
    val imageUrl = varchar("image_url", 512).nullable()
    val createdAt = varchar("created_at", 512)
    val updatedAt = varchar("updated_at", 512)
}