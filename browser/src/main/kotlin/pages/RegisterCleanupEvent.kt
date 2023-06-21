package pages

import components.RouteState
import kotlinx.datetime.toJSDate
import model.CleanUpEventDTO
import model.CleanupDayDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import utils.getMonthString
import web.html.ButtonType
import web.html.InputType

private val RegisterForm = FC<Props> {
    val (firstName, setFirstName) = useState("")
    val (lastName, setLastName) = useState("")
    val (emailAddress, setEmailAddress) = useState("")
    val (organization, setOrganization) = useState("")
    val (websiteAddress, setWebsiteAddress) = useState("")
    val (eventName, setEventName) = useState("")
    val (street, setStreet) = useState("")
    val (zipCode, setZipCode) = useState("")
    val (description, setDescription) = useState("")
    val (startTime, setStartTime) = useState("")
    val (endTime, setEndTime) = useState("")

    ReactHTML.h1 {
        +"TODO" // TODO finish this -> make it more beautiful and handle image upload
    }

    ReactHTML.form {
        ReactHTML.div {
            +"firstname: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setFirstName(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"lastName: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setLastName(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"email: "
            ReactHTML.input {
                type = InputType.email
                onChange = {
                    setEmailAddress(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"organization: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setOrganization(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"websiteAddress: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setWebsiteAddress(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"eventName: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setEventName(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"street: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setStreet(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"zipCode: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setZipCode(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"startTime: "
            ReactHTML.input {
                type = InputType.time
                onChange = {
                    setStartTime(it.target.value)
                    console.log(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"endTime: "
            ReactHTML.input {
                type = InputType.time
                onChange = {
                    setEndTime(it.target.value)
                    console.log(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"description: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setDescription(it.target.value)
                }
            }
        }

        ReactHTML.div {
            +"file: "
            ReactHTML.input {
                type = InputType.file
            }
        }

        ReactHTML.button {
            +"Submit"
            type = ButtonType.submit
        }

        onSubmit = {
            it.preventDefault()
            Requests.post(
                "/data/cleanupEvent", CleanUpEventDTO(
                    1,
                    firstName,
                    lastName,
                    emailAddress,
                    organization,
                    websiteAddress,
                    eventName,
                    street,
                    zipCode,
                    startTime,
                    endTime,
                    description,
                    ByteArray(0)
                )
            ) {}
        }
    }
}

object RegisterCleanupEvent : RouteState {
    override val route: String = "register-event"
    override val component: FC<Props>
        get() = FC {
            val (loaded, setLoaded) = useState(false)
            val (cleanupDay, setCleanupDay) = useState<CleanupDayDTO?>(null)

            ReactHTML.h3 {
                +"Cleanup anmelden"
            }

            if (loaded) {
                cleanupDay?.let { _ ->
                    ReactHTML.div {
                        val date = cleanupDay.timestamp.toJSDate()

                        +"Wir freuen uns sehr, dass du als Organisator:in beim World Cleanup Day am ${date.getDate()}. ${date.getMonthString()}. ${date.getFullYear()} dabei sein möchtest! Bitte füll das folgende Formular aus."
                    }

                    RegisterForm { }
                } ?: run {
                    ReactHTML.div {
                        +"Wir freuen uns sehr, dass du als Organisator:in beim nächsten World Cleanup Day dabei sein möchtest!"
                    }
                    ReactHTML.br {}
                    ReactHTML.div {
                        +"Leider ist dieser noch nicht festgelegt worden!"
                    }
                }
            }

            useEffectOnce {
                Requests.getMessage("/data/cleanupDay") {
                    setCleanupDay(it as? CleanupDayDTO)
                    setLoaded(true)
                }
            }
        }
}
