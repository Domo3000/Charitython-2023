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
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import react.router.useParams
import utils.MapUtils
import utils.Requests
import web.cssom.*

external interface IconTextProps : Props {
    var icon: String
    var text: String
}

val IconText = FC<IconTextProps> { props ->
    ReactHTML.div {
        ReactHTML.i {
            className = ClassName("fa-solid fa-${props.icon}")
        }
        ReactHTML.span {
            css {
                paddingLeft = 15.px
            }
            +props.text
        }
    }
}

private external interface CleanUpEventProps : Props {
    var cleanupDayDate: String
    var cleanUpEvent: CleanUpEventDTO
}

private val DesktopCleanupDetails = FC<CleanUpEventProps> { props ->
    val cleanUpEvent = props.cleanUpEvent
    val mapId = "desktop-map-holder"

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
                marginBottom = 2.rem
            }

            ReactHTML.div {
                ReactHTML.h2 {
                    +"WANN"
                }

                IconText {
                    icon = "calendar-days"
                    text = props.cleanupDayDate
                }

                ReactHTML.br {}

                IconText {
                    icon = "clock"
                    text = "${cleanUpEvent.startTime} Uhr - ${cleanUpEvent.endTime} Uhr"
                }
            }

            ReactHTML.div {
                ReactHTML.h2 {
                    +"VERANSTALTER:INNEN"
                }

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
            }

            ReactHTML.img {
                src = cleanUpEvent.fileName.let { fileName -> "/files/$fileName" }
                css {
                    width = 300.px
                    borderLeft = css.Style.border
                }
            }
        }

        MapUtils.mapHolder(MapUtils.Format.Wide, mapId)()

        ReactHTML.p {
            +cleanUpEvent.description
        }

        useEffectOnce {
            val coordinates = LatLng(cleanUpEvent.latitude, cleanUpEvent.longitude)

            hydrateRoot(web.dom.document.getElementById(mapId)!!, reactWrapper<FC<Props>> {
                val map = MapUtils.map(
                    id = mapId,
                    center = coordinates,
                    zoom = 11
                )

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
    val mapId = "mobile-map-holder"

    ReactHTML.div {
        className = ClassNames.mobileElement

        ReactHTML.img {
            src = cleanUpEvent.fileName.let { fileName -> "/files/$fileName" }
            css {
                width = 100.pct
                borderBottom = css.Style.border
            }
        }

        ReactHTML.h1 {
            +cleanUpEvent.eventName
        }

        // TODO Table
        IconText {
            icon = "calendar-days"
            text = props.cleanupDayDate
        }

        ReactHTML.br {}

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
            css {
                marginBottom = 2.rem
            }
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

        MapUtils.mapHolder(MapUtils.Format.Wide, mapId)()

        ReactHTML.p {
            +cleanUpEvent.description
        }

        useEffectOnce {
            val coordinates = LatLng(cleanUpEvent.latitude, cleanUpEvent.longitude)

            hydrateRoot(web.dom.document.getElementById(mapId)!!, reactWrapper<FC<Props>> {
                val map = MapUtils.map(
                    id = mapId,
                    center = coordinates,
                    zoom = 11
                )

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
                    if (loading) {
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