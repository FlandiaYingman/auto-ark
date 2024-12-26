package dev.flandia.ark.update

import java.net.URI
import java.net.URL

object ArkUrls {

    val arkVersionUrl: URL
        get() {
            return URI.create("https://ak-conf.hypergryph.com/config/prod/official/Android/version").toURL()
        }

    val officialApkUrl: URL
        get() {
            return URI.create("https://ak.hypergryph.com/downloads/android_lastest").toURL()
        }

}