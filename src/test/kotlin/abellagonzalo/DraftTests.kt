package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.scenarios.Outcome.PASSED
import abellagonzalo.scenarios.Scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration.ofSeconds

class ExecuteSingleScenarioTests {
    @Test
    fun `Execute a PASSED scenario`() {
        var executedCalled = false

        class Scenario1 : Scenario() {
            override val id: String = "Scenario1"
            override val execute: () -> Unit = {
                executedCalled = true
            }
        }

        val eventBus = EventBusImpl()
        val timeProvider = FakeTimeProvider(ofSeconds(1))
        val spyListener = SpyListener().apply {
            eventBus.subscribe<StartScenarioEvent>(subscription())
            eventBus.subscribe<EndScenarioEvent>(subscription())
        }

        val scenario1 = Scenario1()
        val executor = ScenarioExecutor(timeProvider, eventBus)
        val outcome = executor.execute(Scenario1())

        // Publish start scenario event
        spyListener.assertEquals(StartScenarioEvent(timeProvider[0], scenario1.id))

        // Execute is called
        assertTrue(executedCalled)

        // Publish ended scenario event
        spyListener.assertEquals(EndScenarioEvent(timeProvider[1], scenario1.id))

        // Outcome is PASSED
        assertEquals(PASSED, outcome)
    }
}
