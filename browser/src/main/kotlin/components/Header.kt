package components

import css.ClassNames
import emotion.react.css
import pages.IndexState
import pages.OrganizeState
import pages.ShareResultsState
import pages.SignUpState
import react.FC
import react.Props
import react.dom.events.MouseEventHandler
import react.dom.html.ReactHTML
import react.useState
import utils.Style
import web.cssom.*
import web.html.HTMLButtonElement

private const val LOGO = "/static/WCD-logo.png"

private typealias MenuButton = Triple<RouteState, String, String>

private external interface HeaderButtonProps : Props {
    var text: String
    var color: Color
    var disabled: Boolean
    var width: Width
    var margin: Margin
    var onClick: MouseEventHandler<HTMLButtonElement>
}

private val HeaderButton = FC<HeaderButtonProps> { props ->
    ReactHTML.button {
        +props.text
        css {
            width = props.width
            margin = props.margin
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

private external interface MenuProps : Props {
    var buttons: List<MenuButton>
    var currentState: OverviewState
    var stateSetter: (String, OverviewState) -> Unit
}

private val PhoneHeader = FC<MenuProps> { props ->
    val (isMenuOpen, setMenuOpen) = useState(false)

    ReactHTML.div {
        css(ClassNames.phoneElement) {
            height = 60.px
        }

        ReactHTML.img {
            css {
                height = 100.pct
                objectFit = ObjectFit.contain
                float = Float.left
            }
            src = LOGO
            onClick = {
                setMenuOpen(false)
                props.stateSetter("/", IndexState)
            }
        }
        ReactHTML.button {
            +"☰"
            css {
                fontSize = 3.rem
                background = None.none
                border = None.none
                float = Float.right
            }
            onClick = { setMenuOpen(!isMenuOpen) }
        }

        ReactHTML.div {
            css {
                display = if (isMenuOpen) Display.block else None.none
                position = Position.absolute
                top = 60.px
                left = 0.px
                width = 100.pct
                zIndex = integer(2000)
                marginTop = 10.px
                background = Color(Style.backgroundColor)
            }
            ReactHTML.div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    flexWrap = FlexWrap.nowrap
                }
                props.buttons.forEach { (state, c, t) ->
                    HeaderButton {
                        text = t
                        color = Color(c)
                        disabled = props.currentState == state
                        onClick = {
                            setMenuOpen(false)
                            props.stateSetter("/${state.route}", state)
                        }
                    }
                }
            }
        }
    }
}

private val DesktopHeader = FC<MenuProps> { props ->
    ReactHTML.div {
        css(ClassNames.desktopElement) {
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
            src = LOGO
            onClick = {
                props.stateSetter("/", IndexState)
            }
        }
        props.buttons.forEach { (state, c, t) ->
            HeaderButton {
                text = t
                color = Color(c)
                disabled = props.currentState == state
                width = 110.0.px
                onClick = {
                    props.stateSetter("/${state.route}", state)
                }
            }
        }
    }
}

external interface HeaderProps : Props {
    var currentState: OverviewState
    var stateSetter: (String, OverviewState) -> Unit
}

val Header = FC<HeaderProps> { props ->
    val buttons: List<MenuButton> = listOf(
        Triple(SignUpState, Style.yellowColor, "Mach mit!"),
        Triple(ShareResultsState, Style.pinkColor, "Teile Ergebnisse!"),
        Triple(OrganizeState, Style.blueColor, "Organisiere Event!")
    )

    ReactHTML.div {
        PhoneHeader {
            this.buttons = buttons
            stateSetter = props.stateSetter
            currentState = props.currentState
        }

        DesktopHeader {
            this.buttons = buttons
            stateSetter = props.stateSetter
            currentState = props.currentState
        }
    }
}