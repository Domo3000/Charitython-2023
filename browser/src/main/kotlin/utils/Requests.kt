package utils

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import model.Message
import web.http.fetch

object Requests {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    fun getMessage(url: String): Message? {
        var message: Message? = null

        MainScope().launch {
            client.use {
                message = it.get(url).body() as Message
            }
        }

        return message
    }

    fun get(url: String, callback: (HttpResponse) -> Unit) {
        MainScope().launch {
            client.use {
                callback(it.get(url))
            }
        }.start()
    }

    fun post(url: String, body: Message) {
        MainScope().launch {
            client.use {
                it.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            }
        }
    }

    class AdminRequests(private val username: String, private val password: String) {
        fun get(url: String, callback: (HttpResponse) -> Unit) {
            MainScope().launch {
                client.use {
                    callback(it.get(url) {
                        basicAuth(username, password)
                    })
                }
            }.start()
        }

        fun post(url: String, body: Message) {
            MainScope().launch {
                client.use {
                    it.post(url) {
                        basicAuth(username, password)
                        contentType(ContentType.Application.Json)
                        setBody(body)
                    }
                }
            }
        }
    }
}