package pages.index

import emotion.react.css
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.react.reactWrapper
import pages.DetailsPage
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.MapUtils
import utils.getFileName
import web.cssom.*
import web.dom.document

val Index = FC<IndexProps> { props ->
    val (mapInitialized, setMapInitialized) = useState(false)

    ReactHTML.div {
        ReactHTML.div {
            ReactHTML.img {
                src = getFileName(props.background?.fileName, "background.jpg")
                css {
                    width = 100.pct
                    objectFit = ObjectFit.contain
                }
            }

            CleanupDayHeader {
                this.cleanupDay = props.cleanupDay
                stateSetter = props.stateSetter
            }
        }

        props.cleanupDay?.let { cleanupDay ->
            CleanupDaySetBody {
                this.cleanupDay = cleanupDay
            }
        }

        ReactHTML.div {
            css {
                margin = Auto.auto
                maxWidth = 1000.px
                if (props.events.isEmpty()) {
                    display = None.none
                } else {
                    paddingTop = 10.px
                    paddingBottom = 10.px
                }
            }
            MapUtils.MapHolder {}
        }

        props.results?.let { previousCleanupResults ->
            CleanupDayResults {
                this.results = previousCleanupResults
            }
        }

        if (props.events.isNotEmpty() && !mapInitialized) {
            hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
                val map = MapUtils.map()

                props.events.forEach { event ->
                    val marker = MapUtils.marker(
                        coordinates = LatLng(event.latitude, event.longitude),
                        title = event.eventName
                    )

                    marker.on(
                        "click",
                        { props.stateSetter("/details/${event.id}", DetailsPage(event.id)) })

                    marker.addTo(map)
                }
            }.create())
            setMapInitialized(true)
        }
    }
}