package pages

import components.RouteState
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toJSDate
import model.CleanupDayDTO
import model.CreateCleanupDay
import react.*
import react.dom.html.ReactHTML
import utils.Requests
import utils.getMonthString
import web.html.InputType
import kotlin.js.Date

private external interface CreateEventFormProps : Props {
    var admin: Requests.AdminRequests
}

private val CreateEventForm = FC<CreateEventFormProps> { props ->
    val (cleanupDay, setCleanupDay) = useState<CleanupDayDTO?>(null)

    ReactHTML.div {
        cleanupDay?.let { _ ->
            val date = cleanupDay.timestamp.toJSDate()

            +"Next: ${date.getDate()}. ${date.getMonthString()}. ${date.getFullYear()}"
        } ?: run {
            +"Next CleanupDay is not set"
        }
    }

    ReactHTML.button {
        +"Create CleanupDay"
        onClick = {
            val date = Date.UTC(2023, 9, 16)
            props.admin.post(
                "/admin/data/cleanupDay",
                CreateCleanupDay(Instant.fromEpochMilliseconds(date.toLong()))
            ) { maybeMessage ->
                setCleanupDay(maybeMessage as? CleanupDayDTO)
            }
        }
    }

    useEffectOnce {
        Requests.getMessage("/data/cleanupDay") {
            setCleanupDay(it as? CleanupDayDTO)
        }
    }
}

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
            adminRequests.get("/admin/login") {
                if (it.status == HttpStatusCode.OK) {
                    props.setAdmin(adminRequests)
                }
            }
        }
    }
}

object AdminState : RouteState {
    override val route: String = "admin"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Admin"

                val (admin, setAdmin) = useState<Requests.AdminRequests?>(null)

                admin?.let {
                    // TODO delete Day/Events
                    // TODO list all Event results
                    // TODO accept Event Registrations
                    CreateEventForm {
                        this.admin = admin
                    }
                } ?: run {
                    PasswordForm {
                        this.setAdmin = setAdmin
                    }
                }
            }
        }
}
