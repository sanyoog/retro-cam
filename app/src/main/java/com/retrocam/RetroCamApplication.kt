package com.retrocam

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for RetroCam.
 * Initializes Hilt dependency injection.
 */
@HiltAndroidApp
class RetroCamApplication : Application()
