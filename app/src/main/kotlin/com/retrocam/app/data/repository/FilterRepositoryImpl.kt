package com.retrocam.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.retrocam.app.domain.filter.FilterProcessor
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterPreset
import com.retrocam.app.domain.repository.FilterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FilterRepository
 */
@Singleton
class FilterRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val filterProcessor: FilterProcessor
) : FilterRepository {
    
    private val _presets = MutableStateFlow<List<FilterPreset>>(emptyList())
    private var currentFilterConfig: FilterConfig = FilterConfig()
    
    override suspend fun applyFilter(bitmap: Bitmap, config: FilterConfig): Bitmap = 
        withContext(Dispatchers.Default) {
            currentFilterConfig = config
            filterProcessor.applyFilter(bitmap, config)
        }
    
    override fun getPresets(): Flow<List<FilterPreset>> = _presets.asStateFlow()
    
    override suspend fun savePreset(preset: FilterPreset) {
        withContext(Dispatchers.IO) {
            // TODO: Save to persistent storage
            val updatedList = _presets.value.toMutableList()
            updatedList.removeAll { it.id == preset.id }
            updatedList.add(preset)
            _presets.value = updatedList
        }
    }
    
    override suspend fun deletePreset(presetId: String) {
        withContext(Dispatchers.IO) {
            _presets.value = _presets.value.filter { it.id != presetId }
        }
    }
    
    override suspend fun applyPreset(presetId: String) {
        withContext(Dispatchers.Default) {
            val preset = _presets.value.find { it.id == presetId }
            preset?.let {
                currentFilterConfig = FilterConfig(
                    type = it.filterType,
                    intensity = it.intensity
                )
            }
        }
    }
    
    override suspend fun saveCurrentAsPreset(name: String) {
        val newPreset = FilterPreset(
            id = UUID.randomUUID().toString(),
            name = name,
            filterType = currentFilterConfig.type,
            intensity = currentFilterConfig.intensity,
            isBuiltIn = false
        )
        savePreset(newPreset)
    }
    
    override fun release() {
        filterProcessor.release()
    }
}
