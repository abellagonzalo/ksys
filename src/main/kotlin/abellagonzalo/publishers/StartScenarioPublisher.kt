package abellagonzalo.publishers

import abellagonzalo.commands.ResultCode
import abellagonzalo.events.Event
import abellagonzalo.events.EventBusInterface
import abellagonzalo.services.ExecutionIdService
import abellagonzalo.services.TimeService
import java.time.Duration
import java.time.LocalDateTime

data class StartScenarioEvent2(
    override val time: LocalDateTime,
    val executionId: String,
    val scenarioId: String
) : Event

data class EndScenarioEvent2(
    override val time: LocalDateTime,
    val executionId: String,
    val scenarioId: String,
    val duration: Duration
) : Event

interface StartScenarioPublisher {
    fun start(scenarioId: String): EndScenarioPublisher
    fun end(result: ResultCode)
}

class StartScenarioPublisherImpl(
    private val timeService: TimeService,
    private val executionIdService: ExecutionIdService,
    private val eventBus: EventBusInterface
) : StartScenarioPublisher {

    override fun start(scenarioId: String): EndScenarioPublisher {
        val startTime = timeService.now
        eventBus.publish(
            StartScenarioEvent2(
                startTime,
                executionIdService.executionId,
                scenarioId
            )
        )
        return EndScenarioPublisher(
            startTime,
            scenarioId,
            timeService,
            executionIdService,
            eventBus
        )
    }

    override fun end(result: ResultCode) {
        TODO("Not yet implemented")
    }
}

class EndScenarioPublisher(
    private val startTime: LocalDateTime,
    private val scenarioId: String,
    private val timeService: TimeService,
    private val executionIdService: ExecutionIdService,
    private val eventBus: EventBusInterface
) {
    fun end() {
        val duration = Duration.between(startTime, timeService.now)
        eventBus.publish(
            EndScenarioEvent2(
                startTime + duration,
                executionIdService.executionId,
                scenarioId,
                duration
            )
        )
    }
}