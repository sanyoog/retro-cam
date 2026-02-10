package com.retrocam.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.retrocam.app.domain.filter.FilterProcessor
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterPreset
import com.retrocam.app.domain.repository.FilterRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    
    private val presets = mutableListOf<FilterPreset>()
    
    override suspend fun applyFilter(bitmap: Bitmap, config: FilterConfig): Bitmap = 
        withContext(Dispatchers.Default) {
            filterProcessor.applyFilter(bitmap, config)
        }
    
    override suspend fun getPresets(): List<FilterPreset> = withContext(Dispatchers.IO) {
        // TODO: Load from persistent storage (SharedPreferences or Room DB)
        presets.toList()
    }
    
    override suspend fun savePreset(preset: FilterPreset) {
        withContext(Dispatchers.IO) {
            // TODO: Save to persistent storage
            presets.removeAll { it.id == preset.id }
            presets.add(preset)
        }
    }
    
    override suspend fun deletePreset(presetId: String) {
        withContext(Dispatchers.IO) {
            presets.removeAll { it.id == presetId }
        }
    }
    
    override fun release() {
        filterProcessor.release()
    }
}
