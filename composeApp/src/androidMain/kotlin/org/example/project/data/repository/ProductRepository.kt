package org.example.project.data.repository

import org.example.project.data.remote.ApiService
import org.example.project.model.entities.Product
import org.example.project.model.Response
import javax.inject.Inject

class ProductRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getProducts(): Response<List<Product>> = apiService.getProducts()
}

