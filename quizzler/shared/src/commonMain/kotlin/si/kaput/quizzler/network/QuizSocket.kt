package si.kaput.quizzler.network

import kotlinx.coroutines.flow.StateFlow

expect object QuizSocket {
    val players: StateFlow<List<String>>

    suspend fun connect(url: String)
    suspend fun join(code: String, name: String): Result<Unit>
    suspend fun watch(code: String): Result<Unit>
    fun disconnect()
}