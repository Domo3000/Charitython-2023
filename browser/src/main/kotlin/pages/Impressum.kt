package pages

import components.RoutePage
import css.Classes
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML
import web.cssom.ObjectFit
import web.cssom.px
import web.location.location

object ImpressumPage : RoutePage {
    override val route: String = "impressum"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                css(Classes.limitedWidth)

                ReactHTML.h1 {
                    +"Impressum"
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
}
