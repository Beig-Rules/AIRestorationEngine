package com.restoration.engine.pipeline
import com.restoration.engine.domain.*
import com.restoration.engine.error.EngineError
import com.restoration.engine.planner.RuleBasedPipelinePlanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.isActive

class RestorationPipeline(private val planner: RuleBasedPipelinePlanner = RuleBasedPipelinePlanner()) {
    
    suspend fun execute(request: RestoreRequest): Flow<com.restoration.engine.RestoreProgress> = flow {
        if (!coroutineContext.isActive) { emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.Cancelled))); return@flow }
        
        emit(com.restoration.engine.RestoreProgress.StageStarted("ANALYZE", 0f))
        val quality = analyzeQuality(request.input)
        emit(com.restoration.engine.RestoreProgress.StageCompleted("ANALYZE"))
        
        val planned = planner.plan(request, quality)
        val warnings = mutableListOf<String>()
        var currentQuality = quality
        var stagesDone = 0
        val startTime = System.currentTimeMillis()
        
        for ((idx, stage) in planned.stages.withIndex()) {
            if (!coroutineContext.isActive) { emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.Cancelled))); return@flow }
            emit(com.restoration.engine.RestoreProgress.StageStarted(stage, idx.toFloat() / planned.stages.size))
            try {
                currentQuality = executeStage(stage, request, currentQuality)
                stagesDone++
                emit(com.restoration.engine.RestoreProgress.StageCompleted(stage))
            } catch (e: OutOfMemoryError) {
                emit(com.restoration.engine.RestoreProgress.Completed(Result.failure(EngineError.MemoryError(0, 0))))
                return@flow
            }
        }
        
        val outputPath = request.input.path.replace(Regex("\\.[^.]+$"), "_restored.${request.outputFormat.name.lowercase()}")
        val stats = ProcessingStats(System.currentTimeMillis() - startTime, 0L, stagesDone)
        warnings.add("AI-generated details may differ from source.")
        val result = RestoreResult(request.jobId, outputPath, stats, warnings, planned.stages, quality, currentQuality)
        emit(com.restoration.engine.RestoreProgress.Completed(Result.success(result)))
    }
    
    private fun analyzeQuality(input: ImageInput): ImageQualityProfile {
        // TODO: Real analysis via native
        return ImageQualityProfile(1920, 1080, 0.7f, 0.3f, 0.4f, 0.5f, 2, 64)
    }
    
    private fun executeStage(stage: String, request: RestoreRequest, quality: ImageQualityProfile): ImageQualityProfile {
        // TODO: Dispatch to native backend per stage
        return quality.copy(sharpness = quality.sharpness + 0.1f)
    }
}
