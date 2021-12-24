@file:Suppress("unused")

package top.anagke.auto_android.util

import java.util.concurrent.TimeUnit

val Int.millis: Long get() = TimeUnit.MILLISECONDS.toMillis(this.toLong())
val Int.seconds: Long get() = TimeUnit.SECONDS.toMillis(this.toLong())
val Int.minutes: Long get() = TimeUnit.MINUTES.toMillis(this.toLong())
val Int.hours: Long get() = TimeUnit.HOURS.toMillis(this.toLong())

val Int.millisInt: Int get() = TimeUnit.MILLISECONDS.toMillis(this.toLong()).toInt()
val Int.secondsInt: Int get() = TimeUnit.SECONDS.toMillis(this.toLong()).toInt()
val Int.minutesInt: Int get() = TimeUnit.MINUTES.toMillis(this.toLong()).toInt()
val Int.hoursInt: Int get() = TimeUnit.HOURS.toMillis(this.toLong()).toInt()
