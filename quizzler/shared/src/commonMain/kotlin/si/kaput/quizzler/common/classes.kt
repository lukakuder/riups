package si.kaput.quizzler.common

data class Question (
    val title: String,
    val content: String,
    val availableTime: Int,
    val index: Int,
    val total: Int
)
data class AnswerResult(
    val isCorrect: Boolean,
    val score: Int,
)