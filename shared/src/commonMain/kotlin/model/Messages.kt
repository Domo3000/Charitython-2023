package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
sealed class Message {
    fun encode(): String = Messages.encode(this)
}

@Serializable
data class IdMessage(val id: Int) : Message()

@Serializable
data class CreateCleanupDay(val timestamp: Instant) : Message()

@Serializable
data class CleanupDayDTO(val id: Int, val timestamp: Instant, val fileName: String) : Message()

@Serializable
object DeletedCleanupDay : Message()

@Serializable
data class CleanUpEventCreationDTO(
    val cleanupDayId: Int,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val organization: String,
    val websiteAddress: String,
    val eventName: String,
    //val street: String,
    //val zipCode: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: String,
    val endTime: String,
    val description: String,
) : Message()

@Serializable
data class CleanUpEventDTO(
    val id: Int,
    val cleanupDayId: Int,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val organization: String,
    val websiteAddress: String,
    val eventName: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: String,
    val endTime: String,
    val description: String,
    val fileName: String
) : Message()

@Serializable
data class CleanUpEvents(
    val events: List<CleanUpEventDTO>
) : Message()

object Messages {
    private val json: Json = Json {
        prettyPrint = true
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