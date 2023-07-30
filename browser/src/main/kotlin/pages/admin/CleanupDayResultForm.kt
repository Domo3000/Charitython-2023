package pages.admin

import components.IntFormField
import emotion.react.css
import kotlinx.datetime.toJSDate
import model.CleanupDayResultsDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import web.cssom.px
import web.html.ButtonType
import web.prompts.alert

external interface CleanupDayResultProps : Props {
    var admin: Requests.AdminRequests
}

val CleanupDayResultForm = FC<GDPRFormProps> { props ->
    val (results, setResults) = useState<CleanupDayResultsDTO?>(null)
    val (numberOfParticipants, setNumberOfParticipants) = useState(0)
    val (totalWeight, setTotalWeight) = useState(0)

    results?.let { result ->
        val date = result.timestamp.toJSDate()

        ReactHTML.p {
            +"Beim WorldCleanupDay ${date.getFullYear()} waren"
        }

        ReactHTML.p {
            +"über ${result.participants} grüne Held:innen"
        }

        ReactHTML.p {
            +"und haben ${result.garbage.toInt()} Tonnen Müll"
        }

        ReactHTML.p {
            +"in 24 Stunden gesammelt"
        }

        ReactHTML.form {
            IntFormField {
                text = "Anzahl der Teilnehmer:innen"
                value = numberOfParticipants
                stateSetter = setNumberOfParticipants
            }
            IntFormField {
                text = "Gesamtgewicht in kg"
                value = totalWeight
                stateSetter = setTotalWeight
            }

            ReactHTML.button {
                +"Abschicken"
                type = ButtonType.submit
            }

            onSubmit = {
                it.preventDefault()

                props.admin.post(
                    "/data/previousCleanupDayResults",
                    CleanupDayResultsDTO(
                        1,
                        result.cleanupDayId,
                        result.timestamp,
                        totalWeight.toDouble(),
                        numberOfParticipants
                    )
                ) {
                    alert("Ergebniss wurde upgedated!")
                    web.location.location.reload()
                }
            }
        }
    }

    useEffectOnce {
        Requests.getMessage("/data/previousCleanupDayResults") { message ->
            setResults((message as CleanupDayResultsDTO))
        }
    }
}

