package org.example.project.application.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.copyTo
import org.example.project.application.dtos.successResponse
import java.io.File
import java.util.UUID

// Supported file types
private val SUPPORTED_IMAGE_TYPES = setOf("image/jpeg", "image/png", "image/jpg")
private val SUPPORTED_VIDEO_TYPES = setOf("video/mp4", "video/quicktime", "video/x-msvideo")
private val SUPPORTED_AUDIO_TYPES = setOf("audio/mpeg", "audio/wav", "audio/ogg")
private val SUPPORTED_TYPES = SUPPORTED_IMAGE_TYPES + SUPPORTED_VIDEO_TYPES + SUPPORTED_AUDIO_TYPES

fun Application.uploadRoute() {
    routing {
        route("/api/reports") {
            post("/upload-evidence") {
                val uploadsDir = File("uploads")
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs()
                }

                val filePaths = mutableListOf<String>()
                val multipartData = call.receiveMultipart()

                try {
                    multipartData.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            val contentType = part.contentType?.toString() ?: "application/octet-stream"
                            
                            // Validate file type
                            if (contentType !in SUPPORTED_TYPES) {
                                call.respond(
                                    HttpStatusCode.UnsupportedMediaType,
                                    successResponse(null, "Tipe file tidak didukung. Hanya mendukung gambar (JPEG, PNG), video (MP4, MOV, AVI), dan audio (MP3, WAV, OGG)")
                                )
                                return@forEachPart
                            }

                            // Get file extension based on content type
                            val fileExtension = when (contentType) {
                                in SUPPORTED_IMAGE_TYPES -> when (contentType) {
                                    "image/jpeg" -> "jpg"
                                    "image/png" -> "png"
                                    else -> "jpg"
                                }
                                in SUPPORTED_VIDEO_TYPES -> when (contentType) {
                                    "video/mp4" -> "mp4"
                                    "video/quicktime" -> "mov"
                                    "video/x-msvideo" -> "avi"
                                    else -> "mp4"
                                }
                                in SUPPORTED_AUDIO_TYPES -> when (contentType) {
                                    "audio/mpeg" -> "mp3"
                                    "audio/wav" -> "wav"
                                    "audio/ogg" -> "ogg"
                                    else -> "mp3"
                                }
                                else -> "dat"
                            }

                            val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"
                            val filePath = "uploads/$uniqueFileName"
                            val file = File(filePath)

                            file.outputStream().use { output ->
                                part.provider().copyTo(output)
                            }

                            filePaths.add(filePath)
                        }
                        part.dispose()
                    }

                    if (filePaths.isEmpty()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            successResponse(null, "Tidak ada file yang diunggah")
                        )
                        return@post
                    }

                    call.respond(
                        HttpStatusCode.OK,
                        successResponse(filePaths, "File berhasil diunggah")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        successResponse(null, "Gagal mengunggah file: ${e.message}")
                    )
                }
            }
        }
    }
}