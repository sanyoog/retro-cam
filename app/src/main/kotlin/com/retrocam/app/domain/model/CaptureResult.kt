package com.retrocam.app.domain.model

import android.net.Uri

/**
 * Result of a photo capture
 */
sealed class CaptureResult {
    data class Success(val uri: Uri, val filePath: String) : CaptureResult()
    data class Error(val message: String, val exception: Throwable? = null) : CaptureResult()
}
