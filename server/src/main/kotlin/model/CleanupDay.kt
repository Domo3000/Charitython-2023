package model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import utils.toInstant

object CleanupDay : IntIdTable() {
    val date = datetime("date")
}

class CleanupDayDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupDayDao>(CleanupDay) {
        fun insert(new: LocalDateTime) = transaction { new { date = new } }
        fun getNext() = transaction {
            find {
                CleanupDay.date.greater(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }.orderBy(CleanupDay.date to SortOrder.ASC).firstOrNull()
        }
    }

    var date by CleanupDay.date

    fun toDTO(): CleanupDayDTO = CleanupDayDTO(id.value, date.toInstant())
}