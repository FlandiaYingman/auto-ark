package top.anagke.auto_android.util

class FrequencyLimiter(
    private val timePerInvocation: Long,
) {

    private var nextInvocationTime: Long = Long.MIN_VALUE


    fun <T> run(block: () -> T): T {
        awaitUntilNextInvocationTime()
        computeNextInvocationTime()
        return block()
    }

    private fun awaitUntilNextInvocationTime() {
        val currentTime = System.currentTimeMillis()
        if (currentTime < nextInvocationTime) {
            val sleepTime = nextInvocationTime - currentTime
            Thread.sleep(sleepTime)
        }
    }

    private fun computeNextInvocationTime() {
        nextInvocationTime = System.currentTimeMillis() + timePerInvocation
    }

}