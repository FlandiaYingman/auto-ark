package top.anagke.auto_android

interface AutoModule {

    val moduleName: String get() = this::class.simpleName!!

    fun run()

    fun auto() {
        run()
    }

}