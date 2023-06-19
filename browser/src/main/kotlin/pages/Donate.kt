package pages

import components.RouteState
import react.FC
import react.Props
import react.dom.html.ReactHTML

object DonateState : RouteState {
    override val route: String = "organize"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Donate"
            }
        }
}