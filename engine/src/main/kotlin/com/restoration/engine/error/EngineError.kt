package com.restoration.engine.error

sealed class EngineError(val code: String, val userMessage: String, val diagnostic: String, val recovery: String) : Exception(userMessage) {
    data class MemoryError(val available: Long, val required: Long) : EngineError("ERR_MEM_01", "Memory low. Try lower quality.", "avail=$available req=$required", "Reduce scale")
    data class DecodeError(val path: String, val reason: String) : EngineError("ERR_DEC_01", "Cannot read image.", "decode:$path:$reason", "Check file")
    data class NativeCrash(val signal: String) : EngineError("ERR_NAT_01", "Processing failed.", "signal:$signal", "Retry")
    data class ModelError(val modelId: String, val reason: String) : EngineError("ERR_MOD_01", "Model failed.", "model:$modelId:$reason", "Reinstall model")
    data class InputError(val reason: String) : EngineError("ERR_INP_01", "Invalid input.", reason, "Provide valid image")
    object Cancelled : EngineError("ERR_CAN_01", "Cancelled.", "user_cancel", "None")
    data class Unsupported(val feature: String) : EngineError("ERR_UNSU_01", "Not supported.", "feature:$feature", "Use alternative")
}
