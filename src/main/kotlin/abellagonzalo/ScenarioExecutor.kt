package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.providers.TimeProvider
import abellagonzalo.scenarios.Outcome
import abellagonzalo.scenarios.Scenario

class ScenarioExecutor(private val timeProvider: TimeProvider, private val eventBus: EventBus) {
    fun execute(scenario: Scenario): Outcome {
        eventBus.publish(StartScenarioEvent(timeProvider.now(), scenario.id))
        scenario.execute()
        eventBus.publish(EndScenarioEvent(timeProvider.now(), scenario.id))
        return Outcome.PASSED
    }
}

