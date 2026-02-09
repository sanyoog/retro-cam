package com.retrocam.domain.model

/**
 * Represents a saved preset containing filter settings and camera parameters.
 */
data class Preset(
    val id: Long = 0,
    val name: String,
    val filter: Filter,
    val cameraSettings: CameraSettings? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Camera settings for Pro Mode.
 */
data class CameraSettings(
    val iso: Int? = null,           // null = auto
    val shutterSpeed: Long? = null, // in nanoseconds, null = auto
    val whiteBalance: Int? = null,  // in Kelvin, null = auto
    val focusDistance: Float? = null, // null = auto
    val exposureCompensation: Int? = null // null = auto (0)
)
