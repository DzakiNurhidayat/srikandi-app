package org.example.project.infastructure.repositories.inmemory

import io.ktor.server.plugins.*
import org.example.project.infastructure.repositories.interfaces.IEntityRepository
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class BaseRepository<T : Table, E, ID>(
    private val table: T, private val idColumn: Column<ID>
) : IEntityRepository<E, ID> {

    protected suspend fun <R> dbQuery(block: () -> R): R = newSuspendedTransaction { block() }

    override suspend fun getAll(): List<E> = dbQuery {
        table.selectAll().map { rowToEntity(it) }
    }

    override suspend fun getById(id: ID): E? = dbQuery {
        table.selectAll().where(idColumn eq id).map { rowToEntity(it) }.singleOrNull()
    }

    override suspend fun delete(id: ID): Boolean = try {
        val deletedRows = dbQuery {
            table.deleteWhere { idColumn eq id }
        }
        if (deletedRows > 0) {
            true
        } else {
            throw NoSuchElementException("Data dengan ID $id tidak ditemukan")
        }
    } catch (e: Exception) {
        throw RuntimeException("Gagal menghapus data dengan ID $id: ${e.message}")
    }

    protected abstract fun rowToEntity(row: ResultRow): E

    fun getCurrentTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.now().format(formatter)
    }
}


