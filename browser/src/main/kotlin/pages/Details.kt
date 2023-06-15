package pages

import components.OverviewState
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.router.useParams

class DetailsState(private val id: String?) : OverviewState {
    override val component: FC<Props>
        get() = FC {
            val eventId = id ?: useParams()["id"]!!

            ReactHTML.div {
                +"Details: $eventId"
            }
        }
}
