package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object CleanupEvent : IntIdTable() {
    val cleanupDayId = integer("cleanupDayId").references(CleanupDay.id)
    val firstName = varchar("firstName", 100)
    val lastName = varchar("lastName", 100)
    val emailAddress = varchar("emailAddress", 100)
    val organization = varchar("organization", 100)
    val websiteAddress = varchar("website", 200).nullable()
    val eventName = varchar("eventName", 100)
    val street = varchar("street", 100)
    val zipCode = varchar("zipCode", 10)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val startTime = varchar("startTime", 30)
    val endTime = varchar("endTime", 30)
    val description = varchar("description", 5000)
    val fileName = varchar("fileName", 50).nullable()
    var approved = bool("approved")
}

class CleanupEventDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupEventDao>(CleanupEvent) {
        fun insert(dto: CleanUpEventCreationDTO, fileName: String?): CleanupEventDao = transaction {
            return@transaction new {
                cleanupDayId = dto.cleanupDayId
                firstName = dto.firstName
                lastName = dto.lastName
                emailAddress = dto.emailAddress
                organization = dto.organization
                websiteAddress = dto.websiteAddress
                eventName = dto.eventName
                street = dto.street
                zipCode = dto.zipCode
                latitude = dto.latitude
                longitude = dto.longitude
                startTime = dto.startTime
                endTime = dto.endTime
                description = dto.description
                this.fileName = fileName
                approved = false
            }
        }

        fun getAll(): List<CleanupEventDao> = transaction {
            all().toList()
        }

        fun getAllByCleanupDayId(cleanupDayId: Int): List<CleanupEventDao> = transaction {
            find {
                CleanupEvent.cleanupDayId.eq(cleanupDayId)
            }.toList()
        }

        fun getById(id: Int): CleanupEventDao? = transaction {
            findById(id)
        }

        fun approve(id: Int) = transaction {
            CleanupEvent.update({ CleanupEvent.id.eq(id) }) {
                it[approved] = true
            }
        }

        fun delete(id: Int) = transaction {
            CleanupEvent.deleteWhere { this.id.eq(id) }
        }
    }

    var cleanupDayId by CleanupEvent.cleanupDayId
    var firstName by CleanupEvent.firstName
    var lastName by CleanupEvent.lastName
    var emailAddress by CleanupEvent.emailAddress
    var organization by CleanupEvent.organization
    var websiteAddress by CleanupEvent.websiteAddress
    var eventName by CleanupEvent.eventName
    var street by CleanupEvent.street
    var zipCode by CleanupEvent.zipCode
    var latitude by CleanupEvent.latitude
    var longitude by CleanupEvent.longitude
    var startTime by CleanupEvent.startTime
    var endTime by CleanupEvent.endTime
    var description by CleanupEvent.description
    var fileName by CleanupEvent.fileName
    var approved by CleanupEvent.approved

    fun toDTO(): CleanUpEventDTO = CleanUpEventDTO(
        id.value,
        cleanupDayId,
        firstName,
        lastName,
        emailAddress,
        organization,
        websiteAddress,
        eventName,
        street,
        zipCode,
        latitude,
        longitude,
        startTime,
        endTime,
        description,
        fileName,
        approved
    )
}