package com.retrocam.app.domain.camera

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import com.retrocam.app.domain.model.CameraCapabilities
import com.retrocam.app.domain.model.ManualSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Camera2 API manual controls
 * Handles ISO, shutter speed, white balance, and focus
 */
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@Singleton
class ManualCameraController @Inject constructor() {

    private val _capabilities = MutableStateFlow<CameraCapabilities?>(null)
    val capabilities: StateFlow<CameraCapabilities?> = _capabilities.asStateFlow()

    private var camera2Control: Camera2CameraControl? = null

    /**
     * Initialize with camera control and info
     */
    fun initialize(cameraControl: CameraControl, cameraInfo: CameraInfo) {
        camera2Control = Camera2CameraControl.from(cameraControl)
        val camera2Info = Camera2CameraInfo.from(cameraInfo)
        
        // Extract camera characteristics
        val characteristics = camera2Info.getCameraCharacteristic(
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL
        )
        
        extractCapabilities(camera2Info)
    }

    /**
     * Extract supported manual control capabilities
     */
    private fun extractCapabilities(camera2Info: Camera2CameraInfo) {
        val isoRange = camera2Info.getCameraCharacteristic(
            CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE
        )
        
        val exposureTimeRange = camera2Info.getCameraCharacteristic(
            CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE
        )
        
        val focusDistanceRange = camera2Info.getCameraCharacteristic(
            CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE
        )
        
        val exposureCompensationRange = camera2Info.getCameraCharacteristic(
            CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE
        )
        
        val availableCapabilities = camera2Info.getCameraCharacteristic(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
        )
        
        val isManualSensorSupported = availableCapabilities?.contains(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR
        ) ?: false
        
        val isManualPostProcessingSupported = availableCapabilities?.contains(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING
        ) ?: false

        _capabilities.value = CameraCapabilities(
            isoRange = isoRange?.let { IntRange(it.lower, it.upper) },
            exposureTimeRange = exposureTimeRange?.let { LongRange(it.lower, it.upper) },
            focusDistanceRange = focusDistanceRange?.let { 0f..it },
            exposureCompensationRange = exposureCompensationRange?.let { 
                IntRange(it.lower, it.upper) 
            },
            isManualFocusSupported = focusDistanceRange != null && focusDistanceRange > 0f,
            isManualExposureSupported = isManualSensorSupported,
            isManualWhiteBalanceSupported = isManualPostProcessingSupported
        )
    }

    /**
     * Apply manual camera settings
     * Note: Manual controls are placeholders for Phase 2 - will be fully implemented in Phase 3
     */
    fun applyManualSettings(settings: ManualSettings) {
        // TODO: Implement manual controls using Camera2 CaptureRequest
        // For now, we'll use CameraX standard controls
    }

    /**
     * Reset to auto mode
     */
    fun resetToAuto() {
        // TODO: Reset to auto exposure/focus/white balance
    }
}
