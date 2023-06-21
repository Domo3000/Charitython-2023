package utils

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import model.Message
import model.Messages

object Requests {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                classDiscriminator = "class"
            })
        }
    }

    fun getMessage(url: String, callback: (Message?) -> Unit) {
        MainScope().launch {
            val response = client.get(url)
            var message: Message? = null

            if (response.status.isSuccess()) {
                message = Messages.decode(response.bodyAsText())
            }

            callback(message)
        }
    }

    fun get(url: String, callback: (HttpResponse) -> Unit) {
        MainScope().launch {
            callback(client.get(url))
        }
    }

    fun post(url: String, body: Message, callback: (HttpResponse) -> Unit) {
        MainScope().launch {
            callback(client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(body)
            })
        }
    }

    class AdminRequests(private val username: String, private val password: String) {
        fun get(url: String, callback: (HttpResponse) -> Unit) {
            MainScope().launch {
                callback(client.get(url) {
                    basicAuth(username, password)
                })
            }
        }

        fun post(url: String, body: Message, callback: (Message?) -> Unit) {
            MainScope().launch {
                var message: Message? = null

                val response = client.post(url) {
                    basicAuth(username, password)
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }

                if (response.status.isSuccess()) {
                    message = Messages.decode(response.bodyAsText())
                }

                callback(message)
            }
        }
    }
}