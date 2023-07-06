package utils

import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.map.LeafletMap
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

object MapUtils {
    fun map(id: String = "map-holder", center: LatLng = LatLng(47.69, 13.34), zoom: Int = 7): LeafletMap {
        val map = LeafletObjectFactory.map(document.getElementById(id)!! as HTMLElement) {
            this.center = center
            this.zoom = zoom
            preferCanvas = true
        }

        val layer = LeafletObjectFactory.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png") {
            attribution =
                "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
        }
        layer.addTo(map)

        return map
    }
}
