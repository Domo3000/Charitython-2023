package pages

import components.OverviewProps
import components.RoutePage
import css.Classes
import emotion.react.css
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.events.LeafletMouseEvent
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.maps.externals.leaflet.layer.marker.Marker
import io.kvision.react.reactWrapper
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import kotlinx.datetime.toJSDate
import model.CleanUpEventCreationDTO
import org.w3c.dom.HTMLElement
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.Requests
import utils.getMonthString
import web.cssom.px
import web.dom.document
import web.file.FileReader
import web.html.ButtonType
import web.html.InputType
import kotlinx.browser.document as ktxDocument

private val RegisterForm = FC<Props> {
    val (firstName, setFirstName) = useState("")
    val (lastName, setLastName) = useState("")
    val (emailAddress, setEmailAddress) = useState("")
    val (organization, setOrganization) = useState("")
    val (websiteAddress, setWebsiteAddress) = useState("")
    val (eventName, setEventName) = useState("")
    //val (street, setStreet) = useState("") // TODO require either address or coordinates?
    //val (zipCode, setZipCode) = useState("")
    val (description, setDescription) = useState("")
    val (startTime, setStartTime) = useState("")
    val (endTime, setEndTime) = useState("")
    val (imageInput, setImageInput) = useState<ArrayBuffer?>(null)
    val (coordinates, setCoordinates) = useState<LatLng?>(null)

    ReactHTML.h1 {
        +"TODO" // TODO finish this -> make it Deutsch and more beautiful
    }

    ReactHTML.form {
        ReactHTML.div {
            +"firstname: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setFirstName(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"lastName: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setLastName(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"email: "
            ReactHTML.input {
                type = InputType.email
                onChange = {
                    setEmailAddress(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"organization: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setOrganization(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"websiteAddress: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setWebsiteAddress(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"eventName: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setEventName(it.target.value)
                }
            }
        }
        /*
        ReactHTML.div {
            +"street: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setStreet(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"zipCode: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setZipCode(it.target.value)
                }
            }
        }

         */
        ReactHTML.div {
            +"startTime: "
            ReactHTML.input {
                type = InputType.time
                onChange = {
                    setStartTime(it.target.value)
                    console.log(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"endTime: "
            ReactHTML.input {
                type = InputType.time
                onChange = {
                    setEndTime(it.target.value)
                    console.log(it.target.value)
                }
            }
        }
        ReactHTML.div {
            +"description: "
            ReactHTML.input {
                type = InputType.text
                onChange = {
                    setDescription(it.target.value)
                }
            }
        }

        // TODO for file input see Admin Page in /browser and post("/cleanupDay") route in /server
        ReactHTML.div {
            +"file: "
            ReactHTML.input {
                type = InputType.file
                required = true
                onChange = {
                    val fileReader = FileReader()

                    fileReader.readAsArrayBuffer(it.target.files!![0])

                    fileReader.onload = { e ->
                        setImageInput(e.target!!.result as ArrayBuffer)
                    }
                }
            }
        }

        ReactHTML.div {
            css {
                height = 500.px // TODO better
                maxWidth = 800.px
            }
            id = "map-holder"
        }

        ReactHTML.button {
            +"Submit"
            type = ButtonType.submit
        }

        onSubmit = {
            it.preventDefault()

            @Suppress("CAST_NEVER_SUCCEEDS") // it evidently does succeed
            val image = imageInput!!.run { Int8Array(this) as ByteArray }

            Requests.postImage(
                "/data/cleanupEvent",
                image,
                CleanUpEventCreationDTO(
                    1,
                    firstName,
                    lastName,
                    emailAddress,
                    organization,
                    websiteAddress,
                    eventName,
                    coordinates!!.lat.toDouble(),
                    coordinates.lng.toDouble(),
                    startTime,
                    endTime,
                    description
                )
            ) { maybeMessage ->
                // TODO some success message
            }
        }
    }

    useEffectOnce {
        hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
            val map = LeafletObjectFactory.map(ktxDocument.getElementById("map-holder")!! as HTMLElement) {
                center = LatLng(47, 11) // TODO center a bit better
                zoom = 7
                preferCanvas = true
            }

            val layer = LeafletObjectFactory.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png") {
                attribution =
                    "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
            }
            layer.addTo(map)

            var marker: Marker? = null

            map.on("click", { event ->
                val latLng = (event as LeafletMouseEvent).latlng
                setCoordinates(latLng)

                marker?.removeFrom(map)

                marker = LeafletObjectFactory.marker(latLng) {
                    title = "Event"
                    icon = LeafletObjectFactory.icon {
                        iconUrl = "/static/logo-oval.png"
                        iconSize = Point(25, 25, true)
                    }
                }

                marker!!.addTo(map)
            })
        }.create())
    }
}

object RegisterCleanupEvent : RoutePage {
    override val route: String = "register-event"
    override val component: FC<OverviewProps>
        get() = FC { props ->
            ReactHTML.div {
                css(Classes.limitedWidth)

                ReactHTML.h3 {
                    +"Cleanup anmelden"
                }

                props.cleanupDay?.let { cleanupDay ->
                    ReactHTML.div {
                        val date = cleanupDay.timestamp.toJSDate()

                        +"Wir freuen uns sehr, dass du als Organisator:in beim World Cleanup Day am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} dabei sein möchtest! Bitte füll das folgende Formular aus."
                    }

                    RegisterForm { }
                } ?: run {
                    ReactHTML.div {
                        +"Wir freuen uns sehr, dass du als Organisator:in beim nächsten World Cleanup Day dabei sein möchtest!"
                    }
                    ReactHTML.br {}
                    ReactHTML.div {
                        +"Leider ist dieser noch nicht festgelegt worden!"
                    }
                }
            }
        }
}
