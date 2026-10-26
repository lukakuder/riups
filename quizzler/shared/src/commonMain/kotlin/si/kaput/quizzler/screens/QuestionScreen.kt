package si.kaput.quizzler.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import si.kaput.quizzler.network.QuizSocket

@Composable
fun QuestionScreen(navController: NavHostController, code: Int) {
    val question by QuizSocket.question.collectAsState()

    Text("code $code")
    Text("question $question")
}