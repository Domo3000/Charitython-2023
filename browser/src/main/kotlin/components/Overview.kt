package components

import emotion.react.css
import pages.IndexState
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import web.cssom.Auto
import web.cssom.Clear
import web.cssom.px
import web.history.history

external interface OverviewProps : Props {
    var stateSetter: (String, OverviewState) -> Unit
}

interface OverviewState {
    val component: FC<OverviewProps>
}

interface RouteState : OverviewState {
    val route: String
}

object NotFoundState : OverviewState {
    override val component = FC<Props> {
        ReactHTML.div {
            +"404 - Not Found"
        }
    }
}

fun overview(component: OverviewState = IndexState) = FC<Props> {
    val (state, setState) = useState(component)

    fun changeState(route: String, newState: OverviewState) {
        history.replaceState(Unit, "", route)
        setState(newState)
    }

    Header {
        currentState = state
        stateSetter = { route, newState -> changeState(route, newState) }
    }

    ReactHTML.div {
        id = "content-holder"
        css {
            margin = Auto.auto
            maxWidth = 1000.px
            minHeight = 600.px
            paddingBottom = 200.px
            clear = Clear.left
        }

        state.component { stateSetter = { route, newState -> changeState(route, newState) } }
    }

    Footer { stateSetter = { route, newState -> changeState(route, newState) } }
}
