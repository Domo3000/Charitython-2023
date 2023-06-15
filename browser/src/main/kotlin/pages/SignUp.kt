package pages

import components.RouteState
import react.FC
import react.Props
import react.dom.html.ReactHTML

object SignUpState : RouteState {
    override val route: String = "signUp"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Sign Up Table"
            }
        }
}
