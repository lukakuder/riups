package si.kaput.quizzler.navigation

import kotlinx.serialization.Serializable

object Routes {
    const val HOME = "home"
    const val CREATE_QUIZ = "create"
    const val JOIN_QUIZ = "join"
}

@Serializable
data class WaitingRoute(val code: Int)

@Serializable
data class QuestionRoute(val code: Int)