package model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
open class Message {
    fun encode(): String = Messages.encode(this)
}

object Messages {
    private val json: Json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "class"
    }

    fun decode(s: String): Message? = try {
        json.decodeFromString(Message.serializer(), s)
    } catch (e: SerializationException) {
        null
    }

    fun encode(m: Message): String = json.encodeToString(Message.serializer(), m)
}