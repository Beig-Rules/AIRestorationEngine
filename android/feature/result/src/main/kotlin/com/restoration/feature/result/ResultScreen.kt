package com.restoration.feature.result

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    jobId: String,
    beforeUri: String? = null,
    afterUri: String? = null,
    onBack: () -> Unit,
    onSave: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Result") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✅ Restoration Complete", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text("Job: $jobId", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            BeforeAfterSlider(beforeUri = beforeUri, afterUri = afterUri, modifier = Modifier.fillMaxWidth().weight(1f, fill = false))
            Spacer(Modifier.height(8.dp))
            Text("Drag the divider to compare", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { onShare?.invoke(); MediaActions.share(context, afterUri ?: beforeUri) }, modifier = Modifier.weight(1f)) { Text("Share") }
                Button(onClick = { onSave?.invoke(); MediaActions.saveToGallery(context, afterUri ?: beforeUri) }, modifier = Modifier.weight(1f)) { Text("Save to Gallery") }
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onBack) { Text("Restore Another") }
        }
    }
}
