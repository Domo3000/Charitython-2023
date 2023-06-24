package utils

import kotlin.js.Date

fun Date.getMonthString(): String {
    return when (this.getMonth()) {
        0 -> "Jänner"
        1 -> "Februar"
        2 -> "März"
        3 -> "April"
        4 -> "Mai"
        5 -> "Juni"
        6 -> "Juli"
        7 -> "August"
        8 -> "September"
        9 -> "Oktober"
        10 -> "November"
        else -> "Dezember"
    }
}