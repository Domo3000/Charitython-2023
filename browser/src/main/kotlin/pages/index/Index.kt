package pages.index

import components.OverviewPage
import components.OverviewProps
import emotion.react.css
import model.CleanupDayDTO
import model.CleanupDayResultsDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import utils.Style
import web.cssom.*

object IndexCommons {
    val fontSize = 2.5.em
    val padding = Padding(0.em, 1.em, 0.em, 1.em)
    val textColor = Color(Style.whiteColor)
}

external interface CleanupDayProps : Props {
    var cleanupDay: CleanupDayDTO
}

object IndexPage : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val (results, setResults) = useState<CleanupDayResultsDTO?>(null)

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
                    CleanupDaySetHeader {
                        this.cleanupDay = cleanupDay
                    }
                }  ?: run {
                    CleanupDayNotSetHeader { }
                }
            }

            props.cleanupDay?.let { cleanupDay ->
                CleanupDaySetBody {
                    this.cleanupDay = cleanupDay
                }
            }

            results?.let { previousCleanupResults ->
                CleanupDayResults {
                    this.results = previousCleanupResults
                }
            }

            useEffectOnce {
                Requests.getMessage("/data/previousCleanupDayResults") { message ->
                    setResults((message as CleanupDayResultsDTO))
                }
            }
        }
}