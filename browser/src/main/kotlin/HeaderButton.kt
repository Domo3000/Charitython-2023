import emotion.react.css
import react.FC
import react.Props
import react.dom.events.MouseEventHandler
import react.dom.html.ReactHTML
import utils.Style
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
                background = Color(Style.greyColor)
                textDecoration = TextDecoration.lineThrough
            }
        }
        disabled = props.disabled
        onClick = props.onClick
    }
}