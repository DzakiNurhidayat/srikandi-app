package org.example.project.data.repositories

import org.example.project.data.remote.ApiService
import org.example.project.model.Response
import org.example.project.model.entities.Product
import javax.inject.Inject
import javax.inject.Named

class ProductRepository @Inject constructor(@Named("internal") private val apiService: ApiService) {
    suspend fun getProducts(): Response<List<Product>> = apiService.getProducts()
}

