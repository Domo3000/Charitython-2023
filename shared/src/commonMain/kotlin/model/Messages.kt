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
data class BackgroundDTO(val fileName: String) : Message()

@Serializable
data class CleanupDayResultsDTO(
    val id: Int,
    val cleanupDayId: Int,
    val timestamp: Instant,
    val garbage: Double,
    val participants: Int
) : Message()

@Serializable
object DeletedCleanupDay : Message()

@Serializable
data class CleanUpEventCreationDTO(
    val cleanupDayId: Int,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val organization: String,
    val websiteAddress: String?,
    val eventName: String,
    val street: String,
    val zipCode: String,
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
    val websiteAddress: String?,
    val eventName: String,
    val street: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: String,
    val endTime: String,
    val description: String,
    val fileName: String?,
    val approved: Boolean
) : Message()

@Serializable
data class CleanUpEvents(
    val events: List<CleanUpEventDTO>
) : Message()

@Serializable
data class Location(
    val latitude: String,
    val longitude: String,
    val street: String,
    val zipCode: String
) : Message()

@Serializable
data class CleanupEventResultDTO(
    val cleanupDayId: Int,
    val emailAddress: String,
    val organization: String,
    val partnerOrganization: String?,
    val province: String,
    val location: String,
    val zipCode: String,
    val numberOfParticipants: Int,
    val totalWeight: Double,
    val amountOfTrashBags: Double?,
    val cleanedAreaSize: String?,
    val cigaretteButtsCount: Int?,
    val canCount: Int?,
    val petBottleCount: Int?,
    val glassBottleCount: Int?,
    val hazardousWaste: String?,
    val strangeFinds: String?,
    val miscellaneous: String?,
    val wayOfRecognition: String,
    val id: Int? = null
) : Message()

@Serializable
data class CleanupEventResultsDTO(
    val results: List<CleanupEventResultDTO>
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