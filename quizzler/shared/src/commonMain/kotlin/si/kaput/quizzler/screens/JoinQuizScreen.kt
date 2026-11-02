package si.kaput.quizzler.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import quizzler.shared.generated.resources.Res
import quizzler.shared.generated.resources.logo_quizzler
import si.kaput.quizzler.OurColors
import si.kaput.quizzler.components.NameInput
import si.kaput.quizzler.components.QuizCodeInput
import si.kaput.quizzler.navigation.QuestionRoute
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
    val quizStartsAt by QuizSocket.quizStartsAt.collectAsState()

    LaunchedEffect(quizStartsAt) {
        if (quizStartsAt != null) {
            val codeInt = code.toIntOrNull()
            if (codeInt != null) {
                navController.navigate(QuestionRoute(codeInt))
            }
        }
    }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Image(
                painter = painterResource(Res.drawable.logo_quizzler),
                contentDescription = "Quizzler logo",
                modifier = Modifier.size(160.dp)
            )
        }

        QuizCodeInput(code = code, onValueChange = { newInput ->
            code = newInput.filter { it.isDigit() }.take(6)
        })

        Spacer(Modifier.height(12.dp))

        NameInput(name = name, onNameChange = { name = it })

        if (!joined) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!loading && code.isNotBlank() && name.isNotBlank()) OurColors.Blue else OurColors.LightGray)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OurColors.Blue,
                        contentColor = OurColors.White,
                        disabledContainerColor = OurColors.LightGray,
                        disabledContentColor = OurColors.Gray
                    ),
                    enabled = !loading && code.isNotBlank() && name.isNotBlank() && !joined,
                    onClick = {
                        scope.launch {
                            loading = true
                            error = null
                            try {
                                QuizSocket.disconnect()
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (loading) "Pridruževanje..." else "Pridruži se",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        if (joined) {
            Text(
                text = "Pridružili ste se kvizu! Počakajte na začetek.",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        error?.let {
            Text("Error: $it")
        }
    }

}
