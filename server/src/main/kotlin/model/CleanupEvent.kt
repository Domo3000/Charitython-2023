package model

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

object CleanupEvent : IntIdTable() {
    val cleanupDayId = integer("cleanupDayId").references(CleanupDay.id)
    val firstName = varchar("firstName", 100)
    val lastName = varchar("lastName", 100)
    val emailAddress = varchar("emailAddress", 100)
    val organization = varchar("organization", 100)
    val websiteAddress = varchar("website", 200)
    val eventName = varchar("eventName", 100)
    val street = varchar("street", 100)
    val zipCode = varchar("zipCode", 10)
    val startTime = datetime("startTime")
    val endTime = datetime("endTime")
    val description = varchar("description", 500)
    val image = blob("image")
}


class CleanupEventDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupEventDao>(CleanupEvent) {
        fun insert(
            cleanupDayId: Int,
            firstName: String,
            lastName: String,
            emailAddress: String,
            organization: String,
            websiteAddress: String,
            eventName: String,
            street: String,
            zipCode: String,
            startTime: LocalDateTime,
            endTime: LocalDateTime,
            description: String,
            image: ExposedBlob
        ) = transaction {
            new {
                this.cleanupDayId = cleanupDayId
                this.firstName = firstName
                this.lastName = lastName
                this.emailAddress = emailAddress
                this.organization = organization
                this.websiteAddress = websiteAddress
                this.eventName = eventName
                this.street = street
                this.zipCode = zipCode
                this.startTime = startTime
                this.endTime = endTime
                this.description = description
                this.image = image
            }
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
    var startTime by CleanupEvent.startTime
    var endTime by CleanupEvent.endTime
    var description by CleanupEvent.description
    var image by CleanupEvent.image
}