package pages

import components.RoutePage
import css.Classes
import emotion.react.css
import kotlinx.datetime.Instant
import model.CreateShareResultsEntity
import react.*
import react.dom.html.ReactHTML
import utils.Requests
import web.cssom.Auto
import web.cssom.TextAlign
import web.html.HTMLInputElement
import web.html.InputType

data class FormField(
    var label: String,
    var value: String,
    var setter: StateSetter<String>
)

object ShareResultsPage : RoutePage {
    override val route: String = "share-results"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                css(Classes.limitedWidth)
                val (mail, setMail) = useState<String>("")
                val (entity, setEntity) = useState<String>("")
                val (province, setProvince) = useState<String>("")
                val (postCode, setPostCode) = useState<String>("")
                val (location, setLocation) = useState<String>("")
                val (dateOfCleanup, setDateOfCleanup) = useState<String>("")
                val (amountOfParticipants, setAmountOfParticipants) = useState<String>("0")
                val (totalWeight, setTotalWeight) = useState<String>("0")
                val (amountOfTrashBags, setAmountOfTrashBags) = useState<String>("0")
                val (partnerOrganisation, setPartnerOrganisation) = useState<String>("")
                val (cleanedAreaSize, setCleanedAreaSize) = useState<String>("")
                val (cigaretteButtsCount, setCigaretteButtsCount) = useState<String>("0")
                val (canCount, setCanCount) = useState<String>("0")
                val (petBottleCount, setPetBottleCount) = useState<String>("0")
                val (glassBottleCount, setGlassBottleCount) = useState<String>("0")
                val (hazardousWaste, setHazardousWaste) = useState<String>("")
                val (strangeFinds, setStrangeFinds) = useState<String>("")
                val (miscellaneous, setMiscellaneous) = useState<String>("")
                val (wayOfRecognition, setWayOfRecognition) = useState<String>("")

                val formFields = listOf(
                    FormField("Mail", mail, setMail),
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
                        Requests.post("/shareresults", CreateShareResultsEntity(
                            timestamp = Instant.parse("01012020"),
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
