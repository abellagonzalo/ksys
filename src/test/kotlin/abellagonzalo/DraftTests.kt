package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.scenarios.Outcome.*
import abellagonzalo.scenarios.Scenario
import abellagonzalo.scenarios.SkipException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration.ofSeconds
import java.util.*

class ExecuteSingleScenarioTests {

    private lateinit var eventBus: EventBus
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var spyListener: SpyListener
    private lateinit var executor: ScenarioExecutor

    private abstract class TestScenario : Scenario() {
        override val id: String = "Scenario1"
    }

    @BeforeEach
    fun beforeEach() {
        eventBus = EventBus.create()
        timeProvider = FakeTimeProvider(ofSeconds(1))
        spyListener = SpyListener().apply {
            eventBus.subscribe<StartScenarioEvent>(subscription())
            eventBus.subscribe<EndScenarioEvent>(subscription())
        }
        executor = ScenarioExecutor(timeProvider, eventBus)
    }

    @Test
    fun `PASSED scenario`() {
        val scenario1 = object : TestScenario() {
            var executedCalled = false
                private set
            override val execute = { executedCalled = true }
        }
        executor.execute(scenario1)
        assertTrue(scenario1.executedCalled)
        spyListener.assertEquals(StartScenarioEvent(timeProvider[0], scenario1.id))
        spyListener.assertEquals(EndScenarioEvent(timeProvider[1], scenario1.id, PASSED))
    }

    @Test
    fun `FAILED scenario`() {
        val scenario1 = object : TestScenario() {
            override val execute = { throw Exception("Something went wrong...") }
        }
        executor.execute(scenario1)
        spyListener.assertEquals(EndScenarioEvent(timeProvider[1], scenario1.id, FAILED))
    }

    @Test
    fun `SKIPPED scenario`() {
        val scenario1 = object : TestScenario() {
            override val execute = { throw SkipException("Skipping for no particular reason...") }
        }
        executor.execute(scenario1)
        spyListener.assertEquals(EndScenarioEvent(timeProvider[1], scenario1.id, SKIPPED))
    }

    @Test
    fun `Clean scenario`() {
        val scenario = object : TestScenario() {
            var cleaningCalled = false
            override val execute: () -> Unit = {
                clean { cleaningCalled = true }
            }
        }
        executor.execute(scenario)
        assertTrue(scenario.cleaningCalled)
    }
}
