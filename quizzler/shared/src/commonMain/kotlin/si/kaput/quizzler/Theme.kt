package si.kaput.quizzler

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import quizzler.shared.generated.resources.Res
import quizzler.shared.generated.resources.nunito_black
import quizzler.shared.generated.resources.nunito_bold
import quizzler.shared.generated.resources.nunito_extrabold

object OurColors {
    val Navy = Color(0xFF131F2E)
    val Background = Color(0xFFFAFAFA)
    val Blue = Color(0xFF0D6EFD)
    val BlueDark = Color(0xFF0A58CA)
    val Yellow = Color(0xFFFDB827)
    val YellowDark = Color(0xFFD99A0B)
    val Green = Color(0xFF06B472)
    val GreenDark = Color(0xFF048A58)
    val Red = Color(0xFFE0304B)
}

@Composable
fun nunito() = FontFamily(
    Font(Res.font.nunito_bold, FontWeight.Bold),
    Font(Res.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(Res.font.nunito_black, FontWeight.Black)
)
