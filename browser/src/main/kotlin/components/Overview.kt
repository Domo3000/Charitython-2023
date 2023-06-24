package components

import emotion.react.css
import model.CleanupDayDTO
import pages.IndexPage
import react.*
import react.dom.html.ReactHTML
import react.useState
import utils.Style
import web.cssom.*
import utils.Requests
import web.cssom.Auto
import web.cssom.Clear
import web.cssom.px
import web.history.history

external interface OverviewProps : Props {
    var stateSetter: (String, OverviewPage) -> Unit
    var cleanupDay: CleanupDayDTO?
    var setCleanupDay: StateSetter<CleanupDayDTO?>
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
    val (cleanupDay, setCleanupDay) = useState<CleanupDayDTO?>(null)

    fun changeState(route: String, newState: OverviewPage) {
        history.replaceState(Unit, "", route)
        setPage(newState)
    }

    Header {
        fileName = cleanupDay?.fileName?.let { "/files/$it" }
        currentPage = page
        pageSetter = { route, newState -> changeState(route, newState) }
    }

    ReactHTML.div {
        ReactHTML.div {
            id = "content-holder"
            css {
                margin = Auto.auto
                maxWidth = 1000.px
                minHeight = 600.px
                paddingBottom = 200.px
                clear = Clear.left
            }
        }
    }

    page.component {
        stateSetter = { route, newState -> changeState(route, newState) }
        this.cleanupDay = cleanupDay
        this.setCleanupDay = setCleanupDay
    }

    Footer { stateSetter = { route, newState -> changeState(route, newState) } }

    useEffectOnce {
        Requests.getMessage("/data/cleanupDay") {
            setCleanupDay(it as? CleanupDayDTO)
        }
    }
}
