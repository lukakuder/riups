package si.kaput.quizzler.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import si.kaput.quizzler.OurColors
import si.kaput.quizzler.network.QuizSocket
import si.kaput.quizzler.nunito
import kotlin.time.Duration.Companion.milliseconds
import si.kaput.quizzler.components.TextInput


data class QuestionState(
    val questionNumber: Int,
    val totalQuestions: Int? = null,
    val question: String,
    val secondsLeft: Int,
    val totalSeconds: Int,
    val rank: Int? = null,
    val playerName: String? = null,
    val score: Int? = null,
    val submittedAnswer: String? = null,
)
@Composable
fun QuestionContent(
    state: QuestionState,
    onSubmit: (String) -> Unit,
) {
    val font = nunito()

    var answer by remember(state.question) { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().background(OurColors.Background).safeDrawingPadding().padding(24.dp)
    ){
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Bubble(OurColors.TransparentBlue){
                Text(
                    "Question ${state.questionNumber} / ${state.totalQuestions}",
                    color = OurColors.Blue,
                    fontFamily = font,
                    fontWeight = FontWeight.ExtraBold, fontSize = 13.sp,
                )
            }
            Bubble(OurColors.TransparentYellow){
                Box(Modifier.size(9.dp).clip(CircleShape).background(OurColors.Yellow))
                Spacer(Modifier.width(8.dp))
                Text(
                    formatTime(state.secondsLeft),
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(OurColors.White)
                .border(1.dp, OurColors.LightGray,RoundedCornerShape(20.dp))
                .padding(20.dp)

        ) {
            Text(
                state.question,
                color = OurColors.Black,
                fontFamily = font,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Score: ${state.score?.toString() ?: "0"}",
            color = OurColors.Black,
            fontFamily = font,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
        )
        TextInput(
            label = "Your answer",
            value = answer,
            onValueChange = { answer = it },
            placeholder = "Type your answer",
            readOnly = state.submittedAnswer != null || state.secondsLeft <= 0
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onSubmit(answer.trim())},
            enabled = answer.isNotBlank() && state.submittedAnswer == null,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OurColors.Blue,
                contentColor = OurColors.White,
                disabledContainerColor = OurColors.LightGray,
                disabledContentColor = OurColors.Gray
            ),
        ){
            Text(if (state.submittedAnswer == null) "Submit" else "Answer sent",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun QuestionScreen(code: Int) {
    val question by QuizSocket.question.collectAsState()
    val endedIndex by QuizSocket.questionEnd.collectAsState()
    val scope = rememberCoroutineScope()

    var secondsLeft by remember { mutableIntStateOf(0) }
    var submitted by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableStateOf<Int?>(null) }
    var pendingScore by remember { mutableStateOf<Int?>(null) }
    var showLeaderboard by remember { mutableStateOf(false) }

    LaunchedEffect(question) {
        val q = question ?: return@LaunchedEffect
        showLeaderboard = false
        score = pendingScore
        submitted = null
        secondsLeft = q.availableTime
        while (secondsLeft > 0) {
            delay(1000.milliseconds)
            secondsLeft -= 1
        }
        score = pendingScore
        showLeaderboard = true
    }
    LaunchedEffect(endedIndex) {
        if (endedIndex != null && endedIndex == question?.index) {
            secondsLeft = 0
            score = pendingScore
            showLeaderboard = true
        }
    }

    val q = question ?: return
    if (showLeaderboard) {
        LeaderboardScreen(code)
    }
    else{
        QuestionContent(
            state = QuestionState(
                questionNumber = q.index,
                totalQuestions = q.total,
                question = q.content,
                secondsLeft = secondsLeft,
                totalSeconds = q.availableTime,
                submittedAnswer = submitted,
                score = score
            ),
            onSubmit = { text ->
                scope.launch {
                    QuizSocket.answer(code.toString(), text)
                        .onSuccess { result ->
                            submitted = text
                            pendingScore = result.score
                            if (secondsLeft <= 0) score = result.score
                        }
                }
            },
        )
    }
}


@Composable
private fun Bubble(
    background: Color,
    radius: Dp = 14.dp,
    content: @Composable RowScope.() -> Unit
){
    Row(
        Modifier.clip(RoundedCornerShape(radius)).background(background).padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

private fun formatTime(seconds: Int): String {
    return "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
}
