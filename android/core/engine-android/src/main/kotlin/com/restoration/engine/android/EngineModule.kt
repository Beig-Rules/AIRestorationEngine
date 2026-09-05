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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

object EngineModule {

    private val probed = AtomicBoolean(false)
    private val appContext = AtomicReference<Context?>(null)

    private val onnx: OnnxRunner by lazy { OrtOnnxRunner.create() }

    private val cpuBackend by lazy {
        CpuBackend(weightsDir = appContext.get()?.let { weightsDir(it) }, onnx = onnx)
    }

    private val gpuRef = AtomicReference(GpuBackend(isAvailable = false))
    private val npuRef = AtomicReference(NpuBackend(isAvailable = false))

    val activeJobs = ConcurrentHashMap<String, AtomicBoolean>()

    fun backendSelector(): BackendSelector =
        BackendSelector(listOf(cpuBackend, gpuRef.get(), npuRef.get()))

    val planner by lazy { RuleBasedPipelinePlanner() }

    val pipeline by lazy {
        RestorationPipeline(planner) { backendSelector() }
    }

    val engine: RestorationEngine by lazy { AndroidRestorationEngine(pipeline) }

    fun probeHardware(context: Context? = null) {
        if (context != null) appContext.set(context.applicationContext)
        if (!probed.compareAndSet(false, true)) return

        var gpu = false
        var npu = false
        val ctx = appContext.get()
        if (ctx != null) {
            val pm = ctx.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) npu = true
            gpu = pm.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
                || pm.hasSystemFeature("android.hardware.vulkan.level")
                || pm.hasSystemFeature("android.hardware.vulkan.version")
        }
        gpuRef.set(GpuBackend(isAvailable = gpu, onnx = onnx))
        npuRef.set(NpuBackend(isAvailable = npu, onnx = onnx))
    }

    fun weightsDir(context: Context): File {
        val dir = File(context.filesDir, "models/weights")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun markJob(jobId: String) { activeJobs[jobId] = AtomicBoolean(false) }
    fun requestCancel(jobId: String) { activeJobs[jobId]?.set(true) }
    fun isCancelled(jobId: String): Boolean = activeJobs[jobId]?.get() == true
    fun clearJob(jobId: String) { activeJobs.remove(jobId) }
}
