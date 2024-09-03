import components.overview
import pages.*
import pages.admin.AdminPage
import react.FC
import react.Props
import react.createElement
import react.router.IndexRoute
import react.router.PathRoute
import react.router.Routes
import react.router.dom.BrowserRouter

val Routing = FC<Props> {
    val pages = listOf(
        AdminPage,
        ImpressumPage,
        DataProtectionPolicyPage,
        RegisterCleanupEvent,
        ShareResultsPage,
        FindCleanup,
        Donations
    )

    BrowserRouter {
        Routes {
            IndexRoute {
                index = true
                element = createElement(overview())
            }
            pages.forEach { page ->
                PathRoute {
                    path = "/${page.route}"
                    element = createElement(overview(page))
                }
            }
            PathRoute {
                path = "/details/:id"
                element = createElement(overview(DetailsPage(null)))
            }

            PathRoute {
                path = "*"
                element = createElement(overview(NotFoundPage))
            }
        }
    }
}