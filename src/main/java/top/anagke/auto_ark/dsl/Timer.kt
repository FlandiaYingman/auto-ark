package top.anagke.auto_ark.dsl

import java.time.Instant

/**
 * A class helps restrict the minimal interval of calling a method.
 */
class Timer(private val interval: Long) {

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