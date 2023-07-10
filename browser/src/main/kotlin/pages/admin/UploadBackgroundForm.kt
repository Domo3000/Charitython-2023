package pages.admin

import emotion.react.css
import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import model.BackgroundDTO
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useEffectOnce
import react.useState
import utils.Requests
import web.cssom.pct
import web.cssom.rem
import web.file.FileReader
import web.html.InputType

external interface UploadBackgroundFormProps : Props {
    var admin: Requests.AdminRequests
}

val UploadBackgroundForm = FC<UploadBackgroundFormProps> { props ->
    val (imageInput, setImageInput) = useState<ArrayBuffer?>(null)
    val (background, setBackground) = useState<BackgroundDTO?>(null)

    ReactHTML.div {
        +"Aktuelles Hintergrundbild:"

        ReactHTML.div {
            ReactHTML.img {
                css {
                    width = 50.pct
                }
                src = background?.fileName?.let { "/files/$it" } ?: "/static/background.jpg"
            }
        }

        ReactHTML.form {
            ReactHTML.div {
                +"Neues Hintergrundbild:\t"
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
                    +"Hintergrundbild ändern"
                }
            }
            onSubmit = {
                it.preventDefault()

                @Suppress("CAST_NEVER_SUCCEEDS") // it evidently does succeed
                val image = imageInput!!.run { Int8Array(this) as ByteArray }

                props.admin.postImage(
                    "/data/background",
                    image,
                    null
                ) { maybeMessage ->
                    setBackground(maybeMessage as? BackgroundDTO)
                }
            }
        }
    }

    useEffectOnce {
        Requests.getMessage("/data/background") { message ->
            setBackground((message as BackgroundDTO))
        }
    }
}