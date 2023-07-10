package utils

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
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

    // TODO handle Admin requests better, maybe optional Auth parameter?
    fun postImage(url: String, image: ByteArray, body: Message, callback: (Message?) -> Unit) {
        MainScope().launch {
            var message: Message? = null

            val response = client.submitFormWithBinaryData(url,
                formData {
                    append("image", image, Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Image.PNG)
                        append(HttpHeaders.ContentDisposition, "filename=image.png")
                    })
                    append("message", body.encode(), Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Application.Json)
                    })
                }) {
                onUpload { bytesSentTotal, contentLength ->
                    console.log("$bytesSentTotal / $contentLength")
                }
            }

            if (response.status.isSuccess()) {
                message = Messages.decode(response.bodyAsText())
            }

            callback(message)
        }
    }

    class AdminRequests(private val username: String, private val password: String) {
        private val prefix = "/admin"

        fun get(url: String, callback: (HttpResponse) -> Unit) {
            MainScope().launch {
                callback(client.get(prefix + url) {
                    basicAuth(username, password)
                })
            }
        }

        fun delete(url: String, callback: (Message?) -> Unit) {
            MainScope().launch {
                var message: Message? = null

                val response = client.delete(prefix + url) {
                    basicAuth(username, password)
                    contentType(ContentType.Application.Json)
                }

                if (response.status.isSuccess()) {
                    message = Messages.decode(response.bodyAsText())
                }

                callback(message)
            }
        }

        fun post(url: String, body: Message, callback: (Message?) -> Unit) {
            MainScope().launch {
                var message: Message? = null

                val response = client.post(prefix + url) {
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

        fun postImage(url: String, image: ByteArray, body: Message?, callback: (Message?) -> Unit) {
            MainScope().launch {
                var message: Message? = null

                val response = client.submitFormWithBinaryData(prefix + url,
                    formData {
                        append("image", image, Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.PNG)
                            append(HttpHeaders.ContentDisposition, "filename=image.png")
                        })
                        body?.let { b ->
                            append("message", b.encode(), Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Application.Json)
                            })
                        }
                    }) {
                    basicAuth(username, password)
                    onUpload { bytesSentTotal, contentLength ->
                        console.log("$bytesSentTotal / $contentLength")
                    }
                }

                if (response.status.isSuccess()) {
                    message = Messages.decode(response.bodyAsText())
                }

                callback(message)
            }
        }
    }
}