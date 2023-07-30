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

                val lines = listOf(
                    "Green Heroes Austria",
                    "Lerchenfelder Straße 44",
                    "A-1080 Wien",
                    "Email: office@greenheroes.at",
                    "Web: www.greenheroes.at"
                )

                lines.forEach {
                    ReactHTML.p {
                        +it
                    }
                }

                ReactHTML.div {
                    css(Classes.centered)
                    ReactHTML.a {
                        href = "https://greenheroes.at/impressum/"
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
}
