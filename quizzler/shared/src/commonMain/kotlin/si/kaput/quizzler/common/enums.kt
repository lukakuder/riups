package si.kaput.quizzler.common

enum class QuizSubject(
    val value: String,
    val difficulties: List<Int>
) {
    MATH("math", listOf(3, 6, 9)),
    PHYSICS("physics", listOf(8, 9))
}