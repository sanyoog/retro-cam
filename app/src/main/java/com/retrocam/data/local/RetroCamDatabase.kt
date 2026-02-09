package com.retrocam.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for RetroCam app.
 */
@Database(
    entities = [PresetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RetroCamDatabase : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}
