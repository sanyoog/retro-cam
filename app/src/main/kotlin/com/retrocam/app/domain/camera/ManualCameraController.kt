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
     */
    fun applyManualSettings(settings: ManualSettings) {
        camera2Control?.let { control ->
            try {
                // Build Camera2 capture request with manual settings
                control.setCaptureRequestOptions(
                    androidx.camera.camera2.interop.CaptureRequestOptions.Builder()
                        .apply {
                            // Set ISO if available
                            settings.iso?.let { iso ->
                                setCaptureRequestOption(
                                    CaptureRequest.SENSOR_SENSITIVITY,
                                    iso
                                )
                                // Disable auto-ISO when manual ISO is set
                                setCaptureRequestOption(
                                    CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_OFF
                                )
                            }
                            
                            // Set shutter speed (exposure time) if available
                            settings.shutterSpeed?.let { speed ->
                                setCaptureRequestOption(
                                    CaptureRequest.SENSOR_EXPOSURE_TIME,
                                    speed * 1000000 // Convert ms to ns
                                )
                            }
                            
                            // Set white balance if available
                            settings.whiteBalance?.let { kelvin ->
                                // Disable auto white balance
                                setCaptureRequestOption(
                                    CaptureRequest.CONTROL_AWB_MODE,
                                    CaptureRequest.CONTROL_AWB_MODE_OFF
                                )
                                // Note: Kelvin conversion to RGB gains would be needed
                                // For now just disable auto
                            }
                            
                            // Set focus distance if available
                            settings.focusDistance?.let { distance ->
                                setCaptureRequestOption(
                                    CaptureRequest.LENS_FOCUS_DISTANCE,
                                    distance
                                )
                                // Disable auto-focus
                                setCaptureRequestOption(
                                    CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_OFF
                                )
                            }
                            
                            // Set exposure compensation if available
                            settings.exposureCompensation?.let { ev ->
                                setCaptureRequestOption(
                                    CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
                                    ev
                                )
                            }
                        }
                        .build()
                )
            } catch (e: Exception) {
                // Log but don't crash - some devices may not support all controls
                android.util.Log.e("ManualCameraController", "Failed to apply settings", e)
            }
        }
    }

    /**
     * Reset to auto mode
     */
    fun resetToAuto() {
        camera2Control?.let { control ->
            try {
                control.setCaptureRequestOptions(
                    androidx.camera.camera2.interop.CaptureRequestOptions.Builder()
                        .apply {
                            // Re-enable auto exposure
                            setCaptureRequestOption(
                                CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON
                            )
                            // Re-enable auto white balance
                            setCaptureRequestOption(
                                CaptureRequest.CONTROL_AWB_MODE,
                                CaptureRequest.CONTROL_AWB_MODE_AUTO
                            )
                            // Re-enable auto focus
                            setCaptureRequestOption(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                        }
                        .build()
                )
            } catch (e: Exception) {
                android.util.Log.e("ManualCameraController", "Failed to reset to auto", e)
            }
        }
    }
}
