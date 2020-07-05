package hola

object RunningScenarioLocator {

    private val scenarios = mutableMapOf<String, ScenarioInstance>()

    private val key: String
        get() = Thread.currentThread().threadGroup.name

    val current: ScenarioInstance
        get() = scenarios[key]!!

    internal fun register(scenario: ScenarioInstance) {
        scenarios[key] = scenario
    }

    internal fun removeScenario() {
        scenarios.remove(key)
    }
}