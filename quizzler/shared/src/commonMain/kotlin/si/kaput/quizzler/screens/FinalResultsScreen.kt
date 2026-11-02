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
import androidx.compose.ui.text.font.FontFamily
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
    val myRank = leaderboard.indexOfFirst { it.player == currentPlayerName } + 1

    val top3 = leaderboard.take(3)
    val rest = leaderboard.drop(3)

    Column(
        Modifier.fillMaxSize().background(OurColors.Background).safeDrawingPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.clip(RoundedCornerShape(50)).background(OurColors.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                "Konec igre • $totalQuestions vprašanj",
                color = OurColors.DarkGreen, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Končni rezultati ", fontSize = 36.sp, fontWeight = FontWeight.Black, color = OurColors.Black, fontFamily = font)
        Text("${myRank ?: "-"}. mesto", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))

        if (top3.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PodiumStep(top3.getOrNull(1), 2, 120.dp, OurColors.Silver, font)
                PodiumStep(top3.getOrNull(0), 1, 150.dp, OurColors.Gold, font)
                PodiumStep(top3.getOrNull(2), 3, 90.dp, OurColors.Bronze, font)
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(rest) { index, player ->
                LeaderboardRow(
                    rank = index + 4,
                    playerName = player.player,
                    score = player.score,
                    delta = 0,
                    isMe = player.player == currentPlayerName
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                QuizSocket.disconnect()
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OurColors.Blue)
        ) {
            Text("Na domačo stran", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun PodiumStep(
    player: si.kaput.quizzler.common.PlayerScore?,
    rank: Int,
    height: Dp,
    color: Color,
    font: FontFamily
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.height(110.dp)
        ) {
            if (player != null) {
                PlayerAvatar(name = player.player, size = 50.dp, shape = CircleShape, fontSize = 22.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    player.player,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = font,
                    maxLines = 1
                )
                Text("${player.score}", fontWeight = FontWeight.Black, color = OurColors.Gray, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .width(90.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", fontSize = 40.sp, fontWeight = FontWeight.Black, color = OurColors.White)
        }
    }
}