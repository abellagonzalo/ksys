package abellagonzalo.publishers

import abellagonzalo.commands.ResultCode
import abellagonzalo.commands.ScenarioPhase
import abellagonzalo.events.Event
import abellagonzalo.events.EventBusInterface
import abellagonzalo.services.ExecutionIdService
import abellagonzalo.services.TimeService
import java.time.Duration
import java.time.LocalDateTime

data class StartPhaseEvent3(
    override val time: LocalDateTime,
    val executionId: String,
    val phase: ScenarioPhase
) : Event

data class EndPhaseEvent3(
    override val time: LocalDateTime,
    val executionId: String,
    val phase: ScenarioPhase,
    val duration: Duration,
    val result: ResultCode,
    val exception: Exception?
) : Event

class StartPhaseNotPublished : Exception("Start phase event must be published before publishing end phase event")
interface StartEndPhasePublisher {
    fun start(phase: ScenarioPhase)
    fun end(result: ResultCode, exception: Exception?)
}

class StartEndPhasePublisherImpl(
    private val timeService: TimeService,
    private val executionIdService: ExecutionIdService,
    private val eventBus: EventBusInterface
) : StartEndPhasePublisher {

    lateinit var startTime: LocalDateTime
    lateinit var startPhase: ScenarioPhase

    override fun start(phase: ScenarioPhase) {
        startTime = timeService.now
        startPhase = phase
        eventBus.publish(
            StartPhaseEvent3(startTime, executionIdService.executionId, startPhase)
        )
    }

    override fun end(result: ResultCode, exception: Exception?) {
        if (!this::startTime.isInitialized) throw StartPhaseNotPublished()

        val duration = Duration.between(startTime, timeService.now)
        eventBus.publish(
            EndPhaseEvent3(startTime + duration, executionIdService.executionId, startPhase, duration, result, exception)
        )
    }
}