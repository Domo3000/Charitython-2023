package pages

import components.OverviewPage
import components.OverviewProps
import components.RoutePage
import css.ClassNames
import css.Classes
import emotion.react.css
import io.ktor.client.statement.*
import io.kvision.maps.LeafletObjectFactory
import io.kvision.maps.externals.leaflet.events.LeafletMouseEvent
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.geometry.Point
import io.kvision.maps.externals.leaflet.layer.marker.Marker
import io.kvision.maps.externals.leaflet.map.LeafletMap
import io.kvision.react.reactWrapper
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.toJSDate
import model.CleanUpEventCreationDTO
import model.CleanupDayDTO
import model.Location
import model.Messages
import react.*
import react.dom.client.hydrateRoot
import react.dom.html.ReactHTML
import utils.MapUtils
import utils.Requests
import utils.getMonthString
import web.cssom.*
import web.dom.document
import web.file.FileReader
import web.html.ButtonType
import web.html.InputType
import web.location.location
import web.prompts.alert

private external interface InputProps : Props {
    var label: String
    var setter: StateSetter<String>
    var type: InputType?
    var optional: Boolean?
    var error: Boolean
}

private val Input = FC<InputProps> { props ->
    ReactHTML.tr {
        ReactHTML.td {
            +"${props.label}:"
        }
        ReactHTML.td {
            ReactHTML.input {
                if (props.error) {
                    css {
                        background = Color("#FFAA99")
                    }
                }
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

private object RequestHelper {
    val scope = MainScope()

    fun getWithTimeout(
        url: String,
        setAddressError: StateSetter<Boolean>,
        setTimeout: StateSetter<Int?>,
        callback: (Location) -> Unit
    ) {
        Requests.get(url) { response ->
            when (response.status.value) {
                200 -> scope.launch {
                    val location = Messages.decode(response.bodyAsText()) as Location
                    callback(location)
                    setAddressError(false)
                }

                429 -> setTimeout(window.setTimeout({
                    getWithTimeout(url, setAddressError, setTimeout, callback)
                }, 1000))

                else -> {
                    setAddressError(true)
                }
            }
        }
    }
}

class MapHolder(var map: LeafletMap?)
class MarkerHolder(var marker: Marker?)

private val RegisterForm = FC<RegisterFormProps> { props ->
    val cleanupDay = props.cleanupDay
    val (firstName, setFirstName) = useState("")
    val (lastName, setLastName) = useState("")
    val (emailAddress, setEmailAddress) = useState("")
    val (organization, setOrganization) = useState("")
    val (websiteAddress, setWebsiteAddress) = useState("")
    val (eventName, setEventName) = useState("")
    val (street, setStreet) = useState("")
    val (zipCode, setZipCode) = useState("")
    val (description, setDescription) = useState("")
    val (startTime, setStartTime) = useState("")
    val (endTime, setEndTime) = useState("")
    val (imageInput, setImageInput) = useState<ArrayBuffer?>(null)
    val (coordinates, setCoordinates) = useState<LatLng?>(null)

    val (addressError, setAddressError) = useState(false)
    val (timeout, setTimeout) = useState<Int?>(null)

    val (mapHolder, _) = useState(MapHolder(null))
    val (markerHolder, _) = useState(MarkerHolder(null))

    ReactHTML.h2 {
        val date = cleanupDay.timestamp.toJSDate()

        +"Wir freuen uns sehr, dass du als Organisator:in beim World Cleanup Day am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} dabei sein möchtest! Bitte füll das folgende Formular aus."
    }

    ReactHTML.form {
        ReactHTML.table {
            css(ClassNames.phoneFullWidth) {
                width = 50.pct
                float = Float.left
                tableLayout = TableLayout.fixed
            }
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
        }
        ReactHTML.table {
            css(ClassNames.phoneFullWidth) {
                width = 50.pct
                float = Float.left
                tableLayout = TableLayout.fixed
            }
            Input {
                label = "Straße"
                setter = setStreet
                error = addressError
            }
            Input {
                label = "PLZ"
                setter = setZipCode
                error = addressError
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

        MapUtils.MapHolder { }

        ReactHTML.p {
            +"Veranstaltungstext:"
        }

        ReactHTML.textarea {
            css {
                width = 100.pct
            }
            required = true
            maxLength = 5000
            rows = 8
            onChange = {
                setDescription(it.target.value)
            }
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

            coordinates?.let { c ->
                @Suppress("CAST_NEVER_SUCCEEDS") // it evidently does succeed
                val image = imageInput!!.run { Int8Array(this) as ByteArray }

                Requests.postImage(
                    "/data/cleanupEvent",
                    image,
                    CleanUpEventCreationDTO(
                        cleanupDayId = cleanupDay.id,
                        firstName = firstName,
                        lastName = lastName,
                        emailAddress = emailAddress,
                        organization = organization,
                        websiteAddress = websiteAddress,
                        eventName = eventName,
                        street = street,
                        zipCode = zipCode,
                        latitude = c.lat.toDouble(),
                        longitude = c.lng.toDouble(),
                        startTime = startTime,
                        endTime = endTime,
                        description = description
                    )
                ) {
                    alert("Event wurde zur Bestätigung übermittelt!")
                    location.reload()
                }
            } ?: run {
                alert("Click auf die Karte um einen Ort festzulegen!")
            }
        }
    }

    fun getLocationWithTimeout() {
        if (timeout != null) {
            window.clearTimeout(timeout)
        }

        setTimeout(window.setTimeout({
            setAddressError(false)
            setCoordinates(null)
            RequestHelper.getWithTimeout(
                "/data/coordinates/$zipCode/$street",
                setAddressError,
                setTimeout
            ) { location ->
                val latitude = location.latitude.toDouble()
                val longitude = location.longitude.toDouble()
                setCoordinates(LatLng(latitude, longitude))
            }
        }, 1000))
    }

    useEffect(street) {
        if (zipCode.length > 3) {
            getLocationWithTimeout()
        }
    }

    useEffect(zipCode) {
        if (street.length > 3) {
            getLocationWithTimeout()
        }
    }

    useEffect(coordinates) {
        mapHolder.map?.let { map ->
            if (coordinates != null) {
                markerHolder.marker?.removeFrom(map)

                map.flyTo(coordinates, 15)

                markerHolder.marker = LeafletObjectFactory.marker(coordinates) {
                    title = "Event"
                    icon = LeafletObjectFactory.icon {
                        iconUrl = "/static/logo-oval.png"
                        iconSize = Point(25, 25, true)
                    }
                }

                markerHolder.marker!!.addTo(map)
                setAddressError(false)
            } else {
                markerHolder.marker?.let {
                    it.removeFrom(map)
                }

                map.flyTo(MapUtils.center, 7)
            }
        }
    }

    useEffectOnce {
        hydrateRoot(document.getElementById("map-holder")!!, reactWrapper<FC<Props>> {
            val map = MapUtils.map()

            map.on("click", { event ->
                val latLng = (event as LeafletMouseEvent).latlng
                setCoordinates(latLng)
            })

            mapHolder.map = map
        }.create())
    }
}

object RegisterCleanupEvent : RoutePage {
    override val route: String = "cleanupAnmelden"
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
