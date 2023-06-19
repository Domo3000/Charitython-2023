import components.overview
import css.Classes
import emotion.react.css
import pages.*
import react.FC
import react.Props
import react.createElement
import react.dom.html.ReactHTML
import react.router.IndexRoute
import react.router.PathRoute
import react.router.Routes
import react.router.dom.BrowserRouter
import web.cssom.ObjectFit
import web.cssom.px
import web.location.location

private val notFound = FC<Props> {
    ReactHTML.div {
        css(Classes.centered)
        +"404 - Not Found"
    }
    ReactHTML.div {
        css(Classes.centered)
        ReactHTML.img {
            css {
                maxWidth = 300.px
                objectFit = ObjectFit.contain
            }
            src = "/static/logo-oval.png"
            onClick = {
                location.assign("/")
            }
        }
    }
}

val Routing = FC<Props> {
    val routeStates = listOf(
        AdminState,
        ImpressumState,
        OrganizeState,
        ShareResultsState,
        SignUpState
    )

    BrowserRouter {
        Routes {
            IndexRoute {
                index = true
                element = createElement(overview())
            }
            routeStates.forEach { routeState ->
                PathRoute {
                    path = "/${routeState.route}"
                    element = createElement(overview(routeState))
                }
            }
            PathRoute {
                path = "/details/:id"
                element = createElement(overview(DetailsState(null)))
            }

            PathRoute {
                path = "*"
                element = createElement(notFound)
            }
        }
    }
}