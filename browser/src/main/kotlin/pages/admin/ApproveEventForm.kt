package pages.admin

import css.Style
import emotion.react.css
import model.CleanUpEventDTO
import model.CleanUpEvents
import model.CleanupDayDTO
import pages.IconText
import react.*
import react.dom.html.ReactHTML
import utils.Requests
import web.cssom.ClassName
import web.cssom.Float
import web.cssom.pct
import web.cssom.px
import web.prompts.alert

private external interface ApproveEventProps : Props {
    var admin: Requests.AdminRequests
    var cleanupDay: CleanupDayDTO
    var cleanUpEvent: CleanUpEventDTO
    var setEvents: StateSetter<List<CleanUpEventDTO>>
}

private val CleanupDetails = FC<ApproveEventProps> { props ->
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
        }

        ReactHTML.div {
            css {
                padding = 10.px
            }

            ReactHTML.h1 {
                +cleanUpEvent.eventName
            }

            ReactHTML.p {
                +"${cleanUpEvent.firstName} ${cleanUpEvent.lastName}"
            }

            IconText {
                icon = "envelope"
                text = cleanUpEvent.emailAddress
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

        ReactHTML.div {
            ReactHTML.button {
                css {
                    width = 50.pct
                }
                ReactHTML.span {
                    +"Event akzeptieren"
                }
                onClick = {
                    props.admin.post("/data/approveEvent/${cleanUpEvent.id}", cleanUpEvent) {
                        alert("Approved event ${cleanUpEvent.eventName}")
                        Requests.getMessage("/data/cleanupEvents/${props.cleanupDay.id}") {
                            props.setEvents((it as CleanUpEvents).events)
                        }
                    }
                }
            }
            ReactHTML.button {
                css {
                    width = 50.pct
                }
                ReactHTML.span {
                    +"Event löschen"
                }
                // TODO
                onClick = {
                    props.admin.delete("/data/deleteEvent/${cleanUpEvent.id}") {
                        alert("Deleted event ${cleanUpEvent.eventName}")
                        Requests.getMessage("/data/cleanupEvents/${props.cleanupDay.id}") {
                            props.setEvents((it as CleanUpEvents).events)
                        }
                    }
                }
            }
        }
    }
}

external interface ApproveEventFormProps : Props {
    var admin: Requests.AdminRequests
    var cleanupDay: CleanupDayDTO
}

val ApproveEventForm = FC<ApproveEventFormProps> { props ->
    val cleanupDay = props.cleanupDay
    val (events, setEvents) = useState<List<CleanUpEventDTO>>(emptyList())

    ReactHTML.div {
        events.filterNot { it.approved }.forEach { event ->
            CleanupDetails {
                this.cleanupDay = cleanupDay
                cleanUpEvent = event
                admin = props.admin
                this.setEvents = setEvents
            }
        }
    }

    useEffectOnce {
        Requests.getMessage("/data/cleanupEvents/${cleanupDay.id}") {
            setEvents((it as CleanUpEvents).events)
        }
    }
}