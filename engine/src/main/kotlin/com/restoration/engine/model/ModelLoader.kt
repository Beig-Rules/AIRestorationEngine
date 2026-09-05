package com.restoration.engine.model

import java.io.File

data class ModelSpec(
    val id: String,
    val fileName: String,
    val stage: String,
    val inputSize: Int = 256,
    val scale: Float = 1f
)

object ModelCatalog {
    val defaults = listOf(
        ModelSpec("realesrgan-x4", "realesrgan_x4.onnx", "SUPER_RESOLUTION", 64, 4f),
        ModelSpec("gfpgan-v1.4", "gfpgan_v1.4.onnx", "FACE_RESTORATION", 512, 1f),
        ModelSpec("scunet-denoise", "scunet.onnx", "DENOISE", 256, 1f),
        ModelSpec("deblock-light", "deblock.onnx", "DEBLOCK", 256, 1f)
    )

    fun forStage(stage: String): ModelSpec? =
        defaults.firstOrNull { it.stage == stage }
}

class ModelLoader(private val weightsDir: File) {
    fun resolve(spec: ModelSpec): File? {
        val f = File(weightsDir, spec.fileName)
        return if (f.exists() && f.canRead() && f.length() > 0) f else null
    }

    fun listAvailable(): List<Pair<ModelSpec, File>> =
        ModelCatalog.defaults.mapNotNull { spec ->
            resolve(spec)?.let { spec to it }
        }

    fun isReady(stage: String): Boolean =
        ModelCatalog.forStage(stage)?.let { resolve(it) != null } == true
}

interface OnnxSessionFactory {
    fun createSession(modelFile: File): Any
    fun releaseAll()
}

class NoOpOnnxSessionFactory : OnnxSessionFactory {
    override fun createSession(modelFile: File): Any =
        error("ONNX Runtime not linked. Add onnxruntime-android.")

    override fun releaseAll() {}
}
