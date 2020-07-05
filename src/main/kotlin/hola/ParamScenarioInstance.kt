package hola

import hola.scenarios.ParamScenario
import hola.scenarios.SharedScenario

class ParamScenarioInstance(private val index: Int, scenario: ParamScenario, sharedScenario: SharedScenario?) :
    BaseScenarioInstance<ParamScenario>(scenario, sharedScenario) {

    override val id = scenario.id + "_" + index.toString().padStart(4, '0')

    override val params = scenario.parameters[index]

    override fun execute() {
        Logging.registerLogger(
            scenario::class.java.name, "execute", id, sharedScenario?.id
        ).use {
            exceptionSafe { scenario.executeWithParam(index) }
        }
    }
}