package org.example.project.domain.services.inmemory

import org.example.project.domain.services.interfaces.IEntityService
import org.example.project.infastructure.repositories.interfaces.IEntityRepository

abstract class BaseService<T, ID>(
    private val repository: IEntityRepository<T, ID>
) : IEntityService<T, ID> {
    override suspend fun getAll(): List<T> = repository.getAll()
    override suspend fun getById(id: ID): T? = repository.getById(id)
    override suspend fun create(entity: T): T = repository.create(entity)
    override suspend fun update(id: ID, entity: T): Boolean = repository.update(id, entity)
    override suspend fun delete(id: ID): Boolean = repository.delete(id)
}