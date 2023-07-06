package pages

import components.OverviewProps
import components.RoutePage
import css.Classes
import emotion.react.css
import io.kvision.MapsModule
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.react.reactWrapper
import model.CleanUpEventDTO
import model.CleanUpEvents
import org.w3c.dom.HTMLElement
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.Requests
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.px
import web.dom.document
import kotlinx.browser.document as ktxDocument

object SignUpPage : RoutePage {
    override val route: String = "cleanupFinden" // TODO
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val (events, setEvents) = useState<List<CleanUpEventDTO>>(emptyList())

            ReactHTML.div {
                css(Classes.limitedWidth)
                +"TODO"

                if(events.isNotEmpty()) {
                    ReactHTML.div {
                        css {
                            height = 800.px
                        }
                        id = "map-holder"
                    }
                } else {
                    +"TODO some message that no events are set"
                }
            }


            ReactHTML.div {
                css (Classes.limitedWidth)

                ReactHTML.div {
                    css {
                        marginTop = 50.px
                        display = Display.flex

                    }


                    events.forEach { event ->
                        ReactHTML.div {

                            css {
                                display = Display.flex
                                flexDirection = FlexDirection.column
                                padding = 20.px
//                                height = 300.px
//                                width = 150.px
//                                background = Color("black")
                            }

                            ReactHTML.img {
                                src = "/files/${event.fileName}"
                            }
                        }
                    }
                }
            }


            useEffect(events) {
                if(events.isNotEmpty()) {
                    hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
                        val map = LeafletObjectFactory.map(ktxDocument.getElementById("map-holder")!! as HTMLElement) {
                            center = LatLng(47, 11) // TODO center a bit better
                            zoom = 7
                            preferCanvas = true

                        }

                        val layer = LeafletObjectFactory.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png") {
                            attribution =
                                "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
                        }

                        layer.addTo(map)

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
                                { props.stateSetter("/details/${event.id}", DetailsPage(event.id.toString())) })

                            marker.addTo(map)
                        }
                    }.create())
                }
            }

            useEffectOnce {
                MapsModule.initialize()

                Requests.getMessage("/data/cleanupEvents") {
                    setEvents((it as CleanUpEvents).events)
                }
            }
        }
}
