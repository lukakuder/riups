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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import si.kaput.quizzler.network.BASE_URL
import si.kaput.quizzler.network.QuizSocket
import si.kaput.quizzler.nunito

@Composable
fun JoinQuizScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var joined by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val font = nunito()
    val greyColor = Color(0xFFE2E8F0)
    val lightBlueColor = Color(0xFF2563EB)
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
//        Text("Join quiz")
//        Button(onClick = { navController.popBackStack() }) { Text("Back") }
        QuizCodeInput(code = code, onValueChange = { newInput ->
            code = newInput.filter { it.isDigit() }.take(6)
        })
        //OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Quiz code") })
        NameInput(name = name, onNameChange = { name = it })

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!loading && code.isNotBlank() && name.isNotBlank()) Color(0xFF1D4ED8) else Color(0xFFCBD5E1))
                    // ^ This is the dark "shadow" layer. We make it grey if disabled.
                ) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = lightBlueColor,
                            contentColor = Color.White,
                            disabledContainerColor = greyColor,
                            disabledContentColor = Color.Gray
                        ),
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
                                text = if (loading) "Joining..." else "Join lobby",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

        if (joined) {
            Text("Joined! Waiting for the host to start.")
        }
        error?.let {
            Text("Error: $it")
        }
        Spacer(Modifier.weight(1f))
    }

}

