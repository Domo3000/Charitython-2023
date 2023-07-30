package pages

import components.*
import css.Classes
import emotion.react.css
import io.ktor.client.statement.*
import io.kvision.maps.externals.leaflet.events.LeafletMouseEvent
import io.kvision.maps.externals.leaflet.geo.LatLng
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
import web.cssom.pct
import web.cssom.px
import web.dom.document
import web.file.FileReader
import web.html.ButtonType
import web.html.InputType
import web.location.location
import web.prompts.alert

private external interface RegisterFormProps : Props {
    var cleanupDay: CleanupDayDTO
    var stateSetter: (String, OverviewPage) -> Unit
}

private object RequestHelper {
    val scope = MainScope()

    fun getWithTimeout(
        url: String,
        setTimeout: StateSetter<Int?>,
        callback: (Location) -> Unit
    ) {
        Requests.get(url) { response ->
            when (response.status.value) {
                200 -> scope.launch {
                    val location = Messages.decode(response.bodyAsText()) as Location
                    callback(location)
                }

                429 -> setTimeout(window.setTimeout({
                    getWithTimeout(url, setTimeout, callback)
                }, 1000))

                else -> {}
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
    val (websiteAddress, setWebsiteAddress) = useState<String?>(null)
    val (eventName, setEventName) = useState("")
    val (street, setStreet) = useState("")
    val (zipCode, setZipCode) = useState("")
    val (description, setDescription) = useState("")
    val (startTime, setStartTime) = useState("")
    val (endTime, setEndTime) = useState("")
    val (maybeImageInput, setImageInput) = useState<ArrayBuffer?>(null)
    val (coordinates, setCoordinates) = useState<LatLng?>(null)

    val (timeout, setTimeout) = useState<Int?>(null)

    val (mapHolder, _) = useState(MapHolder(null))
    val (markerHolder, _) = useState(MarkerHolder(null))

    ReactHTML.h2 {
        val date = cleanupDay.timestamp.toJSDate()

        +"Wir freuen uns sehr, dass du als Organisator:in beim World Cleanup Day am ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()} dabei sein möchtest! Bitte füll das folgende Formular aus."
    }

    ReactHTML.form {
        TextFormField {
            text = "Vorname"
            value = firstName
            stateSetter = setFirstName
        }
        TextFormField {
            text = "Nachname"
            stateSetter = setLastName
        }
        TextFormField {
            text = "Email"
            stateSetter = setEmailAddress
            type = InputType.email
        }
        TextFormField {
            text = "Name: Organisation/Verein/Unternehmen/Privatperson"
            stateSetter = setOrganization
        }
        OptionalTextFormField {
            text = "Webseite"
            stateSetter = setWebsiteAddress
            type = InputType.url
        }
        TextFormField {
            text = "Event Name"
            stateSetter = setEventName
        }
        TextFormField {
            text = "Straße"
            stateSetter = setStreet
        }
        TextFormField {
            text = "PLZ"
            stateSetter = setZipCode
        }
        TextFormField {
            text = "Beginn"
            stateSetter = setStartTime
            type = InputType.time
        }
        TextFormField {
            text = "Ende"
            stateSetter = setEndTime
            type = InputType.time
        }

        ReactHTML.div {
            css(formFieldStyle)
            FormFieldLabel {
                text = "Bild"
                required = false
            }
            ReactHTML.input {
                css(inputStyle)
                type = InputType.file
                required = false
                onChange = {
                    val fileReader = FileReader()

                    fileReader.readAsArrayBuffer(it.target.files!![0])

                    fileReader.onload = { e ->
                        setImageInput(e.target!!.result as ArrayBuffer)
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
                val dto = CleanUpEventCreationDTO(
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

                maybeImageInput?.let { imageInput ->
                    @Suppress("CAST_NEVER_SUCCEEDS") // it evidently does succeed
                    val image = imageInput.run { Int8Array(this) as ByteArray }

                    Requests.postImage(
                        "/data/cleanupEventWithPicture",
                        image,
                        dto
                    ) {
                        alert("Event wurde zur Bestätigung übermittelt!")
                        location.reload()
                    }
                } ?: run {
                    Requests.post("/data/cleanupEvent", dto) {
                        alert("Event wurde zur Bestätigung übermittelt!")
                        location.reload()
                    }
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
            setCoordinates(null)
            RequestHelper.getWithTimeout(
                "/data/coordinates/$zipCode/$street",
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

                markerHolder.marker = MapUtils.marker(coordinates)

                markerHolder.marker!!.addTo(map)
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
