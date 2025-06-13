package org.example.project.domain.entities

import org.jetbrains.exposed.sql.Table

object DeviceTokens : Table() {
    val userId = varchar("user_id", 50)
    val token = varchar("token", 256)
}
