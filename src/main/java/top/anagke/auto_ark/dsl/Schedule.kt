package top.anagke.auto_ark.dsl

import mu.KotlinLogging
import java.time.Instant

class Scheduler(val interval: Long) {

    private var nextInvoking: Long = Long.MIN_VALUE

    fun <T> invoke(op: () -> T): T {
        val current = Instant.now().toEpochMilli()
        if (nextInvoking > current) {
            Thread.sleep(nextInvoking - current)
        }
        nextInvoking = Instant.now().toEpochMilli() + interval
        return op()
    }

}