package com.retrocam.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Optimized bitmap loading with downsampling
 */
@Singleton
class BitmapLoader @Inject constructor(
    private val imageCache: ImageCache
) {
    
    /**
     * Load bitmap with efficient memory usage
     * Uses inSampleSize to reduce memory footprint for large images
     */
    suspend fun loadBitmap(
        file: File,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? = withContext(Dispatchers.IO) {
        val cacheKey = "${file.absolutePath}_${reqWidth}x${reqHeight}"
        
        // Check cache first
        imageCache.get(cacheKey)?.let { return@withContext it }
        
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
            
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565 // Use less memory
            
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
            
            // Cache the loaded bitmap
            bitmap?.let { imageCache.put(cacheKey, it) }
            
            bitmap
        } catch (e: OutOfMemoryError) {
            // If OOM, try with even more aggressive downsampling
            try {
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 4 // Aggressive downsampling
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                BitmapFactory.decodeFile(file.absolutePath, options)
            } catch (e: Exception) {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Calculate optimal sample size for bitmap loading
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * Create thumbnail from bitmap with efficient scaling
     */
    fun createThumbnail(source: Bitmap, maxSize: Int): Bitmap {
        val width = source.width
        val height = source.height
        
        if (width <= maxSize && height <= maxSize) {
            return source
        }
        
        val scale = min(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }
}
