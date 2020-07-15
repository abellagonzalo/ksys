package abellagonzalo.commands

import abellagonzalo.events.EventBus
import abellagonzalo.events.ThreadCreatedEvent
import java.time.Duration
import java.time.LocalDateTime

class UnitOfExecution(private val sharedSetup: SharedSetupDelegate?, private val scenarios: List<ScenarioDelegate>) {
    fun execute(threadGroupId: String) {
        sharedSetup?.setup()
        sharedSetup?.validate()

        for (scenario in scenarios) {

            EventBus.instance.publish(
                ThreadCreatedEvent(
                    threadGroupId,
                    scenario.id
                )
            )

            val startTime = LocalDateTime.now()
            EventBus.instance.publish(
                StartScenarioExecutionEvent(sharedSetup?.id, scenario.id, startTime)
            )

            scenario.setup()
            scenario.execute()
            scenario.validate()
            scenario.teardown()

            val endTime = LocalDateTime.now()
            EventBus.instance.publish(
                EndScenarioExecutionEvent(
                    sharedSetup?.id,
                    scenario.id,
                    endTime,
                    Duration.between(startTime, endTime),
                    scenario.result
                )
            )
        }

        sharedSetup?.teardown()
    }
}

data class StartScenarioExecutionEvent(
    val sharedSetupId: String?,
    val scenarioId: String,
    val time: LocalDateTime
)

data class EndScenarioExecutionEvent(
    val sharedSetupId: String?,
    val scenarioId: String,
    val time: LocalDateTime,
    val executionTime: Duration,
    val resultCode: ResultCode
)
