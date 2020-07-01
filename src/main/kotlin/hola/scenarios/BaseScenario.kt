package hola.scenarios

import hola.Logging
import hola.SimpleLogger
import java.util.*

abstract class BaseScenario {
    abstract val id: String
    abstract val description: String
    abstract fun setup()
    abstract fun validate()

    fun teardown() {
        while (!stack.empty())
        // TODO - exception-safe
            stack.pop().invoke()
    }

    fun clean(fn: () -> Unit) {
        stack.push(fn)
    }

    val logger: SimpleLogger
        get() = Logging.current

    private val stack = Stack<() -> Unit>()
}