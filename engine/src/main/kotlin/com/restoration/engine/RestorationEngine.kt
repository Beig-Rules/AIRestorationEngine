package com.restoration.engine
import com.restoration.engine.domain.*
import kotlinx.coroutines.flow.Flow

interface RestorationEngine {
    suspend fun analyze(input: ImageInput): Result<AnalysisResult>
    suspend fun restore(request: RestoreRequest): Flow<RestoreProgress>
    fun cancel(jobId: String)
    fun capabilities(): EngineCapabilities
}

sealed class RestoreProgress {
    data class StageStarted(val stage: String, val progress: Float) : RestoreProgress()
    data class StageCompleted(val stage: String) : RestoreProgress()
    data class Completed(val result: Result<RestoreResult>) : RestoreProgress()
}

data class EngineCapabilities(
    val supportedScales: List<Float>, val maxInputPixels: Long,
    val availableBackends: List<String>, val faceRestorationSupported: Boolean,
    val tilingSupported: Boolean, val offlineOnly: Boolean = true
)
