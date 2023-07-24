package pages.index

import components.OverviewPage
import components.OverviewProps
import css.Classes
import emotion.react.css
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.react.reactWrapper
import model.*
import pages.DetailsPage
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.MapUtils
import utils.Requests
import utils.Style
import web.cssom.*
import web.dom.document

object IndexCommons {
    val fontSize = 2.5.em
    val padding = Padding(0.em, 1.em, 0.em, 1.em)
    val textColor = Color(Style.whiteColor)
}

external interface CleanupDayProps : Props {
    var cleanupDay: CleanupDayDTO
}

object IndexPage : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val (results, setResults) = useState<CleanupDayResultsDTO?>(null)
            val (background, setBackground) = useState<BackgroundDTO?>(null)
            val (events, setEvents) = useState<List<CleanUpEventDTO>>(emptyList())

            ReactHTML.div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.spaceEvenly
                    backgroundImage = url(background?.fileName?.let { "/files/$it" } ?: "/static/background.jpg")
                    backgroundRepeat = BackgroundRepeat.noRepeat
                    backgroundSize = BackgroundSize.cover
                    backgroundAttachment = BackgroundAttachment.fixed
                    height = 100.vh
                }

                props.cleanupDay?.let { cleanupDay ->
                    CleanupDaySetHeader {
                        this.cleanupDay = cleanupDay
                    }
                } ?: run {
                    CleanupDayNotSetHeader { }
                }
            }

            props.cleanupDay?.let { cleanupDay ->
                CleanupDaySetBody {
                    this.cleanupDay = cleanupDay
                }
            }

            ReactHTML.div {
                css {
                    margin = Auto.auto
                    maxWidth = 1000.px
                    if (events.isEmpty()) {
                        display = None.none
                    } else {
                        paddingTop = 10.px
                        minHeight = 600.px
                    }
                }
                MapUtils.MapHolder { }
            }

            results?.let { previousCleanupResults ->
                CleanupDayResults {
                    this.results = previousCleanupResults
                }
            }

            useEffect(events) {
                if(events.isNotEmpty()) {
                    hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
                        val map = MapUtils.map()

                        events.forEach { event ->
                            val marker = MapUtils.marker(
                                coordinates = LatLng(event.latitude, event.longitude),
                                title = event.eventName
                            )

                            marker.on(
                                "click",
                                { props.stateSetter("/details/${event.id}", DetailsPage(event.id)) })

                            marker.addTo(map)
                        }
                    }.create())
                }
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
