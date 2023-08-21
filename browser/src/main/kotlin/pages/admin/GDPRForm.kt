package pages.admin

import csstype.PropertiesBuilder
import emotion.react.css
import kotlinx.datetime.toJSDate
import model.CleanUpEventDTO
import model.CleanUpEvents
import model.CleanupDayDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import utils.getMonthString
import web.cssom.Border
import web.cssom.BorderCollapse
import web.cssom.LineStyle
import web.cssom.px
import web.prompts.alert

external interface GDPRFormProps : Props {
    var admin: Requests.AdminRequests
}

private fun getDate(cleanupDay: CleanupDayDTO): String {
    val date = cleanupDay.timestamp.toJSDate()
    return "${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()}"
}

private val borderStyle: PropertiesBuilder.() -> Unit = {
    border = Border(1.px, LineStyle.solid)
}

val GDPRForm = FC<GDPRFormProps> { props ->
    val (cleanupEvents, setCleanupEvents) = useState<List<CleanUpEventDTO>>(emptyList())
    val (cleanupDayDates, setCleanupDayDates) = useState(mapOf<Int, String>())
    val mutableDates = mutableMapOf<Int, String>()

    ReactHTML.div {
        +"Hier sind Buttons um beliebige Events zu löschen."
    }

    ReactHTML.div {
        +"Die Id kannst du verifizieren indem du zu den Details eines Events gehst."
    }

    ReactHTML.table {
        css {
            borderStyle
            borderCollapse = BorderCollapse.collapse
        }

        val labels = listOf(
            "Id",
            "Datum",
            "EventName",
            "Organization",
            "Löschen"
        )

        ReactHTML.tr {
            css(borderStyle)
            labels.forEach { label ->
                ReactHTML.th {
                    css(borderStyle)
                    +label
                }
            }
        }

        cleanupEvents.forEach { event ->
            val values = listOf(
                event.id.toString(),
                cleanupDayDates[event.cleanupDayId] ?: "empty",
                event.eventName,
                event.organization
            )

            ReactHTML.tr {
                css(borderStyle)
                values.forEach { label ->
                    ReactHTML.td {
                        css(borderStyle)
                        +label
                    }
                }
                ReactHTML.td {
                    css(borderStyle)
                    ReactHTML.button {
                        +"X"
                        onClick = {
                            props.admin.delete("/data/deleteEvent/${event.id}") {
                                alert("Deleted event ${event.eventName}") // TODO verification dialog
                                props.admin.getMessage("/data/cleanupEvents") {
                                    setCleanupEvents((it as CleanUpEvents).events)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    useEffectOnce {
        props.admin.getMessage("/data/cleanupEvents") { eventsMessage ->
            val events = (eventsMessage as CleanUpEvents).events

            events.map { it.cleanupDayId }.toSet().map {
                Requests.getMessage("/data/cleanupDay/$it") { cleanupDayMessage ->
                    mutableDates[it] = getDate(cleanupDayMessage as CleanupDayDTO)
                    setCleanupDayDates(mutableDates.toMap())
                }
            }

            setCleanupEvents(events)
        }
    }
}
