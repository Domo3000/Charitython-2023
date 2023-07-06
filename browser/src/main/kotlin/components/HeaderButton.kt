package components

import emotion.react.css
import react.FC
import react.Props
import react.dom.events.MouseEventHandler
import react.dom.html.ReactHTML
import web.cssom.*
import web.html.HTMLButtonElement

external interface HeaderButtonProps : Props {
    var text: String
    var color: Color
    var disabled: Boolean
    var width: Width
    var margin: Margin
    var onClick: MouseEventHandler<HTMLButtonElement>
}

val HeaderButton = FC<HeaderButtonProps> { props ->
    ReactHTML.button {
        +props.text
        css {
            width = props.width
            margin = props.margin
            height = 80.px
            padding = 15.px
            margin = 20.px
            float = Float.right
            background = props.color
            if (props.disabled) {
                opacity = number(0.8)
                textDecoration = TextDecoration.underline
            }
        }
        disabled = props.disabled
        onClick = props.onClick
    }
}