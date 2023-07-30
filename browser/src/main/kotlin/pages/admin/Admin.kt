package pages.admin

import components.OverviewProps
import components.RoutePage
import css.Classes
import emotion.react.css
import io.ktor.http.*
import kotlinx.browser.window
import org.w3c.dom.get
import react.*
import react.dom.html.ReactHTML
import utils.Requests
import web.cssom.Clear
import web.cssom.Float

const val AdminPassword = "adminPassword"

enum class AdminState(val text: String, val requireCleanupDay: Boolean) {
    CreateCleanupDayState("Cleanup Day", false),
    UploadBackground("Hintergrundbild", false),
    ApproveEventState("Cleanup Events", true),
    EventResultsState("Cleanup Ergebnisse", false),
    CleanupResultState("CleanupDay Ergebniss", false),
    GDPRState("GDPR", false)
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
                        val cleanupDayExists = props.cleanupDay != null

                        AdminState.values().forEach { s ->
                            if (!s.requireCleanupDay || cleanupDayExists) {
                                AdminControlButton {
                                    this.state = s
                                    this.stateSetter = setState
                                }
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

                            AdminState.EventResultsState -> CleanupResultsForm {
                                this.admin = admin
                            }

                            AdminState.UploadBackground -> UploadBackgroundForm {
                                this.admin = admin
                            }

                            AdminState.CleanupResultState -> CleanupDayResultForm {
                                this.admin = admin
                            }

                            AdminState.GDPRState -> GDPRForm {
                                this.admin = admin
                            }
                        }
                    }
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
