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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import quizzler.shared.generated.resources.Res
import quizzler.shared.generated.resources.logo_quizzler
import si.kaput.quizzler.OurColors
import si.kaput.quizzler.navigation.QuestionRoute
import si.kaput.quizzler.network.Api
import si.kaput.quizzler.network.QuizSocket
import si.kaput.quizzler.network.StartQuizRequest
import kotlin.math.absoluteValue

@Composable
fun WaitingScreen(navController: NavController, quizCode: Int) {
    val players by QuizSocket.players.collectAsState()
    val scope = rememberCoroutineScope()
    var starting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val question by QuizSocket.question.collectAsState()

    LaunchedEffect(question != null) {
        if (question != null) {
            navController.navigate(QuestionRoute(quizCode))
        }
    }

    LaunchedEffect(quizCode) {
        QuizSocket.watch(quizCode.toString())
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

        Text("Join with code", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OurColors.LightBlue)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quizCode.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp,
                letterSpacing = 6.sp,
                color = OurColors.Blue
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Players", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text("${players.size} joined", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (players.isEmpty()) {
                Text(
                    "Waiting for players to join...",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(players, key = { it }) { player -> PlayerRow(player) }
                }
            }
        }

        error?.let {
            Text(
                "Error: $it",
                color = Color(0xFFDC2626),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OurColors.Blue,
                contentColor = OurColors.White,
                disabledContainerColor = OurColors.LightGray,
                disabledContentColor = OurColors.Gray
            ),
            enabled = players.isNotEmpty() && !starting,
            onClick = {
                scope.launch {
                    starting = true
                    error = null
                    try {
                        Api.startQuiz(
                            StartQuizRequest(quizCode)
                        )
                        // TODO: navigacija na zaslon z vprašanji
                    } catch (e: Exception) {
                        error = e.message ?: "Could not start quiz"
                    } finally {
                        starting = false
                    }
                }
            },
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (starting) "Starting..." else "Start quiz",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun PlayerRow(name: String) {
    val hash = name.hashCode().absoluteValue
    val bgColor = Color.hsl((hash % 60).toFloat(), 0.8f, 0.7f)
    val textColor = if (bgColor.luminance() > 0.5f) Color.Black else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
    }
}