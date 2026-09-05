package com.restoration.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.restoration.app.session.RestoreSession
import com.restoration.engine.android.EngineModule
import com.restoration.feature.editor.EditorScreen
import com.restoration.feature.editor.EditorViewModel
import com.restoration.feature.home.HomeScreen
import com.restoration.feature.result.ResultScreen

object Routes {
    const val HOME = "home"
    const val EDITOR = "editor/{imagePath}"
    const val RESULT = "result/{jobId}"
}

@Composable
fun AppNavigation() {
    LaunchedEffect(Unit) {
        EditorViewModel.onJobStarted = { jobId, uri ->
            RestoreSession.start(jobId, uri)
        }
        EditorViewModel.onJobCompleted = { jobId, restored, pipeline, backend ->
            RestoreSession.complete(jobId, restored, pipeline, backend)
        }
    }

    val navController = rememberNavController()
    val session by RestoreSession.state.collectAsState()
    val engine = EngineModule.engine

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onImageSelected = { path ->
                RestoreSession.clear()
                val encoded = java.net.URLEncoder.encode(path, "UTF-8")
                navController.navigate("editor/$encoded")
            })
        }
        composable(Routes.EDITOR) { backStack ->
            val raw = backStack.arguments?.getString("imagePath") ?: ""
            val imagePath = try {
                java.net.URLDecoder.decode(raw, "UTF-8")
            } catch (_: Exception) {
                raw
            }
            EditorScreen(
                imagePath = imagePath,
                engine = engine,
                onRestoreComplete = { jobId ->
                    navController.navigate("result/$jobId") {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }
        composable(Routes.RESULT) { backStack ->
            val jobId = backStack.arguments?.getString("jobId") ?: session.jobId ?: ""
            ResultScreen(
                jobId = jobId,
                beforeUri = session.originalUri,
                afterUri = session.restoredUri,
                onBack = {
                    RestoreSession.clear()
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onSave = { },
                onShare = { }
            )
        }
    }
}
