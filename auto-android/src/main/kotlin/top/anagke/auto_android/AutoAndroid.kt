package top.anagke.auto_android

import mu.KotlinLogging
import top.anagke.auto_android.device.Device
import top.anagke.auto_android.util.safeCast

/**
 * [AutoAndroid] is a configured framework to run
 */
abstract class AutoAndroid<Self : AutoAndroid<Self>>(
    val device: Device
) {

    companion object {
        protected val logger = KotlinLogging.logger {}
        protected const val maxRetryTimes = 2
    }

    abstract val name: String


    abstract val initModules: List<AutoModule<Self>>
    abstract val workModules: List<AutoModule<Self>>
    abstract val finalModules: List<AutoModule<Self>>


    fun doRoutine() {
        initModules.forEach { initInitModule(it) }
        workModules.forEach { initWorkModule(it) }
        finalModules.forEach { initFinalModule(it) }

        initModules.forEach { runInitModule(it) }
        workModules.forEach { runWorkModule(it) }
        finalModules.forEach { runFinalModule(it) }
    }

    private fun initInitModule(module: AutoModule<Self>) {
        try {
            logger.info { "初始化初始模块 $module" }
            module.init()
        } catch (e: Exception) {
            logger.warn(e) { "初始化初始模块 $module 时出现错误；跳过该模块" }
        }
    }

    private fun runInitModule(module: AutoModule<Self>) {
        try {
            logger.info { "运行初始模块 $module" }
            module.run()
        } catch (e: Exception) {
            logger.warn(e) { "运行初始模块 $module 时出现错误；跳过" }
        }
    }

    private fun initWorkModule(module: AutoModule<Self>) {
        try {
            logger.info { "初始化模块 $module" }
            module.init()
        } catch (e: Exception) {
            logger.warn(e) { "初始化模块 $module 时出现错误；跳过该模块" }
        }
    }

    private fun runWorkModule(module: AutoModule<Self>, retryTimes: Int = 0) {
        try {
            beforeModule()
            logger.info { "运行模块 $module" }
            module.run()
        } catch (e: Exception) {
            if (retryTimes != maxRetryTimes) {
                logger.warn(e) { "运行模块 $module 时出现错误；已重试 $retryTimes/$maxRetryTimes 次；重试该模块" }
                resetInterface()
                runWorkModule(module, retryTimes = retryTimes + 1)
            } else {
                logger.warn(e) { "运行模块 $module 时出现错误；已重试 $retryTimes/$maxRetryTimes 次；跳过该模块" }
                resetInterface()
            }
        } finally {
            afterModule()
        }
    }

    private fun initFinalModule(module: AutoModule<Self>) {
        try {
            logger.info { "初始化结束模块 $module" }
            module.init()
        } catch (e: Exception) {
            logger.warn(e) { "初始化结束模块 $module 时出现错误；跳过该模块" }
        }
    }

    private fun runFinalModule(module: AutoModule<Self>) {
        try {
            logger.info { "运行结束模块 $module" }
            module.run()
        } catch (e: Exception) {
            logger.warn(e) { "运行结束模块 $module 时出现错误；跳过" }
        }
    }


    /**
     * [isAtMain] indicates whether this device is at the main interface.
     */
    abstract fun isAtMain(): Boolean

    /**
     * [returnToMain] returns this device to the main interface.
     */
    abstract fun returnToMain()

    private fun resetInterface() {
        logger.info { "重置用户界面至主界面" }
        if (isAtMain()) return
        try {
            logger.info { "返回至主界面" }
            returnToMain()
        } catch (e: Exception) {
            logger.info { "无法返回至主界面；重新启动应用 $this" }
            finalModules.forEach { runFinalModule(it) }
            initModules.forEach { runInitModule(it) }
        }
    }


    protected fun createModule(name: String, run: AutoModule<Self>.() -> Unit) =
        object : AutoModule<Self>(safeCast(this)) {
            override val name: String = name
            override fun run() = run(run)
        }

    protected open fun beforeModule() {
    }

    protected open fun afterModule() {
    }


    override fun toString(): String {
        return "AutoAndroid($name)"
    }

}