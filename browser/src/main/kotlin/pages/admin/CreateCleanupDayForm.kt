package pages.admin

import emotion.react.css
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import kotlinx.datetime.Instant
import kotlinx.datetime.toJSDate
import model.CleanupDayDTO
import model.CreateCleanupDay
import model.DeletedCleanupDay
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import react.useState
import utils.Requests
import utils.getMonthString
import web.cssom.rem
import web.file.FileReader
import web.html.InputType
import kotlin.js.Date

external interface CreateCleanupDayFormProps : Props {
    var admin: Requests.AdminRequests
    var cleanupDay: CleanupDayDTO?
    var setCleanupDay: StateSetter<CleanupDayDTO?>
}

val CreateCleanupDayForm = FC<CreateCleanupDayFormProps> { props ->
    val (dateInput, setDateInput) = useState("")
    val (imageInput, setImageInput) = useState<ArrayBuffer?>(null)
    val (warningDialog, setWarningDialog) = useState(false)
    val cleanupDay = props.cleanupDay

    ReactHTML.div {
        cleanupDay?.let { _ ->
            val date = cleanupDay.timestamp.toJSDate()

            ReactHTML.p {
                +"Nächster CleanupDay: id=${cleanupDay.id}: ${date.getDate()}. ${date.getMonthString()} ${date.getFullYear()}"
            }

            ReactHTML.dialog {
                +"Wirklich? Das löscht alle Events und sonstige Daten"
                open = warningDialog
                ReactHTML.div {
                    ReactHTML.button {
                        +"Doch nicht!"
                        onClick = {
                            setWarningDialog(false)
                        }
                    }
                    ReactHTML.button {
                        +"Wirklich löschen!"
                        onClick = {
                            props.admin.delete("/data/cleanupDay/${cleanupDay.id}") {
                                if (it == DeletedCleanupDay) {
                                    props.setCleanupDay(null)
                                }
                            }
                        }
                    }
                }
            }

            ReactHTML.div {
                ReactHTML.button {
                    +"Löschen"
                    onClick = {
                        setWarningDialog(true)
                    }
                }
            }
        } ?: run {
            ReactHTML.p {
                +"Nächster CleanupDay ist noch nicht erstellt worden"
            }

            ReactHTML.form {
                ReactHTML.div {
                    +"Datum:\t"
                    ReactHTML.input {
                        type = InputType.date
                        required = true
                        onChange = {
                            setDateInput(it.target.value)
                        }
                    }
                }
                ReactHTML.div {
                    +"Logo:\t"
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
                        marginTop = 1.5.rem
                    }
                    ReactHTML.button {
                        +"Nächsten CleanupDay erstellen"
                    }
                }
                onSubmit = {
                    it.preventDefault()
                    val date = Instant.fromEpochMilliseconds(Date.parse(dateInput).toLong())

                    @Suppress("CAST_NEVER_SUCCEEDS") // it evidently does succeed
                    val image = imageInput!!.run { Int8Array(this) as ByteArray }

                    props.admin.postImage(
                        "/data/cleanupDay",
                        image,
                        CreateCleanupDay(date)
                    ) { maybeMessage ->
                        props.setCleanupDay(maybeMessage as? CleanupDayDTO)
                    }
                }
            }
        }
    }
}