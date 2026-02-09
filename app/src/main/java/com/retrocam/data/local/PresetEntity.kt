package com.retrocam.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.retrocam.domain.model.CameraSettings
import com.retrocam.domain.model.Filter
import com.retrocam.domain.model.Preset

/**
 * Room entity for storing presets.
 */
@Entity(tableName = "presets")
data class PresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    
    // Filter settings
    val filterId: String,
    val filterName: String,
    val temperature: Float,
    val tint: Float,
    val contrast: Float,
    val highlights: Float,
    val shadows: Float,
    val grain: Float,
    val vignette: Float,
    val distortion: Float,
    val chromaticAberration: Float,
    val intensity: Float,
    
    // Camera settings (nullable for Normal Mode presets)
    val iso: Int? = null,
    val shutterSpeed: Long? = null,
    val whiteBalance: Int? = null,
    val focusDistance: Float? = null,
    val exposureCompensation: Int? = null,
    
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Convert Room entity to domain model.
 */
fun PresetEntity.toDomain(): Preset {
    return Preset(
        id = id,
        name = name,
        filter = Filter(
            id = filterId,
            name = filterName,
            temperature = temperature,
            tint = tint,
            contrast = contrast,
            highlights = highlights,
            shadows = shadows,
            grain = grain,
            vignette = vignette,
            distortion = distortion,
            chromaticAberration = chromaticAberration,
            intensity = intensity
        ),
        cameraSettings = if (iso != null || shutterSpeed != null || whiteBalance != null || 
                            focusDistance != null || exposureCompensation != null) {
            CameraSettings(
                iso = iso,
                shutterSpeed = shutterSpeed,
                whiteBalance = whiteBalance,
                focusDistance = focusDistance,
                exposureCompensation = exposureCompensation
            )
        } else null,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convert domain model to Room entity.
 */
fun Preset.toEntity(): PresetEntity {
    return PresetEntity(
        id = id,
        name = name,
        filterId = filter.id,
        filterName = filter.name,
        temperature = filter.temperature,
        tint = filter.tint,
        contrast = filter.contrast,
        highlights = filter.highlights,
        shadows = filter.shadows,
        grain = filter.grain,
        vignette = filter.vignette,
        distortion = filter.distortion,
        chromaticAberration = filter.chromaticAberration,
        intensity = filter.intensity,
        iso = cameraSettings?.iso,
        shutterSpeed = cameraSettings?.shutterSpeed,
        whiteBalance = cameraSettings?.whiteBalance,
        focusDistance = cameraSettings?.focusDistance,
        exposureCompensation = cameraSettings?.exposureCompensation,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
