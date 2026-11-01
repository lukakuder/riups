package si.kaput.quizzler.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import si.kaput.quizzler.OurColors


@Composable
fun LeaderboardRow(rank: Int, playerName: String, score: Int, delta: Int, isMe: Boolean) {
    val rankColor = when (rank) {
        1 -> Color(0xFFD4AF37) // Gold
        2 -> Color(0xFFB0B0B0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color.LightGray.copy(alpha = 0.4f)
    }

    val bgColor = if (isMe) OurColors.TransparentBlue else OurColors.White
    val borderMod = if (isMe) Modifier.border(2.dp, OurColors.Blue, RoundedCornerShape(20.dp)) else Modifier

    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(bgColor)
            .then(borderMod).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Badge
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(rankColor),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", fontWeight = FontWeight.Black, color = OurColors.White)
        }

        Spacer(Modifier.width(12.dp))
        PlayerAvatar(
            name = playerName,
            size = 44.dp,
            shape = CircleShape,
            fontSize = 20.sp
        )
        Spacer(Modifier.width(12.dp))

        // Name and "You" indicator
        Column(Modifier.weight(1f)) {
            Text(playerName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OurColors.Black)
            if (isMe) {
                Text("You", color = OurColors.Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Score and Delta
        Column(horizontalAlignment = Alignment.End) {
            Text("$score", fontWeight = FontWeight.Black, fontSize = 20.sp, color = OurColors.Black)
            if (delta != 0) {
                Text(
                    text = if (delta > 0) "+$delta" else "$delta",
                    color = if (delta > 0) Color(0xFF00C853) else Color.Red,
                    fontSize = 12.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}