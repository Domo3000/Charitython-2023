package pages.admin

import csstype.PropertiesBuilder
import react.Props
import utils.Requests
import emotion.react.css
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import kotlinx.datetime.toJSDate
import model.*
import react.FC
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import web.cssom.*
import web.file.FileReader
import web.html.InputType


external interface CleanupResultsFormProps : Props {
    var admin: Requests.AdminRequests
}

private val borderStyle: PropertiesBuilder.() -> Unit = {
    border = Border(1.px, LineStyle.solid)
}

val CleanupResultsForm = FC<CleanupResultsFormProps> { props ->
    val (previousCleanupDay, setPreviousCleanupDay) = useState<CleanupDayDTO?>(null)
    val (cleanupResults, setCleanupResults) = useState<List<CleanupEventResultDTO>>(emptyList())
    val (warningDialog, setWarningDialog) = useState(false)

    if(cleanupResults.isEmpty()) {
        ReactHTML.div {
            +"Keine Ergebnisse für den vorherigen CleanupDay vorhanden!"
        }
    } else {
        val date = previousCleanupDay!!.timestamp.toJSDate()

        ReactHTML.p {
            +"Ergebnisse für World Cleanup Day ${date.getFullYear()}"
        }

        ReactHTML.a {
            css {
                margin = 20.px
                fontSize = 2.rem
            }
            +"Excel Datei herunterladen"
            href = "/admin/data/cleanupEventResults/${previousCleanupDay.id}/excel"
        }

        ReactHTML.table {
            css {
                borderStyle
                borderCollapse = BorderCollapse.collapse
            }

            val labels = listOf(
                "E-Mail-Addresse",
                "Organisation",
                "Partnerorganisationen",
                "Bundesland",
                "Ort",
                "PLZ",
                "Anzahl der Teilnehmer:innen",
                "Gesamtgewicht in kg",
                "Anzahl der Müllsäcke in Liter",
                "Größe des gesäuberten Gebiets",
                "Anzahl der Zigarettenstummel",
                "Anzahl der Dosen",
                "Anzahl der PET Flaschen",
                "Anzahl der Glasflaschen",
                "Gefährliche Abfälle",
                "Sonderbare Funde",
                "Sonstiges",
                "Woher erfahren",
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

            cleanupResults.forEach { result ->
                val values = listOf(
                    result.emailAddress,
                    result.organization,
                    result.partnerOrganization ?: "",
                    result.province,
                    result.location,
                    result.zipCode,
                    result.numberOfParticipants.toString(),
                    result.totalWeight.toString(),
                    result.amountOfTrashBags?.toString() ?: "",
                    result.cleanedAreaSize ?: "",
                    result.cigaretteButtsCount?.toString() ?: "",
                    result.canCount?.toString() ?: "",
                    result.petBottleCount?.toString() ?: "",
                    result.glassBottleCount?.toString() ?: "",
                    result.hazardousWaste ?: "",
                    result.strangeFinds ?: "",
                    result.miscellaneous ?: "",
                    result.wayOfRecognition
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
                                props.admin.delete("/data/cleanupEventResults/${result.id}") {
                                    setCleanupResults(cleanupResults.filterNot { it.id == result.id })
                                }
                            }
                        }
                    }
                }
            }
        }

        ReactHTML.div {
            css {
                marginTop = 20.px
            }
            ReactHTML.p {
                +"Verwende den folgenden Button nur falls die Excel Datei heruntergeladen wurde!"
            }
            ReactHTML.button {
                +"Alle unnötigen Daten Löschen"
                onClick = {
                    setWarningDialog(true)
                }
            }
            ReactHTML.span {
                +" * Nur Tonnen Müll und Anzahl der Teilnehmer werden in die Datenbank übernommen"
            }
        }

        ReactHTML.dialog {
            +"Wirklich? Hast du die Excel Datei heruntergeladen?"
            css {
                backgroundColor = css.Style.blackColor
            }
            open = warningDialog
            ReactHTML.div {
                ReactHTML.button {
                    +"Doch nicht!"
                    onClick = {
                        setWarningDialog(false)
                    }
                }
                ReactHTML.button {
                    +"Wirklich löschen!"
                    onClick = {
                        props.admin.delete("/data/cleanupEventResults") {
                            setCleanupResults(emptyList())
                        }
                    }
                }
            }
        }
    }

    useEffectOnce {
        Requests.getMessage("/data/previousCleanupDay") {
            val previous = it as? CleanupDayDTO
            previous?.let { cleanupDay ->
                setPreviousCleanupDay(cleanupDay)

                props.admin.getMessage("/data/cleanupEventResults/${cleanupDay.id}") { results ->
                    setCleanupResults((results as CleanupEventResultsDTO).results)
                }
            }
        }
    }
}