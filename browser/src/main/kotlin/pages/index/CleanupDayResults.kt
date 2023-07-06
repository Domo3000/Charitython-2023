package pages.index

import emotion.react.css
import kotlinx.datetime.toJSDate
import model.CleanupDayResultsDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import utils.Style
import web.cssom.*

private external interface InfoProps : Props {
    var icon: String
    var text: String
}

private val Info = FC<InfoProps> { props ->
    ReactHTML.div {
        css {
            width = 33.33.pct
            float = Float.left
        }
        ReactHTML.div {
            css {
                height = 132.px
                width = 132.px
                borderRadius = 50.pct
                margin = Auto.auto
                background = Color(Style.greyColor)
            }
            ReactHTML.div {
                css {
                    height = 132.px
                    textAlign = TextAlign.center
                }
                ReactHTML.i {
                    css(ClassName("fa-solid fa-${props.icon}")) {
                        paddingTop = 35.px
                        fontSize = 66.px
                        color = Color(Style.whiteColor)
                    }
                }
            }
       }
        ReactHTML.p {
            +props.text
            css {
                textAlign = TextAlign.center
                color = Color(Style.whiteColor)
            }
        }
    }
}

external interface CleanupDayResultsProps : Props {
    var results: CleanupDayResultsDTO
}

val CleanupDayResults = FC<CleanupDayResultsProps> { props ->
    val date = props.results.timestamp.toJSDate()

    ReactHTML.div {
        ReactHTML.p {
            css {
                textAlign = TextAlign.center
                fontSize = IndexCommons.fontSize
                color = IndexCommons.textColor
            }
            +"Beim WorldCleanupDay ${date.getFullYear()} waren"
        }
    }
    ReactHTML.div {
        Info {
            icon = "users"
            text = "über ${props.results.participants} grüne Held:innen"
        }
        Info {
            icon = "trash-can"
            text = "und haben ${props.results.garbage.toInt()} Tonnen Müll"
        }
        Info {
            icon = "clock"
            text = "in 24 Stunden gesammelt"
        }
    }
}
