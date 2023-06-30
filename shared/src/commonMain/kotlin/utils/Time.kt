package utils

import kotlinx.datetime.*

fun Instant.toLocalDateTime() = this.toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.toInstant() = this.toInstant(TimeZone.currentSystemDefault())