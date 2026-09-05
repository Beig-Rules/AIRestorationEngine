package com.restoration.engine.android

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.restoration.engine.RestorationEngine
import com.restoration.engine.android.onnx.OrtOnnxRunner
import com.restoration.engine.backend.BackendSelector
import com.restoration.engine.backend.CpuBackend
import com.restoration.engine.backend.GpuBackend
import com.restoration.engine.backend.NpuBackend
import com.restoration.engine.model.OnnxRunner
import com.restoration.engine.pipeline.RestorationPipeline
import com.restoration.engine.planner.RuleBasedPipelinePlanner
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

object EngineModule {

    private val probed = AtomicBoolean(false)
    private var appContext: Context? = null

    private val onnx: OnnxRunner by lazy { OrtOnnxRunner.create() }

    private val cpuBackend by lazy {
        CpuBackend(weightsDir = appContext?.let { weightsDir(it) }, onnx = onnx)
    }
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
        if (context != null) appContext = context.applicationContext
        if (!probed.compareAndSet(false, true)) return

        var gpu = false
        var npu = false
        val ctx = appContext
        if (ctx != null) {
            val pm = ctx.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) npu = true
            gpu = pm.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
                || pm.hasSystemFeature("android.hardware.vulkan.level")
                || pm.hasSystemFeature("android.hardware.vulkan.version")
        }
        gpuBackend = GpuBackend(isAvailable = gpu, onnx = onnx)
        npuBackend = NpuBackend(isAvailable = npu, onnx = onnx)
    }

    fun weightsDir(context: Context): File {
        val dir = File(context.filesDir, "models/weights")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}
