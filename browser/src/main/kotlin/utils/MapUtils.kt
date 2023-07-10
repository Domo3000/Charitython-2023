package utils

import emotion.react.css
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.maps.externals.leaflet.map.LeafletMap
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import react.*
import react.dom.html.ReactHTML
import web.cssom.*

external interface MapProps : Props {
    var id: String?
}

object MapUtils {
    val center = LatLng(47.69, 13.34)

    val MapHolder = FC<MapProps> { props ->
        ReactHTML.div {
            css {
                clear = Clear.left
                media("only screen and (min-width: 800px)") {
                    aspectRatio = AspectRatio(16.0 / 9.0)
                }
                media("only screen and (max-width: 800px)") {
                    aspectRatio = AspectRatio(1.0)
                }
            }
            this.id = props.id ?: "map-holder"
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

    fun marker(coordinates: LatLng, size: Int = 100, title: String = "Event") = LeafletObjectFactory.marker(coordinates) {
        this.title = title
        icon = LeafletObjectFactory.icon {
            iconUrl = "/static/marker.png"
            iconSize = Point(size, size, true)
        }
    }
}
