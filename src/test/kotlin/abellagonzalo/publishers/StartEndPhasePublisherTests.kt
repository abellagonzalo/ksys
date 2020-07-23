package abellagonzalo.publishers

import abellagonzalo.commands.ResultCode.PASSED
import abellagonzalo.commands.ScenarioPhase.SETUP
import abellagonzalo.events.EventBusInterface
import abellagonzalo.services.ExecutionIdService
import abellagonzalo.services.TimeService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.Duration
import java.time.LocalDateTime.now

class StartEndPhasePublisherTests {

    private val executionId = "my-execution-id"
    private val startTime = now()
    private val duration = Duration.ofSeconds(2)
    private val endTime = startTime + duration
    private val phase = SETUP
    private val result = PASSED
    private val exception: Exception? = null

    private val expectedStartEvent =
        StartPhaseEvent3(startTime, executionId, phase)
    private val expectedEndEvent =
        EndPhaseEvent3(endTime, executionId, phase, duration, result, exception)

    private val mockTimeService = mock(TimeService::class.java)
    private val mockExecutionIdService = mock(ExecutionIdService::class.java)
    private val mockEventBusInterface = mock(EventBusInterface::class.java)

    private val startEndPhasePublisher: StartEndPhasePublisher =
        StartEndPhasePublisherImpl(
            mockTimeService,
            mockExecutionIdService,
            mockEventBusInterface
        )

    @BeforeEach
    fun beforeEach() {
        `when`(mockExecutionIdService.executionId)
            .thenReturn(executionId)

        `when`(mockTimeService.now)
            .thenReturn(startTime)
            .thenReturn(endTime)
    }

    @Test
    fun `Publish start phase event`() {
        startEndPhasePublisher.start(phase)
        verify(mockEventBusInterface, times(1)).publish(expectedStartEvent)
    }

    @Test
    fun `Publish end phase event not calling start before throws exception`() {
        Assertions.assertThrows(StartPhaseNotPublished::class.java) {
            startEndPhasePublisher.end(result, exception)
        }
    }

    @Test
    fun `Publish end phase after start event`() {
        startEndPhasePublisher.start(phase)
        startEndPhasePublisher.end(result, exception)
        verify(mockEventBusInterface, times(1)).publish(expectedEndEvent)
    }
}

