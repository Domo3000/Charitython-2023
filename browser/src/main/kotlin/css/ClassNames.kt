package css

import web.cssom.ClassName

object ClassNames {
    val mobileElement = ClassName("phone-element")
    val desktopElement = ClassName("desktop-element")
}

infix fun ClassName.and(s: String) = ClassName("${this.unsafeCast<String>()} $s")