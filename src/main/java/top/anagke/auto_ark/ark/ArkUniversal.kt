package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.Tmpl
import top.anagke.auto_ark.ark.OperateResult.SUCCESS
import top.anagke.auto_ark.AUTO_PROPS
import top.anagke.auto_ark.img.Img
import java.io.FileNotFoundException
import java.net.URL

@Serializable
data class ArkProps(
    val loginProps: LoginProps = LoginProps(),
    val operateProps: OperateProps = OperateProps(),
    val riicProps: RiicProps = RiicProps(),
    val recruitProps: RecruitProps = RecruitProps(),
)

val arkProps = AUTO_PROPS.arkProps


object ArkRes {
    operator fun invoke(name: String): URL? {
        return this.javaClass.getResource(name)
    }
}

fun template(name: String, diff: Double = 0.05): Tmpl {
    val url = ArkRes(name)
    var count = 0

    if (url == null) {
        val urlList = mutableListOf<URL>()
        do {
            val varUrl = ArkRes("${name.substringBeforeLast(".")}_${count++}.${name.substringAfterLast(".")}")
            if (varUrl != null) urlList.add(varUrl)
        } while (varUrl != null)
        if (urlList.isEmpty()) {
            throw FileNotFoundException("resource '$name' not found by '${ArkRes.javaClass.packageName}'")
        } else {
            return Tmpl(urlList.map { Img(it.readBytes()) }, diff, name = name)
        }
    } else {
        return Tmpl(Img(url.readBytes()), diff, name)
    }
}

// 主界面
val atMainScreen = template("atMainScreen.png", diff = 0.06)


fun dailyRoutine(device: Device) {
    device(login())
    device(autoRecruit())
    device(autoRiic())
    device(lastOperation())
    while (device(autoOperation()) == SUCCESS);
    device(exitOperation())
    device(autoMission())
    //TODO: device(autoCreditStore())
}


fun main() {
//    stopNemu()
//    startNemu()
    Device().let { dailyRoutine(it) }
//    stopNemu()
}