package pages

import components.HeaderButton
import components.OverviewProps
import components.OverviewState
import emotion.react.css
import react.FC
import react.dom.html.ReactHTML
import utils.Style
import web.cssom.*

object IndexState : OverviewState {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            ReactHTML.div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.spaceEvenly
                }

                ReactHTML.h1 {
                    css {
                        fontSize = 2.2.em
                        background = Color(Style.whiteColor)
                    }

                    +"Am 16. September 2023 ist wieder World Cleanup Day!"
                }
                ReactHTML.h2 {
                    css {
                        fontSize = 3.em
                        background = Color(Style.whiteColor)
                    }

                    +"In ganz Österreich wird aufgeräumt. Gemeinsam schaffen wir eine saubere Umwelt."
                }

                ReactHTML.div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.row
                    }

                    HeaderButton {
                        text = "Cleanup anmelden"
                        color = Color(Style.yellowColor)
                    }

                    HeaderButton {
                        text = "Cleanup finden"
                        color = Color(Style.pinkColor)
                    }
                }
            }
        }
}
