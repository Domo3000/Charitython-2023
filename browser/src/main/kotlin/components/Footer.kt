package components

import css.Classes
import emotion.react.css
import pages.DataProtectionPolicyPage
import pages.ImpressumPage
import react.FC
import react.dom.html.ReactHTML
import web.cssom.Clear
import web.cssom.px

val Footer = FC<OverviewProps> { props ->
    ReactHTML.div {
        css {
            paddingTop = 100.px
            clear = Clear.left
        }
        ReactHTML.div {
            css(Classes.centered)
            ReactHTML.a {
                css {
                    margin = 20.px
                }
                href = "/${ImpressumPage.route}"
                onClick = {
                    it.preventDefault()
                    props.stateSetter("/${ImpressumPage.route}", ImpressumPage)
                }
                +"Impressum"
            }
            ReactHTML.a {
                css {
                    margin = 20.px
                }
                href = "/${DataProtectionPolicyPage.route}"
                onClick = {
                    it.preventDefault()
                    props.stateSetter("/${DataProtectionPolicyPage.route}", DataProtectionPolicyPage)
                }
                +"Datenschutz"
            }
        }
    }
}