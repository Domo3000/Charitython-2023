package pages

import components.RoutePage
import css.Classes
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML

object ShareResultsPage : RoutePage {
    override val route: String = "share-results"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                css(Classes.limitedWidth)
                +"Share Results Table"
            }
        }
}
