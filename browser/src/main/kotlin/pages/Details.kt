package pages

import components.OverviewPage
import components.OverviewProps
import css.Classes
import emotion.react.css
import io.kvision.MapsModule
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.react.reactWrapper
import kotlinx.datetime.toJSDate
import model.CleanUpEventDTO
import org.w3c.dom.HTMLElement
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import react.router.useParams
import utils.Requests
import utils.Style
import web.cssom.*

private external interface CleanUpEventProps : Props {
    var cleanupDayDate: String
    var cleanUpEvent: CleanUpEventDTO
}

private val CleanupDetails = FC<CleanUpEventProps> { props ->
    val cleanUpEvent = props.cleanUpEvent

    ReactHTML.h1 {
        +cleanUpEvent.eventName
    }

    ReactHTML.div {
        css {
            display = Display.flex
            flexWrap = FlexWrap.nowrap
            justifyContent = JustifyContent.spaceBetween
        }

        ReactHTML.div {
            ReactHTML.h2 {
                +"WANN"
            }

            ReactHTML.p {
                +props.cleanupDayDate
            }

            ReactHTML.p {
                +"${cleanUpEvent.startTime} Uhr - ${cleanUpEvent.endTime} Uhr"
            }
        }

        ReactHTML.div {

            ReactHTML.h2 {
                +"VERANSTALTER:INNEN"
            }

            ReactHTML.p {
                +cleanUpEvent.organization
            }

            ReactHTML.a {
                +cleanUpEvent.websiteAddress
                href = cleanUpEvent.websiteAddress
            }
        }

        ReactHTML.img {
            src = cleanUpEvent.fileName.let { fileName -> "/files/$fileName" }
            css {
                width = 300.px
                borderLeft = Border(4.px, LineStyle.solid, Color(Style.blueColor))
            }
        }
    }

    ReactHTML.div {
        css {
            height = 800.px
            marginTop = 2.rem
        }
        id = "map-holder"
    }

    ReactHTML.p {
        +cleanUpEvent.description
    }

    useEffectOnce {
        MapsModule.initialize()

        val coordinates = LatLng(cleanUpEvent.latitude, cleanUpEvent.longitude)

        hydrateRoot(web.dom.document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
            val map =
                LeafletObjectFactory.map(kotlinx.browser.document.getElementById("map-holder")!! as HTMLElement) {
                    center = coordinates
                    zoom = 11
                    preferCanvas = false
                }

            val layer = LeafletObjectFactory.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png") {
                attribution =
                    "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
            }

            layer.addTo(map)

            val marker =
                LeafletObjectFactory.marker(coordinates) {
                    title = cleanUpEvent.eventName
                    icon = LeafletObjectFactory.icon {
                        iconUrl = "/static/logo-oval.png"
                        iconSize = Point(25, 25, true)
                        shadowSize = Point(35, 35, true)
                    }
                }

            marker.addTo(map)
        }.create())
    }
}

class DetailsPage : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val eventId = useParams()["id"]!!
            val (cleanUpEvent, setCleanupEvent) = useState<CleanUpEventDTO?>(null)

            ReactHTML.div {
                css(Classes.limitedWidth)
                cleanUpEvent?.let {
                    val cleanupDayDate =
                        props.cleanupDay!!.timestamp.toJSDate().toLocaleDateString("de-AT", dateLocaleOptions {
                            day = "2-digit"
                            month = "2-digit"
                            year = "numeric"
                        })
                    CleanupDetails {
                        this.cleanupDayDate = cleanupDayDate
                        this.cleanUpEvent = it
                    }
                }
            }

            useEffectOnce {
                props.cleanupDay?.let {
                    Requests.getMessage("/data/cleanupEvent/$eventId") {
                        setCleanupEvent((it as CleanUpEventDTO))
                    }
                }
            }

        }
}
