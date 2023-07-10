package components

import emotion.react.css
import io.kvision.MapsModule
import model.CleanupDayDTO
import pages.index.IndexPage
import react.*
import react.dom.html.ReactHTML
import utils.Requests
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

fun overview(component: OverviewPage = IndexPage) = FC<Props> {
    val (page, setPage) = useState(component)
    val (cleanupDay, setCleanupDay) = useState<CleanupDayDTO?>(null)

    fun changeState(route: String, newState: OverviewPage) {
        history.pushState(Unit, "", route)
        setPage(newState)
    }

    Header {
        fileName = cleanupDay?.fileName?.let { "/files/$it" }
        currentPage = page
        pageSetter = { route, newState -> changeState(route, newState) }
    }

    ReactHTML.div {
        id = "content-holder"
        css {
            minHeight = 600.px
            clear = Clear.left
        }

        page.component {
            stateSetter = { route, newState -> changeState(route, newState) }
            this.cleanupDay = cleanupDay
            this.setCleanupDay = setCleanupDay
        }
    }

    Footer { stateSetter = { route, newState -> changeState(route, newState) } }

    useEffectOnce {
        MapsModule.initialize()

        Requests.getMessage("/data/cleanupDay") {
            setCleanupDay(it as? CleanupDayDTO)
        }
    }
}
