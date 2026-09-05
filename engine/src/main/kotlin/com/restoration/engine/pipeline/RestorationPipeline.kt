package com.restoration.engine.pipeline
import com.restoration.engine.domain.*
import com.restoration.engine.error.EngineError
import com.restoration.engine.planner.RuleBasedPipelinePlanner
import com.restoration.engine.backend.BackendSelector
import com.restoration.engine.backend.CpuBackend
import com.restoration.engine.backend.HardwareBackend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.isActive

class RestorationPipeline(
    private val planner: RuleBasedPipelinePlanner = RuleBasedPipelinePlanner(),
    private val backendSelector: BackendSelector = BackendSelector(listOf(CpuBackend()))
) {
    
    suspend fun execute(request: RestoreRequest): Flow<com.restoration.engine.RestoreProgress> = flow {
        if (!coroutineContext.isActive) {
            emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.Cancelled)))
            return@flow
        }
        
        val backend: HardwareBackend = try {
            backendSelector.select(request.hardwarePreference)
        } catch (e: Exception) {
            emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.Unsupported("backend"))))
            return@flow
        }

        emit(com.restoration.engine.RestoreProgress.StageStarted("ANALYZE", 0f))
        val quality = analyzeQuality(request.input)
        emit(com.restoration.engine.RestoreProgress.StageCompleted("ANALYZE"))
        
        val planned = planner.plan(request, quality)
        val warnings = mutableListOf<String>()
        warnings.add("Backend: ${backend.id}")
        var currentQuality = quality
        var stagesDone = 0
        val startTime = System.currentTimeMillis()
        
        try {
            backend.initialize()
        } catch (e: Exception) {
            emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.ModelError(backend.id, e.message ?: "init failed"))))
            return@flow
        }

        for ((idx, stage) in planned.stages.withIndex()) {
            if (!coroutineContext.isActive) {
                emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.Cancelled)))
                return@flow
            }
            emit(com.restoration.engine.RestoreProgress.StageStarted(stage, idx.toFloat() / planned.stages.size))
            try {
                val outPath = request.input.path.replace(Regex("\\.[^.]+$"), "_${stage.lowercase()}.tmp")
                currentQuality = backend.runStage(stage, request.input.path, outPath, request.targetScale, currentQuality)
                stagesDone++
                emit(com.restoration.engine.RestoreProgress.StageCompleted(stage))
            } catch (e: OutOfMemoryError) {
                emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.MemoryError(0, 0))))
                return@flow
            } catch (e: Exception) {
                emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.NativeCrash(e.message ?: "stage:$stage"))))
                return@flow
            }
        }

        try { backend.release() } catch (_: Exception) {}
        
        val outputPath = request.input.path.replace(Regex("\\.[^.]+$"), "_restored.${request.outputFormat.name.lowercase()}")
        val stats = ProcessingStats(System.currentTimeMillis() - startTime, 0L, stagesDone)
        warnings.add("AI-generated details may differ from source.")
        val result = RestoreResult(request.jobId, outputPath, stats, warnings, planned.stages, quality, currentQuality)
        emit(com.restoration.engine.RestoreProgress.Completed(Result.success(result)))
    }
    
    private fun analyzeQuality(input: ImageInput): ImageQualityProfile {
        return ImageQualityProfile(1920, 1080, 0.7f, 0.3f, 0.4f, 0.5f, 2, 64)
    }
}
