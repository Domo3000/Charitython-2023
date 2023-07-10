package components

import css.Classes
import emotion.react.css
import pages.ImpressumPage
import react.FC
import react.dom.html.ReactHTML
import web.cssom.Clear
import web.cssom.px

val Footer = FC<OverviewProps> { props ->
    ReactHTML.div {
        css {
            paddingTop = 100.px
            clear = Clear.left
        }
        ReactHTML.h6 {
            css(Classes.centered)
            onClick = { props.stateSetter("/${ImpressumPage.route}", ImpressumPage) }
            +"Impressum"
        }
    }
}