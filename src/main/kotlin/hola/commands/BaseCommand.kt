package hola.commands

import hola.ScenarioInstance
import hola.services.ScenarioInstantiator
import java.util.concurrent.Callable

abstract class BaseCommand(private val scenarioInstantiator: ScenarioInstantiator) : Callable<Int> {
    protected val scenariosGrouped: Map<String, List<ScenarioInstance>>
            by lazy { scenarioInstantiator.instantiateAll() }
}