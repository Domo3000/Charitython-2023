package pages

import components.OverviewProps
import components.OverviewPage
import css.ClassNames
import css.Classes.centered
import css.and
import react.FC
import react.dom.html.ReactHTML


import emotion.react.css
import io.kvision.maps.LeafletObjectFactory.tileLayer
import io.kvision.maps.LeafletObjectFactory.map
import io.kvision.MapsModule
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.react.reactWrapper
import org.w3c.dom.HTMLElement
import react.Props
import react.create
import react.dom.client.hydrateRoot
import react.router.useParams
import react.useEffectOnce
import web.cssom.*

import web.dom.document

class CleanUpInformation(private val id: String?) : OverviewPage {
    override val component: FC<OverviewProps>
        get() = FC { props ->
            val eventId = id ?: useParams()["id"]!!

            // TODO: use an http call to get the cleanup data

            /* DTO values:
            - Title
            - Time
                - Date
                - Time window
            - Address
                - Address Line
                - map coordinates: latitude, longitude
            -

             */

            ReactHTML.div {

                ReactHTML.h2 {
                    +"Fruhjahrsputz in Schwaan (Mecklenburg-Vorpommern)"
                }

                ReactHTML.div {
                    css(ClassName("grid-container")) {
                        display = Display.grid
                        gridTemplateColumns = array(Auto.auto, Auto.auto, Auto.auto)
                    }

                    ReactHTML.div {
                        ReactHTML.h3 {
                            +"WANN"
                        }
                        id = "cleanup-detail-when"

                        css(ClassName("cleanup-detail-when-column")) {
                            //gridColumn = GridAutoColumns();
                        }

                        ReactHTML.div {
                            ReactHTML.div {
                                id = "cleanup-detail-when-date"

                                ReactHTML.i {
                                    className = ClassName("far fa-calendar-alt")
                                }

                                ReactHTML.span {
                                    +"12.05.2023"
                                }
                            }
                            ReactHTML.div {
                                id = "cleanup-detail-when-time"

                                ReactHTML.i {
                                    className = ClassName("far fa-clock")
                                }
                                ReactHTML.span {
                                    +"08:00 Uhr - 12:30 Uhr"
                                }
                            }
                            ReactHTML.div {
                                id = "cleanup-detail-when-to-calendar"
                            }
                        }
                    }

                    ReactHTML.div {
                        id = "cleanup-detail-where"
                        ReactHTML.h3 {
                            +"WO"
                            css(ClassName("234234")) {
                                width = 33.pct
                            }
                        }

                        ReactHTML.div {
                            id = "cleanup-detail-where-address"

                            ReactHTML.i {
                                className = ClassName("fas fa-map-marker")
                            }

                            ReactHTML.span {
                                +"Schwaan"
                            }

                            ReactHTML.div {
                                css {
                                    maxWidth = 500.px
                                }
                                ReactHTML.span {
                                    +"R.-Breitscheid-Str. 16, Mecklenburg Vorpommern, 18258, Landkreis Rostock"
                                }
                            }
                        }


                        ReactHTML.h3 {
                            +"VERANSTALTUNGSTYP"

                            css {

                            }
                        }
                    }

                    ReactHTML.div {
                        +"Cleanup Icon"

                        id = "cleanup-detail-icon"
                    }
                }

                ReactHTML.div {

                    +"Map Container"

                    css {
                        height = 500.px
                    }
                    id = "cleanup-detail-map-location-holder"
                }

                id = "cleanup-details-container"
            }

            useEffectOnce {
                MapsModule.initialize()

                hydrateRoot(
                    document.getElementById("cleanup-detail-map-location-holder")!!,
                    reactWrapper<FC<Props>> {
                        val map = map(kotlinx.browser.document.getElementById("cleanup-detail-map-location-holder")!! as HTMLElement) {
                            center = LatLng(48.210033, 16.363449)
                            zoom = 15
                            preferCanvas = true
                        }

                        val layer = tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png") {
                            attribution =
                                "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"

                        }
                        layer.addTo(map)

                        val marker = LeafletObjectFactory.marker(LatLng(48.210033, 16.363449)) {
                            title = "Vienna"
                            icon = LeafletObjectFactory.icon {
                                iconUrl = "/static/logo-oval.png"
                                iconSize = Point(50, 50, true)
                            }
                        }
                        marker.addTo(map);
                    }.create()
                )
            }
        }
}