package com.restoration.engine.domain
import java.util.UUID

data class ImageInput(val id: String = UUID.randomUUID().toString(), val path: String, val mimeType: String? = null)

enum class RestoreMode { AUTO, BALANCED, QUALITY, FAST, CUSTOM }
enum class QualityPreset { LOW, MEDIUM, HIGH, MAXIMUM }
enum class HardwarePreference { AUTO, CPU, GPU, NPU }
enum class MemoryPolicy { CONSERVATIVE, BALANCED, AGGRESSIVE }
enum class TilePolicy { AUTO, FIXED, DISABLED }
enum class FaceRestoreStrength { OFF, LIGHT, MEDIUM, STRONG }
enum class DenoiseLevel { OFF, LIGHT, MEDIUM, STRONG, AUTO }
enum class DeblockLevel { OFF, LIGHT, MEDIUM, STRONG, AUTO }
enum class OutputFormat { JPEG, PNG, WEBP }

data class RestoreRequest(
    val input: ImageInput,
    val mode: RestoreMode = RestoreMode.AUTO,
    val targetScale: Float = 2.0f,
    val qualityPreset: QualityPreset = QualityPreset.HIGH,
    val faceRestoration: FaceRestoreStrength = FaceRestoreStrength.MEDIUM,
    val denoise: DenoiseLevel = DenoiseLevel.AUTO,
    val deblock: DeblockLevel = DeblockLevel.AUTO,
    val hardwarePreference: HardwarePreference = HardwarePreference.AUTO,
    val tilePolicy: TilePolicy = TilePolicy.AUTO,
    val memoryPolicy: MemoryPolicy = MemoryPolicy.BALANCED,
    val outputFormat: OutputFormat = OutputFormat.PNG,
    val jobId: String = UUID.randomUUID().toString()
)

data class ProcessingStats(val totalMs: Long, val peakMemoryBytes: Long, val stagesCompleted: Int)

data class RestoreResult(
    val jobId: String, val outputPath: String, val stats: ProcessingStats,
    val warnings: List<String> = emptyList(), val pipeline: List<String> = emptyList(),
    val qualityBefore: ImageQualityProfile? = null, val qualityAfter: ImageQualityProfile? = null
)

data class ImageQualityProfile(
    val width: Int, val height: Int, val sharpness: Float, val blur: Float,
    val noise: Float, val compressionArtifacts: Float, val faceCount: Int,
    val smallestFacePx: Int? = null
)

data class FaceDetection(val id: String, val x: Float, val y: Float, val w: Float, val h: Float, val confidence: Float)

data class AnalysisResult(
    val jobId: String, val quality: ImageQualityProfile, val faces: List<FaceDetection>,
    val suggestedPipeline: List<String>, val warnings: List<String>
)
