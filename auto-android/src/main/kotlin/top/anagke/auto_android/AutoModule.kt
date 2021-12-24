package top.anagke.auto_android

interface AutoModule {

    val name: String get() = this::class.simpleName!!

    fun run()

    fun auto() {
        run()
    }

}