package pages.admin

import model.CleanUpEventDTO
import model.CleanUpEvents
import model.CleanupDayDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import web.cssom.ClassName
import web.prompts.alert

external interface ApproveEventFormProps : Props {
    var admin: Requests.AdminRequests
    var cleanupDay: CleanupDayDTO
}

val ApproveEventForm = FC<ApproveEventFormProps> { props ->
    val cleanupDay = props.cleanupDay
    val (events, setEvents) = useState<List<CleanUpEventDTO>>(emptyList())

    ReactHTML.div {
        +"TODO list all registrations, have a editable form with an accept button for them"
        ReactHTML.table {
            ReactHTML.tr {
                ReactHTML.th {
                    +"Vorname"
                    className = ClassName("event-header")
                }

                ReactHTML.th {
                    +"Nachname"
                }
                ReactHTML.th {
                    +"Email"
                }
                ReactHTML.th {
                    +"Organisation"
                }
                ReactHTML.th {
                    +"Web-Addresse"
                }
                ReactHTML.th {
                    +"Event Name"
                }
                ReactHTML.th {
                    +"Startzeit"
                }
                ReactHTML.th {
                    +"Endzeit"
                }
                ReactHTML.th {
                    +"Beschreibung"
                }
            }
            ReactHTML.tbody {

                events.filterNot { it.approved }.forEach { event ->
                    ReactHTML.tr {
                        ReactHTML.td {
                            ReactHTML.span {
                                +event.firstName
                                className = ClassName("event-detail")
                            }
                        }
                        ReactHTML.td {

                            +event.lastName
                        }
                        ReactHTML.td {
                            +event.emailAddress
                        }
                        ReactHTML.td {
                            +event.organization
                        }
                        ReactHTML.td {
                            +event.websiteAddress
                        }
                        ReactHTML.td {
                            +event.eventName
                        }
                        ReactHTML.td {
                            +event.startTime
                        }
                        ReactHTML.td {
                            +event.endTime
                        }
                        ReactHTML.td {
                            +event.description
                        }
                        ReactHTML.td {
                            ReactHTML.button {
                                ReactHTML.span {
                                    +"Event akzeptieren"
                                }
                                name = "Event akzeptieren"
                                onClick = {
                                    props.admin.post("/data/approveEvent/${event.id}", event) {
                                        alert("Approved event ${event.eventName}")
                                        Requests.getMessage("/data/cleanupEvents/${cleanupDay.id}") {
                                            setEvents((it as CleanUpEvents).events)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    useEffectOnce {
        Requests.getMessage("/data/cleanupEvents/${cleanupDay.id}") {
            setEvents((it as CleanUpEvents).events)
        }
    }
}