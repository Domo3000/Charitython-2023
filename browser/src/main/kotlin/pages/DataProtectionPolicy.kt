package pages

import components.RoutePage
import css.Classes
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML

external interface ParagraphProps : Props {
    var header: String
    var paragraphs: List<String>
}

val Paragraph = FC<ParagraphProps> { props ->
    ReactHTML.h2 {
        +props.header
    }
    props.paragraphs.forEach {
        ReactHTML.p {
            +it
        }
    }
}

object DataProtectionPolicyPage : RoutePage {
    override val route: String = "dataProtectionPolicy"
    override val component: FC<Props>
        get() = FC {
            ReactHTML.div {
                css(Classes.limitedWidth)

                ReactHTML.h1 {
                    +"Datenschutz"
                }

                Paragraph {
                    header = "Datenschutzerklärung von Green Heroes Austria"
                    paragraphs =
                        listOf("Green Heroes Austria legt großen Wert auf den Schutz und die Sicherheit Ihrer persönlichen Daten, die bei uns hinterlegt wurden. Im Hinblick auf die Datenschutz-Grundverordnung (DSGVO) der Europäischen Union informieren wir Sie nachfolgend über die Verwendung Ihrer personenbezogenen Daten, die beim Besuch unserer Webseite erhoben werden. Die vorliegende Datenschutzerklärung gilt allerdings nicht für die Webseiten anderer Anbieter, zu denen wir verlinken.")
                }

                Paragraph {
                    header = "Hinweis zur verantwortlichen Stelle"
                    paragraphs = listOf(
                        "Für die Datenverarbeitung auf unserer Webseite ist folgende Stelle verantwortlich:",
                        "Green Heroes Austria",
                        "Lerchenfelder Straße 44",
                        "1080 Wien",
                        "Email: office@greenheroes.at"
                    )
                }

                Paragraph {
                    header = "Datenverarbeitung"
                    paragraphs =
                        listOf(
                            "Beim Besuch unserer Website werden Ihre Daten automatisch durch unsere IT-Systeme erfasst. Hierbei handelt es sich um die Erfassung technischer Daten, welche beim Aufrufen unserer Website gesammelt werden. Diese Daten geben wir nicht ohne Ihre Einwilligung weiter.",
                            "Ebenso werden Ihre personenbezogenen Daten erfasst, wenn Sie unser Kontaktformular zwecks Kontaktaufnahme verwenden oder Sie sich per E-Mail zur nächsten Fortbildung anmelden. Dabei werden Ihr Vor- und Nachname sowie Ihre E-Mail erfasst. Diese Daten werden nur solange von uns gespeichert als die Bearbeitung Ihrer Anfrage dies erfordert, jedoch längstens sechs Monate nach Erfassung."
                        )
                }

                Paragraph {
                    header = "Kommunikationskanäle"
                    paragraphs = listOf(
                        "Der Verein Green Heroes Austria informiert laufend seine Mitglieder über aktuelle Themen im Zusammenhang mit dem Verein. Dafür werden auch soziale Medien, wie Facebook und Instagram eingesetzt. Über die Website können die einzelnen Kommunikationskanäle erreicht werden. Dabei werden keine personenbezogenen Daten gesammelt."
                    )
                }

                Paragraph {
                    header = "Ihre Rechte"
                    paragraphs = listOf(
                        "Der Verein Green Heroes Austria informiert laufend seine Mitglieder über aktuelle Themen im Zusammenhang mit dem Verein. Dafür werden auch soziale Medien, wie Facebook und Instagram eingesetzt. Über die Website können die einzelnen Kommunikationskanäle erreicht werden. Dabei werden keine personenbezogenen Daten gesammelt."
                    )
                }

                Paragraph {
                    header = "Recht auf Datenübertragbarkeit"
                    paragraphs = listOf(
                        "Sie haben das Recht, die von uns verarbeiteten Daten sich oder einem Dritten zukommen zu lassen. Diese Datenübertragung kann im Rahmen der technischen Möglichkeiten realisiert werden."
                    )
                }

                Paragraph {
                    header = "Widerruf Ihrer Einwilligung zur Datenverarbeitung"
                    paragraphs = listOf(
                        "Um Ihre Daten verarbeiten zu können, benötigen wir Ihre ausdrückliche Einwilligung. Sollten Sie mit der Verarbeitung nicht einverstanden sein, können Sie eine bereits erteilte Einwilligung jederzeit widerrufen. Eine formlose Mitteilung per E-Mail reicht für diesen Zweck vollkommen aus. Die Rechtmäßigkeit der bis zum Widerruf erfolgten Datenverarbeitung bleibt von diesem unberührt."
                    )
                }
            }
        }
}
