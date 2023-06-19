package components

import emotion.react.css
import pages.IndexState
import pages.OrganizeState
import pages.ShareResultsState
import pages.SignUpState
import react.FC
import react.Props
import react.dom.events.MouseEventHandler
import react.dom.html.ReactHTML
import utils.Style
import web.cssom.*
import web.html.HTMLButtonElement

external interface HeaderButtonProps : Props {
    var text: String
    var color: Color
    var disabled: Boolean
    var width: Double
    var onClick: MouseEventHandler<HTMLButtonElement>
}

private val HeaderButton = FC<HeaderButtonProps> { props ->
    ReactHTML.button {
        +props.text
        css {
            width = props.width.px
            height = 80.px
            padding = 15.px
            margin = 20.px
            float = Float.right
            background = props.color
            if (props.disabled) {
                background = Color(Style.greyColor)
                textDecoration = TextDecoration.lineThrough
            }
        }
        disabled = props.disabled
        onClick = props.onClick
    }
}

external interface HeaderProps : Props {
    var currentState: OverviewState
    var stateSetter: (String, OverviewState) -> Unit
}

val Header = FC<HeaderProps> { props ->
    val buttons = listOf(
        Triple(SignUpState, Style.yellowColor, "Cleanup anmelden"),
        Triple(ShareResultsState, Style.pinkColor, "Teile Ergebnisse!"),
        Triple(OrganizeState, Style.blueColor, "Organisiere Event!")
    )
    ReactHTML.div {
        ReactHTML.div {
            // TODO desktop only and introduce hamburger icon menu for mobile
            css {
                margin = Auto.auto
                maxWidth = 1000.px
            }
            id = "header"
            ReactHTML.img {
                css {
                    maxWidth = 200.px
                    objectFit = ObjectFit.contain
                    float = Float.left
                }
                src = "/static/WCD-logo.png"
                onClick = {
                    props.stateSetter("/", IndexState)
                }
            }
            buttons.forEach { (state, c, t) ->
                HeaderButton {
                    text = t
                    color = Color(c)
                    disabled = props.currentState == state
                    width = 110.0
                    onClick = {
                        props.stateSetter("/${state.route}", state)
                    }
                }
            }
        }
    }
}