package com.retrocam.app.presentation.presets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retrocam.app.domain.model.FilterPreset
import com.retrocam.app.domain.repository.FilterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresetsViewModel @Inject constructor(
    private val filterRepository: FilterRepository
) : ViewModel() {

    private val _presets = MutableStateFlow<List<FilterPreset>>(emptyList())
    val presets: StateFlow<List<FilterPreset>> = _presets.asStateFlow()

    private val _selectedPreset = MutableStateFlow<FilterPreset?>(null)
    val selectedPreset: StateFlow<FilterPreset?> = _selectedPreset.asStateFlow()

    init {
        loadPresets()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            filterRepository.getPresets().collect { presetList ->
                _presets.value = presetList
            }
        }
    }

    fun selectPreset(preset: FilterPreset) {
        _selectedPreset.value = preset
        viewModelScope.launch {
            filterRepository.applyPreset(preset.id)
        }
    }

    fun saveCurrentAsPreset(name: String) {
        viewModelScope.launch {
            filterRepository.saveCurrentAsPreset(name)
        }
    }

    fun deletePreset(presetId: String) {
        viewModelScope.launch {
            filterRepository.deletePreset(presetId)
        }
    }
}
