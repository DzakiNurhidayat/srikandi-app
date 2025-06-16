package org.example.project.data.remote

import okhttp3.MultipartBody
import org.example.project.model.Response
import org.example.project.model.entities.FormSatu
import org.example.project.model.entities.Product
import org.example.project.model.entities.Report
import org.example.project.model.request.*
import retrofit2.http.*

interface ApiService {
    @POST("api/reports/ketua/form-1")
    suspend fun createFormSatu(
        @Query("reportId") reportId: Int,
        @Body formSatuRequest: FormSatuRequest
    ): Response<FormSatu>

    @GET("api/reports/ketua/form-1")
    suspend fun getFormSatuByReportId(
        @Query("reportId") reportId: Int
    ): Response<FormSatu>

    // Endpoint lain tetap sama
    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("api/reports/ketua")
    suspend fun getReports(): Response<List<Report>>

    @GET("api/reports/ketua/{id}")
    suspend fun getReportById(@Path("id") id: Int): Response<Report>

    @PATCH("api/reports/{id}")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body statusLaporanRequest: StatusLaporanRequest
    ): Response<Boolean>

    @PATCH("api/reports/{id}/update-invitation")
    suspend fun updateInvitationStatus(
        @Path("id") id: Int,
        @Body request: StatusLaporanRequest
    ): Response<Boolean>

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

    @GET("api/reports/user/{id}")
    suspend fun getUserReportById(@Path("id") id: Int): Response<Report>

    @PUT("api/reports/user/{id}")
    suspend fun editReport(
        @Path("id") id: Int,
        @Body reportRequest: ReportRequest
    ): Response<Report>
}