package si.kaput.quizzler.network

import kotlinx.coroutines.flow.StateFlow
import si.kaput.quizzler.common.Question

expect object QuizSocket {
    val players: StateFlow<List<String>>
    val question: StateFlow<Question?>

    suspend fun connect(url: String)
    suspend fun join(code: String, name: String): Result<Unit>
    suspend fun watch(code: String): Result<Unit>
    fun disconnect()
}