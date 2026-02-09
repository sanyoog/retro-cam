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
@OptIn(ExperimentalCamera2Interop::class)
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
     */
    fun applyManualSettings(settings: ManualSettings) {
        camera2Control?.let { control ->
            control.captureRequestOptions = Camera2CameraControl
                .CaptureRequestOptions.Builder()
                .apply {
                    // Manual ISO
                    settings.iso?.let { iso ->
                        setCaptureRequestOption(
                            CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_OFF
                        )
                        setCaptureRequestOption(
                            CaptureRequest.SENSOR_SENSITIVITY,
                            iso
                        )
                    }
                    
                    // Manual shutter speed
                    settings.shutterSpeed?.let { exposureTime ->
                        setCaptureRequestOption(
                            CaptureRequest.SENSOR_EXPOSURE_TIME,
                            exposureTime
                        )
                    }
                    
                    // Manual white balance
                    settings.whiteBalance?.let { kelvin ->
                        setCaptureRequestOption(
                            CaptureRequest.CONTROL_AWB_MODE,
                            CaptureRequest.CONTROL_AWB_MODE_OFF
                        )
                        // Note: Camera2 doesn't directly support color temperature
                        // We would need to convert Kelvin to RG gains
                    }
                    
                    // Manual focus
                    settings.focusDistance?.let { distance ->
                        setCaptureRequestOption(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_OFF
                        )
                        setCaptureRequestOption(
                            CaptureRequest.LENS_FOCUS_DISTANCE,
                            distance
                        )
                    }
                    
                    // Exposure compensation (works with auto mode)
                    settings.exposureCompensation?.let { compensation ->
                        setCaptureRequestOption(
                            CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
                            compensation
                        )
                    }
                }
                .build()
        }
    }

    /**
     * Reset to auto mode
     */
    fun resetToAuto() {
        camera2Control?.let { control ->
            control.captureRequestOptions = Camera2CameraControl
                .CaptureRequestOptions.Builder()
                .apply {
                    setCaptureRequestOption(
                        CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON
                    )
                    setCaptureRequestOption(
                        CaptureRequest.CONTROL_AWB_MODE,
                        CaptureRequest.CONTROL_AWB_MODE_AUTO
                    )
                    setCaptureRequestOption(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )
                }
                .build()
        }
    }
}
