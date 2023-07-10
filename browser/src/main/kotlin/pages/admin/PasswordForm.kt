package pages.admin

import io.ktor.http.*
import kotlinx.browser.window
import org.w3c.dom.set
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import react.useState
import utils.Requests
import web.html.InputType

external interface PasswordFormProps : Props {
    var setAdmin: StateSetter<Requests.AdminRequests?>
}

val PasswordForm = FC<PasswordFormProps> { props ->
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