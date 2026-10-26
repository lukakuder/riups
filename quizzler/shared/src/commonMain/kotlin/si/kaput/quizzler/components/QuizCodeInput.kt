package si.kaput.quizzler.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizCodeInput(
    code: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navyColor = Color(0xFF1E293B)
    val activeBlue = Color(0xFF2563EB)
    val activeBg = Color(0xFFEFF6FF)
    val inactiveBorder = Color(0xFFE2E8F0)

    Column(modifier = modifier) {
        Text(
            text = "6-digit lobby code",
            color = navyColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box {
            BasicTextField(
                value = code,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0f) // Invisible
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->
                    val char = code.getOrNull(index)?.toString() ?: ""
                    val isActive = index == code.length.coerceAtMost(5)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.85f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isActive) activeBg else Color.White)
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) activeBlue else inactiveBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            color = navyColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )
                    }
                }
            }
        }
    }
}