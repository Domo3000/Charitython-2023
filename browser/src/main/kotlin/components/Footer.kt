package components

import css.Classes
import emotion.react.css
import pages.ImpressumState
import react.FC
import react.dom.html.ReactHTML
import web.cssom.Clear

val Footer = FC<OverviewProps> { props ->
    ReactHTML.div {
        css {
            clear = Clear.left
        }
        ReactHTML.h6 {
            css(Classes.centered)
            onClick = { props.stateSetter("/${ImpressumState.route}", ImpressumState) }
            +"Impressum"
        }
    }
}