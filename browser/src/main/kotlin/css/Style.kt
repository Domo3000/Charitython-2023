package css

import utils.Style
import web.cssom.Border
import web.cssom.Color
import web.cssom.LineStyle
import web.cssom.px

object Style {
    val border = Border(4.px, LineStyle.solid, Color(Style.blueColor))
}