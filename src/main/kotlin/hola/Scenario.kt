package hola

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

abstract class SharedScenario : BaseScenario()

abstract class Scenario : BaseScenario() {
    abstract fun execute()
}

abstract class ParamScenario : BaseScenario() {
    abstract val parameters: List<List<*>>
    abstract fun executeWithParam(index: Int)
}

@Suppress("UNCHECKED_CAST")
abstract class ParamScenario1<T0> : ParamScenario() {
    abstract fun execute(param0: T0)
    override fun executeWithParam(index: Int) {
        with(parameters[index]) { execute(get(0) as T0) }
    }
}

@Suppress("UNCHECKED_CAST")
abstract class ParamScenario2<T0, T1> : ParamScenario() {
    abstract fun execute(param0: T0, param1: T1)
    override fun executeWithParam(index: Int) {
        with(parameters[index]) { execute(get(0) as T0, get(1) as T1) }
    }
}
