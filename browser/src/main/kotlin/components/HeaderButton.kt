package components

import css.Style
import emotion.react.css
import react.FC
import react.Props
import react.dom.events.MouseEvent
import react.dom.events.MouseEventHandler
import react.dom.html.ReactHTML
import web.cssom.*
import web.html.HTMLAnchorElement

external interface HeaderButtonProps : Props {
    var text: String
    var link: String
    var color: Color
    var disabled: Boolean
    var width: Width
    var margin: Margin
    var onClick: MouseEventHandler<HTMLAnchorElement>
}

val HeaderButton = FC<HeaderButtonProps> { props ->
    ReactHTML.button {
        ReactHTML.a {
            css {
                textDecoration = None.none
                color = Style.blackColor
            }
            +props.text
            href = props.link
            onClick = {
                it.preventDefault()
                if (!props.disabled) {
                    props.onClick(it)
                }
            }
        }
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
        onClick = {
            props.onClick(it as MouseEvent<HTMLAnchorElement, *>)
        }
    }
}