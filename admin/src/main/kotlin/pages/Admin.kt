package pages

import components.RouteState
import react.FC
import react.Props
import react.dom.html.ReactHTML

object AdminState : RouteState {
    override val route: String = "admin"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Admin"
            }
        }
}
