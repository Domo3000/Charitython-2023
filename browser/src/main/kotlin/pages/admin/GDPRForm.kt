package pages.admin

import react.FC
import react.Props
import react.dom.html.ReactHTML
import utils.Requests

external interface GDPRFormProps : Props {
    var admin: Requests.AdminRequests
}

val GDPRForm = FC<GDPRFormProps> { props ->
    ReactHTML.div {
        +"Zur Zeit noch nicht implementiert: Buttons um beliebige Events oder Ergebnisse zu löschen"
    }

    ReactHTML.div {
        +"Zur Zeit kann man dies nur über die Datenbank selbst erledigen"
    }

    /*
    useEffectOnce {

    }
     */
}

