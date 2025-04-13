package org.example.project.data.remote

import okhttp3.MultipartBody
import org.example.project.model.Response
import org.example.project.model.entities.Product
import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import org.example.project.model.request.StatusLaporanRequest
import retrofit2.http.*

interface ApiService {
    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    // REPORT
    @GET("api/ketua/reports")
    suspend fun getReports(): Response<List<Report>>

    @GET("api/user/reports")
    suspend fun getUserReports(): Response<List<Report>>

    @POST("api/user/reports")
    suspend fun createReport(
        @Body reportRequest: ReportRequest
    ): Response<Report>

    @Multipart
    @POST("api/reports/upload-evidence")
    suspend fun uploadEvidence(
        @Part files: List<MultipartBody.Part>
    ): Response<List<String>>

    @PATCH("api/ketua/reports/{id}")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body statusRequest: StatusLaporanRequest
    ): Response<Boolean>

    @GET("api/user/reports/{id}")
    suspend fun getReportById(@Path("id") id: Int): Response<Report>

    @PUT("api/user/reports/{id}")
    suspend fun updateReport(
        @Path("id") id: Int,
        @Body reportRequest: ReportRequest
    ): Response<Report>

    @DELETE("api/user/reports/{id}")
    suspend fun deleteReport(@Path("id") id: Int): Response<Unit>
}
