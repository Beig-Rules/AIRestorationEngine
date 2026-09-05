package com.restoration.app

import android.app.Application
import com.restoration.engine.android.EngineModule

/**
 * Application entry – probes hardware backends once at process start.
 */
class RestorationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        EngineModule.probeHardware(applicationContext)
    }
}
