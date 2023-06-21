package components

import emotion.react.css
import pages.AboutState
import pages.DonateState
import pages.FindEventState
import pages.IndexState
import pages.NewsletterState
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

val HeaderButton = FC<HeaderButtonProps> { props ->
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
        Triple(FindEventState, Style.pinkColor, "Cleanup finden"),
        Triple(AboutState, Style.blueColor, "Über den WCD"),
        Triple(ShareResultsState, Style.yellowColor, "Ergebnisse teilen"),
        // Triple(DonateState, Style.pinkColor, "Spenden"),
        // Triple(NewsletterState, Style.blueColor, "Newsletter"),
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