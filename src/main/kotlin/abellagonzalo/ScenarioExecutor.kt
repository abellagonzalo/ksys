package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.providers.TimeProvider
import abellagonzalo.scenarios.Outcome
import abellagonzalo.scenarios.Outcome.FAILED
import abellagonzalo.scenarios.Outcome.PASSED
import abellagonzalo.scenarios.Scenario
import java.lang.Exception

class ScenarioExecutor(private val timeProvider: TimeProvider, private val eventBus: EventBus) {
    fun execute(scenario: Scenario): Outcome {
        publishStart(scenario.id)
        return try {
            scenario.execute()
            publishEnd(scenario.id, PASSED)
        } catch (ex: Exception) {
            publishEnd(scenario.id, FAILED)
        }
    }

    private fun publishStart(scenarioId: String) {
        eventBus.publish(StartScenarioEvent(timeProvider.now(), scenarioId))
    }

    private fun publishEnd(scenarioId: String, outcome: Outcome): Outcome {
        eventBus.publish(EndScenarioEvent(timeProvider.now(), scenarioId, outcome))
        return outcome
    }
}

