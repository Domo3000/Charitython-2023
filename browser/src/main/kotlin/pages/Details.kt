package pages

import components.OverviewPage
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.router.useParams

class DetailsPage(private val id: String?) : OverviewPage {
    override val component: FC<Props>
        get() = FC {
            val eventId = id ?: useParams()["id"]!!

            ReactHTML.div {
                +"Details: $eventId"
            }
        }
}
