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
    }

    val hidden: CSS = {
        display = None.none
    }
}