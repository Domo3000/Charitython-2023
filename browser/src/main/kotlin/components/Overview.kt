package components

import emotion.react.css
import pages.IndexState
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import utils.Style
import web.cssom.Auto
import web.cssom.BackgroundAttachment
import web.cssom.BackgroundRepeat
import web.cssom.BackgroundSize
import web.cssom.Clear
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.TextAlign
import web.cssom.em
import web.cssom.px
import web.cssom.url
import web.cssom.vh
import web.history.history

external interface OverviewProps : Props {
    var stateSetter: (String, OverviewState) -> Unit
}

interface OverviewState {
    val component: FC<OverviewProps>
}

interface RouteState : OverviewState {
    val route: String
}

object NotFoundState : OverviewState {
    override val component = FC<Props> {
        ReactHTML.div {
            +"404 - Not Found"
        }
    }
}

fun overview(component: OverviewState = IndexState) = FC<Props> {
    val (state, setState) = useState(component)

    fun changeState(route: String, newState: OverviewState) {
        history.replaceState(Unit, "", route)
        setState(newState)
    }

    ReactHTML.div {
        css {
            backgroundImage = url("/static/landing-background-1.jpeg")
            backgroundRepeat = BackgroundRepeat.noRepeat
            backgroundSize = BackgroundSize.cover
            backgroundAttachment = BackgroundAttachment.fixed
            height = 100.vh
        }

        Header {
            currentState = state
            stateSetter = { route, newState -> changeState(route, newState) }
        }

        ReactHTML.div {
            id = "content-holder"
            css {
                margin = Auto.auto
                maxWidth = 1000.px
                minHeight = 600.px
                paddingBottom = 200.px
                clear = Clear.left
            }

            state.component { stateSetter = { route, newState -> changeState(route, newState) } }
        }
    }

    ReactHTML.div {
        css {
            height = 100.vh
            backgroundColor = Color(Style.backgroundColor)
            display = Display.flex
            flexDirection = FlexDirection.column
            margin = Auto.auto
        }

        ReactHTML.h2 {
            css {
                textAlign = TextAlign.center
                fontSize = 4.em
            }

            +"World Cleanup Day 2023"
        }

        ReactHTML.h3 {
            css {
                textAlign = TextAlign.center
                fontSize = 3.em
            }

            +"Wir setzen weltweit ein Zeichen für den Umweltschutz!"
        }

        ReactHTML.div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                gap = 4.em
                justifyContent = JustifyContent.center
            }

            ReactHTML.div {
                css {
                    backgroundColor = Color(Style.yellowColor)
                }

                ReactHTML.p {
                    +"XY Tage"
                }
            }

            ReactHTML.div {
                css {
                    backgroundColor = Color(Style.pinkColor)
                }

                ReactHTML.p {
                    +"XY Stunden"
                }
            }

            ReactHTML.div {
                css {
                    backgroundColor = Color(Style.blueColor)
                }

                ReactHTML.p {
                    +"XY Minuten"
                }
            }
        }

        ReactHTML.div {
            css {
                textAlign = TextAlign.center
            }

            ReactHTML.p {
                +"Am 16. September 2023 findet der World Cleanup Day statt. Weltweit werden an diesem Tag Straßen, Flüsse, Wälder "
                +"und Strände von Müll befreit. Wir koordinieren dieses Event österreichweit."
            }

            ReactHTML.p {
                +"Mit unserem Engagement wollen wir weltweit ein Zeichen im Sinne des Umweltschutzes setzen und gleichzeitig in der "
                +"Bevölkerung das Bewusstsein für Abfallvermeidung steigern."
            }
        }
    }

    Footer { stateSetter = { route, newState -> changeState(route, newState) } }
}
