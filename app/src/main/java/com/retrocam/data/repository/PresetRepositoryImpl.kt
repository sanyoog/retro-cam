package com.retrocam.data.repository

import com.retrocam.data.local.PresetDao
import com.retrocam.data.local.toDomain
import com.retrocam.data.local.toEntity
import com.retrocam.domain.model.Preset
import com.retrocam.domain.repository.PresetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PresetRepository using Room database.
 */
@Singleton
class PresetRepositoryImpl @Inject constructor(
    private val presetDao: PresetDao
) : PresetRepository {
    
    override fun getAllPresets(): Flow<List<Preset>> {
        return presetDao.getAllPresets().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getPresetById(id: Long): Preset? {
        return presetDao.getPresetById(id)?.toDomain()
    }
    
    override suspend fun savePreset(preset: Preset): Long {
        return presetDao.insertPreset(preset.toEntity())
    }
    
    override suspend fun updatePreset(preset: Preset) {
        presetDao.updatePreset(preset.toEntity())
    }
    
    override suspend fun deletePreset(preset: Preset) {
        presetDao.deletePreset(preset.toEntity())
    }
    
    override suspend fun deletePresetById(id: Long) {
        presetDao.deletePresetById(id)
    }
}
