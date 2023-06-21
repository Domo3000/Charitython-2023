package pages

import components.OverviewProps
import components.OverviewPage
import emotion.react.css
import io.kvision.MapsModule
import io.kvision.maps.LeafletObjectFactory.icon
import io.kvision.maps.LeafletObjectFactory.map
import io.kvision.maps.LeafletObjectFactory.marker
import io.kvision.maps.LeafletObjectFactory.tileLayer
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.react.reactWrapper
import org.w3c.dom.HTMLElement
import react.FC
import react.Props
import react.create
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import react.useEffectOnce
import web.cssom.px
import web.dom.document
import kotlinx.browser.document as ktxDocument

object IndexPage : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            ReactHTML.div {
                +"Overview"
            }

            ReactHTML.div {
                css {
                    height = 800.px
                }
                id = "map-holder"
            }

            useEffectOnce {
                MapsModule.initialize()

                hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
                    val map = map(ktxDocument.getElementById("map-holder")!! as HTMLElement) {
                        center = LatLng(47, 11) // TODO center a bit better
                        zoom = 7
                        preferCanvas = true
                    }

                    val layer = tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png") {
                        attribution =
                            "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
                    }
                    layer.addTo(map)

                    // TODO load Events and forEach create marker
                    val marker = marker(LatLng(48.210033, 16.363449)) {
                        title = "Vienna"
                        icon = icon {
                            iconUrl = "/static/logo-oval.png"
                            iconSize = Point(50, 50, true)
                        }
                    }

                    marker.on("click", { props.stateSetter("/details/1", DetailsPage("1")) })

                    marker.addTo(map)
                }.create())
            }
        }
}
