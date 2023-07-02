package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateShareResultsEntity(
    val timestamp: Instant,
    val mail: String,
    val entity: String,
    val province: String,
    val postCode: String,
    val location: String,
    val dateOfCleanup: Instant,
    val amountOfParticipants: Int,
    val totalWeight: Int,
    val amountOfTrashBags: Int,
    val partnerOrganisation: String,
    val cleanedAreaSize: String,
    val cigaretteButtsCount: Int,
    val canCount: Int,
    val petBottleCount: Int,
    val glassBottleCount: Int,
    val hazardousWaste: String,
    val strangeFinds: String,
    val miscellaneous: String,
    val wayOfRecognition: String
) : Message()

@Serializable
data class ShareResultsEntity(
    val id: Int,
    val timestamp: Instant,
    val mail: String,
    val entity: String,
    val province: String,
    val postCode: String,
    val location: String,
    val dateOfCleanup: Instant,
    val amountOfParticipants: Int,
    val totalWeight: Int,
    val amountOfTrashBags: Int,
    val partnerOrganisation: String,
    val cleanedAreaSize: String,
    val cigaretteButtsCount: Int,
    val canCount: Int,
    val petBottleCount: Int,
    val glassBottleCount: Int,
    val hazardousWaste: String,
    val strangeFinds: String,
    val miscellaneous: String,
    val wayOfRecognition: String
) : Message()
