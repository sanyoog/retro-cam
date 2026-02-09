package com.retrocam.app.di

import com.retrocam.app.data.repository.FilterRepositoryImpl
import com.retrocam.app.domain.filter.DefaultFilterProcessor
import com.retrocam.app.domain.filter.FilterProcessor
import com.retrocam.app.domain.repository.FilterRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for filters
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FilterModule {
    
    @Binds
    @Singleton
    abstract fun bindFilterRepository(
        filterRepositoryImpl: FilterRepositoryImpl
    ): FilterRepository
}

@Module
@InstallIn(SingletonComponent::class)
object FilterProcessorModule {
    
    @Provides
    @Singleton
    fun provideFilterProcessor(): FilterProcessor {
        return DefaultFilterProcessor()
    }
}
