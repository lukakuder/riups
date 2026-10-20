package si.kaput.quizzler.network

expect object QuizSocket {
    suspend fun connect(url: String)
    suspend fun join(code: String, name: String): Result<Unit>
    fun disconnect()
}