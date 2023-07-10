package css

import utils.Style
import web.cssom.Border
import web.cssom.Color
import web.cssom.LineStyle
import web.cssom.px

object Style {
    fun backgroundColor(opacity: Int = 100) = Color(Style.backgroundColor(opacity))
    val blueColor = Color(Style.blueColor)
    val pinkColor = Color(Style.pinkColor)
    val yellowColor = Color(Style.yellowColor)
    val whiteColor = Color(Style.whiteColor)
    val blackColor = Color("black")

    val border = Border(4.px, LineStyle.solid, Color(Style.blueColor))
}