package com.restoration.feature.editor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditorScreen(imagePath: String, onRestoreComplete: (String) -> Unit, viewModel: EditorViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(imagePath) { viewModel.startRestore(imagePath) }
    LaunchedEffect(state) { if (state is EditorUiState.Completed) onRestoreComplete((state as EditorUiState.Completed).jobId) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        when (val s = state) {
            is EditorUiState.Idle -> Text("Loading...")
            is EditorUiState.Analyzing -> { CircularProgressIndicator(); Spacer(Modifier.height(16.dp)); Text("Analyzing image...") }
            is EditorUiState.Processing -> {
                Text("Stage: ${s.stage}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = s.progress, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.cancel() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cancel") }
            }
            is EditorUiState.Completed -> Text("✅ Complete!", style = MaterialTheme.typography.headlineSmall)
            is EditorUiState.Error -> { Text("❌ ${s.message}", color = MaterialTheme.colorScheme.error); Spacer(Modifier.height(16.dp)); Button(onClick = { viewModel.startRestore(imagePath) }) { Text("Retry") } }
        }
    }
}
