package hola.scenarios

abstract class ParamScenario : BaseScenarioSetup() {
    abstract val parameters: List<List<*>>
    abstract fun executeWithParam(index: Int)
}