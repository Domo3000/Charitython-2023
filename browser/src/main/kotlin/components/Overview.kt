package components

import emotion.react.css
import pages.IndexPage
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import web.cssom.Auto
import web.cssom.Clear
import web.cssom.px
import web.history.history

external interface OverviewProps : Props {
    var stateSetter: (String, OverviewPage) -> Unit
}

interface OverviewPage {
    val component: FC<OverviewProps>
}

interface RoutePage : OverviewPage {
    val route: String
}

object NotFoundPage : OverviewPage {
    override val component = FC<Props> {
        ReactHTML.div {
            +"404 - Not Found"
        }
    }
}

fun overview(component: OverviewPage = IndexPage) = FC<Props> {
    val (page, setPage) = useState(component)

    fun changeState(route: String, newState: OverviewPage) {
        history.replaceState(Unit, "", route)
        setPage(newState)
    }

    Header {
        currentPage = page
        pageSetter = { route, newState -> changeState(route, newState) }
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

        page.component { stateSetter = { route, newState -> changeState(route, newState) } }
    }

    Footer { stateSetter = { route, newState -> changeState(route, newState) } }
}
