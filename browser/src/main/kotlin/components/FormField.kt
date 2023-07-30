package components

import css.Style
import csstype.PropertiesBuilder
import emotion.react.css
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import web.cssom.pct
import web.cssom.px
import web.html.InputType


external interface FormFieldLabelProps : Props {
    var text: String
    var required: Boolean?
}

val FormFieldLabel = FC<FormFieldLabelProps> { props ->
    ReactHTML.div {
        +props.text
        if (props.required == true) {
            ReactHTML.span {
                css {
                    color = Style.pinkColor
                }
                +"*"
            }
        }
    }
}

val formFieldStyle: PropertiesBuilder.() -> Unit = {
    margin = 10.px
}

val inputStyle: PropertiesBuilder.() -> Unit = {
    width = 100.pct
}

external interface TextFormFieldProps : Props {
    var text: String
    var value: String
    var stateSetter: StateSetter<String>
    var type: InputType?
}

val TextFormField = FC<TextFormFieldProps> { props ->
    ReactHTML.div {
        css(formFieldStyle)
        FormFieldLabel {
            text = props.text
            required = true
        }
        ReactHTML.input {
            css(inputStyle)
            value = props.value
            placeholder = ""
            type = props.type ?: InputType.text
            required = true
            onChange = {
                props.stateSetter(it.target.value)
            }
        }
    }
}

external interface OptionalTextFormFieldProps : Props {
    var text: String
    var value: String?
    var stateSetter: StateSetter<String?>
    var type: InputType?
}

val OptionalTextFormField = FC<OptionalTextFormFieldProps> { props ->
    ReactHTML.div {
        css(formFieldStyle)
        FormFieldLabel {
            text = props.text
            required = false
        }
        ReactHTML.input {
            css(inputStyle)
            value = props.value
            placeholder = ""
            type = props.type ?: InputType.text
            required = false
            onChange = {
                props.stateSetter(it.target.value)
            }
        }
    }
}

external interface IntFormFieldProps : Props {
    var text: String
    var value: Int
    var stateSetter: StateSetter<Int>
}

val IntFormField = FC<IntFormFieldProps> { props ->
    ReactHTML.div {
        css(formFieldStyle)
        FormFieldLabel {
            text = props.text
            required = true
        }
        ReactHTML.input {
            css(inputStyle)
            value = props.value.toString()
            placeholder = ""
            type = InputType.number
            step = 1.0
            required = true
            onChange = {
                props.stateSetter(it.target.value.toInt())
            }
        }
    }
}

external interface OptionalIntFormFieldProps : Props {
    var text: String
    var value: Int?
    var stateSetter: StateSetter<Int?>
}

val OptionalIntFormField = FC<OptionalIntFormFieldProps> { props ->
    ReactHTML.div {
        css(formFieldStyle)
        FormFieldLabel {
            text = props.text
            required = false
        }
        ReactHTML.input {
            css(inputStyle)
            value = props.value?.toString()
            placeholder = ""
            type = InputType.number
            step = 1.0
            required = false
            onChange = {
                props.stateSetter(it.target.value.toInt())
            }
        }
    }
}

external interface DoubleFormFieldProps : Props {
    var text: String
    var value: Double
    var stateSetter: StateSetter<Double>
}

val DoubleFormField = FC<DoubleFormFieldProps> { props ->
    ReactHTML.div {
        css(formFieldStyle)
        FormFieldLabel {
            text = props.text
            required = true
        }
        ReactHTML.input {
            css(inputStyle)
            value = props.value.toString()
            placeholder = ""
            type = InputType.number
            step = 0.01
            required = true
            onChange = {
                props.stateSetter(it.target.value.toDouble())
            }
        }
    }
}

external interface OptionalDoubleFormFieldProps : Props {
    var text: String
    var value: Double?
    var stateSetter: StateSetter<Double?>
}

val OptionalDoubleFormField = FC<OptionalDoubleFormFieldProps> { props ->
    ReactHTML.div {
        css(formFieldStyle)
        FormFieldLabel {
            text = props.text
            required = false
        }
        ReactHTML.input {
            css(inputStyle)
            value = props.value?.toString()
            placeholder = ""
            type = InputType.number
            step = 0.01
            required = false
            onChange = {
                props.stateSetter(it.target.value.toDouble())
            }
        }
    }
}