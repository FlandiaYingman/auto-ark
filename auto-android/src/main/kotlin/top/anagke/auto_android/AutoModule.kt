package top.anagke.auto_android

import top.anagke.auto_android.device.Device

abstract class AutoModule<Auto : AutoAndroid<*>>(
    protected val auto: Auto
) {

    /**
     * [name] is the name of this [AutoModule], commonly used in logging. e.g:
     * "登录模块", "采集模块".
     */
    abstract val name: String

    protected val device: Device = auto.device


    /**
     * [init] represents the setting up or checking requirements stage.
     *
     * If [init] fails to run, the [Auto] retries directly without calling any
     * other functions.
     */
    open fun init() {}

    /**
     * [run] represents the actual work this [AutoModule] needs to do.
     *
     * The [AutoModule] starts at the main interface, also ends at the main
     * interface. If an error occurs during [run], it doesn't need to return
     * to the main interface - [AutoAndroid.setInterfaceToMain] is called.
     */
    abstract fun run()


    override fun toString(): String {
        return "AutoModule($name)"
    }

}