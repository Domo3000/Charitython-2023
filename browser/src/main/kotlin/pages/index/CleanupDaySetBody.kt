package pages.index

import css.Style
import emotion.react.css
import kotlinx.datetime.Clock
import kotlinx.datetime.toJSDate
import kotlinx.datetime.toKotlinInstant
import react.FC
import react.dom.html.ReactHTML
import utils.getMonthString
import web.cssom.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

val CleanupDaySetBody = FC<CleanupDayProps> { props ->
    val date = props.cleanupDay.timestamp.toJSDate()

    ReactHTML.div {
        css {
            height = 100.vh
            backgroundColor = Style.backgroundColor()
            display = Display.flex
            flexDirection = FlexDirection.column
            justifyContent = JustifyContent.spaceEvenly
            margin = Auto.auto
        }

        ReactHTML.h2 {
            css {
                textAlign = TextAlign.center
                fontSize = 4.em
                color = IndexCommons.textColor
            }

            +"World Cleanup Day ${date.getFullYear()} "
        }

        ReactHTML.h3 {
            css {
                textAlign = TextAlign.center
                fontSize = 3.em
                color = IndexCommons.textColor
            }

            +"Wir setzen weltweit ein Zeichen für den Umweltschutz!"
        }

        ReactHTML.div {
            val difference = date.toKotlinInstant().minus(Clock.System.now())
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
                    backgroundColor = Style.yellowColor
                    fontSize = IndexCommons.fontSize
                    padding = IndexCommons.padding
                }

                ReactHTML.p {
                    css {
                        color = IndexCommons.textColor
                    }
                    +"$daysDifference Tage"
                }
            }

            ReactHTML.div {
                css {
                    backgroundColor = Style.pinkColor
                    fontSize = IndexCommons.fontSize
                    padding = IndexCommons.padding
                }

                ReactHTML.p {
                    css {
                        color = IndexCommons.textColor
                    }
                    +"$hoursDifference Stunden" // TODO store date with 6 in the morning or something
                }
            }

            ReactHTML.div {
                css {
                    backgroundColor = Style.blueColor
                    fontSize = IndexCommons.fontSize
                    padding = IndexCommons.padding
                }

                ReactHTML.p {
                    css {
                        color = IndexCommons.textColor
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
                    color = IndexCommons.textColor
                }

                +"Am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} findet der World Cleanup Day statt. Weltweit werden an diesem Tag Straßen, Flüsse, Wälder und Strände von Müll befreit. Wir koordinieren dieses Event österreichweit."
            }

            ReactHTML.p {
                css {
                    textAlign = TextAlign.center
                    color = IndexCommons.textColor
                }

                +"Mit unserem Engagement wollen wir weltweit ein Zeichen im Sinne des Umweltschutzes setzen und gleichzeitig in der Bevölkerung das Bewusstsein für Abfallvermeidung steigern."
            }
        }
    }
}