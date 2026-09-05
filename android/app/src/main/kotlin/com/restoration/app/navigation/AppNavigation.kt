package com.restoration.app.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.restoration.feature.editor.EditorScreen
import com.restoration.feature.home.HomeScreen
import com.restoration.feature.result.ResultScreen

object Routes {
    const val HOME = "home"
    const val EDITOR = "editor/{imagePath}"
    const val RESULT = "result/{jobId}"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onImageSelected = { path ->
                navController.navigate("editor/${java.net.URLEncoder.encode(path, \"UTF-8\")}")
            })
        }
        composable(Routes.EDITOR) { backStack ->
            val raw = backStack.arguments?.getString("imagePath") ?: ""
            val imagePath = try { java.net.URLDecoder.decode(raw, "UTF-8") } catch (_: Exception) { raw }
            EditorScreen(
                imagePath = imagePath,
                onRestoreComplete = { jobId ->
                    navController.navigate("result/$jobId")
                }
            )
        }
        composable(Routes.RESULT) { backStack ->
            ResultScreen(
                jobId = backStack.arguments?.getString("jobId") ?: "",
                beforeUri = null,
                afterUri = null,
                onBack = { navController.popBackStack(Routes.HOME, false) }
            )
        }
    }
}
