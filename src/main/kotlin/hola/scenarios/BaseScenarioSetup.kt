package hola.scenarios

import hola.Logging
import hola.SimpleLogger
import java.lang.Exception
import java.util.*

abstract class BaseScenarioSetup {
    abstract val id: String
    abstract val description: String
    abstract fun setup()
    abstract fun validate()

    open fun teardown() {
        val a = this::class.simpleName
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