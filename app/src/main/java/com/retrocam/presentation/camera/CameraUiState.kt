package com.retrocam.presentation.camera

import com.retrocam.domain.model.CameraMode
import com.retrocam.domain.model.CameraSettings
import com.retrocam.domain.model.Filter

/**
 * UI state for the camera screen.
 */
data class CameraUiState(
    val mode: CameraMode = CameraMode.NORMAL,
    val currentFilter: Filter = Filter.NONE,
    val cameraSettings: CameraSettings = CameraSettings(),
    val isCapturing: Boolean = false,
    val capturedImagePath: String? = null,
    val error: String? = null,
    val showFilterSheet: Boolean = false,
    val showPresetSheet: Boolean = false,
    val showProControls: Boolean = false
)

/**
 * Events that can be triggered from the camera UI.
 */
sealed class CameraEvent {
    object CapturePhoto : CameraEvent()
    data class SelectFilter(val filter: Filter) : CameraEvent()
    data class UpdateFilterIntensity(val intensity: Float) : CameraEvent()
    data class SwitchMode(val mode: CameraMode) : CameraEvent()
    data class UpdateCameraSettings(val settings: CameraSettings) : CameraEvent()
    object ToggleFilterSheet : CameraEvent()
    object TogglePresetSheet : CameraEvent()
    object ToggleProControls : CameraEvent()
    object ClearError : CameraEvent()
}
