package si.kaput.quizzler.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import si.kaput.quizzler.network.BASE_URL
import si.kaput.quizzler.network.QuizSocket

@Composable
fun JoinQuizScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var joined by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Join quiz")
        Button(onClick = { navController.popBackStack() }) { Text("Back") }

        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Quiz code") })
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Your name") })

        Button(
            enabled = !loading && code.isNotBlank() && name.isNotBlank(),
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    try {
                        QuizSocket.connect(BASE_URL)
                        QuizSocket.join(code.trim(), name.trim())
                            .onSuccess { joined = true }
                            .onFailure { error = it.message }
                    } catch (e: Exception) {
                        error = e.message ?: "Something went wrong"
                    } finally {
                        loading = false
                    }
                }
            },
        ) {
            Text(if (loading) "Joining..." else "Join")
        }

        if (joined) {
            Text("Joined! Waiting for the host to start.")
        }
        error?.let {
            Text("Error: $it")
        }
    }
}