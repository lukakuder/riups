package si.kaput.quizzler.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.graphics.luminance
import si.kaput.quizzler.OurColors
import kotlin.math.absoluteValue

@Composable
fun NameInput(
    name:String,
    onNameChange: (String) -> Unit,
    modifier: Modifier= Modifier
) {
    val hash = name.hashCode().absoluteValue
    val bgColor = Color.hsl((hash % 60).toFloat(), 0.8f, 0.7f)
    val textColor = if (bgColor.luminance() > 0.5f) OurColors.Black else OurColors.White
    val navyColor = OurColors.Navy
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Tvoj vzdevek",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                color = navyColor,
                fontSize = 18.sp,

            ),

            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = OurColors.White,
                unfocusedContainerColor = OurColors.White,
                unfocusedBorderColor = OurColors.LightGray,
                focusedBorderColor = OurColors.Blue
            ),
            leadingIcon = {
                if(name.isNotEmpty()) {
                    PlayerAvatar(
                        name = name,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                        size = 36.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Uredi",
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp)

                )
            },


        )
    }
}