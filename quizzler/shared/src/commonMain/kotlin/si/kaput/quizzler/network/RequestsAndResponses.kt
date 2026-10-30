package si.kaput.quizzler.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateQuizRequest(
    val name: String,
    val subject: String,
    val grade: Int,
    val difficulty: Int,
    @SerialName("question_number") val questionNumber: Int,
    @SerialName("difficulty_mode") val difficultyMode: String = "fixed",
)
@Serializable
data class StartQuizRequest(
    val code: Int
)
@Serializable
data class QuizResponse(
    val code: Int,
)