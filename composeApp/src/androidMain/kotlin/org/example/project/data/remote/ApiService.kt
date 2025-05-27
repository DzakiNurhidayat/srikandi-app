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
    @GET("api/reports/ketua")
    suspend fun getReports(): Response<List<Report>>

    @GET("api/reports/user")
    suspend fun getUserReports(): Response<List<Report>>

    @POST("api/reports/user")
    suspend fun createReport(
        @Body reportRequest: ReportRequest
    ): Response<Report>

    @Multipart
    @POST("api/reports/user/upload-evidence")
    suspend fun uploadEvidence(
        @Part files: List<MultipartBody.Part>
    ): Response<List<String>>

    @PATCH("api/reports/{id}")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body statusLaporanRequest: StatusLaporanRequest
    ): Response<Boolean>

    @GET("api/reports/user/{id}")
    suspend fun getReportById(@Path("id") id: Int): Response<Report>

    @PUT("api/reports/user/{id}")
    suspend fun editReport(
        @Path("id") id: Int,
        @Body reportRequest: ReportRequest
    ): Response<Report>

    @Multipart
    @POST("api/upload/profile-picture")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part,
    ): Response<String>
}