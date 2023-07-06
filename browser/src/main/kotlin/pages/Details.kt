package pages

import components.OverviewPage
import css.Classes
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.router.useParams

class DetailsPage(private val id: String?) : OverviewPage {
    override val component: FC<Props>
        get() = FC {
            val eventId = id ?: useParams()["id"]!!

            ReactHTML.div {
                css(Classes.limitedWidth)
                +"Details: $eventId"
            }
        }
}
