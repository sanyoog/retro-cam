package com.retrocam.di

import com.retrocam.data.repository.PresetRepositoryImpl
import com.retrocam.domain.repository.PresetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindPresetRepository(
        presetRepositoryImpl: PresetRepositoryImpl
    ): PresetRepository
}
