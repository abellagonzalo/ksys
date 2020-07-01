package hola.scenarios

abstract class ParamScenario : BaseScenario() {
    abstract val parameters: List<List<*>>
    abstract fun executeWithParam(index: Int)
}