package pages

import components.RouteState
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import model.CreateCleanupDay
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffect
import react.useState
import utils.Requests
import web.html.InputType

object AdminState : RouteState {
    override val route: String = "admin"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                +"Admin"

                val (admin, setAdmin) = useState<Requests.AdminRequests?>(null)
                val (usernameInput, setUsernameInput) = useState("")
                val (passwordInput, setPasswordInput) = useState("")

                useEffect(admin) {
                    console.log("s")
                    MainScope().launch {
                        Requests.client.use {
                            console.log(it.get("/admin/login") {
                                basicAuth("admin", "password")
                            }.status)
                        }
                    }.start()

                    Requests.get("/data/cleanupDay") {
                        console.log(it.status.toString())
                    }

                    Requests.get("/data/cleanupDay") {
                        console.log(it.status)
                    }
                    admin?.get("/admin/login") {
                        console.log("se")
                        console.log(it.status)

                    }
                    console.log("set")
                }

                Requests.get("/data/cleanupDay") {
                    +it.status.toString()
                    console.log(it.status.toString())
                }

                admin?.let {
                    ReactHTML.button {
                        +"Test"
                        onClick = {
                            Requests.post("/admin/data/cleanupDay", CreateCleanupDay(Instant.parse("01012020")))
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
                            val adminRequests = Requests.AdminRequests(usernameInput, passwordInput)
                            adminRequests.get("/admin/login") {
                                if (it.status == HttpStatusCode.OK) {
                                    setAdmin(adminRequests)
                                }
                            }
                        }
                    }
                    ReactHTML.button {
                        +"login"
                        onClick = {
                            val adminRequests = Requests.AdminRequests(usernameInput, passwordInput)
                            setAdmin(adminRequests)
                        }
                    }
                }
            }
        }
}
