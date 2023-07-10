package css

import csstype.PropertiesBuilder
import web.cssom.*

private typealias CSS = PropertiesBuilder.() -> Unit

object Classes {
    val centered: CSS = {
        margin = Auto.auto
        textAlign = TextAlign.center
    }

    val limitedWidth: CSS = {
        margin = Auto.auto
        maxWidth = 1000.px
        media("only screen and (min-width: 800px)") {
            marginTop = 140.px
        }
        media("only screen and (max-width: 800px)") {
            marginTop = 80.px
        }
    }

    val hidden: CSS = {
        display = None.none
    }
}