package com.retrocam.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retrocam.domain.model.Filter
import com.retrocam.domain.repository.PresetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the camera screen.
 * Manages camera state, filters, and photo capture.
 */
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraManager: CameraManager,
    private val presetRepository: PresetRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()
    
    /**
     * Handle camera events.
     */
    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.CapturePhoto -> capturePhoto()
            is CameraEvent.SelectFilter -> selectFilter(event.filter)
            is CameraEvent.UpdateFilterIntensity -> updateFilterIntensity(event.intensity)
            is CameraEvent.SwitchMode -> switchMode(event.mode)
            is CameraEvent.UpdateCameraSettings -> updateCameraSettings(event.settings)
            is CameraEvent.ToggleFilterSheet -> toggleFilterSheet()
            is CameraEvent.TogglePresetSheet -> togglePresetSheet()
            is CameraEvent.ToggleProControls -> toggleProControls()
            is CameraEvent.ClearError -> clearError()
        }
    }
    
    private fun capturePhoto() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCapturing = true) }
            
            val result = cameraManager.capturePhoto()
            
            result.onSuccess { uri ->
                _uiState.update { 
                    it.copy(
                        isCapturing = false,
                        capturedImagePath = uri.toString()
                    )
                }
            }
            
            result.onFailure { exception ->
                _uiState.update { 
                    it.copy(
                        isCapturing = false,
                        error = exception.message ?: "Failed to capture photo"
                    )
                }
            }
        }
    }
    
    private fun selectFilter(filter: Filter) {
        _uiState.update { 
            it.copy(
                currentFilter = filter,
                showFilterSheet = false
            )
        }
    }
    
    private fun updateFilterIntensity(intensity: Float) {
        _uiState.update { 
            it.copy(
                currentFilter = it.currentFilter.copy(intensity = intensity)
            )
        }
    }
    
    private fun switchMode(mode: com.retrocam.domain.model.CameraMode) {
        _uiState.update { 
            it.copy(
                mode = mode,
                showProControls = mode == com.retrocam.domain.model.CameraMode.PRO
            )
        }
    }
    
    private fun updateCameraSettings(settings: com.retrocam.domain.model.CameraSettings) {
        _uiState.update { 
            it.copy(cameraSettings = settings)
        }
        cameraManager.applySettings(settings)
    }
    
    private fun toggleFilterSheet() {
        _uiState.update { 
            it.copy(showFilterSheet = !it.showFilterSheet)
        }
    }
    
    private fun togglePresetSheet() {
        _uiState.update { 
            it.copy(showPresetSheet = !it.showPresetSheet)
        }
    }
    
    private fun toggleProControls() {
        _uiState.update { 
            it.copy(showProControls = !it.showProControls)
        }
    }
    
    private fun clearError() {
        _uiState.update { 
            it.copy(error = null)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        cameraManager.release()
    }
}
