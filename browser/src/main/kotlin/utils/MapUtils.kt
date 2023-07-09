package utils

import emotion.react.css
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.map.LeafletMap
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import react.*
import react.dom.html.ReactHTML
import web.cssom.*

object MapUtils {
    val center = LatLng(47.69, 13.34)

    enum class Format(val pct: Percentage) {
        Square(100.0.pct),
        Wide(56.25.pct)
    }

    fun mapHolder(format: Format = Format.Wide, id: String = "map-holder") = FC<Props> {
        ReactHTML.div {
            css {
                clear = Clear.left
                paddingTop = format.pct
            }
            this.id = id
        }
    }

    fun map(id: String = "map-holder", center: LatLng = this.center, zoom: Int = 7): LeafletMap {
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
