package com.retrocam.app.domain.model

/**
 * Range bounds for manual camera controls
 */
data class CameraCapabilities(
    val isoRange: IntRange? = null,
    val exposureTimeRange: LongRange? = null,
    val focusDistanceRange: ClosedFloatingPointRange<Float>? = null,
    val exposureCompensationRange: IntRange? = null,
    val isManualFocusSupported: Boolean = false,
    val isManualExposureSupported: Boolean = false,
    val isManualWhiteBalanceSupported: Boolean = false
)
