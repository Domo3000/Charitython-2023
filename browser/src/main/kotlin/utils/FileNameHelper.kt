package utils

object Defaults {
    const val defaultEventPicture = "event-default.png"
}

fun getFileName(maybeFileName: String?, default: String): String =
    maybeFileName?.let { "/files/$it" } ?: "/static/$default"