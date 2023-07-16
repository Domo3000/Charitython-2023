package pages

import components.OverviewProps
import components.RoutePage
import css.Classes
import emotion.react.css
import kotlinx.datetime.Instant
import model.CleanupEventResultsDTO
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import react.useState
import utils.Requests
import web.cssom.Auto
import web.cssom.TextAlign
import web.html.HTMLInputElement
import web.html.InputType

private external interface FormFieldProps<T> : Props {
    var text: String
    var state: T
    var stateSetter: StateSetter<T>
}

private fun <T> formField() = FC<FormFieldProps<T>> { props ->
    ReactHTML.div {
        ReactHTML.div {
            +props.text
        }
        ReactHTML.input {
            val text = when(props.state) {
                is String -> props.state
                is String? -> props.state ?: ""
                is Double -> props.state.toString()
                is Double? -> props.state?.toString() ?: ""
                is Int -> props.state.toString()
                is Int? -> props.state?.toString() ?: ""
                else -> props.state.toString()
            }
            value = text
        }
    }
}

object ShareResultsPage : RoutePage {
    override val route: String = "ergebnisseTeilen"
    override val component: FC<OverviewProps>
        get() = FC {
            ReactHTML.div {
                css(Classes.limitedWidth)

                val (emailAddress, setEmailAddress) = useState("")
                val (organization, setOrganization) = useState("")
                val (partnerOrganization, setPartnerOrganization) = useState<String?>(null)
                val (province, setProvince) = useState("")
                val (location, setLocation) = useState("")
                val (zipCode, setZipCode) = useState("")
                val (numberOfParticipants, setNumberOfParticipants) = useState(0)
                val (totalWeight, setTotalWeight) = useState(0.0)
                val (amountOfTrashBags, setAmountOfTrashBags) = useState<Double?>(null)
                val (cleanedAreaSize, setCleanedAreaSize) = useState<String?>(null)
                val (cigaretteButtsCount, setCigaretteButtsCount) = useState<Int?>(null)
                val (canCount, setCanCount) = useState<Int?>(null)
                val (petBottleCount, setPetBottleCount) = useState<Int?>(null)
                val (glassBottleCount, setGlassBottleCount) = useState<Int?>(null)
                val (hazardousWaste, setHazardousWaste) = useState<String?>(null)
                val (strangeFinds, setStrangeFinds) = useState<String?>(null)
                val (miscellaneous, setMiscellaneous) = useState<String?>(null)
                val (wayOfRecognition, setWayOfRecognition) = useState("")

                val formFields = listOf(
                    FormField("E-Mail-Addresse", mail, setMail),
                    FormField("Entity", entity, setEntity),
                    FormField("Province", province, setProvince),
                    FormField("Post Code", postCode, setPostCode),
                    FormField("Location", location, setLocation),
                    FormField("Date Of Cleanup", dateOfCleanup, setDateOfCleanup),
                    FormField("Amount Of Participants", amountOfParticipants, setAmountOfParticipants),
                    FormField("Total Weight", totalWeight, setTotalWeight),
                    FormField("Amount Of Trash Bags", amountOfTrashBags, setAmountOfTrashBags),
                    FormField("Partner Organisation", partnerOrganisation, setPartnerOrganisation),
                    FormField("Cleaned Area Size", cleanedAreaSize, setCleanedAreaSize),
                    FormField("Cigarette Butts Count", cigaretteButtsCount, setCigaretteButtsCount),
                    FormField("Can Count", canCount, setCanCount),
                    FormField("PET Bottle Count", petBottleCount, setPetBottleCount),
                    FormField("Glass Bottle Count", glassBottleCount, setGlassBottleCount),
                    FormField("Hazardous Waste", hazardousWaste, setHazardousWaste),
                    FormField("Strange Finds", strangeFinds, setStrangeFinds),
                    FormField("Miscellaneous", miscellaneous, setMiscellaneous),
                    FormField("Way Of Recognition", wayOfRecognition, setWayOfRecognition)
                )


                ReactHTML.img {
                    // TODO: Insert img here
                }

                ReactHTML.p {
                    +"Gemeinsam haben wir den World Cleanup Day in Österreich möglich gemacht. Danke!!"
                }

                ReactHTML.form {
                    formFields.forEach { field ->
                        ReactHTML.div {
                            +"${field.label}: "
                            ReactHTML.input {
                                type = when (field.label) {
                                    "Mail" -> InputType.email
                                    "Date Of Cleanup" -> InputType.date
                                    "totalWeight" -> InputType.number
                                    // Add other specific input types as needed
                                    else -> InputType.text
                                }
                                placeholder = ""
                                value = field.value
                                onChange = {
                                    val target = it.target as HTMLInputElement
                                    field.setter(target.value)
                                }
                                css {
                                    margin = Auto.auto
                                    textAlign = TextAlign.center
                                }
                            }
                        }
                    }

                    ReactHTML.button {
                        +"Senden"
                    }

                    onSubmit = {
                        Requests.post("/shareresults", CleanupEventResultsDTO(
                            mail,
                            entity,
                            province,
                            postCode,
                            location,
                            dateOfCleanup = Instant.parse(dateOfCleanup),
                            amountOfParticipants = amountOfParticipants.toInt(),
                            totalWeight = totalWeight.toInt(),
                            amountOfTrashBags = amountOfTrashBags.toInt(),
                            partnerOrganisation,
                            cleanedAreaSize,
                            cigaretteButtsCount = cigaretteButtsCount.toInt(),
                            canCount = canCount.toInt(),
                            petBottleCount = petBottleCount.toInt(),
                            glassBottleCount = glassBottleCount.toInt(),
                            hazardousWaste,
                            strangeFinds,
                            miscellaneous,
                            wayOfRecognition
                        )
                        ) {}
                    }
                }
            }
        }
}
