package com.retrocam.app.domain.model

/**
 * Camera state wrapper for UI
 */
data class CameraState(
    val mode: CameraMode = CameraMode.NORMAL,
    val isReady: Boolean = false,
    val error: String? = null
)
