package com.retrocam.app.domain.repository

import android.graphics.Bitmap
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterPreset
import kotlinx.coroutines.flow.Flow

/**
 * Repository for filter operations
 */
interface FilterRepository {
    /**
     * Apply filter to bitmap
     */
    suspend fun applyFilter(bitmap: Bitmap, config: FilterConfig): Bitmap
    
    /**
     * Get all saved presets as Flow
     */
    fun getPresets(): Flow<List<FilterPreset>>
    
    /**
     * Save a filter preset
     */
    suspend fun savePreset(preset: FilterPreset)
    
    /**
     * Delete a preset
     */
    suspend fun deletePreset(presetId: String)
    
    /**
     * Apply a preset by ID
     */
    suspend fun applyPreset(presetId: String)
    
    /**
     * Save current filter settings as a new preset
     */
    suspend fun saveCurrentAsPreset(name: String)
    
    /**
     * Release filter resources
     */
    fun release()
}
