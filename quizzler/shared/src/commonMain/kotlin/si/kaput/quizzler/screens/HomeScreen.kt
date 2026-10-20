package si.kaput.quizzler.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import si.kaput.quizzler.navigation.Routes

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { navController.navigate(Routes.CREATE_QUIZ) }) { Text("Create quiz") }
        Button(onClick = { navController.navigate(Routes.JOIN_QUIZ) }) { Text("Join quiz") }
    }
}