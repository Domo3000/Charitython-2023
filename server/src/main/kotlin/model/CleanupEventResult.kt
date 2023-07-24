package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object CleanupEventResult : IntIdTable() {
    val cleanupDayId = integer("cleanupDayId").references(CleanupDay.id)
    val emailAddress = varchar("emailAddress", 100)
    val organization = varchar("organization", 100)
    val partnerOrganization = varchar("partnerOrganization", 100).nullable()
    val province = varchar("province", 100)
    val location = varchar("location", 100)
    val zipCode = varchar("zipCode", 10)
    val numberOfParticipants = integer("numberOfParticipants")
    val totalWeight = double("totalWeight")
    val amountOfTrashBags = double("amountOfTrashBags").nullable()
    val cleanedAreaSize = varchar("cleanedAreaSize", 100).nullable()
    val cigaretteButtsCount = integer("cigaretteButtsCount").nullable()
    val canCount = integer("canCount").nullable()
    val petBottleCount = integer("petBottleCount").nullable()
    val glassBottleCount = integer("glassBottleCount").nullable()
    val hazardousWaste = varchar("hazardousWaste", 100).nullable()
    val strangeFinds = varchar("strangeFinds", 100).nullable()
    val miscellaneous = varchar("miscellaneous", 100).nullable()
    val wayOfRecognition = varchar("wayOfRecognition", 100)
    var exported = bool("exported")
}

class CleanupEventResultDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupEventResultDao>(CleanupEventResult) {
        fun insert(dto: CleanupEventResultDTO): CleanupEventResultDao = transaction {
            return@transaction new {
                cleanupDayId = dto.cleanupDayId
                emailAddress = dto.emailAddress
                organization = dto.organization
                partnerOrganization = dto.partnerOrganization
                province = dto.province
                location = dto.location
                zipCode = dto.zipCode
                numberOfParticipants = dto.numberOfParticipants
                totalWeight = dto.totalWeight
                amountOfTrashBags = dto.amountOfTrashBags
                cleanedAreaSize = dto.cleanedAreaSize
                cigaretteButtsCount = dto.cigaretteButtsCount
                canCount = dto.canCount
                petBottleCount = dto.petBottleCount
                glassBottleCount = dto.glassBottleCount
                hazardousWaste = dto.hazardousWaste
                strangeFinds = dto.strangeFinds
                miscellaneous = dto.miscellaneous
                wayOfRecognition = dto.wayOfRecognition
                exported = false
            }
        }

        fun getAllByCleanupDayId(cleanupDayId: Int): List<CleanupEventResultDao> = transaction {
            find {
                CleanupEventResult.cleanupDayId.eq(cleanupDayId)
            }.toList()
        }

        fun markAsExported(id: Int) = transaction {
            CleanupEventResult.update({ CleanupEventResult.id.eq(id) }) {
                it[exported] = true
            }
        }

        fun transformToCleanupDayResult() = transaction {
            all().toList().forEach { result ->
                CleanupDayResultDao.upsert(result.cleanupDayId, result.totalWeight / 1000, result.numberOfParticipants)
            }
        }

        fun getAll() = transaction {
            all().toList()
        }

        fun deleteAll() = transaction {
            CleanupEventResult.deleteAll()
        }

        fun deleteById(id: Int) = transaction {
            CleanupEventResult.deleteWhere { this.id.eq(id) }
        }

        fun deleteExported() = transaction {
            CleanupEventResult.deleteWhere { this.exported.eq(true) }
        }
    }

    var cleanupDayId by CleanupEventResult.cleanupDayId
    var emailAddress by CleanupEventResult.emailAddress
    var organization by CleanupEventResult.organization
    var partnerOrganization by CleanupEventResult.partnerOrganization
    var province by CleanupEventResult.province
    var location by CleanupEventResult.location
    var zipCode by CleanupEventResult.zipCode
    var numberOfParticipants by CleanupEventResult.numberOfParticipants
    var totalWeight by CleanupEventResult.totalWeight
    var amountOfTrashBags by CleanupEventResult.amountOfTrashBags
    var cleanedAreaSize by CleanupEventResult.cleanedAreaSize
    var cigaretteButtsCount by CleanupEventResult.cigaretteButtsCount
    var canCount by CleanupEventResult.canCount
    var petBottleCount by CleanupEventResult.petBottleCount
    var glassBottleCount by CleanupEventResult.glassBottleCount
    var hazardousWaste by CleanupEventResult.hazardousWaste
    var strangeFinds by CleanupEventResult.strangeFinds
    var miscellaneous by CleanupEventResult.miscellaneous
    var wayOfRecognition by CleanupEventResult.wayOfRecognition
    var exported by CleanupEventResult.exported

    fun toDTO(): CleanupEventResultDTO = CleanupEventResultDTO(
        cleanupDayId,
        emailAddress,
        organization,
        partnerOrganization,
        province,
        location,
        zipCode,
        numberOfParticipants,
        totalWeight,
        amountOfTrashBags,
        cleanedAreaSize,
        cigaretteButtsCount,
        canCount,
        petBottleCount,
        glassBottleCount,
        hazardousWaste,
        strangeFinds,
        miscellaneous,
        wayOfRecognition,
        id.value
    )
}