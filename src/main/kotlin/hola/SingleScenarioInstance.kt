package hola

import hola.scenarios.Scenario
import hola.scenarios.SharedSetup

class SingleScenarioInstance(scenario: Scenario, sharedSetup: SharedSetup?) :
    BaseScenarioInstance<Scenario>(scenario, sharedSetup) {

    override val id = scenario.id

    override val params = emptyList<Any>()

    override fun execute() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::execute.name, id, sharedSetup?.id
        ).use {
            exceptionSafe(scenario::execute)
        }
    }
}