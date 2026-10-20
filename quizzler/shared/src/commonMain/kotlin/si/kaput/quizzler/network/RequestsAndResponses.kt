package si.kaput.quizzler.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateQuizRequest(
    val name: String,
    val subject: String,
    val difficulty: Int,
    @SerialName("question_number") val questionNumber: Int,
)
@Serializable
data class QuizResponse(
    // TODO: adjust this
    val code: Int,
)