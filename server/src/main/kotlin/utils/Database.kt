package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object Admin : IntIdTable() {
    val password = varchar("password", 10)
}

class AdminDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AdminDao>(Admin) {
        fun insert(new: String) = transaction { new { password = new } }
        fun getNext() = transaction {
            val max = Admin.slice(Admin.id.max()).selectAll()

            find {
                Admin.id.eq(wrapAsExpression(max))
            }.firstOrNull()
        }
    }

    var password by Admin.password
}

object CleanupDays : IntIdTable() {
    val date = datetime("date")
}

class CleanupDaysDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CleanupDaysDao>(CleanupDays) {
        fun insert(new: LocalDateTime) = transaction { new { date = new } }
        fun getNext() = transaction {
            find {
                CleanupDays.date.greater(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }.orderBy(CleanupDays.date to SortOrder.ASC).firstOrNull()
        }
    }

    var date by CleanupDays.date
}

class Connection(url: String, user: String, password: String) {
    init {
        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )
        transaction {
            SchemaUtils.create(Admin)
            SchemaUtils.create(CleanupDays)
        }
    }
}