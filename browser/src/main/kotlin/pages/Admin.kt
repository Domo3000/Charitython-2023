package pages

import components.RouteState
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toJSDate
import model.CleanupDayDTO
import model.CreateCleanupDay
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import utils.getMonthString
import web.html.InputType
import kotlin.js.Date

object AdminState : RouteState {
    override val route: String = "admin"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Admin"

                val (admin, setAdmin) = useState<Requests.AdminRequests?>(null)
                val (cleanupDay, setCleanupDay) = useState<CleanupDayDTO?>(null)
                val (usernameInput, setUsernameInput) = useState("")
                val (passwordInput, setPasswordInput) = useState("")

                admin?.let {
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
                            admin.post(
                                "/admin/data/cleanupDay",
                                CreateCleanupDay(Instant.fromEpochMilliseconds(date.toLong()))
                            ) { maybeMessage ->
                                setCleanupDay(maybeMessage as? CleanupDayDTO)
                            }
                        }
                    }
                } ?: run {
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
                                //+passwordInput
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
                                    setAdmin(adminRequests)
                                }
                            }
                        }
                    }
                }

                useEffectOnce {
                    Requests.getMessage("/data/cleanupDay") {
                        setCleanupDay(it as? CleanupDayDTO)
                    }
                }
            }
        }
}
