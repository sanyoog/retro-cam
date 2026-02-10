package com.retrocam.app.domain.usecase

import android.graphics.Bitmap
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterPreset
import com.retrocam.app.domain.repository.FilterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for applying filters to images
 */
class ApplyFilterUseCase @Inject constructor(
    private val filterRepository: FilterRepository
) {
    suspend operator fun invoke(bitmap: Bitmap, config: FilterConfig): Bitmap {
        return filterRepository.applyFilter(bitmap, config)
    }
}

/**
 * Use case for managing filter presets
 */
class ManagePresetsUseCase @Inject constructor(
    private val filterRepository: FilterRepository
) {
    fun getAll(): Flow<List<FilterPreset>> {
        return filterRepository.getPresets()
    }
    
    suspend fun save(preset: FilterPreset) {
        filterRepository.savePreset(preset)
    }
    
    suspend fun delete(presetId: String) {
        filterRepository.deletePreset(presetId)
    }
}
