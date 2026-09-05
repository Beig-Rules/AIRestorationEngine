package com.restoration.feature.editor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restoration.engine.RestorationEngine
import com.restoration.engine.RestoreProgress
import com.restoration.engine.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface EditorUiState {
    data object Idle : EditorUiState
    data class Analyzing(val progress: Float) : EditorUiState
    data class Processing(val stage: String, val progress: Float) : EditorUiState
    data class Completed(val jobId: String) : EditorUiState
    data class Error(val message: String) : EditorUiState
}

class EditorViewModel(private val engine: RestorationEngine) : ViewModel() {
    private val _state = MutableStateFlow<EditorUiState>(EditorUiState.Idle)
    val state: StateFlow<EditorUiState> = _state
    private var currentJobId: String? = null

    fun startRestore(imagePath: String, mode: RestoreMode = RestoreMode.AUTO) {
        viewModelScope.launch {
            _state.value = EditorUiState.Analyzing(0f)
            val request = RestoreRequest(input = ImageInput(path = imagePath), mode = mode)
            currentJobId = request.jobId
            engine.restore(request).collect { progress ->
                when (progress) {
                    is RestoreProgress.StageStarted -> _state.value = EditorUiState.Processing(progress.stage, progress.progress)
                    is RestoreProgress.StageCompleted -> {}
                    is RestoreProgress.Completed -> {
                        progress.result.fold(
                            onSuccess = { _state.value = EditorUiState.Completed(it.jobId) },
                            onFailure = { _state.value = EditorUiState.Error(it.message ?: "Unknown error") }
                        )
                    }
                }
            }
        }
    }

    fun cancel() { currentJobId?.let { engine.cancel(it) } }
}
