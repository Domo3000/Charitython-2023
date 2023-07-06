package utils

import model.Admin
import model.CleanupDay
import model.CleanupEvent
import model.CleanupDayResults
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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
            SchemaUtils.create(CleanupDay)
            SchemaUtils.create(CleanupEvent)
            SchemaUtils.create(CleanupDayResults)
        }
    }
}