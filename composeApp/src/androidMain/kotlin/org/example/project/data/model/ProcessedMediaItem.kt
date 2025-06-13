package org.example.project.data.model

import android.graphics.Bitmap
import android.net.Uri

data class ProcessedMediaItem(
    val remoteUri: Uri,
    val thumbnailBitmap: Bitmap?,
    val mediaType: String,
    val originalPath: String
)