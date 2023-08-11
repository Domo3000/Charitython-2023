package pages.index

import components.OverviewPage
import css.ClassNames
import emotion.react.css
import kotlinx.datetime.toJSDate
import react.FC
import react.dom.html.ReactHTML
import css.Style
import css.responsive
import model.CleanupDayDTO
import pages.FindCleanup
import pages.RegisterCleanupEvent
import react.Props
import utils.getMonthString
import web.cssom.*

external interface CleanupDayHeaderProps : Props {
    var cleanupDay: CleanupDayDTO?
    var stateSetter: (String, OverviewPage) -> Unit
}

val CleanupDayHeader = FC<CleanupDayHeaderProps> { props ->
    ReactHTML.h1 {
        css {
            position = Position.absolute
            responsive(desktop = {
                top = 20.pct
                fontSize = 2.em
            }, mobile = {
                top = 10.pct
                fontSize = 1.5.em
            })()
            color = Style.pinkColor
        }

        props.cleanupDay?.let {
            val date = it.timestamp.toJSDate()

            +"Am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} ist wieder World Cleanup Day!"
        } ?: run {
            +"Wir freuen uns sehr, dass du beim nächsten World Cleanup Day dabei sein möchtest!"
        }
    }

    ReactHTML.h2 {
        css {
            position = Position.absolute
            responsive(desktop = {
                top = 40.pct
                fontSize = 2.5.em
            }, mobile = {
                top = 20.pct
                fontSize = 2.em
            })()
            color = Style.pinkColor
        }

        props.cleanupDay?.let {
            +"In ganz Österreich wird aufgeräumt. Gemeinsam schaffen wir eine saubere Umwelt."
        } ?: run {
            +"Leider ist dieser noch nicht festgelegt worden!"
        }
    }

    props.cleanupDay?.let {
        ReactHTML.div {
            css(ClassNames.desktopElement) {
                position = Position.absolute
                top = 60.pct
            }
            components.HeaderButton {
                text = "Cleanup anmelden"
                color = Style.yellowColor
                link = "/${RegisterCleanupEvent.route}"
                disabled = false
                width = 110.0.px
                onClick = {
                    props.stateSetter("/${RegisterCleanupEvent.route}", RegisterCleanupEvent)
                }
            }
        }

        ReactHTML.div {
            css(ClassNames.desktopElement) {
                position = Position.absolute
                top = 60.pct
                left = 150.px
            }
            components.HeaderButton {
                text = "Cleanup finden"
                color = Style.pinkColor
                link = "/${FindCleanup.route}"
                disabled = false
                width = 110.0.px
                onClick = {
                    props.stateSetter("/${FindCleanup.route}", FindCleanup)
                }
            }
        }
    }
}