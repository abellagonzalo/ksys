package hola.commands

import hola.ScenarioRunner
import hola.services.ScenarioInstantiator
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "run")
class RunCommand(scenarioInstantiator: ScenarioInstantiator) : BaseCommand(scenarioInstantiator) {

    @Parameters(index = "0", defaultValue = "all")
    var filter: String = ""

    override fun call(): Int {
        for ((_, instances) in scenariosGrouped)
            ScenarioRunner().run(instances)
        return 0
    }
}