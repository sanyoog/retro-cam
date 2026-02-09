package com.retrocam.app.domain.repository

import android.graphics.Bitmap
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterPreset

/**
 * Repository for filter operations
 */
interface FilterRepository {
    /**
     * Apply filter to bitmap
     */
    suspend fun applyFilter(bitmap: Bitmap, config: FilterConfig): Bitmap
    
    /**
     * Get all saved presets
     */
    suspend fun getPresets(): List<FilterPreset>
    
    /**
     * Save a filter preset
     */
    suspend fun savePreset(preset: FilterPreset)
    
    /**
     * Delete a preset
     */
    suspend fun deletePreset(presetId: String)
    
    /**
     * Release filter resources
     */
    fun release()
}
