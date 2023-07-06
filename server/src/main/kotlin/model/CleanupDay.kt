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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import utils.toInstant

object CleanupDay : IntIdTable() {
    val date = datetime("date")
    val fileName = varchar("fileName", 50)
}

class CleanupDayDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupDayDao>(CleanupDay) {
        fun insert(date: LocalDateTime, fileName: String) = transaction { new {
            this.date = date
            this.fileName = fileName
        } }
        fun getNext() = transaction {
            find {
                CleanupDay.date.greater(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }.orderBy(CleanupDay.date to SortOrder.ASC).firstOrNull()
        }
        fun getLast() = transaction {
            find {
                CleanupDay.date.less(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }.orderBy(CleanupDay.date to SortOrder.DESC).firstOrNull()
        }
        fun deleteById(id: Int) = transaction {
            CleanupDay.deleteWhere { CleanupDay.id eq id } // TODO cascade
        }
    }

    var date by CleanupDay.date
    var fileName by CleanupDay.fileName

    fun toDTO(): CleanupDayDTO = CleanupDayDTO(id.value, date.toInstant(), fileName)
}