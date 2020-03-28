package tech.flandia_yingm.auto_fgo.img

import tech.flandia_yingm.auto_fgo.img.Images.readResource
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB

data class Template(val name: String, val image: BufferedImage, val threshold: Double = 0.95) {

    companion object {
        val EMPTY_TEMPLATE = Template("", BufferedImage(1, 1, TYPE_INT_ARGB))
    }

    override fun toString(): String = name
}

fun template(name: String, threshold: Double = 0.95, clazz: () -> Unit): Template {
    return Template(name, readResource("$name.png", Class.forName(name(clazz))), threshold)
}

internal fun name(func: () -> Unit): String {
    val name = func.javaClass.name
    return when {
        name.contains("Kt$") -> name.substringBefore("Kt$")
        name.contains("$") -> name.substringBefore("$")
        else -> name
    }
}