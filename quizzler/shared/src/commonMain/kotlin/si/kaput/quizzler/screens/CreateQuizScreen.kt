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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import quizzler.shared.generated.resources.Res
import quizzler.shared.generated.resources.logo_quizzler
import si.kaput.quizzler.OurColors
import si.kaput.quizzler.common.QuizSubject
import si.kaput.quizzler.components.TextInput
import si.kaput.quizzler.navigation.WaitingRoute
import si.kaput.quizzler.network.Api
import si.kaput.quizzler.network.BASE_URL
import si.kaput.quizzler.network.CreateQuizRequest
import si.kaput.quizzler.network.QuizSocket

private const val MAX_QUESTIONS = 50

@Composable
fun CreateQuizScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var isPhysics by remember { mutableStateOf(false) }
    var grade by remember { mutableIntStateOf(QuizSubject.MATH.difficulties.first()) }
    var difficulty by remember { mutableIntStateOf(1) }
    var questionCount by remember { mutableStateOf("10") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val grades = if (isPhysics) QuizSubject.PHYSICS.difficulties else QuizSubject.MATH.difficulties
    val count = questionCount.toIntOrNull()
    val canStart = !loading && name.isNotBlank() && count != null && count >= 1 && count <= MAX_QUESTIONS

    fun createQuiz() {
        val selectedCount = count ?: return

        scope.launch {
            loading = true
            error = null

            val quiz = try {
                Api.createQuiz(
                    CreateQuizRequest(
                        name = name.trim(),
                        subject = (if (isPhysics) QuizSubject.PHYSICS else QuizSubject.MATH).value,
                        grade = grade,
                        difficulty = difficulty,
                        questionNumber = selectedCount,
                    )
                )
            } catch (e: Exception) {
                error = "create: ${e.message}"
                loading = false
                return@launch
            }

            try {
                QuizSocket.connect(BASE_URL)
            } catch (e: Exception) {
                error = "socket: ${e.message}"
                loading = false
                return@launch
            }

            navController.navigate(WaitingRoute(quiz.code))
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.logo_quizzler),
            contentDescription = "Quizzler logo",
            modifier = Modifier.size(160.dp)
        )

        TextInput(
            label = "Quiz name",
            value = name,
            onValueChange = { name = it },
            placeholder = "Math quiz",
        )

        Spacer(Modifier.height(16.dp))

        Column(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Math",
                    fontWeight = if (!isPhysics) FontWeight.ExtraBold else FontWeight.Normal,
                    fontSize = 18.sp
                )
                Switch(
                    checked = isPhysics,
                    onCheckedChange = {
                        isPhysics = it
                        grade = (if (it) QuizSubject.PHYSICS.difficulties else QuizSubject.MATH.difficulties).first()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OurColors.Blue,
                        checkedTrackColor = OurColors.LightGray,
                        uncheckedThumbColor = OurColors.Blue,
                        uncheckedTrackColor = OurColors.LightGray,
                        uncheckedBorderColor = OurColors.LightGray,
                    ),
                )
                Text(
                    "Physics",
                    fontWeight = if (isPhysics) FontWeight.ExtraBold else FontWeight.Normal,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Column(Modifier.fillMaxWidth()) {
            Text(
                text = "Grade",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                grades.forEach { g ->
                    Row(
                        modifier = Modifier.selectable(
                            selected = grade == g,
                            onClick = { grade = g },
                            role = Role.RadioButton,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = grade == g,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = OurColors.Blue),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "$g",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = (if (g == grade) OurColors.Blue else OurColors.Black)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Column(Modifier.fillMaxWidth()) {
            Text(
                text = "Difficulty",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                (1..3).forEach { d ->
                    Row(
                        modifier = Modifier.selectable(
                            selected = difficulty == d,
                            onClick = { difficulty = d },
                            role = Role.RadioButton,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = difficulty == d,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = OurColors.Blue),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "$d",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = (if (d == difficulty) OurColors.Blue else OurColors.Black)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        TextInput(
            label = "Number of questions (1–$MAX_QUESTIONS)",
            value = questionCount,
            onValueChange = { questionCount = it.filter(Char::isDigit).take(2) },
            placeholder = "10",
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (canStart) Color(0xFF1D4ED8) else Color(0xFFCBD5E1))
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
                enabled = canStart,
                onClick = { createQuiz() },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (loading) "Creating..." else "Start quiz",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        error?.let { Text("Error: $it", color = Color(0xFFDC2626)) }
    }
}