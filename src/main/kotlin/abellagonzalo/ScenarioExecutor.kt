package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.providers.TimeProvider
import abellagonzalo.scenarios.Outcome
import abellagonzalo.scenarios.Outcome.*
import abellagonzalo.scenarios.Scenario
import abellagonzalo.scenarios.SkipException

class ScenarioExecutor(private val timeProvider: TimeProvider, private val eventBus: EventBus) {
    fun execute(scenario: Scenario) {
        publishStart(scenario.id)
        val outcome = executeScenario(scenario)
        publishEnd(scenario.id, outcome)
    }

    private fun publishStart(scenarioId: String) {
        eventBus.publish(StartScenarioEvent(timeProvider.now(), scenarioId))
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

    private fun publishEnd(scenarioId: String, outcome: Outcome): Outcome {
        eventBus.publish(EndScenarioEvent(timeProvider.now(), scenarioId, outcome))
        return outcome
    }
}

