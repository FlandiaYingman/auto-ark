package top.anagke.auto_ark.ark

import kotlinx.serialization.Serializable
import top.anagke.auto_ark.adb.Device
import top.anagke.auto_ark.adb.Tmpl
import top.anagke.auto_ark.autoArkProps
import top.anagke.auto_ark.img.Img
import java.io.FileNotFoundException
import java.net.URL

@Serializable
data class ArkProps(
    val loginProps: LoginProps = LoginProps(),
    val operateProps: OperateProps = OperateProps(),
    val recruitProps: RecruitProps = RecruitProps(),
    val riicProps: RiicProps = RiicProps(),
)

val arkProps = autoArkProps.arkProps


private object ArkRes {
    operator fun invoke(name: String): URL? {
        return this.javaClass.getResource(name)
    }
}

fun template(name: String, diff: Double = 0.05): Lazy<Tmpl> {
    return lazy {
        val url = ArkRes(name)
        if (url == null) {
            throw FileNotFoundException("resource '$name' not found by '${ArkRes.javaClass.packageName}'")
        } else {
            Tmpl(Img(url.readBytes()), diff, name)
        }
    }
}

// 主界面
val atMainScreen by template("awaitMainScreen.png", diff = 0.10)


fun dailyRoutine(device: Device) {
    device(login())
    device(lastOperation())
    while (device(autoOperation()) == true);
    device(exitOperation())
    device(autoMission())
    device(autoRecruitment())
    //TODO: device(autoRIIC())
    //TODO: device(autoCreditStore())
}