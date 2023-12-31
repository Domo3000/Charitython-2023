package pages.index

import components.OverviewPage
import components.OverviewProps
import model.*
import react.*
import utils.Requests
import utils.Style
import web.cssom.*

object IndexCommons {
    val fontSize = 2.5.em
    val padding = Padding(0.em, 1.em, 0.em, 1.em)
    val textColor = Color(Style.whiteColor)
}

external interface CleanupDayProps : Props {
    var cleanupDay: CleanupDayDTO
}

external interface IndexProps : Props {
    var cleanupDay: CleanupDayDTO?
    var results: CleanupDayResultsDTO?
    var background: BackgroundDTO?
    var events: List<CleanUpEventDTO>
    var stateSetter: (String, OverviewPage) -> Unit
}

object IndexPage : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val (results, setResults) = useState<CleanupDayResultsDTO?>(null)
            val (background, setBackground) = useState<BackgroundDTO?>(null)
            val (events, setEvents) = useState<List<CleanUpEventDTO>>(emptyList())

            Index {
                cleanupDay = props.cleanupDay
                stateSetter = props.stateSetter
                this.results = results
                this.background = background
                this.events = events
            }

            useEffectOnce {
                Requests.getMessage("/data/previousCleanupDayResults") { message ->
                    setResults((message as CleanupDayResultsDTO))
                }
                Requests.getMessage("/data/background") { message ->
                    setBackground((message as BackgroundDTO))
                }
                props.cleanupDay?.let { cleanupDay ->
                    Requests.getMessage("/data/cleanupEvents/${cleanupDay.id}") { message ->
                        setEvents((message as CleanUpEvents).events.filter { it.approved })
                    }
                }
            }
        }
}
