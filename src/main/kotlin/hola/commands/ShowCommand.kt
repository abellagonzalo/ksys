package hola.commands

import hola.ScenarioClassScanner
import hola.ScenarioInstance
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

// TODO - Add support for * wildcard
// TODO - Add support for regex
@Command(name = "show")
class ShowCommand(scanner: ScenarioClassScanner) : BaseCommand(scanner) {

    @Parameters(index = "0")
    var filter: String = ""

    override fun call(): Int {
        val single = scenariosGrouped.values.flatten().singleOrNull { filter == it.id } ?: run {
            println("No scenario found with id $filter")
            return 1
        }

        printScenario(single)
        return 0
    }

    private fun printScenario(single: ScenarioInstance) {
        printScenarioId(single)
        printSharedDescription(single)
        printParams(single)
        printScenarioDescription(single)
    }

    private fun printScenarioId(it: ScenarioInstance) {
        println(it.id)
        println("---")
    }

    private fun printSharedDescription(it: ScenarioInstance) {
        if (it.sharedScenarioDescription == null) return
        println(it.sharedScenarioDescription)
        println("---")
    }

    private fun printParams(it: ScenarioInstance) {
        if (it.params.isEmpty()) return
        println("Params: ${it.params.joinToString()}")
        println("---")
    }

    private fun printScenarioDescription(it: ScenarioInstance) {
        println(it.scenarioDescription)
    }
}