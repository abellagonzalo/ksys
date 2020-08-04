package abellagonzalo.publishers

import abellagonzalo.EventBus
import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.providers.TimeProvider
import abellagonzalo.publish
import abellagonzalo.scenarios.Outcome

class StartScenarioPublisherImpl(
    private val timeProvider: TimeProvider,
    private val eventBus: EventBus
) : StartScenarioPublisher {
    override fun publishStart(id: String): EndScenarioPublisher {
        eventBus.publish(StartScenarioEvent(timeProvider.now(), id))
        return object : EndScenarioPublisher {
            override fun publishEnd(outcome: Outcome) =
                eventBus.publish(EndScenarioEvent(timeProvider.now(), id, outcome))
        }
    }
}