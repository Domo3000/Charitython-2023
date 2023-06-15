package css

import csstype.PropertiesBuilder
import web.cssom.*

private typealias CSS = PropertiesBuilder.() -> Unit

object Classes {
    val centered: CSS = {
        margin = Auto.auto
        textAlign = TextAlign.center
    }

    val hidden: CSS = {
        display = None.none
    }
}