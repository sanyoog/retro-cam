package com.retrocam.domain.model

/**
 * Represents a retro-inspired filter with adjustable parameters.
 * All values are normalized to 0.0f - 1.0f range for consistency.
 */
data class Filter(
    val id: String,
    val name: String,
    val description: String = "",
    
    // Core adjustments
    val temperature: Float = 0.5f,      // 0.0 = cool, 0.5 = neutral, 1.0 = warm
    val tint: Float = 0.5f,             // 0.0 = green, 0.5 = neutral, 1.0 = magenta
    val contrast: Float = 0.5f,         // 0.0 = low, 0.5 = normal, 1.0 = high
    val highlights: Float = 0.5f,       // 0.0 = darkened, 0.5 = normal, 1.0 = brightened
    val shadows: Float = 0.5f,          // 0.0 = darkened, 0.5 = normal, 1.0 = brightened
    
    // Retro effects
    val grain: Float = 0.0f,            // 0.0 = none, 1.0 = maximum
    val vignette: Float = 0.0f,         // 0.0 = none, 1.0 = maximum
    val distortion: Float = 0.0f,       // 0.0 = none, 1.0 = maximum
    val chromaticAberration: Float = 0.0f, // 0.0 = none, 1.0 = maximum
    
    // Overall intensity
    val intensity: Float = 1.0f         // 0.0 = none, 1.0 = full effect
) {
    companion object {
        /**
         * No filter applied - neutral settings
         */
        val NONE = Filter(
            id = "none",
            name = "None",
            description = "No filter applied"
        )
        
        /**
         * Vintage film look with warm tones and grain
         */
        val VINTAGE = Filter(
            id = "vintage",
            name = "Vintage",
            description = "Classic film aesthetic",
            temperature = 0.65f,
            tint = 0.45f,
            contrast = 0.55f,
            highlights = 0.45f,
            shadows = 0.55f,
            grain = 0.25f,
            vignette = 0.3f
        )
        
        /**
         * Warm, golden hour tones
         */
        val WARM = Filter(
            id = "warm",
            name = "Warm",
            description = "Golden hour glow",
            temperature = 0.75f,
            tint = 0.55f,
            contrast = 0.5f,
            highlights = 0.55f,
            shadows = 0.5f,
            grain = 0.1f
        )
        
        /**
         * Cool, crisp tones
         */
        val COOL = Filter(
            id = "cool",
            name = "Cool",
            description = "Crisp and fresh",
            temperature = 0.25f,
            tint = 0.45f,
            contrast = 0.6f,
            highlights = 0.55f,
            shadows = 0.45f
        )
        
        /**
         * Black and white with contrast
         */
        val BLACK_WHITE = Filter(
            id = "bw",
            name = "B&W",
            description = "Monochrome classic",
            temperature = 0.5f,
            tint = 0.5f,
            contrast = 0.65f,
            highlights = 0.5f,
            shadows = 0.5f,
            grain = 0.15f
        )
        
        /**
         * Sepia-toned vintage
         */
        val SEPIA = Filter(
            id = "sepia",
            name = "Sepia",
            description = "Nostalgic warmth",
            temperature = 0.7f,
            tint = 0.4f,
            contrast = 0.5f,
            highlights = 0.45f,
            shadows = 0.55f,
            grain = 0.2f,
            vignette = 0.25f
        )
        
        /**
         * Faded, washed out look
         */
        val FADE = Filter(
            id = "fade",
            name = "Fade",
            description = "Soft and dreamy",
            temperature = 0.55f,
            tint = 0.5f,
            contrast = 0.35f,
            highlights = 0.6f,
            shadows = 0.6f,
            grain = 0.15f,
            vignette = 0.15f
        )
        
        /**
         * Get all predefined filters
         */
        fun getAllFilters(): List<Filter> = listOf(
            NONE,
            VINTAGE,
            WARM,
            COOL,
            BLACK_WHITE,
            SEPIA,
            FADE
        )
    }
}
