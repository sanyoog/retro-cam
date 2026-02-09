package com.retrocam.app.di

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.guava.await
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraModule {

    @Provides
    @Singleton
    fun provideCameraSelector(): CameraSelector {
        return CameraSelector.DEFAULT_BACK_CAMERA
    }

    @Provides
    @Singleton
    suspend fun provideCameraProvider(
        @ApplicationContext context: Context
    ): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(context).await()
    }
}
