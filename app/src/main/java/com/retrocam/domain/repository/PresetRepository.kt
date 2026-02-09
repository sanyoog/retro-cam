package com.retrocam.domain.repository

import com.retrocam.domain.model.Preset
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for preset operations.
 */
interface PresetRepository {
    fun getAllPresets(): Flow<List<Preset>>
    suspend fun getPresetById(id: Long): Preset?
    suspend fun savePreset(preset: Preset): Long
    suspend fun updatePreset(preset: Preset)
    suspend fun deletePreset(preset: Preset)
    suspend fun deletePresetById(id: Long)
}
