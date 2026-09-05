package com.restoration.feature.home
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onImageSelected: (String) -> Unit) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
        uri?.let { onImageSelected(it.toString()) }
    }
    Scaffold(topBar = { TopAppBar(title = { Text("AI Restoration") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Restore old, blurry, or damaged photos with AI", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(32.dp))
            Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)) {
                Text("Choose Image", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(16.dp))
            selectedUri?.let { Text("Selected: ${it.lastPathSegment}", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
