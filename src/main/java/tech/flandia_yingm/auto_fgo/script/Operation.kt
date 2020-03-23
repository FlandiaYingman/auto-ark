package tech.flandia_yingm.auto_fgo.script

fun operation(op: Operation.() -> Unit): Operation {
    return object : Operation() {
        override fun invoke() {
            this.op()
        }
    }
}

infix fun Operation.ableIf(whenOp: Operation.() -> Boolean): Operation {
    return object : Operation() {
        override fun invoke() {
            this@ableIf()
        }

        override fun isAbleToInvoke(): Boolean {
            return whenOp()
        }
    }
}

infix fun Operation.ableAfter(afterOp: Operation.() -> Unit): Operation {
    return object : Operation() {
        override fun invoke() {
            this@ableAfter()
        }

        override fun isAbleToInvoke(): Boolean {
            afterOp()
            return true
        }
    }
}

infix fun Operation.thenInvokeWhenAble(operation: Operation): Operation {
    return object : Operation() {
        override fun invoke() {
            this@thenInvokeWhenAble()
            invoke whenAble operation
        }

        override fun isAbleToInvoke(): Boolean {
            return this@thenInvokeWhenAble.isAbleToInvoke()
        }
    }
}


abstract class Operation {

    abstract operator fun invoke()

    open fun isAbleToInvoke(): Boolean = true


    inner class Invoke {
        infix fun whenAble(operation: Operation) {
            while (!operation.isAbleToInvoke()) {
                Thread.sleep(100)
            }
            operation()
        }

        infix fun ifAble(operation: Operation): Boolean {
            return if (operation.isAbleToInvoke()) {
                operation()
                true
            } else {
                false
            }
        }
    }


    val invoke = Invoke()

    fun exception(message: String): Nothing = throw RuntimeException(message)

}