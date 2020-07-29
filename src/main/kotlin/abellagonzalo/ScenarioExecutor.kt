package abellagonzalo

import abellagonzalo.publishers.StartScenarioPublisher
import abellagonzalo.scenarios.Outcome
import abellagonzalo.scenarios.Outcome.*
import abellagonzalo.scenarios.Scenario
import abellagonzalo.scenarios.SkipException
import abellagonzalo.teardown.CleanerManager

interface Executable {
    val id: String
    fun execute()
}

class ScenarioExecutor(eventBus: EventBus, startScenarioPublisher: StartScenarioPublisher) :
    BaseExecutor(eventBus, startScenarioPublisher)

abstract class BaseExecutor(
    eventBus: EventBus,
    private val startScenarioPublisher: StartScenarioPublisher
) {

    private val cleanerManager = CleanerManager(eventBus)

    fun execute(scenario: Scenario) {
        val endScenario = startScenarioPublisher.publishStart(scenario.id)
        val outcome = executeScenario(scenario)
        cleanerManager.emptyStack()
        endScenario.publishEnd(outcome)
    }

    private fun executeScenario(scenario: Scenario): Outcome {
        return try {
            scenario.execute()
            PASSED
        } catch (ex: SkipException) {
            SKIPPED
        } catch (ex: Exception) {
            FAILED
        }
    }
}

