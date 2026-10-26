package si.kaput.quizzler.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// TODO: Load this from .env, uncomment the one below if only using emulated phones, fill in right ip below if usign real as well
const val BASE_URL = "http://localhost:3000"
//const val BASE_URL = "http://192.168.1.186:3000"


object Api {
    private val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json( Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun createQuiz(request: CreateQuizRequest): QuizResponse {
        return client.post("$BASE_URL/api/quizzes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}