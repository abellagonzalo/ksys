package abellagonzalo

import abellagonzalo.events.EndScenarioEvent
import abellagonzalo.events.StartScenarioEvent
import abellagonzalo.fakes.FakeTimeProvider
import abellagonzalo.fakes.SpyListener
import abellagonzalo.fakes.event
import abellagonzalo.providers.TimeProvider
import abellagonzalo.publishers.StartScenarioPublisher
import abellagonzalo.publishers.StartScenarioPublisherImpl
import abellagonzalo.scenarios.Outcome.*
import abellagonzalo.scenarios.Scenario
import abellagonzalo.scenarios.SkipException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration.ofSeconds

class ExecuteSingleScenarioTests {

    private var eventBus: EventBus = EventBus.create()
    private var timeProvider: TimeProvider = FakeTimeProvider(ofSeconds(1))
    private lateinit var startScenarioPublisher: StartScenarioPublisher

    private lateinit var spyListener: SpyListener

    private lateinit var executor: ScenarioExecutor

    private abstract class TestScenario : Scenario() {
        override val id: String = "Scenario1"
    }

    @BeforeEach
    fun beforeEach() {
        startScenarioPublisher = StartScenarioPublisherImpl(timeProvider, eventBus)
        spyListener = SpyListener().apply {
            eventBus.subscribe<StartScenarioEvent>(subscription())
            eventBus.subscribe<EndScenarioEvent>(subscription())
        }
        executor = ScenarioExecutor(eventBus, startScenarioPublisher)
    }

    @Test
    fun `PASSED scenario`() {
        val scenario1 = object : TestScenario() {
            var executedCalled = false
                private set

            override fun execute() = kotlin.run { executedCalled = true }
        }
        executor.execute(scenario1)
        assertTrue(scenario1.executedCalled)

        assertEquals(scenario1.id, spyListener.event<StartScenarioEvent>(0).scenarioId)
        assertEquals(scenario1.id, spyListener.event<EndScenarioEvent>(0).scenarioId)
        assertEquals(PASSED, spyListener.event<EndScenarioEvent>(0).outcome)
    }

    @Test
    fun `FAILED scenario`() {
        val scenario1 = object : TestScenario() {
            override fun execute(): Unit = throw Exception("Something went wrong...")
        }
        executor.execute(scenario1)
        assertEquals(FAILED, spyListener.event<EndScenarioEvent>(0).outcome)
    }

    @Test
    fun `SKIPPED scenario`() {
        val scenario1 = object : TestScenario() {
            override fun execute(): Unit = throw SkipException("Skipping for no particular reason...")
        }
        executor.execute(scenario1)
        assertEquals(SKIPPED, spyListener.event<EndScenarioEvent>(0).outcome)
    }

    @Test
    fun `Clean scenario`() {
        val scenario = object : TestScenario() {
            var cleaningCalled = false
            override fun execute() = clean { cleaningCalled = true }
        }
        executor.execute(scenario)
        assertTrue(scenario.cleaningCalled)
    }
}

interface BeforeAll {
    val id: String
    fun execute()
}

class ExecuteSharedSetupTests {
    @Test
    fun `Execute a PASSED shared setup`() {
        @Test
        fun `Execute a PASSED shared setup`() {
            val beforeAll = object : BeforeAll {
                var executeCalled = false
                    private set
                override val id: String = "before-all-1"
                override fun execute() {
                    executeCalled = true
                }
            }

            val sharedSetupExecutor = SharedSetupExecutor()
            sharedSetupExecutor.execute(beforeAll)

            assertTrue(beforeAll.executeCalled)
        }
    }
}

class SharedSetupExecutor {
    fun execute(beforeAll: BeforeAll) {
        beforeAll.execute()
    }
}
