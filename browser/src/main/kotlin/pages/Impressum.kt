package pages

import components.RouteState
import react.FC
import react.Props
import react.dom.html.ReactHTML

object ImpressumState : RouteState {
    override val route: String = "impressum"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Impressum"
            }
        }
}
