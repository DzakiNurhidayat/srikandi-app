package org.example.project.data.remote

import org.example.project.model.Response
import org.example.project.model.Product
import retrofit2.http.GET

interface ApiService {
    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>
}
