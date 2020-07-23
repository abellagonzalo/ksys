package abellagonzalo.publishers

import abellagonzalo.events.EventBusInterface
import abellagonzalo.services.ExecutionIdService
import abellagonzalo.services.TimeService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.Duration
import java.time.LocalDateTime

class StartScenarioPublisherTests {
    private val startTime = LocalDateTime.now()
    private val duration = Duration.ofSeconds(1)
    private val endTime = startTime + duration
    private val executionId = "my-execution-id"
    private val scenarioId = "my-scenario-id"


    private val expectedStartEvent =
        StartScenarioEvent2(startTime, executionId, scenarioId)
    private val expectedEndEvent =
        EndScenarioEvent2(endTime, executionId, scenarioId, duration)

    private val mockTimeService = mock(TimeService::class.java)
    private val mockExecutionIdService = mock(ExecutionIdService::class.java)
    private val mockEventBus = mock(EventBusInterface::class.java)

    private val startScenarioPublisher: StartScenarioPublisher =
        StartScenarioPublisherImpl(
            mockTimeService,
            mockExecutionIdService,
            mockEventBus
        )

    @BeforeEach
    fun beforeEach() {
        `when`(mockTimeService.now).thenReturn(startTime).thenReturn(endTime)
        `when`(mockExecutionIdService.executionId).thenReturn(executionId)
    }

    @Test
    fun `Publish starting scenario`() {
        startScenarioPublisher.start(scenarioId)
        verify(mockEventBus, times(1)).publish(expectedStartEvent)
    }

    @Test
    fun `Publish end scenario`() {
        startScenarioPublisher.start(scenarioId).end()
        verify(mockEventBus, times(1)).publish(expectedEndEvent)
    }
}

