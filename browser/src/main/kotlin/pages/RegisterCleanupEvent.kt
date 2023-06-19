package pages

import components.RouteState
import model.CleanUpEventDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import utils.Requests
import web.html.InputType

object RegisterCleanupEvent : RouteState {
    override val route: String = "register-cleanup-event"
    override val component: FC<Props>
        get() = FC {
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
            }
            ReactHTML.button {
                +"create"
                onClick = {
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
                    )
                }
            }
        }
}
