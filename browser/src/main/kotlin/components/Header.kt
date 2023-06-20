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

external interface HeaderButtonProps : Props {
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

external interface HamburgerMenuProps : Props {
    var onClick: MouseEventHandler<HTMLButtonElement>
}

private val HamburgerMenu = FC<HamburgerMenuProps> { props ->
    ReactHTML.button {
        +"☰"
        css(ClassNames.phoneElement) {
            fontSize = 3.rem
            background = None.none
            border = None.none
            marginTop = -1.rem
            position = Position.absolute
            top = 6.px
            left = 0.px
            zIndex = integer(3000)
        }
        onClick = props.onClick
    }
}

external interface HeaderProps : Props {
    var currentState: OverviewState
    var stateSetter: (String, OverviewState) -> Unit
}

val Header = FC<HeaderProps> { props ->
    val buttons = listOf(
        Triple(SignUpState, Style.yellowColor, "Mach mit!"),
        Triple(ShareResultsState, Style.pinkColor, "Teile Ergebnisse!"),
        Triple(OrganizeState, Style.blueColor, "Organisiere Event!")
    )

    val (isHamburgerOpen, setHamburgerOpen) = useState(false)

    ReactHTML.div {
        HamburgerMenu {
            onClick = {
                setHamburgerOpen(!isHamburgerOpen)
            }
        }
        // Because the hamburger menu is absolute, we need to fill the place below it with something so the title is
        // not inside the button.
        ReactHTML.div {
            css {
                height = 3.rem
            }
        }
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
                    width = 110.0.px
                    onClick = {
                        props.stateSetter("/${state.route}", state)
                    }
                }
            }
        }
        ReactHTML.div {
            css(ClassNames.phoneElement) {
                display = if (isHamburgerOpen) Display.block else None.none
                position = Position.absolute
                top = 0.px
                left = 0.px
                zIndex = integer(2000)
                width = 100.pct
                height = 100.pct
                paddingTop = 3.rem
                boxSizing = BoxSizing.borderBox
                backgroundColor = Color("#0000004f")
            }
            ReactHTML.div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    flexWrap = FlexWrap.nowrap
                }
                buttons.forEach { (state, c, t) ->
                    HeaderButton {
                        text = t
                        color = Color(c)
                        disabled = props.currentState == state
                        onClick = {
                            setHamburgerOpen(false)
                            props.stateSetter("/${state.route}", state)
                        }
                    }
                }
            }
        }
    }
}