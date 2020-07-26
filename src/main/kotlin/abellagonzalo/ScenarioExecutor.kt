package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.providers.TimeProvider
import abellagonzalo.scenarios.Outcome
import abellagonzalo.scenarios.Outcome.PASSED
import abellagonzalo.scenarios.Scenario

class ScenarioExecutor(private val timeProvider: TimeProvider, private val eventBus: EventBus) {
    fun execute(scenario: Scenario): Outcome {
        publishStart(scenario.id)
        scenario.execute()
        publishEnd(scenario.id)
        return PASSED
    }

    private fun publishStart(scenarioId: String) {
        eventBus.publish(StartScenarioEvent(timeProvider.now(), scenarioId))
    }

    private fun publishEnd(scenarioId: String) {
        eventBus.publish(EndScenarioEvent(timeProvider.now(), scenarioId, PASSED))
    }
}

