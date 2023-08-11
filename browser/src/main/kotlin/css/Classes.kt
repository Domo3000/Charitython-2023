package css

import csstype.PropertiesBuilder
import web.cssom.*

private typealias CSS = PropertiesBuilder.() -> Unit

fun responsive(desktop: CSS, mobile: CSS): CSS = {
    media("only screen and (min-width: 800px)") {
        desktop()
    }
    media("only screen and (max-width: 800px)") {
        mobile()
    }
}

object Classes {
    val centered: CSS = {
        margin = Auto.auto
        textAlign = TextAlign.center
    }

    val limitedWidth: CSS = {
        margin = Auto.auto
        maxWidth = 1000.px
        responsive(desktop = {
            marginTop = 150.px
        }, mobile = {
            marginTop = 90.px
        })()
    }

    val hidden: CSS = {
        display = None.none
    }
}