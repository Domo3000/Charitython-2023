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

        CleanupDayTimer {
            cleanupDay = props.cleanupDay
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