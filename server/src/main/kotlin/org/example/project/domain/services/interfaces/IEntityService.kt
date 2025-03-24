package org.example.project.domain.services.interfaces

interface IEntityService<T, ID> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: ID): T?
    suspend fun create(entity: T): T
    suspend fun update(id: ID, entity: T): Boolean
    suspend fun delete(id: ID): Boolean
}
