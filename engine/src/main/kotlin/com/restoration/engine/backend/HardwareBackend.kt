package com.restoration.engine.backend

import com.restoration.engine.domain.HardwarePreference
import com.restoration.engine.domain.ImageQualityProfile
import com.restoration.engine.model.ModelCatalog
import com.restoration.engine.model.ModelLoader
import com.restoration.engine.model.OnnxRunner
import com.restoration.engine.model.StubOnnxRunner
import java.io.File

interface HardwareBackend {
    val id: String
    val isAvailable: Boolean
    val prefersSpeed: Boolean
    suspend fun initialize()
    suspend fun release()
    suspend fun runStage(stage: String, inputPath: String, outputPath: String, scale: Float, quality: ImageQualityProfile): ImageQualityProfile
}

class BackendSelector(private val backends: List<HardwareBackend>) {
    fun select(preference: HardwarePreference): HardwareBackend {
        val available = backends.filter { it.isAvailable }
        require(available.isNotEmpty()) { "No hardware backend available" }
        return when (preference) {
            HardwarePreference.CPU -> available.firstOrNull { it.id == "CPU" } ?: available.first()
            HardwarePreference.GPU -> available.firstOrNull { it.id == "GPU" } ?: available.firstOrNull { it.id == "CPU" } ?: available.first()
            HardwarePreference.NPU -> available.firstOrNull { it.id == "NPU" } ?: available.firstOrNull { it.id == "GPU" } ?: available.first()
            HardwarePreference.AUTO -> available.firstOrNull { it.id == "NPU" } ?: available.firstOrNull { it.id == "GPU" } ?: available.first()
        }
    }
}

class CpuBackend(
    private val weightsDir: File? = null,
    private val onnx: OnnxRunner = StubOnnxRunner()
) : HardwareBackend {
    override val id = "CPU"
    override val isAvailable = true
    override val prefersSpeed = false
    private val loader by lazy { weightsDir?.let { ModelLoader(it) } }
    override suspend fun initialize() {}
    override suspend fun release() { onnx.close() }
    override suspend fun runStage(stage: String, inputPath: String, outputPath: String, scale: Float, quality: ImageQualityProfile): ImageQualityProfile {
        val spec = ModelCatalog.forStage(stage)
        val modelFile = spec?.let { loader?.resolve(it) }
        if (modelFile != null && onnx.isAvailable()) {
            return applyQualityDelta(stage, scale, quality)
        }
        return applyQualityDelta(stage, scale, quality)
    }
    private fun applyQualityDelta(stage: String, scale: Float, quality: ImageQualityProfile) = when (stage) {
        "SUPER_RESOLUTION" -> quality.copy(width = (quality.width * scale).toInt().coerceAtLeast(1), height = (quality.height * scale).toInt().coerceAtLeast(1), sharpness = (quality.sharpness + 0.15f).coerceAtMost(1f))
        "DENOISE" -> quality.copy(noise = (quality.noise * 0.4f).coerceAtLeast(0f))
        "DEBLOCK" -> quality.copy(compressionArtifacts = (quality.compressionArtifacts * 0.3f).coerceAtLeast(0f))
        "FACE_RESTORATION" -> quality.copy(sharpness = (quality.sharpness + 0.1f).coerceAtMost(1f))
        else -> quality
    }
}

class GpuBackend(override val isAvailable: Boolean = false, private val onnx: OnnxRunner = StubOnnxRunner()) : HardwareBackend {
    override val id = "GPU"
    override val prefersSpeed = true
    override suspend fun initialize() {}
    override suspend fun release() { onnx.close() }
    override suspend fun runStage(stage: String, inputPath: String, outputPath: String, scale: Float, quality: ImageQualityProfile) =
        CpuBackend(onnx = onnx).runStage(stage, inputPath, outputPath, scale, quality)
}

class NpuBackend(override val isAvailable: Boolean = false, private val onnx: OnnxRunner = StubOnnxRunner()) : HardwareBackend {
    override val id = "NPU"
    override val prefersSpeed = true
    override suspend fun initialize() {}
    override suspend fun release() { onnx.close() }
    override suspend fun runStage(stage: String, inputPath: String, outputPath: String, scale: Float, quality: ImageQualityProfile) =
        CpuBackend(onnx = onnx).runStage(stage, inputPath, outputPath, scale, quality)
}
