package si.kaput.quizzler.network

import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual object QuizSocket {
    private var socket: Socket? = null;

    actual suspend fun connect(url: String) {
        if (socket?.connected() == true) {
            return
        }
        socket?.disconnect()

        suspendCancellableCoroutine { cont ->
            val s = IO.socket(url)

            s.on(Socket.EVENT_CONNECT) {
                if (cont.isActive) {
                    cont.resume(Unit)
                }
            }
            s.on(Socket.EVENT_CONNECT_ERROR) { args ->
                if (cont.isActive) {
                    cont.resumeWithException(
                        Exception(args.firstOrNull()?.toString() ?: "Connection failed")
                    )
                }
            }

            socket = s
            s.connect()
            cont.invokeOnCancellation { s.disconnect() }
        }
    }

    actual suspend fun join(code: String, name: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            if (socket == null) {
                cont.resume(Result.failure(IllegalStateException("Not connected")))
                return@suspendCancellableCoroutine
            }

            socket!!.emit("join", arrayOf(code, name), Ack { args ->
                val res = args.firstOrNull() as? JSONObject

                val result = if (res?.optBoolean("success") == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(res?.optString("error") ?: "Join failed"))
                }

                if (cont.isActive) {
                    cont.resume(result)
                }
            })
        }

    actual fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}