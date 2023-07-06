package pages

import components.OverviewPage
import components.OverviewProps
import css.ClassNames
import css.Classes
import emotion.react.css
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

private object DetailsStyle {
    val border = Border(4.px, LineStyle.solid, Color(Style.blueColor))
}

private external interface CleanUpEventProps : Props {
    var cleanupDayDate: String
    var cleanUpEvent: CleanUpEventDTO
}

private val DesktopCleanupDetails = FC<CleanUpEventProps> { props ->
    val cleanUpEvent = props.cleanUpEvent

    ReactHTML.div {
        className = ClassNames.desktopElement

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
                    borderLeft = DetailsStyle.border
                }
            }
        }

        ReactHTML.div {
            css {
                height = 800.px
                marginTop = 2.rem
            }
            id = "desktop-map-holder"
        }

        ReactHTML.p {
            +cleanUpEvent.description
        }

        useEffectOnce {
            val coordinates = LatLng(cleanUpEvent.latitude, cleanUpEvent.longitude)

            hydrateRoot(web.dom.document.getElementById("desktop-map-holder")!!, reactWrapper<FC<Props>> {
                val map =
                    LeafletObjectFactory.map(kotlinx.browser.document.getElementById("desktop-map-holder")!! as HTMLElement) {
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
}

private val MobileCleanupDetails = FC<CleanUpEventProps> { props ->
    val cleanUpEvent = props.cleanUpEvent

    ReactHTML.div {
        className = ClassNames.mobileElement

        ReactHTML.img {
            src = cleanUpEvent.fileName.let { fileName -> "/files/$fileName" }
            css {
                width = 100.pct
                borderBottom = DetailsStyle.border
            }
        }

        ReactHTML.h1 {
            +cleanUpEvent.eventName
        }

        ReactHTML.p {
            +props.cleanupDayDate
        }

        ReactHTML.p {
            +"${cleanUpEvent.startTime} Uhr - ${cleanUpEvent.endTime} Uhr"
        }

        ReactHTML.p {
            +cleanUpEvent.organization
        }

        ReactHTML.a {
            +cleanUpEvent.websiteAddress
            href = cleanUpEvent.websiteAddress
        }

        ReactHTML.div {
            css {
                paddingTop = 100.pct
                marginTop = 2.rem
            }
            id = "mobile-map-holder"
        }

        ReactHTML.p {
            +cleanUpEvent.description
        }

        useEffectOnce {
            val coordinates = LatLng(cleanUpEvent.latitude, cleanUpEvent.longitude)

            hydrateRoot(web.dom.document.getElementById("mobile-map-holder")!!, reactWrapper<FC<Props>> {
                val map =
                    LeafletObjectFactory.map(kotlinx.browser.document.getElementById("mobile-map-holder")!! as HTMLElement) {
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
}

class DetailsPage(private val maybeEventId: Int?) : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val eventId = maybeEventId ?: useParams()["id"]!!
            val (loading, setLoading) = useState(true)
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
                    DesktopCleanupDetails {
                        this.cleanupDayDate = cleanupDayDate
                        this.cleanUpEvent = it
                    }
                    MobileCleanupDetails {
                        this.cleanupDayDate = cleanupDayDate
                        this.cleanUpEvent = it
                    }
                } ?: run {
                    if(loading) {
                        +"lade Daten..."
                    } else {
                        NotFound { }
                    }
                }
            }

            useEffectOnce {
                props.cleanupDay?.let {
                    Requests.getMessage("/data/cleanupEvent/$eventId") {
                        setLoading(false)
                        setCleanupEvent((it as CleanUpEventDTO))
                    }
                }
                // TODO get cleanupDay of this event
                //  as one could want to look at one in the past so we need to display correct date or message that it's over already
            }
        }
}
