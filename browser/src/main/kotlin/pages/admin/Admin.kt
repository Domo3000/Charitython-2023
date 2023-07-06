package pages.admin

import components.OverviewProps
import components.RoutePage
import css.Classes
import emotion.react.css
import io.ktor.http.*
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set
import react.*
import react.dom.html.ReactHTML
import utils.Requests
import web.cssom.Clear
import web.cssom.Float
import web.html.InputType

private const val AdminPassword = "adminPassword"

private external interface PasswordFormProps : Props {
    var setAdmin: StateSetter<Requests.AdminRequests?>
}

private val PasswordForm = FC<PasswordFormProps> { props ->
    val (usernameInput, setUsernameInput) = useState("")
    val (passwordInput, setPasswordInput) = useState("")

    ReactHTML.form {
        ReactHTML.div {
            +"Username: "
            ReactHTML.input {
                type = InputType.text
                placeholder = "admin"
                onChange = {
                    setUsernameInput(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"Password:"
            ReactHTML.input {
                type = InputType.password
                placeholder = "password"
                onChange = {
                    setPasswordInput(it.target.value)
                }
            }
        }
        ReactHTML.button {
            +"login"
        }
        onSubmit = {
            it.preventDefault()
            val adminRequests = Requests.AdminRequests(usernameInput, passwordInput)
            adminRequests.get("/login") {
                if (it.status == HttpStatusCode.OK) {
                    window.localStorage.set(AdminPassword, passwordInput)
                    props.setAdmin(adminRequests)
                }
            }
        }
    }
}

enum class AdminState(val text: String) {
    CreateCleanupDayState("Cleanup Day"),
    ApproveEventState("Cleanup Events"),
    EventResultsState("Event Results")
}

private external interface ControlButtonProps : Props {
    var state: AdminState
    var stateSetter: StateSetter<AdminState>
}

private val AdminControlButton = FC<ControlButtonProps> { props ->
    ReactHTML.button {
        +props.state.text
        css {
            float = Float.left
        }
        onClick = {
            props.stateSetter(props.state)
        }
    }
}

object AdminPage : RoutePage {
    override val route: String = "admin"
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val (admin, setAdmin) = useState<Requests.AdminRequests?>(null)
            val (state, setState) = useState(AdminState.CreateCleanupDayState)

            ReactHTML.div {
                css(Classes.limitedWidth)
                ReactHTML.h1 {
                    +"Admin"
                }

                admin?.let {
                    ReactHTML.div {
                        props.cleanupDay?.let {
                            AdminState.values().forEach { s ->
                                AdminControlButton {
                                    this.state = s
                                    this.stateSetter = setState
                                }
                            }
                        } ?: run {
                            AdminControlButton {
                                this.state = AdminState.CreateCleanupDayState
                                this.stateSetter = setState
                            }
                        }
                    }

                    ReactHTML.div {
                        css {
                            clear = Clear.left
                        }
                        when (state) {
                            AdminState.CreateCleanupDayState -> CreateCleanupDayForm {
                                this.admin = admin
                                cleanupDay = props.cleanupDay
                                setCleanupDay = props.setCleanupDay
                            }

                            AdminState.ApproveEventState -> ApproveEventForm {
                                this.admin = admin
                                cleanupDay = props.cleanupDay!!
                            }

                            AdminState.EventResultsState -> ApproveEventForm {
                                this.admin = admin
                                cleanupDay = props.cleanupDay!!
                            }
                        }
                    }

                    // TODO delete Day/Events
                    // TODO list all Event results with CSV export
                    // TODO accept Event Registrations

                } ?: run {
                    PasswordForm {
                        this.setAdmin = setAdmin
                    }
                }
            }

            useEffectOnce {
                window.localStorage[AdminPassword]?.let { password ->
                    val adminRequests = Requests.AdminRequests("admin", password)
                    adminRequests.get("/login") {
                        if (it.status == HttpStatusCode.OK) {
                            setAdmin(adminRequests)
                        } else {
                            window.localStorage.removeItem(AdminPassword)
                        }
                    }
                }
            }
        }
}
