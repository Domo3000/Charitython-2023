package pages

import components.OverviewProps
import components.RoutePage
import emotion.react.css
import io.ktor.http.*
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toJSDate
import model.CleanupDayDTO
import model.CreateCleanupDay
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import react.useState
import utils.Requests
import utils.getMonthString
import web.cssom.Clear
import web.cssom.Float
import web.file.FileReader
import web.html.InputType
import kotlin.js.Date

private external interface AdminProps : Props {
    var admin: Requests.AdminRequests
    var cleanupDay: CleanupDayDTO?
    var setCleanupDay: StateSetter<CleanupDayDTO?>
}

private val CreateEventForm = FC<AdminProps> { props ->
    val (dateInput, setDateInput) = useState("")
    val (imageInput, setImageInput) = useState<ArrayBuffer?>(null)
    val cleanupDay = props.cleanupDay

    ReactHTML.div {
        cleanupDay?.let { _ ->
            val date = cleanupDay.timestamp.toJSDate()

            +"Next: ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()}"

            // TODO is delete button with warning dialog
        } ?: run {
            +"Next CleanupDay is not set"

            ReactHTML.form {
                ReactHTML.input {
                    type = InputType.date
                    required = true
                    onChange = {
                        setDateInput(it.target.value)
                        console.log(it.target.value)
                    }
                }
                ReactHTML.input {
                    type = InputType.file
                    required = true
                    onChange = {
                        val fileReader = FileReader()

                        fileReader.readAsArrayBuffer(it.target.files!![0])

                        fileReader.onload = { e ->
                            setImageInput(e.target!!.result as ArrayBuffer)
                        }
                    }
                }
                ReactHTML.button {
                    +"Create CleanupDay"
                }
                onSubmit = {
                    it.preventDefault()
                    val date = Instant.fromEpochMilliseconds(Date.parse(dateInput).toLong())

                    MainScope().launch {
                        val image = imageInput!!.run { Int8Array(this) as ByteArray }

                        props.admin.postImage(
                            "/admin/data/cleanupDay",
                            image,
                            CreateCleanupDay(date)
                        ) { maybeMessage ->
                            props.setCleanupDay(maybeMessage as? CleanupDayDTO)
                        }
                    }
                }
            }
        }
    }
}

private val RegisterEventForm = FC<AdminProps> { props ->
    ReactHTML.div {
        +"TODO list all registrations, have a editable form with an accept button for them"
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

enum class AdminState(val text: String) {
    CreateEventState("Cleanup Day"),
    RegisterEventState("Cleanup Events"),
    EventResultsState("Event Results")
}

object AdminPage : RoutePage {
    override val route: String = "admin"
    override val component: FC<OverviewProps>
        get() = FC { props ->
            ReactHTML.div {
                +"Admin"

                val (admin, setAdmin) = useState<Requests.AdminRequests?>(null)
                val (state, setState) = useState<AdminState>(AdminState.CreateEventState)

                admin?.let {
                    ReactHTML.div {
                        AdminState.values().forEach { s ->
                            ReactHTML.button {
                                +s.text
                                css {
                                    float = Float.left
                                }
                                onClick = {
                                    setState(s)
                                }
                            }
                        }
                    }

                    ReactHTML.div {
                        css {
                            clear = Clear.left
                        }
                        when (state) {
                            AdminState.CreateEventState -> CreateEventForm {
                                this.admin = admin
                                cleanupDay = props.cleanupDay
                                setCleanupDay = props.setCleanupDay
                            }

                            AdminState.RegisterEventState -> RegisterEventForm {
                                this.admin = admin
                            }

                            AdminState.EventResultsState -> RegisterEventForm {
                                this.admin = admin
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
        }
}
