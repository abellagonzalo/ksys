package hola.scenarios

@Suppress("UNCHECKED_CAST")
abstract class ParamScenario2<T0, T1> : ParamScenario() {
    abstract fun execute(param0: T0, param1: T1)
    override fun executeWithParam(index: Int) {
        with(parameters[index]) { execute(get(0) as T0, get(1) as T1) }
    }
}