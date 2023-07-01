package pages

import components.HeaderButton
import components.OverviewPage
import components.OverviewProps
import emotion.react.css
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.toJSDate
import kotlinx.datetime.toKotlinInstant
import react.FC
import react.dom.html.ReactHTML
import utils.Style
import utils.getMonthString
import web.cssom.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private object CountdownStyleCommons {
    val fontSize = 2.5.em
    val padding = Padding(0.em, 1.em, 0.em, 1.em)
    val textColor = Color(Style.whiteColor)
}

// TODO improve overall style
object IndexPage : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            ReactHTML.div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.spaceEvenly
                    backgroundImage = url("/static/landing-background-1.jpeg")
                    backgroundRepeat = BackgroundRepeat.noRepeat
                    backgroundSize = BackgroundSize.cover
                    backgroundAttachment = BackgroundAttachment.fixed
                    height = 100.vh
                }

                props.cleanupDay?.let { cleanupDay ->
                    val date = cleanupDay.timestamp.toJSDate()

                    ReactHTML.h1 {
                        css {
                            fontSize = 2.2.em
                            background = Color(Style.whiteColor)
                        }

                        +"Am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} ist wieder World Cleanup Day!"
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
                }  ?: run {
                    ReactHTML.h1 {
                        css {
                            fontSize = 2.2.em
                            background = Color(Style.whiteColor)
                        }

                        +"Wir freuen uns sehr, dass du beim nächsten World Cleanup Day dabei sein möchtest!"
                    }
                    ReactHTML.h2 {
                        css {
                            fontSize = 3.em
                            background = Color(Style.whiteColor)
                        }

                        +"Leider ist dieser noch nicht festgelegt worden!"
                    } // TODO show amount of garbage collected in last cleanupDay
                }
            }

            props.cleanupDay?.let { cleanupDay ->
                val date = cleanupDay.timestamp.toJSDate()

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

                        +"World Cleanup Day ${date.getFullYear()} "
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
                        val difference = date.toKotlinInstant().minus(now())
                        val daysDifference = difference.inWholeDays
                        val hoursDifference = difference.minus(daysDifference.days).inWholeHours
                        val minuteDifference = difference.minus(daysDifference.days).minus(hoursDifference.hours).inWholeMinutes

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
                                +"$daysDifference Tage"
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
                                +"$hoursDifference Stunden" // TODO store date with 6 in the morning or something
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
                                +"$minuteDifference Minuten"
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

                            +"Am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} der World Cleanup Day statt. Weltweit werden an diesem Tag Straßen, Flüsse, Wälder und Strände von Müll befreit. Wir koordinieren dieses Event österreichweit."
                        }

                        ReactHTML.p {
                            css {
                                textAlign = TextAlign.center
                                color = CountdownStyleCommons.textColor
                            }

                            +"Mit unserem Engagement wollen wir weltweit ein Zeichen im Sinne des Umweltschutzes setzen und gleichzeitig in der Bevölkerung das Bewusstsein für Abfallvermeidung steigern."
                        }
                    }
                }
            }
        }
}
