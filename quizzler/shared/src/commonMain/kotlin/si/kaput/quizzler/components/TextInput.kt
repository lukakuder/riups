package si.kaput.quizzler.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import si.kaput.quizzler.OurColors

@Composable
fun TextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    readOnly: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            readOnly = readOnly,
            placeholder = {
                Text(placeholder, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                color = OurColors.Black,
                fontSize = 18.sp,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = OurColors.White,
                unfocusedContainerColor = OurColors.White,
                unfocusedBorderColor = OurColors.LightGray,
                focusedBorderColor = OurColors.Blue
            ),
        )
    }
}