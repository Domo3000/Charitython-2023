package data

import kotlinx.html.*

fun HTML.donations() {
    head {
        title("Green Heroes")
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1.0"
        }
        link {
            rel = "stylesheet"
            href = "/static/styles.css"
        }
        link {
            rel = "icon"
            type = "image/png"
            sizes = "16x16"
            href = "/static/favicon-16x16.png"
        }
        link {
            rel = "icon"
            type = "image/png"
            sizes = "32x32"
            href = "/static/favicon-32x32.png"
        }
    }
    body {
        script {
            src = "https://secure.fundraisingbox.com/app/paymentJS?hash=qspblsopqchg0tp9"
            type = "text/javascript"
        }
        noScript {
            +"Bitte Javascript aktivieren"
        }
        a {
            target = "_blank"
            href = "https://www.fundraisingbox.com"
            img {
                src = "https://secure.fundraisingbox.com/images/FundraisingBox-Logo-Widget.png"
                alt = "FundraisingBox Logo"
                classes = setOf("noBorder")
            }
        }
    }
}