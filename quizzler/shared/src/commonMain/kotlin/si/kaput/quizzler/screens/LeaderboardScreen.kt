package si.kaput.quizzler.screens

import si.kaput.quizzler.components.LeaderboardRow
import si.kaput.quizzler.components.PlayerAvatar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import si.kaput.quizzler.OurColors
import si.kaput.quizzler.network.QuizSocket

@Composable
fun LeaderboardScreen(
    questionNumber: Int,
    totalQuestions: Int,
    currentPlayerName: StateFlow<String>
) {
    val leaderboard by QuizSocket.leaderboard.collectAsState()
    val currentName by currentPlayerName.collectAsState()

    Column(
        Modifier.fillMaxSize().background(OurColors.Background).safeDrawingPadding().padding(24.dp)
    ) {
        Box(
            Modifier.clip(RoundedCornerShape(50)).background(OurColors.TransparentBlue)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Po vprašanju $questionNumber / $totalQuestions",
                color = OurColors.Blue, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Lestvica", fontSize = 32.sp, fontWeight = FontWeight.Black, color = OurColors.Black)
        Spacer(Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(leaderboard) { index, player ->
                LeaderboardRow(
                    rank = index + 1,
                    playerName = player.player,
                    score = player.score,
                    delta = player.delta,
                    isMe = player.player == currentName
                )
            }
        }
    }
}