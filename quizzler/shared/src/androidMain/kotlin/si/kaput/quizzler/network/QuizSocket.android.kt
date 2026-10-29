package si.kaput.quizzler.network

import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import si.kaput.quizzler.common.Question
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual object QuizSocket {
    private var socket: Socket? = null;
    private val _players = MutableStateFlow<List<String>>(emptyList())
    actual val players: StateFlow<List<String>> = _players.asStateFlow()

    private val _question = MutableStateFlow<Question?>(null)
    actual val question: StateFlow<Question?> = _question.asStateFlow()

    actual suspend fun connect(url: String) {
        if (socket?.connected() == true) {
            return
        }
        socket?.disconnect()

        suspendCancellableCoroutine { cont ->
            val s = IO.socket(url)

            s.on("question") { args ->
                val obj = args.firstOrNull() as? JSONObject ?: return@on
                _question.value = Question(
                    title = obj.optString("title"),
                    content = obj.optString("content"),
                    availableTime = obj.optInt("available_time"),
                    index = obj.optInt("index"),
                    total = obj.optInt("total")
                )
            }
            s.on("players") { args ->
                val arr = args.firstOrNull() as? JSONArray ?: return@on
                _players.value = List(arr.length()) { arr.getString(it) }
            }
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

    actual suspend fun watch(code: String): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val s = socket
            if (s == null) {
                cont.resume(Result.failure(IllegalStateException("Not connected")))
                return@suspendCancellableCoroutine
            }
            s.emit("watch", arrayOf(code)) { args ->
                val res = args.firstOrNull() as? JSONObject

                val result = if (res?.optBoolean("success") == true) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(res?.optString("error") ?: "Watch failed"))
                }

                if (cont.isActive) {
                    cont.resume(result)
                }
            }
        }

    actual suspend fun answer(code: String, answer: String): Result<Boolean> =
        suspendCancellableCoroutine { cont ->
            val s = socket
            if (s == null) {
                cont.resume(Result.failure(IllegalStateException("Not connected")))
                return@suspendCancellableCoroutine
            }
            s.emit("answer", arrayOf(code, answer), Ack { args ->
                val res = args.firstOrNull() as? JSONObject
                val result = if (res?.optBoolean("success") == true) {
                    Result.success(res.optBoolean("is_correct"))
                } else {
                    Result.failure(Exception(res?.optString("error") ?: "Answer failed"))
                }
                if (cont.isActive) {
                    cont.resume(result)
                }
            })
        }

    actual fun disconnect() {
        socket?.disconnect()
        socket = null
        _players.value = emptyList()
        _question.value = null
    }
}