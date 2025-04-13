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
                            val originalFileName = part.originalFileName ?: "evidence_${UUID.randomUUID()}"
                            val fileExtension = originalFileName.substringAfterLast('.', "")
                            val uniqueFileName = "${UUID.randomUUID()}.${fileExtension}"
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