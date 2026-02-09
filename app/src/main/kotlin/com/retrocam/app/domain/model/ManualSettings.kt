package com.retrocam.app.domain.model

/**
 * Manual camera settings for Pro mode
 */
data class ManualSettings(
    val iso: Int? = null,           // ISO sensitivity (e.g., 100, 200, 400, 800)
    val shutterSpeed: Long? = null, // Exposure time in nanoseconds
    val whiteBalance: Int? = null,  // Color temperature in Kelvin
    val focusDistance: Float? = null, // Focus distance (0f = infinity, 1f = minimum)
    val exposureCompensation: Int? = null // EV compensation
)
