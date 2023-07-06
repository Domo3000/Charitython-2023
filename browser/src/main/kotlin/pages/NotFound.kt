package pages

import components.OverviewPage
import css.Classes
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.ObjectFit
import web.cssom.px
import web.location.location

val NotFound = FC<Props> {
    ReactHTML.div {
        css(Classes.limitedWidth)

        ReactHTML.h1 {
            +"Error 404"
        }

        ReactHTML.p {
            +"Etwas konnte nicht gefunden werden"
        }

        ReactHTML.div {
            css(Classes.centered)
            ReactHTML.img {
                css {
                    maxWidth = 300.px
                    objectFit = ObjectFit.contain
                }
                src = "/static/logo-oval.png"
                onClick = {
                    location.assign("/")
                }
            }
        }
    }
}

object NotFoundPage : OverviewPage {
    override val component = FC<Props> {
        NotFound { }
    }
}