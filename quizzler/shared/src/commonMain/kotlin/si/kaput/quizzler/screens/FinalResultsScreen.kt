package si.kaput.quizzler.screens

import si.kaput.quizzler.components.LeaderboardRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import si.kaput.quizzler.OurColors
import si.kaput.quizzler.components.PlayerAvatar
import si.kaput.quizzler.network.QuizSocket
import si.kaput.quizzler.navigation.Routes
import si.kaput.quizzler.nunito

@Composable
fun FinalResultsScreen(
    navController: NavController,
    totalQuestions: Int
) {
    val leaderboard by QuizSocket.leaderboard.collectAsState()
    val currentPlayerName by QuizSocket.playerName.collectAsState()
    val font = nunito()

    val top3 = leaderboard.take(3)
    val rest = leaderboard.drop(3)

    Column(
        Modifier.fillMaxSize().background(OurColors.Background).safeDrawingPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Bubble
        Box(
            Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFE8F5E9))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Game over • $totalQuestions questions",
                color = Color(0xFF1B5E20), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Final results", fontSize = 36.sp, fontWeight = FontWeight.Black, color = OurColors.Black, fontFamily = font)
        Spacer(Modifier.height(32.dp))

        // Podium
        if (top3.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (top3.size >= 2) PodiumStep(top3[1], 2, 120.dp, Color(0xFF4285F4), font)
                if (top3.size >= 1) PodiumStep(top3[0], 1, 160.dp, Color(0xFFD4AF37), font)
                if (top3.size >= 3) PodiumStep(top3[2], 3, 90.dp, Color(0xFF00C853), font)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Rest of the list (4th place and below)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(rest) { index, player ->
                LeaderboardRow(
                    rank = index + 4,
                    playerName = player.player,
                    score = player.score,
                    delta = 0, // Usually don't show delta on final screen
                    isMe = player.player == currentPlayerName
                )
            }
        }

        // Leave Button
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                QuizSocket.disconnect()
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OurColors.Blue)
        ) {
            Text("Back to Home", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun PodiumStep(
    player: si.kaput.quizzler.common.PlayerScore,
    rank: Int,
    height: Dp,
    color: Color,
    font: androidx.compose.ui.text.font.FontFamily
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PlayerAvatar(name = player.player, size = 44.dp, shape = CircleShape, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(player.player, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = font)
        Text("${player.score}", fontWeight = FontWeight.Black, color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}