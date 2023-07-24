package pages.index

import css.Style
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.em

val CleanupDayNotSetHeader = FC<Props> {
    ReactHTML.h1 {
        css {
            fontSize = 2.2.em
            color = Style.pinkColor
        }

        +"Wir freuen uns sehr, dass du beim nächsten World Cleanup Day dabei sein möchtest!"
    }
    ReactHTML.h2 {
        css {
            fontSize = 3.em
            color = Style.pinkColor
        }

        +"Leider ist dieser noch nicht festgelegt worden!"
    }
}