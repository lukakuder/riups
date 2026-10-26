package si.kaput.quizzler

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import si.kaput.quizzler.navigation.QuestionRoute
import si.kaput.quizzler.navigation.Routes
import si.kaput.quizzler.navigation.WaitingRoute
import si.kaput.quizzler.screens.CreateQuizScreen
import si.kaput.quizzler.screens.JoinQuizScreen
import si.kaput.quizzler.screens.QuestionScreen
import si.kaput.quizzler.screens.WaitingScreen


@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(Routes.CREATE_QUIZ) {
                CreateQuizScreen(navController)
            }
            composable(Routes.JOIN_QUIZ) {
                JoinQuizScreen(navController)
            }
            composable<WaitingRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<WaitingRoute>()
                WaitingScreen(navController, args.code)
            }
            composable<QuestionRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<QuestionRoute>()
                QuestionScreen(navController, args.code)
            }
        }
    }
}