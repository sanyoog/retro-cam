package com.retrocam.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RetroCamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
