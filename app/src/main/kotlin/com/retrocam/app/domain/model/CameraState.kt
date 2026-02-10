package com.retrocam.app.domain.model

import androidx.camera.core.CameraSelector

/**
 * Camera state wrapper for UI
 */
data class CameraState(
    val mode: CameraMode = CameraMode.NORMAL,
    val isReady: Boolean = false,
    val error: String? = null,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK
)
