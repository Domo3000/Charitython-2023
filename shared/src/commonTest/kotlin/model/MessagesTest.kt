package model

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class MessagesTest {
    @Test
    fun serialize() {
        val messages: Set<Message> = setOf(
            CreateCleanupDay(Clock.System.now()),
            CleanupDayDTO(1, Clock.System.now(), "fileName")
        )

        messages.forEach {
            assertEquals(
                expected = it,
                actual = Messages.decode(Messages.encode(it))
            )
        }
    }
}