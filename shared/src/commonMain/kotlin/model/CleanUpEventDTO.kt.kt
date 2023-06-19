package model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CleanUpEventDTO(
    val cleanupDayId: Int,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val organization: String,
    val websiteAddress: String,
    val eventName: String,
    val street: String,
    val zipCode: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val description: String,
    val image: ByteArray
) : Message()