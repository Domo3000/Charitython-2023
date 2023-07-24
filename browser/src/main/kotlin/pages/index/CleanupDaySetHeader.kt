package pages.index

import emotion.react.css
import kotlinx.datetime.toJSDate
import react.FC
import react.dom.html.ReactHTML
import css.Style
import utils.getMonthString
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.em

val CleanupDaySetHeader = FC<CleanupDayProps> { props ->
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
        }

        components.HeaderButton {
            text = "Cleanup finden"
            color = Style.pinkColor
        }
    }
}