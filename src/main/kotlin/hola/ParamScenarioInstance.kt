package hola

import hola.scenarios.ParamScenario
import hola.scenarios.SharedSetup

class ParamScenarioInstance(private val index: Int, scenario: ParamScenario, sharedSetup: SharedSetup?) :
    BaseScenarioInstance<ParamScenario>(scenario, sharedSetup) {

    override val id = scenario.id + "_" + index.toString().padStart(4, '0')

    override val params = scenario.parameters[index]

    override fun execute() {
        Logging.registerLogger(
            scenario::class.java.name, "execute", id, sharedSetup?.id
        ).use {
            exceptionSafe { scenario.executeWithParam(index) }
        }
    }
}