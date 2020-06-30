package hola.commands

import hola.ScenarioClassScanner
import hola.ScenarioRunner
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "run")
class RunCommand(scanner: ScenarioClassScanner) : BaseCommand(scanner) {

    @Parameters(index = "0", defaultValue = "all")
    var filter: String = ""

    override fun call(): Int {
        for ((_, instances) in scenariosGrouped)
            ScenarioRunner().run(instances)
        return 0
    }
}