package pages

import components.OverviewPage
import components.OverviewProps
import components.RoutePage
import css.ClassNames
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
import web.cssom.*
import web.dom.document

private external interface EventProps : Props {
    var cleanUpEvent: CleanUpEventDTO
    var stateSetter: (String, OverviewPage) -> Unit
}

private val CleanupDetails = FC<EventProps> { props ->
    val cleanUpEvent = props.cleanUpEvent

    ReactHTML.a {
        href = "/details/${cleanUpEvent.id}"
        css(ClassNames.phoneFullWidth) {
            width = 23.pct
            borderRadius = 25.px
            float = Float.left
            margin = 1.pct
            textDecoration = None.none
        }
        onClick = {
            it.preventDefault()
            props.stateSetter("/details/${cleanUpEvent.id}", DetailsPage(cleanUpEvent.id))
        }

        ReactHTML.img {
            src = cleanUpEvent.fileName.let { fileName -> "/files/$fileName" }
            css {
                width = 100.pct
                aspectRatio = AspectRatio(1.0)
                objectFit = ObjectFit.cover
                borderTopLeftRadius = 10.px
                borderTopRightRadius = 10.px
            }
        }

        ReactHTML.div {
            css {
                paddingTop = 10.px
            }

            ReactHTML.h3 {
                css {
                    overflowWrap = OverflowWrap.breakWord
                }
                +cleanUpEvent.eventName
            }
        }

        ReactHTML.a {
            css {
                color = Style.pinkColor
            }
            +"Weiterlesen »"
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
                    MapUtils.MapHolder { }
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
                                    iconUrl = "/static/WCD-logo.png"
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

