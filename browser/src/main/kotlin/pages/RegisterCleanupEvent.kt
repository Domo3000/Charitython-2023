package pages

import components.OverviewPage
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
import model.CleanupDayDTO
import pages.index.IndexPage
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.MapUtils
import utils.Requests
import utils.getMonthString
import web.cssom.px
import web.dom.document
import web.file.FileReader
import web.html.ButtonType
import web.html.InputType
import web.prompts.alert

private external interface InputProps : Props {
    var label: String
    var setter: StateSetter<String>
    var type: InputType?
    var optional: Boolean?
}

private val Input = FC<InputProps> { props ->
    ReactHTML.tr {
        ReactHTML.td {
            +"${props.label}:"
        }
        ReactHTML.td {
            ReactHTML.input {
                type = props.type ?: InputType.text
                required = props.optional?.let { !it } ?: true
                onChange = {
                    props.setter(it.target.value)
                }
            }
        }
    }
}

private external interface RegisterFormProps : Props {
    var cleanupDay: CleanupDayDTO
    var stateSetter: (String, OverviewPage) -> Unit
}

private val RegisterForm = FC<RegisterFormProps> { props ->
    val cleanupDay = props.cleanupDay
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

    ReactHTML.h2 {
        val date = cleanupDay.timestamp.toJSDate()

        +"Wir freuen uns sehr, dass du als Organisator:in beim World Cleanup Day am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} dabei sein möchtest! Bitte füll das folgende Formular aus."
    }

    ReactHTML.form {
        ReactHTML.table {
            Input {
                label = "Vorname"
                setter = setFirstName
            }
            Input {
                label = "Nachname"
                setter = setLastName
            }
            Input {
                label = "Email"
                setter = setEmailAddress
                type = InputType.email
            }
            Input {
                label = "Organisation"
                setter = setOrganization
            }
            Input {
                label = "Webseite"
                setter = setWebsiteAddress
                optional = true
                type = InputType.url
            }
            Input {
                label = "Event Name"
                setter = setEventName
            }
            Input {
                label = "Beginn"
                setter = setStartTime
                type = InputType.time
            }
            Input {
                label = "Ende"
                setter = setEndTime
                type = InputType.time
            }
            Input {
                label = "Veranstaltungstext"
                setter = setDescription
            }

            ReactHTML.tr {
                ReactHTML.td {
                    +"Bild:"
                }
                ReactHTML.td {
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
            css {
                marginTop = 20.px
            }
            +"Abschicken"
            type = ButtonType.submit
        }

        onSubmit = {
            it.preventDefault()

            imageInput?.let { input ->
                @Suppress("CAST_NEVER_SUCCEEDS") // it evidently does succeed
                val image = input.run { Int8Array(this) as ByteArray }

                Requests.postImage(
                    "/data/cleanupEvent",
                    image,
                    CleanUpEventCreationDTO(
                        cleanupDay.id,
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
                ) {
                    props.stateSetter("/", IndexPage) // TODO better reload
                    alert("Event wurde zur Bestätigung übermittelt!")
                    props.stateSetter("/${RegisterCleanupEvent.route}", RegisterCleanupEvent)
                }
            } ?: run {
                alert("Click auf die Karte um einen Ort festzulegen!")
            }
        }
    }

    useEffectOnce {
        hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
            val map = MapUtils.map()

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

                ReactHTML.h1 {
                    +"Cleanup anmelden"
                }

                props.cleanupDay?.let { cleanupDay ->
                    RegisterForm {
                        this.cleanupDay = cleanupDay
                        this.stateSetter = props.stateSetter
                    }
                } ?: run {
                    ReactHTML.p {
                        +"Wir freuen uns sehr, dass du als Organisator:in beim nächsten World Cleanup Day dabei sein möchtest!"
                    }
                    ReactHTML.p {
                        +"Leider ist dieser noch nicht festgelegt worden!"
                    }
                }
            }
        }
}
