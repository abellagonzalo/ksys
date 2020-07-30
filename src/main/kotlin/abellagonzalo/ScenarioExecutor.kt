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

class ScenarioExecutor(
    cleanerManager: CleanerManager,
    startScenarioPublisher: StartScenarioPublisher
) :
    BaseExecutor(cleanerManager, startScenarioPublisher)

abstract class BaseExecutor(
    private val cleanerManager: CleanerManager,
    private val startScenarioPublisher: StartScenarioPublisher
) {

    fun execute(scenario: Scenario) {
        val endScenario = startScenarioPublisher.publishStart(scenario.id)
        val scenarioCleaner = cleanerManager.createNew()
        val outcome = executeScenario(scenario)
        scenarioCleaner.emptyStack()
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

