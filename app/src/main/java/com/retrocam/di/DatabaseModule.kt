package com.retrocam.di

import android.content.Context
import androidx.room.Room
import com.retrocam.data.local.PresetDao
import com.retrocam.data.local.RetroCamDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideRetroCamDatabase(
        @ApplicationContext context: Context
    ): RetroCamDatabase {
        return Room.databaseBuilder(
            context,
            RetroCamDatabase::class.java,
            "retrocam_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun providePresetDao(database: RetroCamDatabase): PresetDao {
        return database.presetDao()
    }
}
