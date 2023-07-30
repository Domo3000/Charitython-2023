package pages.index

import components.OverviewPage
import emotion.react.css
import kotlinx.datetime.toJSDate
import react.FC
import react.dom.html.ReactHTML
import css.Style
import model.CleanupDayDTO
import pages.FindCleanup
import pages.RegisterCleanupEvent
import react.Props
import utils.getMonthString
import web.cssom.*

external interface CleanupDaySetHeaderProps : Props {
    var cleanupDay: CleanupDayDTO
    var stateSetter: (String, OverviewPage) -> Unit
}

val CleanupDaySetHeader = FC<CleanupDaySetHeaderProps> { props ->
    val date = props.cleanupDay.timestamp.toJSDate()

    ReactHTML.h1 {
        css {
            fontSize = 2.2.em
            color = Style.pinkColor
        }

        +"Am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} ist wieder World Cleanup Day!"
    }

    ReactHTML.h2 {
        css {
            fontSize = 3.em
            color = Style.pinkColor
        }

        +"In ganz Österreich wird aufgeräumt. Gemeinsam schaffen wir eine saubere Umwelt."
    }

    ReactHTML.div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
        }

        components.HeaderButton {
            text = "Cleanup anmelden"
            color = Style.yellowColor
            link = "/${RegisterCleanupEvent.route}"
            disabled = false
            width = 110.0.px
            onClick = {
                props.stateSetter("/${RegisterCleanupEvent.route}", RegisterCleanupEvent)
            }
        }

        components.HeaderButton {
            text = "Cleanup finden"
            color = Style.pinkColor
            link = "/${FindCleanup.route}"
            disabled = false
            width = 110.0.px
            onClick = {
                props.stateSetter("/${FindCleanup.route}", FindCleanup)
            }
        }
    }
}