package hola.scenarios

@Suppress("UNCHECKED_CAST")
abstract class ParamScenario1<T0> : ParamScenario() {
    abstract fun execute(param0: T0)
    override fun executeWithParam(index: Int) {
        with(parameters[index]) { execute(get(0) as T0) }
    }
}