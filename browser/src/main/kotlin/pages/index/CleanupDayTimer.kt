package pages.index

import css.Style
import css.responsive
import emotion.react.css
import kotlinx.datetime.toJSDate
import kotlinx.datetime.toKotlinInstant
import model.CleanupDayDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import web.cssom.*
import web.timers.Timeout
import web.timers.clearTimeout
import web.timers.setTimeout
import kotlin.js.Date
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
            responsive(desktop = {
                width = 33.33.pct
                float = Float.left
            }, mobile = {
                marginTop = 10.px
                marginBottom = 10.px
            })()
        }
        ReactHTML.div {
            css {
                backgroundColor = props.color
                color = Style.whiteColor
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

private fun Date.getDifference() = this.toKotlinInstant().minus(kotlinx.datetime.Clock.System.now())

val CleanupDayTimer = FC<CleanupTimerProps> { props ->
    val date = props.cleanupDay.timestamp.toJSDate()
    val (difference, setDifference) = useState(date.getDifference())

    var timeout: Timeout? = null

    fun startTimeoutLoop() {
        timeout = setTimeout({
            setDifference(date.getDifference())
            startTimeoutLoop()
        }, 60000)
    }

    ReactHTML.div {
        val daysDifference = difference.inWholeDays
        val hoursDifference = difference.minus(daysDifference.days).inWholeHours
        val minuteDifference = difference.minus(daysDifference.days).minus(hoursDifference.hours).inWholeMinutes

        Timer {
            color = Style.yellowColor
            text = "$daysDifference Tage"
        }
        Timer {
            color = Style.pinkColor
            text = "$hoursDifference Stunden"
        }
        Timer {
            color = Style.blueColor
            text = "$minuteDifference Minuten"
        }
    }

    useEffectOnce {
        startTimeoutLoop()

        cleanup {
            timeout?.let { clearTimeout(it) }
        }
    }
}