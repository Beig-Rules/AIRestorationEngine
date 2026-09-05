package com.restoration.engine.backend

import com.restoration.engine.domain.HardwarePreference
import com.restoration.engine.domain.ImageQualityProfile

/**
 * Abstraction over CPU / GPU / NPU execution backends.
 * Real ONNX Runtime / TFLite / NNAPI implementations plug in here.
 */
interface HardwareBackend {
    val id: String
    val isAvailable: Boolean
    val prefersSpeed: Boolean

    suspend fun initialize()
    suspend fun release()

    /**
     * Run a single pipeline stage on the given buffer descriptor.
     * Returns updated quality metrics after the stage.
     */
    suspend fun runStage(
        stage: String,
        inputPath: String,
        outputPath: String,
        scale: Float,
        quality: ImageQualityProfile
    ): ImageQualityProfile
}

/**
 * Selects the best available backend based on preference and device capability.
 */
class BackendSelector(
    private val backends: List<HardwareBackend>
) {
    fun select(preference: HardwarePreference): HardwareBackend {
        val available = backends.filter { it.isAvailable }
        require(available.isNotEmpty()) { "No hardware backend available" }

        return when (preference) {
            HardwarePreference.CPU -> available.firstOrNull { it.id == "CPU" } ?: available.first()
            HardwarePreference.GPU -> available.firstOrNull { it.id == "GPU" }
                ?: available.firstOrNull { it.id == "CPU" }
                ?: available.first()
            HardwarePreference.NPU -> available.firstOrNull { it.id == "NPU" }
                ?: available.firstOrNull { it.id == "GPU" }
                ?: available.first()
            HardwarePreference.AUTO -> {
                available.firstOrNull { it.id == "NPU" }
                    ?: available.firstOrNull { it.id == "GPU" }
                    ?: available.first()
            }
        }
    }
}

/** CPU fallback – always available, pure software path. */
class CpuBackend : HardwareBackend {
    override val id = "CPU"
    override val isAvailable = true
    override val prefersSpeed = false

    override suspend fun initialize() { /* no-op */ }
    override suspend fun release() { /* no-op */ }

    override suspend fun runStage(
        stage: String,
        inputPath: String,
        outputPath: String,
        scale: Float,
        quality: ImageQualityProfile
    ): ImageQualityProfile {
        // TODO: Call native CPU tile engine / ONNX CPU EP
        return when (stage) {
            "SUPER_RESOLUTION" -> quality.copy(
                width = (quality.width * scale).toInt(),
                height = (quality.height * scale).toInt(),
                sharpness = (quality.sharpness + 0.15f).coerceAtMost(1f)
            )
            "DENOISE" -> quality.copy(noise = (quality.noise * 0.4f).coerceAtLeast(0f))
            "DEBLOCK" -> quality.copy(compressionArtifacts = (quality.compressionArtifacts * 0.3f).coerceAtLeast(0f))
            "FACE_RESTORATION" -> quality.copy(sharpness = (quality.sharpness + 0.1f).coerceAtMost(1f))
            else -> quality
        }
    }
}

/**
 * GPU backend scaffold (OpenCL / Vulkan / CUDA / ONNX CUDA EP / TFLite GPU delegate).
 */
class GpuBackend(
    override val isAvailable: Boolean = false
) : HardwareBackend {
    override val id = "GPU"
    override val prefersSpeed = true

    override suspend fun initialize() {
        // TODO: Load ONNX Runtime CUDA / DirectML / TFLite GPU delegate
    }

    override suspend fun release() {
        // TODO: Release GPU resources
    }

    override suspend fun runStage(
        stage: String,
        inputPath: String,
        outputPath: String,
        scale: Float,
        quality: ImageQualityProfile
    ): ImageQualityProfile {
        return CpuBackend().runStage(stage, inputPath, outputPath, scale, quality)
    }
}

/**
 * NPU / NNAPI backend scaffold
 */
class NpuBackend(
    override val isAvailable: Boolean = false
) : HardwareBackend {
    override val id = "NPU"
    override val prefersSpeed = true

    override suspend fun initialize() { /* TODO: NNAPI / QNN init */ }
    override suspend fun release() { /* TODO */ }

    override suspend fun runStage(
        stage: String,
        inputPath: String,
        outputPath: String,
        scale: Float,
        quality: ImageQualityProfile
    ): ImageQualityProfile {
        return CpuBackend().runStage(stage, inputPath, outputPath, scale, quality)
    }
}
