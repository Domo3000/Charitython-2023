package pages

import components.OverviewProps
import components.RoutePage
import css.Classes
import emotion.react.css
import io.ktor.http.*
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toJSDate
import model.CleanupDayDTO
import model.CreateCleanupDay
import model.DeletedCleanupDay
import org.w3c.dom.get
import org.w3c.dom.set
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

private const val AdminPassword = "adminPassword"

private external interface AdminProps : Props {
    var admin: Requests.AdminRequests
    var cleanupDay: CleanupDayDTO?
    var setCleanupDay: StateSetter<CleanupDayDTO?>
}

private val CreateEventForm = FC<AdminProps> { props ->
    val (dateInput, setDateInput) = useState("")
    val (imageInput, setImageInput) = useState<ArrayBuffer?>(null)
    val (warningDialog, setWarningDialog) = useState(false)
    val cleanupDay = props.cleanupDay

    ReactHTML.div {
        cleanupDay?.let { _ ->
            val date = cleanupDay.timestamp.toJSDate()

            +"Nächster CleanupDay: id=${cleanupDay.id}: ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()}"

            ReactHTML.dialog {
                +"Wirklich? Das löscht alle Events und sonstige Daten"
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
                            props.admin.delete("/data/cleanupDay/${cleanupDay.id}") {
                                if(it == DeletedCleanupDay)  {
                                    props.setCleanupDay(null)
                                }
                            }
                        }
                    }
                }
            }

            ReactHTML.div {
                ReactHTML.button {
                    +"Löschen"
                    onClick = {
                        setWarningDialog(true)
                    }
                }
            }
        } ?: run {
            +"Nächster CleanupDay ist noch nicht erstellt worden"

            ReactHTML.form {
                ReactHTML.div {
                    +"Datum:\t"
                    ReactHTML.input {
                        type = InputType.date
                        required = true
                        onChange = {
                            setDateInput(it.target.value)
                            console.log(it.target.value)
                        }
                    }
                }
                ReactHTML.div {
                    +"Logo:\t"
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
                }
                ReactHTML.div {
                    ReactHTML.button {
                        +"Create CleanupDay"
                    }
                }
                onSubmit = {
                    it.preventDefault()
                    val date = Instant.fromEpochMilliseconds(Date.parse(dateInput).toLong())

                    //MainScope().launch {
                        @Suppress("CAST_NEVER_SUCCEEDS") // it evidently does succeed
                        val image = imageInput!!.run { Int8Array(this) as ByteArray }

                        props.admin.postImage(
                            "/data/cleanupDay",
                            image,
                            CreateCleanupDay(date)
                        ) { maybeMessage ->
                            props.setCleanupDay(maybeMessage as? CleanupDayDTO)
                        }
                   // }
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
    CreateEventState("Cleanup Day"),
    RegisterEventState("Cleanup Events"),
    EventResultsState("Event Results")
}

object AdminPage : RoutePage {
    override val route: String = "admin"
    override val component: FC<OverviewProps>
        get() = FC { props ->
            ReactHTML.div {
                css(Classes.limitedWidth)
                ReactHTML.h3 {
                    +"Admin"
                }

                val (admin, setAdmin) = useState<Requests.AdminRequests?>(null)
                val (state, setState) = useState(AdminState.CreateEventState)

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
