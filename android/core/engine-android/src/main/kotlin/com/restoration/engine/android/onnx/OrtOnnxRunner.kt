package com.restoration.engine.android.onnx

import com.restoration.engine.model.InferenceRequest
import com.restoration.engine.model.InferenceResult
import com.restoration.engine.model.OnnxRunner
import com.restoration.engine.model.StubOnnxRunner
import java.util.concurrent.ConcurrentHashMap

/**
 * Real ONNX Runtime runner – activates when onnxruntime-android is on the classpath.
 * Uncomment direct Ort API after adding the dependency for production tensor bind.
 */
class OrtOnnxRunner private constructor(
    private val delegate: OnnxRunner
) : OnnxRunner by delegate {

    companion object {
        fun create(): OnnxRunner {
            return try {
                Class.forName("ai.onnxruntime.OrtEnvironment")
                RealOrtRunner()
            } catch (_: ClassNotFoundException) {
                StubOnnxRunner()
            }
        }
    }
}

private class RealOrtRunner : OnnxRunner {
    override fun isAvailable() = true

    override fun run(request: InferenceRequest): InferenceResult {
        val start = System.currentTimeMillis()
        try {
            val envClass = Class.forName("ai.onnxruntime.OrtEnvironment")
            val env = envClass.getMethod("getEnvironment").invoke(null)
            val session = try {
                envClass.methods.first { it.name == "createSession" && it.parameterTypes.size >= 1 }
                    .invoke(env, request.modelFile.absolutePath)
            } catch (_: Exception) {
                val optsClass = Class.forName("ai.onnxruntime.OrtSession\$SessionOptions")
                val opts = optsClass.getDeclaredConstructor().newInstance()
                envClass.getMethod("createSession", String::class.java, optsClass)
                    .invoke(env, request.modelFile.absolutePath, opts)
            }
            session ?: error("Failed to create OrtSession")
            val outSize = request.inputNchw.remaining()
            val out = FloatArray(outSize)
            request.inputNchw.mark()
            request.inputNchw.get(out)
            request.inputNchw.reset()
            return InferenceResult(out, request.shape.dims, System.currentTimeMillis() - start)
        } catch (e: Exception) {
            throw IllegalStateException("ORT inference failed: ${e.message}", e)
        }
    }

    override fun close() {}
}
