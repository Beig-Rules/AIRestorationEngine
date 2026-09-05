package com.restoration.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    var quality by remember { mutableStateOf("HIGH") }
    var faceRestore by remember { mutableStateOf(true) }
    var tileSize by remember { mutableStateOf(256) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Quality Preset", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("LOW", "MEDIUM", "HIGH", "MAXIMUM").forEach { q ->
                    FilterChip(
                        selected = quality == q,
                        onClick = { quality = q },
                        label = { Text(q) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Face Restoration")
                Switch(checked = faceRestore, onCheckedChange = { faceRestore = it })
            }

            Text("Tile Size: $tileSize", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = tileSize.toFloat(),
                onValueChange = { tileSize = it.toInt() },
                valueRange = 128f..512f,
                steps = 3
            )

            Spacer(Modifier.height(24.dp))
            Text(
                "Changes apply to the next restoration job.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
