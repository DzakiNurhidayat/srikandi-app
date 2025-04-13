package org.example.project.infastructure.repositories.interfaces

interface IEntityRepository<T, ID> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: ID): T?
    suspend fun findById(id: ID): Boolean
    suspend fun create(entity: T): T
    suspend fun update(id: ID, entity: T): T
    suspend fun delete(id: ID): Boolean
}