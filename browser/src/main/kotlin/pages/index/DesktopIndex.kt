package pages.index

import css.ClassNames
import emotion.react.css
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.react.reactWrapper
import pages.DetailsPage
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.MapUtils
import web.cssom.*
import web.dom.document

val DesktopIndex = FC<IndexProps> { props ->
    val (mapInitialized, setMapInitialized) = useState(false)

    ReactHTML.div {
        css(ClassNames.desktopElement) {}
        ReactHTML.div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                justifyContent = JustifyContent.spaceEvenly
                backgroundImage = url(props.background?.fileName?.let { "/files/$it" } ?: "/static/background.jpg")
                backgroundRepeat = BackgroundRepeat.noRepeat
                backgroundSize = BackgroundSize.cover
                backgroundAttachment = BackgroundAttachment.fixed
                height = 100.vh
            }

            props.cleanupDay?.let { cleanupDay ->
                CleanupDaySetHeader {
                    this.cleanupDay = cleanupDay
                    stateSetter = props.stateSetter
                }
            } ?: run {
                CleanupDayNotSetHeader { }
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
                    minHeight = 600.px
                }
            }
            MapUtils.MapHolder {
                id = "desktop-map-holder"
            }
        }

        props.results?.let { previousCleanupResults ->
            CleanupDayResults {
                this.results = previousCleanupResults
            }
        }

        if (props.events.isNotEmpty() && !mapInitialized) {
            hydrateRoot(document.getElementById("desktop-map-holder")!!, reactWrapper<FC<Props>> {
                val map = MapUtils.map("desktop-map-holder")

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