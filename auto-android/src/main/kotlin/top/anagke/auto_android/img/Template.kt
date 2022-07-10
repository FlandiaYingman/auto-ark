package top.anagke.auto_android.img

data class Tmpl(
    val name: String,
    val threshold: Double,
    val imgs: List<Img>,
    val type: TmplType = TmplType.REGULAR
) {

    override fun toString() = "Tmpl($name)"

}

enum class TmplType {
    REGULAR,
    EDGE,
}

fun Img.match(tmpl: Tmpl): Double {
    return tmpl.imgs.minOf { this.match(it) }
}