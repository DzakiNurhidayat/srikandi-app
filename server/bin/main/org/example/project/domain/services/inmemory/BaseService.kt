package org.example.project.domain.services.inmemory

import io.ktor.server.plugins.*
import org.example.project.domain.services.interfaces.IEntityService
import org.example.project.infastructure.repositories.interfaces.IEntityRepository

abstract class BaseService<R, ID, T>(
    val repository: IEntityRepository<T, ID>
) : IEntityService<R, ID, T> {

    override suspend fun getAll(): List<T> {
        return repository.getAll()
    }

    override suspend fun getById(id: ID): T? {
        return repository.getById(id) ?: throw NotFoundException("Data dengan ID $id tidak ditemukan")
    }

    override suspend fun findById(id: ID): Boolean {
        return repository.findById(id)
    }

    override suspend fun delete(id: ID): Boolean {
        if (!repository.findById(id)) {
            throw NotFoundException("Data dengan ID $id tidak ditemukan")
        }
        return repository.delete(id)
    }
}