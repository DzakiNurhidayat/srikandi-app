package org.example.project.firebase

import org.example.project.domain.entities.DeviceTokens
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class FirebaseRepository {

    suspend fun saveToken(userId: String, token: String) {
        newSuspendedTransaction {
            DeviceTokens.deleteWhere { DeviceTokens.userId eq userId }
            DeviceTokens.insert {
                it[DeviceTokens.userId] = userId
                it[DeviceTokens.token] = token
            }
        }
    }

    suspend fun getToken(userId: String): String? = newSuspendedTransaction {
        DeviceTokens.selectAll().where { DeviceTokens.userId eq userId }
            .firstOrNull()?.get(DeviceTokens.token)
    }
}
