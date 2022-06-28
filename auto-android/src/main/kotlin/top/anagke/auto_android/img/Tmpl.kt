package top.anagke.auto_android.img

data class Tmpl(
    val name: String,
    val threshold: Double,
    val imgs: List<Img>,
) {

    override fun toString() = "Tmpl($name)"

    /**
     * Returns the minimum difference between the given image and the
     * images of this template.
     */
    fun diff(img: Img): Double {
        return this.imgs
            .map { img.match(it) }
            .minOf { it }
    }

}