package components

import css.ClassNames
import css.Style
import emotion.react.css
import kotlinx.browser.window
import pages.*
import pages.index.IndexPage
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import react.useState
import web.cssom.*

private const val DEFAULT_LOGO = "/static/WCD-logo-no-date.png"

private typealias MenuButton = Triple<RoutePage, Color, String>

private external interface MenuProps : Props {
    var fileName: String?
    var buttons: List<MenuButton>
    var currentPage: OverviewPage
    var pageSetter: (String, OverviewPage) -> Unit
    var menuOpen: Boolean
    var menuOpenSetter: StateSetter<Boolean>
}

private val PhoneHeader = FC<MenuProps> { props ->
    ReactHTML.div {
        css(ClassNames.mobileElement) {
            height = 60.px // TODO store somewhere and reuse in limitedWidth class marginTop
        }

        ReactHTML.a {
            href = "/"
            css {
                height = 100.pct
                float = Float.left
            }
            onClick = {
                it.preventDefault()
                props.menuOpenSetter(false)
                props.pageSetter("/", IndexPage)
            }

            ReactHTML.img {
                css {
                    height = 100.pct
                    objectFit = ObjectFit.contain
                }
                src = props.fileName ?: DEFAULT_LOGO
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
            onClick = { props.menuOpenSetter(!props.menuOpen) }
        }

        ReactHTML.div {
            css {
                display = if (props.menuOpen) Display.block else None.none
                position = Position.absolute
                top = 60.px
                left = 0.px
                width = 100.pct
                zIndex = integer(2000)
                paddingTop = 10.px
                background = Style.backgroundColor()
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
                        link = "/${state.route}"
                        color = c
                        disabled = props.currentPage == state
                        onClick = {
                            props.menuOpenSetter(false)
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
            height = 140.px
        }
        id = "header"
        ReactHTML.a {
            href = "/"
            css {
                height = 100.pct
                maxWidth = 200.px
                float = Float.left
            }
            onClick = {
                it.preventDefault()
                props.pageSetter("/", IndexPage)
            }
            ReactHTML.img {
                css {
                    maxWidth = 200.px
                    objectFit = ObjectFit.contain
                }
                src = props.fileName ?: DEFAULT_LOGO
            }
        }
        props.buttons.forEach { (state, c, t) ->
            HeaderButton {
                text = t
                link = "/${state.route}"
                color = c
                disabled = props.currentPage == state
                width = 110.0.px
                onClick = {
                    props.pageSetter("/${state.route}", state)
                }
            }
        }
    }
}

private class ButtonColorPicker {
    val colors = listOf(Style.yellowColor, Style.pinkColor, Style.blueColor)
    var next = 0

    fun nextColor(): Color = colors[next++ % colors.size]
}

external interface HeaderProps : Props {
    var fileName: String?
    var currentPage: OverviewPage
    var pageSetter: (String, OverviewPage) -> Unit
}

val Header = FC<HeaderProps> { props ->
    val colorPicker = ButtonColorPicker()
    val buttons: List<MenuButton> = listOf(
        RegisterCleanupEvent to "Cleanup anmelden",
        FindCleanup to "Cleanup finden",
        ShareResultsPage to "Ergebnisse teilen",
        Donations to "Spenden"
    ).map { (page, text) -> Triple(page, colorPicker.nextColor(), text) }

    val (scrollY, setScrollY) = useState(0.0)
    val (isMenuOpen, setMenuOpen) = useState(false)

    ReactHTML.div {
        css {
            position = Position.fixed
            top = 0.px
            left = 0.px
            width = 100.pct
            zIndex = integer(2000)
            background = if (scrollY == 0.0 || isMenuOpen) {
                Style.backgroundColor()
            } else if (scrollY < 200.0) {
                Style.backgroundColor((5000 / scrollY).toInt())
            } else {
                Style.backgroundColor(0)
            }
            opacity = if (scrollY < 200.0 || isMenuOpen) {
                number(1.0)
            } else if (scrollY < 400.0) {
                number((50 / (scrollY - 200.0)))
            } else {
                number(0.0)
            }
            if (scrollY > 400.0 && !isMenuOpen) {
                display = None.none
            }
        }
        PhoneHeader {
            fileName = props.fileName
            this.buttons = buttons
            pageSetter = props.pageSetter
            currentPage = props.currentPage
            menuOpen = isMenuOpen
            menuOpenSetter = setMenuOpen
        }

        DesktopHeader {
            fileName = props.fileName
            this.buttons = buttons.reversed()
            pageSetter = props.pageSetter
            currentPage = props.currentPage
        }
    }

    window.onscroll = {
        setScrollY(window.scrollY)
    }
}