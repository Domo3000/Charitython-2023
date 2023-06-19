package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateCleanupDay(val timestamp: Instant) : Message()

@Serializable
data class CleanupDayDTO(val id: Int, val timestamp: Instant) : Message()