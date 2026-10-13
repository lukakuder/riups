package si.kaput.quizzler

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform