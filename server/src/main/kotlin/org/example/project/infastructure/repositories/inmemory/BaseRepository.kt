package org.example.project.infastructure.repositories.inmemory

import org.example.project.infastructure.repositories.interfaces.IEntityRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class BaseRepository<T : Table, E, ID>(
    private val table: T, private val idColumn: Column<ID>
) : IEntityRepository<E, ID> {

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    protected suspend fun <R> dbQuery(block: () -> R): R = newSuspendedTransaction { block() }

    override suspend fun getAll(): List<E> = dbQuery {
        table.selectAll().map { rowToEntity(it) }
    }

    override suspend fun getById(id: ID): E? = dbQuery {
        table.selectAll().where { idColumn eq id }.map { rowToEntity(it) }.singleOrNull()
    }

    override suspend fun findById(id: ID): Boolean = dbQuery {
        table.select(listOf(idColumn)).where { idColumn eq id }.count() > 0
    }

    override suspend fun delete(id: ID): Boolean = dbQuery {
        table.deleteWhere { idColumn eq id } > 0
    }

    protected abstract fun rowToEntity(row: ResultRow): E

    fun getCurrentTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.now().format(formatter)
    }

    fun getCurrentTime(): LocalDateTime = LocalDateTime.now()

    fun getCurrentTimeAsString(): String {
        return LocalDateTime.now().format(dateTimeFormatter)
    }
}
