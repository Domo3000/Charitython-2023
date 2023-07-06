package components

import css.ClassNames
import emotion.react.css
import pages.*
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import utils.Style
import web.cssom.*

private const val DEFAULT_LOGO = "/static/WCD-logo-no-date.png"

private typealias MenuButton = Triple<RoutePage, String, String>

private external interface MenuProps : Props {
    var fileName: String?
    var buttons: List<MenuButton>
    var currentPage: OverviewPage
    var pageSetter: (String, OverviewPage) -> Unit
}

private val PhoneHeader = FC<MenuProps> { props ->
    val (isMenuOpen, setMenuOpen) = useState(false)

    ReactHTML.div {
        css(ClassNames.mobileElement) {
            height = 60.px
        }

        ReactHTML.img {
            css {
                height = 100.pct
                objectFit = ObjectFit.contain
                float = Float.left
            }
            src = props.fileName ?: DEFAULT_LOGO
            onClick = {
                setMenuOpen(false)
                props.pageSetter("/", IndexPage)
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
                        disabled = props.currentPage == state
                        onClick = {
                            setMenuOpen(false)
                            props.pageSetter("/${state.route}", state)
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
            src = props.fileName ?: DEFAULT_LOGO
            onClick = {
                props.pageSetter("/", IndexPage)
            }
        }
        props.buttons.forEach { (state, c, t) ->
            HeaderButton {
                text = t
                color = Color(c)
                disabled = props.currentPage == state
                width = 110.0.px
                onClick = {
                    props.pageSetter("/${state.route}", state)
                }
            }
        }
    }
}

private object ButtonColorPicker {
    val colors = listOf(Style.yellowColor, Style.pinkColor, Style.blueColor)
    var next = 0

    fun nextColor(): String = colors[next++ % colors.size]
}

external interface HeaderProps : Props {
    var fileName: String?
    var currentPage: OverviewPage
    var pageSetter: (String, OverviewPage) -> Unit
}

val Header = FC<HeaderProps> { props ->
    val buttons: List<MenuButton> = listOf(
        RegisterCleanupEvent to "Cleanup anmelden",
        SignUpPage to "Cleanup finden",
        ShareResultsPage to "Ergebnisse teilen"
    ).map { (page, text) -> Triple(page, ButtonColorPicker.nextColor(), text) }

    ReactHTML.div {
        PhoneHeader {
            fileName = props.fileName
            this.buttons = buttons
            pageSetter = props.pageSetter
            currentPage = props.currentPage
        }

        DesktopHeader {
            fileName = props.fileName
            this.buttons = buttons.reversed()
            pageSetter = props.pageSetter
            currentPage = props.currentPage
        }
    }
}