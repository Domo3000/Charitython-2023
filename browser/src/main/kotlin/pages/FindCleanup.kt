package pages

import components.OverviewPage
import components.OverviewProps
import components.RoutePage
import css.ClassNames
import css.Classes
import css.Style
import emotion.react.css
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.react.reactWrapper
import kotlinx.datetime.toJSDate
import model.CleanUpEventDTO
import model.CleanUpEvents
import model.CleanupDayDTO
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.*
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
            src = getFileName(cleanUpEvent.fileName, Defaults.defaultEventPicture)
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
                    color = Style.blackColor
                    overflowWrap = OverflowWrap.breakWord
                }
                +cleanUpEvent.eventName
            }
        }

        ReactHTML.h4 {
            css {
                color = Style.pinkColor
            }
            +"Weiterlesen »"
        }
    }
}

private fun getEvents(id: Int, stateSetter: StateSetter<List<CleanUpEventDTO>>) =
    Requests.getMessage("/data/cleanupEvents/$id") { message ->
        stateSetter((message as CleanUpEvents).events.filter { it.approved })
    }

private fun getDate(cleanupDay: CleanupDayDTO): String {
    val date = cleanupDay.timestamp.toJSDate()
    return "${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()}"
}

object FindCleanup : RoutePage {
    override val route: String = "cleanupFinden" // TODO use everywhere same style for routes
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val (previous, setPrevious) = useState<CleanupDayDTO?>(null)
            val (events, setEvents) = useState<List<CleanUpEventDTO>>(emptyList())

            ReactHTML.div {
                css(Classes.limitedWidth)

                ReactHTML.h1 {
                    +"Cleanup finden"
                }

                if (events.isNotEmpty()) {
                    previous?.let {
                        ReactHTML.p {
                            +"Es wurden leider noch keine Cleanup Events für den nächsten Cleanup Day erstellt!"
                        }
                        ReactHTML.p {
                            +"Cleanup Events für den vorherigen World Cleanup Day am ${getDate(it)}."
                        }
                    } ?: run {
                        props.cleanupDay?.let { cleanupDay ->
                            ReactHTML.p {
                                +"Cleanup Events für den nächsten World Cleanup Day am ${getDate(cleanupDay)}."
                            }
                        }
                    }
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
                props.cleanupDay?.let { cleanupDay ->
                    setPrevious(null)
                    getEvents(cleanupDay.id, setEvents)
                } ?: run {
                    Requests.getMessage("/data/previousCleanupDay") { cleanupDayMessage ->
                        val previousCleanupDay = cleanupDayMessage as CleanupDayDTO
                        setPrevious(previousCleanupDay)
                        getEvents(previousCleanupDay.id, setEvents)
                    }
                }
            }
        }
}

