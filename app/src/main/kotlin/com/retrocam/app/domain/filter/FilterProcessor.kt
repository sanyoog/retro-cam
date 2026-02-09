package com.retrocam.app.domain.filter

import android.graphics.Bitmap
import com.retrocam.app.domain.model.FilterConfig
import com.retrocam.app.domain.model.FilterType

/**
 * GPU-accelerated filter processor interface
 */
interface FilterProcessor {
    /**
     * Apply filter to bitmap
     * @param source Input bitmap
     * @param config Filter configuration
     * @return Filtered bitmap
     */
    fun applyFilter(source: Bitmap, config: FilterConfig): Bitmap
    
    /**
     * Release GPU resources
     */
    fun release()
}

/**
 * Default CPU-based filter processor implementation
 */
class DefaultFilterProcessor : FilterProcessor {
    
    override fun applyFilter(source: Bitmap, config: FilterConfig): Bitmap {
        if (config.type == FilterType.NONE || config.intensity == 0f) {
            return source.copy(source.config, true)
        }
        
        return when (config.type) {
            FilterType.NONE -> source.copy(source.config, true)
            FilterType.VINTAGE -> applyVintage(source, config.intensity)
            FilterType.BLACK_AND_WHITE -> applyBlackAndWhite(source, config.intensity)
            FilterType.SEPIA -> applySepia(source, config.intensity)
            FilterType.FILM_GRAIN -> applyFilmGrain(source, config.intensity)
            FilterType.WARM_VINTAGE -> applyWarmVintage(source, config.intensity)
            FilterType.COOL_VINTAGE -> applyCoolVintage(source, config.intensity)
            FilterType.FADED -> applyFaded(source, config.intensity)
            FilterType.DRAMATIC -> applyDramatic(source, config.intensity)
            FilterType.SOFT_GLOW -> applySoftGlow(source, config.intensity)
            FilterType.VIGNETTE -> applyVignette(source, config.intensity)
            FilterType.CROSS_PROCESS -> applyCrossProcess(source, config.intensity)
            FilterType.POLAROID -> applyPolaroid(source, config.intensity)
            FilterType.RETRO_70S -> apply70sRetro(source, config.intensity)
            FilterType.RETRO_80S -> apply80sRetro(source, config.intensity)
        }
    }
    
    private fun applyVintage(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Vintage color shift
                val newR = (r * 0.9 + g * 0.1 + 30).toInt().coerceIn(0, 255)
                val newG = (g * 0.9 + 10).toInt().coerceIn(0, 255)
                val newB = (b * 0.7 - 10).toInt().coerceIn(0, 255)
                
                // Apply intensity
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun applyBlackAndWhite(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                val r = android.graphics.Color.red(pixel)
                val g = android.graphics.Color.green(pixel)
                val b = android.graphics.Color.blue(pixel)
                
                // Luminosity method for better B&W conversion
                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                
                // Apply intensity
                val finalR = (r + (gray - r) * intensity).toInt()
                val finalG = (g + (gray - g) * intensity).toInt()
                val finalB = (b + (gray - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(finalR, finalG, finalB))
            }
        }
        
        return output
    }
    
    private fun applySepia(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                val r = android.graphics.Color.red(pixel)
                val g = android.graphics.Color.green(pixel)
                val b = android.graphics.Color.blue(pixel)
                
                // Sepia transformation
                val tr = (0.393 * r + 0.769 * g + 0.189 * b).toInt().coerceIn(0, 255)
                val tg = (0.349 * r + 0.686 * g + 0.168 * b).toInt().coerceIn(0, 255)
                val tb = (0.272 * r + 0.534 * g + 0.131 * b).toInt().coerceIn(0, 255)
                
                // Apply intensity
                val finalR = (r + (tr - r) * intensity).toInt()
                val finalG = (g + (tg - g) * intensity).toInt()
                val finalB = (b + (tb - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(finalR, finalG, finalB))
            }
        }
        
