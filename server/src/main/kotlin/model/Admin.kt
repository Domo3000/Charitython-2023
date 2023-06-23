package model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.wrapAsExpression

object Admin : IntIdTable() {
    val password = varchar("password", 10)
}

class AdminDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AdminDao>(Admin) {
        fun insert(new: String) = transaction { new { password = new } }
        fun getLatest() = transaction {
            val max = Admin.slice(Admin.id.max()).selectAll()

            find {
                Admin.id.eq(wrapAsExpression(max))
            }.firstOrNull()
        }
    }

    var password by Admin.password
}
