package com.retrocam.app.presentation.camera

import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retrocam.app.domain.camera.ManualCameraController
import com.retrocam.app.domain.model.CameraCapabilities
import com.retrocam.app.domain.model.CameraMode
import com.retrocam.app.domain.model.CameraState
import com.retrocam.app.domain.model.CaptureResult
import com.retrocam.app.domain.model.ManualSettings
import com.retrocam.app.domain.repository.CameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CameraViewModel"

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val manualCameraController: ManualCameraController
) : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private val _captureResult = MutableStateFlow<CaptureResult?>(null)
    val captureResult: StateFlow<CaptureResult?> = _captureResult.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var camera: Camera? = null

    private val _manualSettings = MutableStateFlow(ManualSettings())
    val manualSettings: StateFlow<ManualSettings> = _manualSettings.asStateFlow()

    val cameraCapabilities: StateFlow<CameraCapabilities?> = 
        manualCameraController.capabilities

    init {
        initializeCamera()
    }

    private fun initializeCamera() {
        viewModelScope.launch {
            try {
                cameraProvider = cameraRepository.getCameraProvider()
                preview = cameraRepository.createPreview()
                imageCapture = cameraRepository.createImageCapture()
                
                _cameraState.update { it.copy(isReady = true, error = null) }
                Log.d(TAG, "Camera initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize camera", e)
                _cameraState.update { 
                    it.copy(isReady = false, error = "Failed to initialize camera: ${e.message}")
                }
            }
        }
    }

    fun getPreview(): Preview? = preview

    fun getCamera(): Camera? = camera

    fun setCamera(cam: Camera) {
        camera = cam
        // Initialize manual controls with camera
        manualCameraController.initialize(cam.cameraControl, cam.cameraInfo)
    }

    fun getCameraProvider(): ProcessCameraProvider? = cameraProvider

    fun getCameraSelector() = cameraRepository.getCameraSelector()

    fun capturePhoto() {
        val capture = imageCapture ?: run {
            Log.e(TAG, "ImageCapture not initialized")
            _captureResult.value = CaptureResult.Error("Camera not ready")
            return
        }

        viewModelScope.launch {
            try {
                val settings = if (_cameraState.value.mode == CameraMode.PRO) {
                    _manualSettings.value
                } else {
                    null
                }
                
                val result = cameraRepository.capturePhoto(capture, settings)
                _captureResult.value = result
                
                when (result) {
                    is CaptureResult.Success -> {
                        Log.d(TAG, "Photo captured successfully: ${result.uri}")
                    }
                    is CaptureResult.Error -> {
                        Log.e(TAG, "Photo capture failed: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Photo capture exception", e)
                _captureResult.value = CaptureResult.Error("Failed to capture: ${e.message}")
            }
        }
    }

    fun toggleCameraMode() {
        val newMode = if (_cameraState.value.mode == CameraMode.NORMAL) {
            CameraMode.PRO
        } else {
            CameraMode.NORMAL
        }
        
        _cameraState.update { it.copy(mode = newMode) }
        
        // Reset to auto when switching to normal mode
        if (newMode == CameraMode.NORMAL) {
            manualCameraController.resetToAuto()
            _manualSettings.value = ManualSettings()
        }
        
        Log.d(TAG, "Camera mode: $newMode")
    }

    fun updateManualSettings(settings: ManualSettings) {
        if (_cameraState.value.mode == CameraMode.PRO) {
            _manualSettings.value = settings
            manualCameraController.applyManualSettings(settings)
            Log.d(TAG, "Applied manual settings: $settings")
        }
    }

    fun updateIso(iso: Int) {
        updateManualSettings(_manualSettings.value.copy(iso = iso))
    }

    fun updateShutterSpeed(speed: Long) {
        updateManualSettings(_manualSettings.value.copy(shutterSpeed = speed))
    }

    fun updateWhiteBalance(kelvin: Int) {
        updateManualSettings(_manualSettings.value.copy(whiteBalance = kelvin))
    }

    fun updateFocusDistance(distance: Float) {
        updateManualSettings(_manualSettings.value.copy(focusDistance = distance))
    }

    fun updateExposureCompensation(ev: Int) {
        updateManualSettings(_manualSettings.value.copy(exposureCompensation = ev))
    }

    fun clearCaptureResult() {
        _captureResult.value = null
    }

    override fun onCleared() {
        super.onCleared()
        cameraProvider?.unbindAll()
    }
}
