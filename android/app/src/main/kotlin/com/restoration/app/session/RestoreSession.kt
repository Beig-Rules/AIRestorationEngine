package com.restoration.app.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory session shared across Home → Editor → Result.
 * Holds original + restored paths for the Before/After slider.
 */
object RestoreSession {
    data class Snapshot(
        val jobId: String? = null,
        val originalUri: String? = null,
        val restoredUri: String? = null,
        val pipeline: List<String> = emptyList(),
        val backendId: String? = null
    )

    private val _state = MutableStateFlow(Snapshot())
    val state: StateFlow<Snapshot> = _state.asStateFlow()

    fun start(jobId: String, originalUri: String) {
        _state.value = Snapshot(jobId = jobId, originalUri = originalUri)
    }

    fun complete(
        jobId: String,
        restoredUri: String,
        pipeline: List<String> = emptyList(),
        backendId: String? = null
    ) {
        val cur = _state.value
        _state.value = cur.copy(
            jobId = jobId,
            restoredUri = restoredUri,
            pipeline = pipeline,
            backendId = backendId
        )
    }

    fun clear() {
        _state.value = Snapshot()
    }
}
