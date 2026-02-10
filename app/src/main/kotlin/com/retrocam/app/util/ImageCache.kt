package com.retrocam.app.util

import android.graphics.Bitmap
import android.util.LruCache
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple LRU cache for bitmaps to improve performance
 */
@Singleton
class ImageCache @Inject constructor() {
    
    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an int in its constructor.
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    
    // Use 1/8th of the available memory for this memory cache.
    private val cacheSize = maxMemory / 8
    
    private val cache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // The cache size will be measured in kilobytes rather than number of items.
            return bitmap.byteCount / 1024
        }
        
        override fun entryRemoved(
            evicted: Boolean,
            key: String?,
            oldValue: Bitmap?,
            newValue: Bitmap?
        ) {
            // Bitmap removed from cache
            if (evicted && oldValue != null && !oldValue.isRecycled) {
                oldValue.recycle()
            }
        }
    }
    
    fun put(key: String, bitmap: Bitmap) {
        if (get(key) == null) {
            cache.put(key, bitmap)
        }
    }
    
    fun get(key: String): Bitmap? {
        return cache.get(key)
    }
    
    fun remove(key: String): Bitmap? {
        return cache.remove(key)
    }
    
    fun clear() {
        cache.evictAll()
    }
    
    fun size(): Int {
        return cache.size()
    }
    
    fun maxSize(): Int {
        return cache.maxSize()
    }
}
