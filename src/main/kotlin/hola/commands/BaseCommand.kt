package hola.commands

import hola.ScenarioClassScanner
import hola.ScenarioInstance
import hola.groupScenarioInstances
import java.util.concurrent.Callable

abstract class BaseCommand(private val scanner: ScenarioClassScanner) : Callable<Int> {
    protected val scenariosGrouped: Map<String, List<ScenarioInstance>> by lazy { groupScenarioInstances(scanner.findAll()) }
}