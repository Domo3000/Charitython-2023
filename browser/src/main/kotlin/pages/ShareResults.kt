package pages

import components.RouteState
import react.FC
import react.Props
import react.dom.html.ReactHTML

object ShareResultsState : RouteState {
    override val route: String = "shareResults"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Share Results Table"
            }
        }
}
