package hola.scenarios

abstract class Scenario : BaseScenarioSetup() {
    abstract fun execute()
}
