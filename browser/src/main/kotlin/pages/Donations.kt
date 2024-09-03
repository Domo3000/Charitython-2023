package pages

import components.OverviewProps
import components.RoutePage
import css.Classes
import emotion.react.css
import react.FC
import react.dom.html.ReactHTML
import web.cssom.None
import web.cssom.pct

object Donations : RoutePage {
    override val route: String = "spenden"
    override val component: FC<OverviewProps>
        get() = FC {
            ReactHTML.div {
                css(Classes.limitedWidth)

                ReactHTML.h1 {
                    +"Spenden"
                }

                ReactHTML.iframe {
                    css {
                        width = 100.pct
                        borderStyle = None.none
                    }
                    src = "/donations"
                }
            }
        }
}
