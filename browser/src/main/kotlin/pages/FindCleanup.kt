package pages

import components.OverviewPage
import components.OverviewProps
import components.RoutePage
import css.Classes
import css.Style
import emotion.react.css
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.react.reactWrapper
import model.CleanUpEventDTO
import model.CleanUpEvents
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.MapUtils
import utils.Requests
import web.cssom.ClassName
import web.cssom.Float
import web.cssom.pct
import web.cssom.px
import web.dom.document

private external interface EventProps : Props {
    var cleanUpEvent: CleanUpEventDTO
    var stateSetter: (String, OverviewPage) -> Unit
}

private val CleanupDetails = FC<EventProps> { props ->
    val cleanUpEvent = props.cleanUpEvent

    ReactHTML.div {
        css {
            width = 25.pct
            border = Style.border
            float = Float.left
        }

        ReactHTML.img {
            src = cleanUpEvent.fileName.let { fileName -> "/files/$fileName" }
            css {
                width = 100.pct
                borderBottom = Style.border
            }
            onClick = {
                props.stateSetter("/details/${cleanUpEvent.id}", DetailsPage(cleanUpEvent.id))
            }
        }

        ReactHTML.div {
            css {
                padding = 10.px
            }

            ReactHTML.h1 {
                +cleanUpEvent.eventName
            }

            // TODO wrap in Table
            IconText {
                icon = "clock"
                text = "${cleanUpEvent.startTime} Uhr - ${cleanUpEvent.endTime} Uhr"
            }

            ReactHTML.br {}

            IconText {
                icon = "person"
                text = cleanUpEvent.organization
            }

            ReactHTML.br {}

            ReactHTML.div {
                ReactHTML.i {
                    className = ClassName("fa-solid fa-link")
                }
                ReactHTML.a {
                    css {
                        paddingLeft = 15.px
                    }
                    +cleanUpEvent.websiteAddress
                    href = cleanUpEvent.websiteAddress
                }
            }

            ReactHTML.p {
                +cleanUpEvent.description
            }
        }
    }
}

object FindCleanup : RoutePage {
    override val route: String = "cleanupFinden" // TODO use everywhere same style for routes
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val (events, setEvents) = useState<List<CleanUpEventDTO>>(emptyList())

            ReactHTML.div {
                css(Classes.limitedWidth)

                ReactHTML.h1 {
                    +"Cleanup finden"
                }

                if (events.isNotEmpty()) {
                    ReactHTML.div {
                        css {
                            height = 800.px
                        }
                        id = "map-holder"
                    }
                } else {
                    ReactHTML.p {
                        +"Es wurden leider noch keine Cleanup Events für den nächsten Cleanup Day erstellt!"
                    }
                }

                ReactHTML.div {

                    ReactHTML.div {
                        css {
                            marginTop = 50.px
                        }

                        events.forEach { event ->
                            CleanupDetails {
                                cleanUpEvent = event
                                stateSetter = props.stateSetter
                            }
                        }
                    }
                }
            }

            useEffect(events) {
                if (events.isNotEmpty()) {
                    hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
                        val map = MapUtils.map()

                        events.forEach { event ->
                            val marker = LeafletObjectFactory.marker(LatLng(event.latitude, event.longitude)) {
                                title = event.eventName
                                icon = LeafletObjectFactory.icon {
                                    iconUrl = "/static/logo-oval.png"
                                    iconSize = Point(25, 25, true)
                                }
                            }

                            marker.on(
                                "click",
                                { props.stateSetter("/details/${event.id}", DetailsPage(event.id)) })

                            marker.addTo(map)
                        }
                    }.create())
                }
            }

            useEffectOnce {
                props.cleanupDay?.let { cleanupDay ->
                    Requests.getMessage("/data/cleanupEvents/${cleanupDay.id}") { message ->
                        setEvents((message as CleanUpEvents).events.filter { it.approved })
                    }
                }
            }
        }
}

