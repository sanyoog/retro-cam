import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retrocam.app.data.preferences.AppPreferences
import com.retrocam.app.domain.camera.ManualCameraController
import com.retrocam.app.domain.model.CameraCapabilities
import com.retrocam.app.domain.model.CameraMode
import com.retrocam.app.domain.model.CameraState
import com.retrocam.app.domain.model.CaptureResult
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.ManualSettings
import com.retrocam.app.domain.repository.CameraRepository
import com.retrocam.app.domain.repository.FilterRepository
import com.retrocam.app.util.SoundEffects
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CameraViewModel"

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cameraRepository: CameraRepository,
    private val manualCameraController: ManualCameraController,
    private val filterRepository: FilterRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private val _captureResult = MutableStateFlow<CaptureResult?>(null)
    val captureResult: StateFlow<CaptureResult?> = _captureResult.asStateFlow()
    
    private val _lastPhotoUri = MutableStateFlow<Uri?>(null)
    val lastPhotoUri: StateFlow<Uri?> = _lastPhotoUri.asStateFlow()
    
    private val _captureFlashTrigger = MutableStateFlow(false)
    val captureFlashTrigger: StateFlow<Boolean> = _captureFlashTrigger.asStateFlow()
    
    val soundEffects = SoundEffects(context)
    
    // Observe preferences and update sound effects
    val soundEnabled: StateFlow<Boolean> = appPreferences.soundEnabled.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    val hapticsEnabled: StateFlow<Boolean> = appPreferences.hapticsEnabled.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    init {
        // Observe sound preference and update sound effects
        viewModelScope.launch {
            soundEnabled.collect { enabled ->
                soundEffects.setSoundEnabled(enabled)
            }
        }
    }

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var camera: Camera? = null

    private val _manualSettings = MutableStateFlow(ManualSettings())
    val manualSettings: StateFlow<ManualSettings> = _manualSettings.asStateFlow()

    val cameraCapabilities: StateFlow<CameraCapabilities?> = 
        manualCameraController.capabilities
    
    // Filter state
    private val _currentFilter = MutableStateFlow(FilterConfig())
    val currentFilter: StateFlow<FilterConfig> = _currentFilter.asStateFlow()

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

    fun getImageCapture(): androidx.camera.core.ImageCapture? = imageCapture

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
        
        // Trigger flash animation
        _captureFlashTrigger.value = !_captureFlashTrigger.value
        
        // Play shutter sound
        soundEffects.playShutter()

        viewModelScope.launch {
            try {
                val settings = if (_cameraState.value.mode == CameraMode.PRO) {
                    _manualSettings.value
                } else {
                    null
                }
                
                val result = cameraRepository.capturePhoto(capture, settings)
                
                // Apply filter if one is selected (not NONE)
                val finalResult = if (result is CaptureResult.Success && _currentFilter.value.type != com.retrocam.app.domain.model.FilterType.NONE) {
                    applyFilterToPhoto(result.uri, _currentFilter.value) ?: result
                } else {
                    result
                }
                
                _captureResult.value = finalResult
                
                when (finalResult) {
                    is CaptureResult.Success -> {
                        Log.d(TAG, "Photo captured successfully: ${finalResult.uri}")
                        _lastPhotoUri.value = finalResult.uri
                    }
                    is CaptureResult.Error -> {
                        Log.e(TAG, "Photo capture failed: ${finalResult.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Photo capture exception", e)
                _captureResult.value = CaptureResult.Error("Failed to capture: ${e.message}")
            }
        }
    }
    
    private suspend fun applyFilterToPhoto(uri: Uri, filterConfig: FilterConfig): CaptureResult? {
        return try {
            // Load the image
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                
                // Apply filter
                val filteredBitmap = filterRepository.applyFilter(bitmap, filterConfig)
                
                // Save filtered image (replace original)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    filteredBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                }
                
                CaptureResult.Success(uri = uri, filePath = uri.toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply filter", e)
            null
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
    
    // Filter methods
    fun updateFilter(config: FilterConfig) {
        _currentFilter.value = config
        Log.d(TAG, "Filter updated: ${config.type} at ${config.intensity * 100}%")
    }

    fun clearCaptureResult() {
        _captureResult.value = null
    }

    override fun onCleared() {
        super.onCleared()
        cameraProvider?.unbindAll()
        soundEffects.release()
    }
}
