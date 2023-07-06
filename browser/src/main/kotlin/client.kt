import react.create
import react.dom.client.createRoot
import web.dom.document

suspend fun main() {
    document.getElementById("script-holder")?.let {
        createRoot(it).render(Routing.create())
    }
}