package hola.commands

import hola.ScenarioInstance
import hola.services.ScenarioInstantiator
import picocli.CommandLine.Command

// TODO - Add support for * wildcard
// TODO - Add support for regex
@Command(name = "list")
class ListCommand(scenarioInstantiator: ScenarioInstantiator) : BaseCommand(scenarioInstantiator) {
    override fun call(): Int {
        ListPrinter().print(scenariosGrouped)
        return 0
    }
}

class ListPrinter {
    fun print(allScenarios: Map<String, List<ScenarioInstance>>) {
        allScenarios.forEach(this::printScenario)
    }

    private fun printScenario(shared: String, scenarios: List<ScenarioInstance>) {
        println("|- $shared")
        if (scenarios.first().id != scenarios.first().sharedId)
            printSharedScenarios(scenarios)
    }

    private fun printSharedScenarios(scenarios: List<ScenarioInstance>) {
        for (scenario in scenarios)
            println("|  |- ${scenario.id}")
    }
}