        return output
    }
    
    private fun applyFilmGrain(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Add random noise
                val noise = ((Math.random() - 0.5) * 50 * intensity).toInt()
                
                r = (r + noise).coerceIn(0, 255)
                g = (g + noise).coerceIn(0, 255)
                b = (b + noise).coerceIn(0, 255)
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun applyWarmVintage(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Warm color shift
                val newR = (r * 1.1 + 20).toInt().coerceIn(0, 255)
                val newG = (g * 1.0 + 10).toInt().coerceIn(0, 255)
                val newB = (b * 0.8).toInt().coerceIn(0, 255)
                
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun applyCoolVintage(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Cool color shift
                val newR = (r * 0.8).toInt().coerceIn(0, 255)
                val newG = (g * 1.0 + 10).toInt().coerceIn(0, 255)
                val newB = (b * 1.1 + 20).toInt().coerceIn(0, 255)
                
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun applyFaded(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Faded look - reduce contrast, add brightness
                val newR = (r * 0.8 + 50).toInt().coerceIn(0, 255)
                val newG = (g * 0.8 + 50).toInt().coerceIn(0, 255)
                val newB = (b * 0.8 + 50).toInt().coerceIn(0, 255)
                
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun applyDramatic(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Increase contrast
                val factor = 1.5 * intensity + (1 - intensity)
                val newR = ((r - 128) * factor + 128).toInt().coerceIn(0, 255)
                val newG = ((g - 128) * factor + 128).toInt().coerceIn(0, 255)
                val newB = ((b - 128) * factor + 128).toInt().coerceIn(0, 255)
                
                output.setPixel(x, y, android.graphics.Color.rgb(newR, newG, newB))
            }
        }
        
        return output
    }
    
    private fun applySoftGlow(source: Bitmap, intensity: Float): Bitmap {
        // Simplified soft glow - slight brightness increase
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                val newR = (r + 30 * intensity).toInt().coerceIn(0, 255)
                val newG = (g + 30 * intensity).toInt().coerceIn(0, 255)
                val newB = (b + 30 * intensity).toInt().coerceIn(0, 255)
                
                output.setPixel(x, y, android.graphics.Color.rgb(newR, newG, newB))
            }
        }
        
        return output
    }
    
    private fun applyVignette(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        val centerX = width / 2f
        val centerY = height / 2f
        val maxDist = Math.sqrt((centerX * centerX + centerY * centerY).toDouble()).toFloat()
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Calculate distance from center
                val dx = x - centerX
                val dy = y - centerY
                val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                val vignette = 1 - ((dist / maxDist) * intensity * 0.7f).coerceIn(0f, 0.7f)
                
                r = (r * vignette).toInt()
                g = (g * vignette).toInt()
                b = (b * vignette).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun applyCrossProcess(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Cross-process color shift (cyan/magenta)
                val newR = (r * 1.2).toInt().coerceIn(0, 255)
                val newG = (g * 0.9).toInt().coerceIn(0, 255)
                val newB = (b * 1.1).toInt().coerceIn(0, 255)
                
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun applyPolaroid(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // Polaroid look - slightly faded with slight color cast
                val newR = (r * 0.95 + 15).toInt().coerceIn(0, 255)
                val newG = (g * 0.95 + 12).toInt().coerceIn(0, 255)
                val newB = (b * 0.85 + 5).toInt().coerceIn(0, 255)
                
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun apply70sRetro(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // 70s warm, saturated look
                val newR = (r * 1.15 + 20).toInt().coerceIn(0, 255)
                val newG = (g * 1.05 + 15).toInt().coerceIn(0, 255)
                val newB = (b * 0.85).toInt().coerceIn(0, 255)
                
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    private fun apply80sRetro(source: Bitmap, intensity: Float): Bitmap {
        val output = source.copy(source.config, true)
        val width = output.width
        val height = output.height
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = output.getPixel(x, y)
                
                var r = android.graphics.Color.red(pixel)
                var g = android.graphics.Color.green(pixel)
                var b = android.graphics.Color.blue(pixel)
                
                // 80s cool, vibrant look with magenta/cyan tint
                val newR = (r * 1.1 + 10).toInt().coerceIn(0, 255)
                val newG = (g * 0.95).toInt().coerceIn(0, 255)
                val newB = (b * 1.15 + 15).toInt().coerceIn(0, 255)
                
                r = (r + (newR - r) * intensity).toInt()
                g = (g + (newG - g) * intensity).toInt()
                b = (b + (newB - b) * intensity).toInt()
                
                output.setPixel(x, y, android.graphics.Color.rgb(r, g, b))
            }
        }
        
        return output
    }
    
    override fun release() {
        // Nothing to release for CPU-based processor
    }
}
