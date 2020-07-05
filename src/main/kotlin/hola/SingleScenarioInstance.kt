package hola

import hola.scenarios.Scenario
import hola.scenarios.SharedScenario

class SingleScenarioInstance(scenario: Scenario, sharedScenario: SharedScenario?) :
    BaseScenarioInstance<Scenario>(scenario, sharedScenario) {

    override val id = scenario.id

    override val params = emptyList<Any>()

    override fun execute() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::execute.name, id, sharedScenario?.id
        ).use {
            exceptionSafe(scenario::execute)
        }
    }
}