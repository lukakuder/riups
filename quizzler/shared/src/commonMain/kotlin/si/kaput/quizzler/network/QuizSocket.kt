package si.kaput.quizzler.network

import kotlinx.coroutines.flow.StateFlow
import si.kaput.quizzler.common.AnswerResult
import si.kaput.quizzler.common.Question
import si.kaput.quizzler.common.PlayerScore

expect object QuizSocket {
    val players: StateFlow<List<String>>
    val question: StateFlow<Question?>
    val questionEnd: StateFlow<Int?>
    val quizStartsAt: StateFlow<Long?>
    val serverTimeOffset: StateFlow<Long>

    val leaderboard: StateFlow<List<PlayerScore>>
    val playerName: StateFlow<String>
    val isGameOver: StateFlow<Boolean>

    suspend fun connect(url: String)
    suspend fun join(code: String, name: String): Result<Unit>
    suspend fun watch(code: String): Result<Unit>
    suspend fun answer(code: String, answer: String): Result<AnswerResult>
    fun disconnect()
}