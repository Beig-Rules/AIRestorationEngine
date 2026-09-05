package com.restoration.app.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.restoration.feature.editor.EditorScreen
import com.restoration.feature.home.HomeScreen
import com.restoration.feature.result.ResultScreen

object Routes { const val HOME = "home"; const val EDITOR = "editor/{imagePath}"; const val RESULT = "result/{jobId}" }

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(onImageSelected = { path -> navController.navigate("editor/$path") }) }
        composable(Routes.EDITOR) { backStack -> EditorScreen(imagePath = backStack.arguments?.getString("imagePath") ?: "", onRestoreComplete = { jobId -> navController.navigate("result/$jobId") }) }
        composable(Routes.RESULT) { backStack -> ResultScreen(jobId = backStack.arguments?.getString("jobId") ?: "", onBack = { navController.popBackStack(Routes.HOME, false) }) }
    }
}
