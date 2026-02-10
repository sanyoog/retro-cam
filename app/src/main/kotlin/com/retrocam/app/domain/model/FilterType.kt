package com.retrocam.app.domain.model

/**
 * Available retro filter types
 */
enum class FilterType(val displayName: String) {
    NONE("None"),
    VINTAGE("Vintage"),
    BLACK_AND_WHITE("B&W"),
    SEPIA("Sepia"),
    FILM_GRAIN("Film Grain"),
    WARM_VINTAGE("Warm Vintage"),
    COOL_VINTAGE("Cool Vintage"),
    FADED("Faded"),
    DRAMATIC("Dramatic"),
    SOFT_GLOW("Soft Glow"),
    VIGNETTE("Vignette"),
    CROSS_PROCESS("Cross Process"),
    POLAROID("Polaroid"),
    RETRO_70S("70s Retro"),
    RETRO_80S("80s Retro")
}

/**
 * Filter configuration with intensity
 */
data class FilterConfig(
    val type: FilterType = FilterType.NONE,
    val intensity: Float = 1.0f // 0.0 to 1.0
) {
    init {
        require(intensity in 0f..1f) { "Intensity must be between 0 and 1" }
    }
}

/**
 * Custom filter preset that can be saved
 */
data class FilterPreset(
    val id: String,
    val name: String,
    val filterType: FilterType,
    val intensity: Float,
    val isBuiltIn: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
