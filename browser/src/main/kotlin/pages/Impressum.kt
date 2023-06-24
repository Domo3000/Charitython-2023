package pages

import components.RoutePage
import react.FC
import react.Props
import react.dom.html.ReactHTML

object ImpressumPage : RoutePage {
    override val route: String = "impressum"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Impressum"
            }
        }
}
