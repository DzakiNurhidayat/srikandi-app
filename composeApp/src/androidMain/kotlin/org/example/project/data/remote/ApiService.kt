package org.example.project.data.remote

import org.example.project.model.Response
import org.example.project.model.entities.Product
import org.example.project.model.entities.Report
import org.example.project.model.request.StatusLaporanRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApiService {
    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    // REPORT
    @GET("api/ketua/reports")
    suspend fun getReports(): Response<List<Report>>
    @PATCH("api/ketua/reports/{id}")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body statusRequest: StatusLaporanRequest
    ): Response<Boolean>
}
