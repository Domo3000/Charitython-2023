package components

import emotion.react.css
import pages.IndexState
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import utils.Style
import web.cssom.*
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

private object CountdownStyleCommons {
    val fontSize = 2.5.em
    val padding = Padding(0.em, 1.em, 0.em, 1.em)
    val textColor = Color(Style.whiteColor)
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
            justifyContent = JustifyContent.spaceEvenly
            margin = Auto.auto
        }

        ReactHTML.h2 {
            css {
                textAlign = TextAlign.center
                fontSize = 4.em
                color = CountdownStyleCommons.textColor
            }

            +"World Cleanup Day 2023"
        }

        ReactHTML.h3 {
            css {
                textAlign = TextAlign.center
                fontSize = 3.em
                color = CountdownStyleCommons.textColor
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
                    fontSize = CountdownStyleCommons.fontSize
                    padding = CountdownStyleCommons.padding
                }

                ReactHTML.p {
                    css {
                        color = CountdownStyleCommons.textColor
                    }
                    +"XY Tage"
                }
            }

            ReactHTML.div {
                css {
                    backgroundColor = Color(Style.pinkColor)
                    fontSize = CountdownStyleCommons.fontSize
                    padding = CountdownStyleCommons.padding
                }

                ReactHTML.p {
                    css {
                        color = CountdownStyleCommons.textColor
                    }
                    +"XY Stunden"
                }
            }

            ReactHTML.div {
                css {
                    backgroundColor = Color(Style.blueColor)
                    fontSize = CountdownStyleCommons.fontSize
                    padding = CountdownStyleCommons.padding
                }

                ReactHTML.p {
                    css {
                        color = CountdownStyleCommons.textColor
                    }
                    +"XY Minuten"
                }
            }
        }

        ReactHTML.div {
            css {
                marginLeft = Auto.auto
                marginRight = Auto.auto
                fontSize = 2.5.em
                padding = Padding(0.em, 2.em)
            }

            ReactHTML.p {
                css {
                    textAlign = TextAlign.center
                    color = CountdownStyleCommons.textColor
                }

                +"Am 16. September 2023 findet der World Cleanup Day statt. Weltweit werden an diesem Tag Straßen, Flüsse, Wälder "
                +"und Strände von Müll befreit. Wir koordinieren dieses Event österreichweit."
            }

            ReactHTML.p {
                css {
                    textAlign = TextAlign.center
                    color = CountdownStyleCommons.textColor
                }

                +"Mit unserem Engagement wollen wir weltweit ein Zeichen im Sinne des Umweltschutzes setzen und gleichzeitig in der "
                +"Bevölkerung das Bewusstsein für Abfallvermeidung steigern."
            }
        }
    }

    Footer { stateSetter = { route, newState -> changeState(route, newState) } }
}
