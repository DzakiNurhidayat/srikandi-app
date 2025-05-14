package org.example.project.domain.entities

import org.example.project.common.enums.Roles
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId = varchar("user_id", 256)
    val deviceToken = varchar("token", 256)
    val createdAt = varchar("created_at", 25)
    val updatedAt = varchar("updated_at", 25)
    override val primaryKey = PrimaryKey(userId)
}

object RolesTable : Table() {
    val name = enumerationByName("role", 50, Roles::class)
    override val primaryKey = PrimaryKey(name)
}

object UserRoles : Table() {
    val user = reference("user_id", Users.userId)
    val role = reference("role", RolesTable.name)

    override val primaryKey = PrimaryKey(user, role, name = "PK_UserRole")
}


