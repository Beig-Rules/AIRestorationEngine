package com.restoration.engine.android

import com.restoration.engine.RestorationEngine
import com.restoration.engine.backend.BackendSelector
import com.restoration.engine.backend.CpuBackend
import com.restoration.engine.backend.GpuBackend
import com.restoration.engine.backend.NpuBackend
import com.restoration.engine.pipeline.RestorationPipeline
import com.restoration.engine.planner.RuleBasedPipelinePlanner

/**
 * Simple composition root / manual DI for the restoration engine.
 * Replace with Hilt/Koin when the project grows.
 */
object EngineModule {

    private val cpuBackend by lazy { CpuBackend() }
    private val gpuBackend by lazy { GpuBackend(isAvailable = false) }
    private val npuBackend by lazy { NpuBackend(isAvailable = false) }

    val backendSelector by lazy {
        BackendSelector(listOf(cpuBackend, gpuBackend, npuBackend))
    }

    val planner by lazy { RuleBasedPipelinePlanner() }

    val pipeline by lazy { RestorationPipeline(planner) }

    val engine: RestorationEngine by lazy { AndroidRestorationEngine() }

    fun probeHardware() {
        // TODO: Set gpuBackend / npuBackend availability via native or Android APIs
    }
}
