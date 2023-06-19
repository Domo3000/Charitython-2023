package pages

import components.HeaderButton
import components.OverviewProps
import components.OverviewState
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
import web.history.history
import kotlinx.browser.document as ktxDocument

object IndexState : OverviewState {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            ReactHTML.div {
                ReactHTML.h1 {
                    +"Am 16. September 2023 ist wieder World Cleanup Day!"
                }
                ReactHTML.h2 {
                    +"In ganz Österreich wird aufgeräumt. Gemeinsam schaffen wir eine saubere Umwelt."
                }
            }

            ReactHTML.div {
                HeaderButton {
                    text = "Cleanup anmelden"
                }

                HeaderButton {
                    text = "Cleanup finden"
                }
            }
        }
}
