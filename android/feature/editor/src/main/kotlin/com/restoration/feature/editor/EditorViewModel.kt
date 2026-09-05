package com.restoration.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restoration.engine.RestorationEngine
import com.restoration.engine.RestoreProgress
import com.restoration.engine.domain.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface EditorUiState {
    data object Idle : EditorUiState
    data class Analyzing(val progress: Float) : EditorUiState
    data class Processing(val stage: String, val progress: Float) : EditorUiState
    data class Completed(val jobId: String, val outputPath: String) : EditorUiState
    data class Error(val message: String) : EditorUiState
}

class EditorViewModel(
    private val engine: RestorationEngine
) : ViewModel() {
    private val _state = MutableStateFlow<EditorUiState>(EditorUiState.Idle)
    val state: StateFlow<EditorUiState> = _state
    private var currentJobId: String? = null
    private var restoreJob: Job? = null

    fun startRestore(imagePath: String, mode: RestoreMode = RestoreMode.AUTO) {
        restoreJob?.cancel()
        restoreJob = viewModelScope.launch {
            _state.value = EditorUiState.Analyzing(0f)
            val request = RestoreRequest(input = ImageInput(path = imagePath), mode = mode)
            currentJobId = request.jobId
            onJobStarted?.invoke(request.jobId, imagePath)

            try {
                engine.restore(request).collect { progress ->
                    when (progress) {
                        is RestoreProgress.StageStarted ->
                            _state.value = EditorUiState.Processing(progress.stage, progress.progress)
                        is RestoreProgress.StageCompleted -> { }
                        is RestoreProgress.Completed -> {
                            progress.result.fold(
                                onSuccess = { result ->
                                    _state.value = EditorUiState.Completed(result.jobId, result.outputPath)
                                    onJobCompleted?.invoke(
                                        result.jobId,
                                        result.outputPath,
                                        result.pipeline,
                                        result.warnings.firstOrNull { it.startsWith("Backend:") }?.removePrefix("Backend: ")?.trim()
                                    )
                                },
                                onFailure = {
                                    _state.value = EditorUiState.Error(it.message ?: "Unknown error")
                                }
                            )
                        }
                    }
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                _state.value = EditorUiState.Error("Cancelled")
                throw e
            } catch (e: Exception) {
                _state.value = EditorUiState.Error(e.message ?: "Failed")
            }
        }
    }

    fun cancel() {
        currentJobId?.let { engine.cancel(it) }
        restoreJob?.cancel()
        restoreJob = null
        _state.value = EditorUiState.Idle
    }

    override fun onCleared() {
        cancel()
        super.onCleared()
    }

    companion object {
        var onJobStarted: ((jobId: String, originalUri: String) -> Unit)? = null
        var onJobCompleted: ((jobId: String, restoredUri: String, pipeline: List<String>, backendId: String?) -> Unit)? = null
    }
}
