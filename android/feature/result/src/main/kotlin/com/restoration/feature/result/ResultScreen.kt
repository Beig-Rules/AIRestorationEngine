package com.restoration.feature.result
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(jobId: String, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Result") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("✅ Restoration Complete", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Text("Job ID: $jobId", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(32.dp))
            Text("Before/After comparison will appear here", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(32.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(0.8f)) { Text("Restore Another") }
        }
    }
}
