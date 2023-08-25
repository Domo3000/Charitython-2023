package pages

import components.OverviewPage
import components.OverviewProps
import css.ClassNames
import css.Classes
import css.Style
import emotion.react.css
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.react.reactWrapper
import kotlinx.datetime.toJSDate
import model.CleanUpEventDTO
import model.CleanupDayDTO
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import react.router.useParams
import utils.Defaults
import utils.MapUtils
import utils.Requests
import utils.getFileName
import web.cssom.*

external interface IconTextProps : Props {
    var icon: String
    var text: String
}

val IconText = FC<IconTextProps> { props ->
    ReactHTML.div {
        css {
            marginTop = 15.px
            marginBottom = 15.px
        }
        ReactHTML.i {
            className = ClassName("fa-solid fa-${props.icon}")
        }
        ReactHTML.span {
            css {
                color = Style.whiteColor
                paddingLeft = 15.px
            }
            +props.text
        }
    }
}

private external interface DescriptionProps : Props {
    var description: String
}

private val Description = FC<DescriptionProps> { props ->
    props.description.split("\n").forEach { line ->
        ReactHTML.p {
            +line
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
                    +"WANN & WO"
                }

                IconText {
                    icon = "calendar-days"
                    text = props.cleanupDayDate
                }

                IconText {
                    icon = "clock"
                    text = "${cleanUpEvent.startTime} Uhr - ${cleanUpEvent.endTime} Uhr"
                }

                IconText {
                    icon = "map-marker-alt"
                    text = "${cleanUpEvent.zipCode} ${cleanUpEvent.street}"
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

                if (!cleanUpEvent.websiteAddress.isNullOrEmpty()) {
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
            }

            ReactHTML.img {
                src = getFileName(cleanUpEvent.fileName, Defaults.defaultEventPicture)
                css {
                    width = 300.px
                    borderLeft = css.Style.border
                }
            }
        }

        MapUtils.MapHolder {
            id = mapId
        }

        Description {
            description = cleanUpEvent.description
        }

        useEffectOnce {
            val coordinates = LatLng(cleanUpEvent.latitude, cleanUpEvent.longitude)

            hydrateRoot(web.dom.document.getElementById(mapId)!!, reactWrapper<FC<Props>> {
                val map = MapUtils.map(
                    id = mapId,
                    center = coordinates,
                    zoom = 11
                )

                val marker = MapUtils.marker(
                    coordinates = coordinates,
                    title = cleanUpEvent.eventName
                )

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
            src = getFileName(cleanUpEvent.fileName, Defaults.defaultEventPicture)
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

        IconText {
            icon = "clock"
            text = "${cleanUpEvent.startTime} Uhr - ${cleanUpEvent.endTime} Uhr"
        }

        IconText {
            icon = "person"
            text = cleanUpEvent.organization
        }

        IconText {
            icon = "map-marker-alt"
            text = "${cleanUpEvent.zipCode} ${cleanUpEvent.street}"
        }

        if (!cleanUpEvent.websiteAddress.isNullOrEmpty()) {
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
        }

        MapUtils.MapHolder {
            id = mapId
        }

        Description {
            description = cleanUpEvent.description
        }

        useEffectOnce {
            val coordinates = LatLng(cleanUpEvent.latitude, cleanUpEvent.longitude)

            hydrateRoot(web.dom.document.getElementById(mapId)!!, reactWrapper<FC<Props>> {
                val map = MapUtils.map(
                    id = mapId,
                    center = coordinates,
                    zoom = 11
                )

                val marker = MapUtils.marker(
                    coordinates = coordinates,
                    title = cleanUpEvent.eventName
                )

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
            val (cleanUpDay, setCleanupDay) = useState<CleanupDayDTO?>(null)
            val (cleanUpEvent, setCleanupEvent) = useState<CleanUpEventDTO?>(null)

            ReactHTML.div {
                css(Classes.limitedWidth)
                cleanUpEvent?.let { event ->
                    cleanUpDay?.let { day ->
                        val cleanupDayDate =
                            day.timestamp.toJSDate().toLocaleDateString("de-AT", dateLocaleOptions {
                                this.day = "2-digit"
                                this.month = "2-digit"
                                this.year = "numeric"
                            })

                        DesktopCleanupDetails {
                            this.cleanupDayDate = cleanupDayDate
                            this.cleanUpEvent = event
                        }
                        MobileCleanupDetails {
                            this.cleanupDayDate = cleanupDayDate
                            this.cleanUpEvent = event
                        }
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
                Requests.getMessage("/data/cleanupEvent/$eventId") { eventMessage ->
                    (eventMessage as? CleanUpEventDTO)?.let { event ->
                        Requests.getMessage("/data/cleanupDay/${event.cleanupDayId}") { cleanupDayMessage ->
                            setCleanupDay(cleanupDayMessage as CleanupDayDTO)
                        }
                        setCleanupEvent(event)
                    }
                    setLoading(false)
                }
            }
        }
}