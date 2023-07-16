package model

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import utils.toInstant

object CleanupDayResult : IntIdTable() {
    val cleanupDayId = integer("cleanupDayId").references(CleanupDay.id)
    var garbage = double("tonsOfGarbage")
    var participants = integer("participants")
}

class CleanupDayResultDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupDayResultDao>(CleanupDayResult) {
        fun insert(cleanupDayId: Int, garbage: Double, participants: Int) = transaction {
            new {
                this.cleanupDayId = cleanupDayId
                this.garbage = garbage
                this.participants = participants
            }
        }

        fun upsert(cleanupDayId: Int, garbage: Double, participants: Int) = transaction {
            getByIdCleanupDayId(cleanupDayId)?.let { result ->
                CleanupDayResult.update({ CleanupDayResult.cleanupDayId.eq(cleanupDayId) }) {
                    it[this.garbage] = result.garbage + garbage
                    it[this.participants] = result.participants + participants
                }
            } ?: run {
                new {
                    this.cleanupDayId = cleanupDayId
                    this.garbage = garbage
                    this.participants = participants
                }
            }
        }

        fun getByIdCleanupDayId(id: Int): CleanupDayResultDao? = transaction {
            find {
                CleanupDayResult.cleanupDayId.eq(id)
            }.firstOrNull()
        }
    }

    var cleanupDayId by CleanupDayResult.cleanupDayId
    var garbage by CleanupDayResult.garbage
    var participants by CleanupDayResult.participants

    fun toDTO(date: LocalDateTime): CleanupDayResultsDTO = CleanupDayResultsDTO(
        id.value,
        cleanupDayId,
        date.toInstant(),
        garbage,
        participants
    )
}