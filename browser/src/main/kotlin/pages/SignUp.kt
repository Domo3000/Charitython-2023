package pages

import components.RoutePage
import react.FC
import react.Props
import react.dom.html.ReactHTML

object SignUpPage : RoutePage {
    override val route: String = "signUp"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Sign Up Table"
            }
        }
}
