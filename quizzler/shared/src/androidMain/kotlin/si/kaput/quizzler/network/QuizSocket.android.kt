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
import si.kaput.quizzler.common.AnswerResult
import si.kaput.quizzler.common.PlayerScore
import si.kaput.quizzler.common.Question
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual object QuizSocket {
    private var socket: Socket? = null;
    private val _players = MutableStateFlow<List<String>>(emptyList())
    private val _leaderboard = MutableStateFlow<List<PlayerScore>>(emptyList())
    private val _playerName = MutableStateFlow<String>("")
    private val _isGameOver = MutableStateFlow<Boolean>(false)
    actual val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()
    actual val playerName: StateFlow<String> = _playerName.asStateFlow()
    actual val leaderboard: StateFlow<List<PlayerScore>> = _leaderboard.asStateFlow()
    actual val players: StateFlow<List<String>> = _players.asStateFlow()

    private val _question = MutableStateFlow<Question?>(null)
    private val _questionEnd = MutableStateFlow<Int?>(null)
    actual val questionEnd: StateFlow<Int?> = _questionEnd.asStateFlow()
    private val _quizStartsAt = MutableStateFlow<Long?>(null)
    actual val quizStartsAt: StateFlow<Long?> = _quizStartsAt.asStateFlow()
    actual val question: StateFlow<Question?> = _question.asStateFlow()

    actual suspend fun connect(url: String) {
        if (socket?.connected() == true) {
            return
        }
        socket?.disconnect()

        suspendCancellableCoroutine { cont ->
            val s = IO.socket(url)

            s.on("quiz_starting") { args ->
                val obj = args.firstOrNull() as? JSONObject ?: return@on
                _quizStartsAt.value = obj.optLong("startsAt")
                _isGameOver.value = false
            }
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
            s.on("question_end") { args ->
                println("RAW question_end: ${args.firstOrNull()}")
                val obj = args.firstOrNull() as? JSONObject ?: return@on
                val arr = obj.optJSONArray("leaderboard") ?: JSONArray()
                _questionEnd.value = obj.optInt("index")
                _leaderboard.value = List(arr.length()) { i ->
                    val o = arr.getJSONObject(i)
                    val name = o.optString("player")
                    val newScore = o.optInt("score")
                    val oldScore = _leaderboard.value.find { it.player == name }?.score ?: 0
                    PlayerScore(name, newScore, newScore - oldScore)
                }
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

            s.on("results") { args ->
                _isGameOver.value = true
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
                    _playerName.value = name
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

    actual suspend fun answer(code: String, answer: String): Result<AnswerResult> =
        suspendCancellableCoroutine { cont ->
            val s = socket
            if (s == null) {
                cont.resume(Result.failure(IllegalStateException("Not connected")))
                return@suspendCancellableCoroutine
            }
            s.emit("answer", arrayOf(code, answer), Ack { args ->
                val res = args.firstOrNull() as? JSONObject
                val result = if (res?.optBoolean("success") == true) {
                    Result.success(AnswerResult(res.optBoolean("is_correct"), res.optInt("score")))
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
        _questionEnd.value = null
        _quizStartsAt.value = null
    }
}