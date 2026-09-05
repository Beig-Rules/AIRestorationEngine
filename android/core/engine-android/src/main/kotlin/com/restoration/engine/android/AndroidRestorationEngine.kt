package com.restoration.engine.android
import com.restoration.engine.*
import com.restoration.engine.domain.*
import com.restoration.engine.pipeline.RestorationPipeline
import kotlinx.coroutines.flow.Flow

class AndroidRestorationEngine(
    private val pipeline: RestorationPipeline = EngineModule.pipeline
) : RestorationEngine {
    override suspend fun analyze(input: ImageInput) = Result.success(
        AnalysisResult(
            input.id,
            ImageQualityProfile(1920, 1080, 0.7f, 0.3f, 0.4f, 0.5f, 2, 64),
            emptyList(),
            listOf("DENOISE", "SUPER_RESOLUTION"),
            emptyList()
        )
    )
    override suspend fun restore(request: RestoreRequest): Flow<RestoreProgress> = pipeline.execute(request)
    override fun cancel(jobId: String) { /* Signal native via NativeBridge */ }
    override fun capabilities() = EngineCapabilities(
        supportedScales = listOf(1f, 2f, 4f, 8f),
        maxInputPixels = 16_000_000L,
        availableBackends = listOf("CPU"),
        faceRestorationSupported = true,
        tilingSupported = true,
        offlineOnly = true
    )
}
