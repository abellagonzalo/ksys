package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.scenarios.Outcome.FAILED
import abellagonzalo.scenarios.Outcome.PASSED
import abellagonzalo.scenarios.Scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration.ofSeconds

class ExecuteSingleScenarioTests {

    private lateinit var eventBus: EventBus
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var spyListener: SpyListener
    private lateinit var executor: ScenarioExecutor

    @BeforeEach
    fun beforeEach() {
        eventBus = EventBusImpl()
        timeProvider = FakeTimeProvider(ofSeconds(1))
        spyListener = SpyListener().apply {
            eventBus.subscribe<StartScenarioEvent>(subscription())
            eventBus.subscribe<EndScenarioEvent>(subscription())
        }
        executor = ScenarioExecutor(timeProvider, eventBus)
    }


    @Test
    fun `PASSED scenario`() {
        val scenario1 = object : Scenario() {
            override val id: String = "Scenario1"
            override val execute: () -> Unit = { executedCalled = true }
            var executedCalled = false
                private set
        }
        val outcome = executor.execute(scenario1)

        assertTrue(scenario1.executedCalled)
        assertEquals(PASSED, outcome)

        spyListener.assertEquals(StartScenarioEvent(timeProvider[0], scenario1.id))
        spyListener.assertEquals(EndScenarioEvent(timeProvider[1], scenario1.id, PASSED))
    }

    @Test
    fun `FAILED scenario`() {
        val scenario1 = object : Scenario() {
            override val id: String = "Scenario1"
            override val execute: () -> Unit = {
                throw Exception("Something went wrong...")
            }
        }

        val outcome = executor.execute(scenario1)

        assertEquals(FAILED, outcome)

        spyListener.assertEquals(EndScenarioEvent(timeProvider[1], scenario1.id, FAILED))
    }
}
