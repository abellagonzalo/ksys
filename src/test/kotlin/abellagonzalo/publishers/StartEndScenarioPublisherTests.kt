package abellagonzalo.publishers

import abellagonzalo.EventBus
import abellagonzalo.assertEquals
import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.fakes.FakeTimeProvider
import abellagonzalo.fakes.SpyListener
import abellagonzalo.fakes.event
import abellagonzalo.scenarios.Outcome.PASSED
import abellagonzalo.subscribe
import org.junit.jupiter.api.Test
import java.time.Duration.ofSeconds


class StartEndScenarioPublisherTests {

    private val timeProvider = FakeTimeProvider(ofSeconds(2))
    private val eventBus = EventBus.createSingleton()
    private val spyListener = SpyListener().apply {
        eventBus.subscribe<StartScenarioEvent>(subscription())
        eventBus.subscribe<EndScenarioEvent>(subscription())
    }

    private val startPublisher: StartScenarioPublisher = StartScenarioPublisherImpl(timeProvider, eventBus)

    @Test
    fun `Publish scenario execution starts and ends`() {
        val id = "my-id"
        val outcome = PASSED

        startPublisher.publishStart(id).publishEnd(outcome)

        assertEquals(StartScenarioEvent(timeProvider[0], id), spyListener.event(0))
        assertEquals(EndScenarioEvent(timeProvider[1], id, outcome), spyListener.event(0))
    }
}
