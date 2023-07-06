package model

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import utils.toInstant

object CleanupDayResults : IntIdTable() {
    val cleanupDayId = integer("cleanupDayId").references(CleanupDay.id)
    val garbage = double("tonsOfGarbage")
    val participants = integer("participants")
}

class CleanupDayResultsDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupDayResultsDao>(CleanupDayResults) {
        fun insert(cleanupDayId: Int, garbage: Double, participants: Int) = transaction {
            new {
                this.cleanupDayId = cleanupDayId
                this.garbage = garbage
                this.participants = participants
            }
        }

        fun getByIdCleanupDayId(id: Int): CleanupDayResultsDao? = transaction {
            find {
                CleanupDayResults.cleanupDayId.eq(id)
            }.firstOrNull()
        }
    }

    var cleanupDayId by CleanupDayResults.cleanupDayId
    var garbage by CleanupDayResults.garbage
    var participants by CleanupDayResults.participants

    fun toDTO(date: LocalDateTime): CleanupDayResultsDTO = CleanupDayResultsDTO(
        id.value,
        cleanupDayId,
        date.toInstant(),
        garbage,
        participants
    )
}