package com.restoration.engine.model

import java.io.File
import java.nio.FloatBuffer

data class TensorShape(val dims: LongArray) {
    override fun equals(other: Any?) = other is TensorShape && dims.contentEquals(other.dims)
    override fun hashCode() = dims.contentHashCode()
}

data class InferenceRequest(
    val modelId: String,
    val modelFile: File,
    val inputNchw: FloatBuffer,
    val shape: TensorShape,
    val useGpu: Boolean = false
)

data class InferenceResult(
    val outputNchw: FloatArray,
    val outputShape: LongArray,
    val latencyMs: Long
)

interface OnnxRunner {
    fun isAvailable(): Boolean
    fun run(request: InferenceRequest): InferenceResult
    fun close()
}

class StubOnnxRunner : OnnxRunner {
    override fun isAvailable() = false
    override fun run(request: InferenceRequest): InferenceResult {
        error("ONNX Runtime not available. Add onnxruntime-android dependency.")
    }
    override fun close() {}
}
