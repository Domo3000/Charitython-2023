package model

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object FinishedCleanupDay : IntIdTable() {
    val date = datetime("date")
    val garbage = double("tonsOfGarbage")
    val participants = integer("participants")
}

class FinishedCleanupDayDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FinishedCleanupDayDao>(FinishedCleanupDay) {
        fun insert(date: LocalDateTime, garbage: Double, participants: Int) = transaction { new {
            this.date = date
            this.garbage = garbage
            this.participants = participants
        } }
        fun getLast() = transaction {
            all().orderBy(FinishedCleanupDay.date to SortOrder.DESC).firstOrNull()
        }
    }

    var date by FinishedCleanupDay.date
    var garbage by FinishedCleanupDay.garbage
    var participants by FinishedCleanupDay.participants
}