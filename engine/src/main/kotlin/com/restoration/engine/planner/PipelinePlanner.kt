package com.restoration.engine.planner
import com.restoration.engine.domain.*

class RuleBasedPipelinePlanner {
    fun plan(request: RestoreRequest, quality: ImageQualityProfile): PlannedPipeline {
        val stages = mutableListOf<String>()
        val rationale = mutableListOf<String>()
        if (quality.compressionArtifacts > 0.6f) { stages.add("DEBLOCK"); rationale.add("artifacts=${quality.compressionArtifacts}") }
        if (quality.noise > 0.5f) { stages.add("DENOISE"); rationale.add("noise=${quality.noise}") }
        if (quality.width * quality.height < 1_000_000 && request.targetScale > 1f) { stages.add("SUPER_RESOLUTION"); rationale.add("lowres") }
        if (quality.faceCount > 0 && request.faceRestoration.name != "OFF") { stages.add("FACE_RESTORATION"); rationale.add("faces=${quality.faceCount}") }
        stages.add("POST_PROCESSING")
        return PlannedPipeline(stages, rationale)
    }
}

data class PlannedPipeline(val stages: List<String>, val rationale: List<String>)
