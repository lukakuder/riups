package si.kaput.quizzler

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import quizzler.shared.generated.resources.Res
import quizzler.shared.generated.resources.logo_quizzler
import androidx.compose.ui.graphics.Color
import si.kaput.quizzler.navigation.Routes
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController
){
    val font = nunito()
    Column(
        Modifier.fillMaxSize().background(OurColors.Background).padding(horizontal = 20.dp)
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
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(OurColors.Blue),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "U",
                    color = Color.White,
                    fontFamily = font,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                )
            }
        }
        Text(
            "Pick a quiz.\nChallenge your friends.",
            color = OurColors.Navy,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(50.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("What will you choose?", color = OurColors.Navy, fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 20.sp)
//            Text("127 online", color = OurColors.Navy, fontFamily = font, fontWeight = FontWeight.Black, fontSize = 18.sp) //TODO()
        }
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Card("Join with a code",
                "Join your friends with an invite code.",
                "#",
                OurColors.Yellow,
                OurColors.YellowDark,
                OurColors.Navy,
                onClick = {
                    navController.navigate(Routes.JOIN_QUIZ)
                }
            )
            Card("Create lobby",
                "Pick a quiz preset and invite your friends",
                "+",
                OurColors.Green,
                OurColors.GreenDark,
                OurColors.Navy,
                onClick = {
                    navController.navigate(Routes.CREATE_QUIZ)
                }
            )
        }
    }
}

@Composable
private fun Card(
    title: String,
    describtion: String,
    icon: String,
    color: Color,
    shadow: Color,
    textColor: Color,
    onClick: () -> Unit,
){
    val font = nunito()
    val shape = RoundedCornerShape(20.dp)

    Box(
        Modifier.fillMaxWidth().clip(shape).background(shadow).clickable(onClick = onClick)
    ){
        Row(
            Modifier.fillMaxWidth().padding(bottom = 6.dp).background(color, shape).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Color.White),
                contentAlignment = Alignment.Center,
            ){
                Text(
                    icon,
                    color = textColor,
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
            }

            Row(
                Modifier.fillMaxWidth().padding(end=10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        color = textColor,
                        fontFamily = font,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        describtion,
                        color = textColor.copy(alpha = 0.8f),
                        fontFamily = font,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                }
                Text(
                    "↗",
                    color = textColor,
                    fontFamily = font,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                )
            }
        }
    }
}
