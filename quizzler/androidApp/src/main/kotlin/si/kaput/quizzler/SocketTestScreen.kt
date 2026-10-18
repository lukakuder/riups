package si.kaput.quizzler

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket

// TODO: dodaj .env
private const val SERVER_URL = "http://10.0.2.2:3000"
private const val LOCAL_NETWORK_PERMISSION = "android.permission.ACCESS_LOCAL_NETWORK"

@Composable
fun SocketTestScreen() {
    val context = LocalContext.current
    val logs = remember { mutableStateListOf<String>() }
    var status by remember { mutableStateOf("connecting…") }
    var allowed by remember { mutableStateOf(hasLocalNetwork()) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> allowed = granted }

    LaunchedEffect(Unit) {
        if (!allowed) launcher.launch(LOCAL_NETWORK_PERMISSION)
    }

    val socket = remember {
        IO.socket(
            SERVER_URL,
            IO.Options().apply { transports = arrayOf("websocket") }
        )
    }

    DisposableEffect(allowed) {
        if (!allowed) {
            status = "waiting for local network permission"
            return@DisposableEffect onDispose { }
        }

        socket.on(Socket.EVENT_CONNECT) { status = "connected" }
        socket.on(Socket.EVENT_DISCONNECT) { status = "disconnected" }
        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            val err = args.firstOrNull() as? Throwable
            status = "error: ${err?.message} | cause: ${err?.cause?.message}"
        }
        socket.on("tick") { args ->
            logs.add("tick ${args[0]}")
            if (logs.size > 50) logs.removeAt(0)
        }
        socket.connect()

        onDispose {
            socket.off()
            socket.disconnect()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Status: $status")

        Button(
            enabled = allowed,
            onClick = {
                socket.emit("echo", "hello", Ack { args ->
                    logs.add("echo reply: ${args[0]}")
                })
            }
        ) {
            Text("Send echo")
        }

        LazyColumn {
            items(logs.reversed()) { Text(it) }
        }
    }
}

fun hasLocalNetwork() {
    Build.VERSION.SDK_INT < 37 || context.checkSelfPermission(LOCAL_NETWORK_PERMISSION) == PackageManager.PERMISSION_GRANTED
}
