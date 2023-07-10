package model

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object Background : IntIdTable()  {
    val date = datetime("date")
    val fileName = varchar("fileName", 50)
}

class BackgroundDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BackgroundDao>(Background) {
        fun insert(date: LocalDateTime, fileName: String) = transaction { new {
            this.date = date
            this.fileName = fileName
        } }
        fun getLatest() = transaction {
            all().orderBy(Background.date to SortOrder.DESC).firstOrNull()
        }
    }

    var date by Background.date
    var fileName by Background.fileName

    fun toDTO(): BackgroundDTO = BackgroundDTO(fileName)
}