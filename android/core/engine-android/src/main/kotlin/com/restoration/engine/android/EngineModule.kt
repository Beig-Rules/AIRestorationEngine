package com.restoration.engine.android

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.restoration.engine.RestorationEngine
import com.restoration.engine.backend.BackendSelector
import com.restoration.engine.backend.CpuBackend
import com.restoration.engine.backend.GpuBackend
import com.restoration.engine.backend.NpuBackend
import com.restoration.engine.pipeline.RestorationPipeline
import com.restoration.engine.planner.RuleBasedPipelinePlanner
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

object EngineModule {

    private val probed = AtomicBoolean(false)

    private val cpuBackend = CpuBackend()
    @Volatile private var gpuBackend: GpuBackend = GpuBackend(isAvailable = false)
    @Volatile private var npuBackend: NpuBackend = NpuBackend(isAvailable = false)

    val backendSelector: BackendSelector
        get() = BackendSelector(listOf(cpuBackend, gpuBackend, npuBackend))

    val planner by lazy { RuleBasedPipelinePlanner() }

    val pipeline by lazy {
        RestorationPipeline(planner, BackendSelector(listOf(cpuBackend, gpuBackend, npuBackend)))
    }

    val engine: RestorationEngine by lazy { AndroidRestorationEngine(pipeline) }

    fun probeHardware(context: Context? = null) {
        if (!probed.compareAndSet(false, true)) return

        var gpu = false
        var npu = false

        if (context != null) {
            val pm = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                npu = true
            }
            gpu = pm.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
                || pm.hasSystemFeature("android.hardware.vulkan.level")
                || pm.hasSystemFeature("android.hardware.vulkan.version")
        }

        gpuBackend = GpuBackend(isAvailable = gpu)
        npuBackend = NpuBackend(isAvailable = npu)
    }

    fun weightsDir(context: Context): File {
        val dir = File(context.filesDir, "models/weights")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}
