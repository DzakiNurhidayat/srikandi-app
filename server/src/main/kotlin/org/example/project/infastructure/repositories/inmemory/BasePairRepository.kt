package org.example.project.infastructure.repositories.inmemory

import org.example.project.infastructure.repositories.interfaces.IEntityRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class BasePairRepository<T : Table, E, ID>(
    private val table: T, private val idColumn: Pair<Column<Int>, Column<Int>>
) : IEntityRepository<E, ID> {

    protected val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    protected suspend fun <R> dbQuery(block: () -> R): R = newSuspendedTransaction { block() }

    override suspend fun getAll(): List<E> = dbQuery {
        table.selectAll().map { rowToEntity(it) }
    }

    override suspend fun getById(id: ID): E? = dbQuery {
        val (first, second) = id as Pair<Int, Int>
        table.selectAll().where { (idColumn.first eq first) and (idColumn.second eq second) }
            .mapNotNull { rowToEntity(it) }
            .singleOrNull()
    }

    override suspend fun findById(id: ID): Boolean = dbQuery {
        val (first, second) = id as Pair<Int, Int>
        table.selectAll().where { (idColumn.first eq first) and (idColumn.second eq second) }
            .count() > 0
    }

    override suspend fun delete(id: ID): Boolean = dbQuery {
        val (first, second) = id as Pair<Int, Int>
        table.deleteWhere { (idColumn.first eq first) and (idColumn.second eq second) } > 0
    }

    protected abstract fun rowToEntity(row: ResultRow): E

    fun getCurrentTimestamp(): String {
        return LocalDateTime.now().format(dateTimeFormatter)
    }

    fun getCurrentTime(): LocalDateTime = LocalDateTime.now()

    fun getCurrentTimeAsString(): String {
        return LocalDateTime.now().format(dateTimeFormatter)
    }
}