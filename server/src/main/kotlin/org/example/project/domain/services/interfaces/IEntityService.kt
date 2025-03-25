package org.example.project.domain.services.interfaces

interface IEntityService<R, ID, T> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: ID): T?
    suspend fun create(request: R): T?
    suspend fun update(id: ID, request: R): T
    suspend fun delete(id: ID): Boolean
}
