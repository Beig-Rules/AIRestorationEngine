package com.restoration.engine.android

import com.restoration.engine.*
import com.restoration.engine.domain.*
import com.restoration.engine.pipeline.RestorationPipeline
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion

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

    override suspend fun restore(request: RestoreRequest): Flow<RestoreProgress> {
        EngineModule.markJob(request.jobId)
        return pipeline.execute(request).onCompletion { EngineModule.clearJob(request.jobId) }
    }

    override fun cancel(jobId: String) {
        EngineModule.requestCancel(jobId)
    }

    override fun capabilities(): EngineCapabilities {
        val sel = EngineModule.backendSelector()
        val backends = buildList {
            add("CPU")
            try { if (sel.select(HardwarePreference.GPU).id == "GPU") add("GPU") } catch (_: Exception) {}
            try { if (sel.select(HardwarePreference.NPU).id == "NPU") add("NPU") } catch (_: Exception) {}
        }.distinct()
        return EngineCapabilities(
            supportedScales = listOf(1f, 2f, 4f, 8f),
            maxInputPixels = 16_000_000L,
            availableBackends = backends.ifEmpty { listOf("CPU") },
            faceRestorationSupported = true,
            tilingSupported = true,
            offlineOnly = true
        )
    }
}
