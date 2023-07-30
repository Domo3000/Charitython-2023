package pages

import components.*
import css.Classes
import emotion.react.css
import kotlinx.datetime.toJSDate
import model.CleanupDayDTO
import model.CleanupEventResultDTO
import react.FC
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import web.cssom.px
import web.html.ButtonType
import web.html.InputType
import web.prompts.alert

object ShareResultsPage : RoutePage {
    override val route: String = "ergebnisseTeilen"
    override val component: FC<OverviewProps>
        get() = FC {
            val (previousCleanupDay, setPreviousCleanupDay) = useState<CleanupDayDTO?>(null)

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

            previousCleanupDay?.let { cleanupDay ->
                ReactHTML.div {
                    css(Classes.limitedWidth)

                    val date = previousCleanupDay.timestamp.toJSDate()

                    ReactHTML.h1 {
                        +"Ergebnisse Teilen"
                    }

                    ReactHTML.p {
                        +"Gemeinsam haben wir den World Cleanup Day ${date.getFullYear()} in Österreich möglich gemacht. Danke!!"
                    }

                    ReactHTML.form {
                        TextFormField {
                            text = "E-Mail-Addresse"
                            value = emailAddress
                            stateSetter = setEmailAddress
                            type = InputType.email
                        }

                        TextFormField {
                            text = "Name: Organisation/Verein/Unternehmen/Privatperson"
                            value = organization
                            stateSetter = setOrganization
                        }

                        OptionalTextFormField {
                            text = "Partnerorganisationen"
                            value = partnerOrganization
                            stateSetter = setPartnerOrganization
                        }

                        TextFormField {
                            text = "Bundesland"
                            value = province
                            stateSetter = setProvince
                        }

                        TextFormField {
                            text = "Ort"
                            value = location
                            stateSetter = setLocation
                        }

                        TextFormField {
                            text = "PLZ"
                            value = zipCode
                            stateSetter = setZipCode
                        }

                        IntFormField {
                            text = "Anzahl der Teilnehmer:innen"
                            value = numberOfParticipants
                            stateSetter = setNumberOfParticipants
                        }

                        DoubleFormField {
                            text = "Gesamtgewicht in kg"
                            value = totalWeight
                            stateSetter = setTotalWeight
                        }

                        OptionalDoubleFormField {
                            text = "Anzahl der Müllsäcke in Liter"
                            value = amountOfTrashBags
                            stateSetter = setAmountOfTrashBags
                        }

                        OptionalTextFormField {
                            text = "Größe des gesäuberten Gebiets"
                            value = cleanedAreaSize
                            stateSetter = setCleanedAreaSize
                        }

                        OptionalIntFormField {
                            text = "Anzahl der Zigarettenstummel"
                            value = cigaretteButtsCount
                            stateSetter = setCigaretteButtsCount
                        }

                        OptionalIntFormField {
                            text = "Anzahl der Dosen"
                            value = canCount
                            stateSetter = setCanCount
                        }

                        OptionalIntFormField {
                            text = "Anzahl der PET Flaschen"
                            value = petBottleCount
                            stateSetter = setPetBottleCount
                        }

                        OptionalIntFormField {
                            text = "Anzahl der Glasflaschen"
                            value = glassBottleCount
                            stateSetter = setGlassBottleCount
                        }

                        OptionalTextFormField {
                            text = "Gefährliche Abfälle (Styropor, Spraydosen, Spritzen, etc.)"
                            value = hazardousWaste
                            stateSetter = setHazardousWaste
                        }

                        OptionalTextFormField {
                            text = "Sonderbare Funde"
                            value = strangeFinds
                            stateSetter = setStrangeFinds
                        }

                        OptionalTextFormField {
                            text = "Sonstiges"
                            value = miscellaneous
                            stateSetter = setMiscellaneous
                        }

                        TextFormField {
                            text = "Wie hast du vom World Cleanup Day erfahren" // TODO enum choice
                            value = wayOfRecognition
                            stateSetter = setWayOfRecognition
                        }

                        ReactHTML.button {
                            css {
                                marginTop = 20.px
                            }
                            +"Abschicken"
                            type = ButtonType.submit
                        }

                        onSubmit = {
                            it.preventDefault()

                            Requests.post(
                                "/data/cleanupEventResult", CleanupEventResultDTO(
                                    cleanupDay.id,
                                    emailAddress,
                                    organization,
                                    partnerOrganization,
                                    province,
                                    location,
                                    zipCode,
                                    numberOfParticipants,
                                    totalWeight,
                                    amountOfTrashBags,
                                    cleanedAreaSize,
                                    cigaretteButtsCount,
                                    canCount,
                                    petBottleCount,
                                    glassBottleCount,
                                    hazardousWaste,
                                    strangeFinds,
                                    miscellaneous,
                                    wayOfRecognition
                                )
                            ) {
                                alert("Ergebnis wurde zur Bestätigung übermittelt!")
                                web.location.location.reload()
                            }
                        }

                        ReactHTML.p {
                            +"Der Bericht über den World Cleanup Day ${date.getFullYear()} erscheint bis Ende Oktober auf dieser Webseite."
                        }

                        ReactHTML.div {
                            +"Bitte übermittel uns deine Bilder per Mail an: "

                            ReactHTML.a {
                                +"office@greenheroes.at"
                                href = "mailto:office@greenheroes.at"
                            }

                            +" oder über Drive. Nochmals vielen lieben Dank fürs Mitmachen. Dank grüner Held:innen wie dir konnten wir am diesjährigen World Cleanup Day ein Zeichen für Umweltschutz setzen."
                        }
                    }
                }
            }

            useEffectOnce {
                Requests.getMessage("/data/previousCleanupDay") {
                    setPreviousCleanupDay(it as? CleanupDayDTO)
                }
            }
        }
}
