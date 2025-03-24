package org.example.project.infastructure.repositories.inmemory

import org.example.project.infastructure.repositories.interfaces.IEntityRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class BaseRepository<T : Table, E, ID>(
    private val table: T,
    private val idColumn: Column<ID>
) : IEntityRepository<E, ID> {

    protected abstract fun rowToEntity(row: ResultRow): E

    protected suspend fun <R> dbQuery(block: () -> R): R =
        newSuspendedTransaction { block() }

    override suspend fun getAll(): List<E> = dbQuery {
        table.selectAll().map { rowToEntity(it) }
    }

    override suspend fun getById(id: ID): E? = dbQuery {
        table.selectAll().where(idColumn eq id).map { rowToEntity(it) }.singleOrNull()
    }

    override suspend fun delete(id: ID): Boolean = dbQuery {
        table.deleteWhere { idColumn eq id } > 0
    }
}


