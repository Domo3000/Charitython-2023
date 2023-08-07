package pages.index

import emotion.react.css
import kotlinx.datetime.toJSDate
import kotlinx.datetime.toKotlinInstant
import model.CleanupDayDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

external interface TimerProps : Props {
    var color: Color
    var text: String
}

//  TODO mobile design
val Timer = FC<TimerProps> { props ->
    ReactHTML.div {
        css {
            width = 33.33.pct
            float = Float.left
        }
        ReactHTML.div {
            css {
                backgroundColor = props.color
                color = css.Style.whiteColor
                fontSize = 2.em
                textAlign = TextAlign.center
                margin = Auto.auto
                padding = 1.em
                maxWidth = 200.px
            }
            +props.text
        }
    }
}

external interface CleanupTimerProps : Props {
    var cleanupDay: CleanupDayDTO
}

val CleanupDayTimer = FC<CleanupTimerProps> { props ->
    val date = props.cleanupDay.timestamp.toJSDate()

    ReactHTML.div {
        val difference = date.toKotlinInstant().minus(kotlinx.datetime.Clock.System.now())
        val daysDifference = difference.inWholeDays
        val hoursDifference = difference.minus(daysDifference.days).inWholeHours
        val minuteDifference = difference.minus(daysDifference.days).minus(hoursDifference.hours).inWholeMinutes

        Timer {
            color = css.Style.yellowColor
            text = "$daysDifference Tage"
        }
        Timer {
            color = css.Style.pinkColor
            text = "$hoursDifference Stunden"
        }
        Timer {
            color = css.Style.blueColor
            text = "$minuteDifference Minuten"
        }
    }
}