package si.kaput.quizzler.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import si.kaput.quizzler.common.QuizSubject
import si.kaput.quizzler.network.Api
import si.kaput.quizzler.network.BASE_URL
import si.kaput.quizzler.network.CreateQuizRequest
import si.kaput.quizzler.network.QuizSocket

@Composable
fun CreateQuizScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var quizCode by remember { mutableStateOf<Int?>(null) }

    fun createQuiz() {
        scope.launch {
            loading = true
            error = null
            try {
                val quiz = Api.createQuiz(
                    CreateQuizRequest(
                        name = "generatedTestQuiz",
                        subject = QuizSubject.MATH.value,
                        difficulty = 9,
                        questionNumber = 10,
                    )
                )

                QuizSocket.connect(BASE_URL)
                quizCode = quiz.code
            } catch (e: Exception) {
                error = e.message ?: "Error during quiz creation"
            } finally {
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Create quiz")
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        Button(
            enabled = !loading,
            onClick = { createQuiz() },
        ) {
            Text(if (loading) "Creating..." else "Create Test Quiz")
        }

        quizCode?.let { Text("Quiz code: $it (socket connected)") }
        error?.let { Text("Error: $it") }
    }
}