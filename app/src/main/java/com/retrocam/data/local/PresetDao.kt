package com.retrocam.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for preset database operations.
 */
@Dao
interface PresetDao {
    
    @Query("SELECT * FROM presets ORDER BY updatedAt DESC")
    fun getAllPresets(): Flow<List<PresetEntity>>
    
    @Query("SELECT * FROM presets WHERE id = :id")
    suspend fun getPresetById(id: Long): PresetEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: PresetEntity): Long
    
    @Update
    suspend fun updatePreset(preset: PresetEntity)
    
    @Delete
    suspend fun deletePreset(preset: PresetEntity)
    
    @Query("DELETE FROM presets WHERE id = :id")
    suspend fun deletePresetById(id: Long)
    
    @Query("SELECT COUNT(*) FROM presets")
    suspend fun getPresetCount(): Int
}
