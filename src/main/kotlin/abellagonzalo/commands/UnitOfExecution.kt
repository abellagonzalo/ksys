package abellagonzalo.commands

import abellagonzalo.events.EndScenarioExecutionEvent
import abellagonzalo.events.EventBus
import abellagonzalo.events.StartScenarioExecutionEvent
import abellagonzalo.events.ThreadCreatedEvent
import java.time.Duration
import java.time.LocalDateTime

class UnitOfExecution(private val sharedSetup: SharedSetupDelegate?, private val scenarios: List<ScenarioDelegate>) {

    private lateinit var currentThreadGroupName: String
    private lateinit var currentScenarioDelegate: ScenarioDelegate
    private lateinit var currentScenarioStartTime: LocalDateTime

    fun execute(threadGroupId: String) {
        currentThreadGroupName = threadGroupId

        sharedSetup?.setup()
        sharedSetup?.validate()

        for (scenario in scenarios) {
            currentScenarioDelegate = scenario

            publishThreadAndScenarioLink()
            publishScenarioStartExecution()

            scenario.setup()
            scenario.execute()
            scenario.validate()
            scenario.teardown()

            publishScenarioFinishedExecution()
        }

        sharedSetup?.teardown()
    }

    private fun publishThreadAndScenarioLink() {
        EventBus.instance.publish(
            ThreadCreatedEvent(currentThreadGroupName, currentScenarioDelegate.id)
        )
    }

    private fun publishScenarioStartExecution() {
        currentScenarioStartTime = LocalDateTime.now()
        EventBus.instance.publish(
            StartScenarioExecutionEvent(currentThreadGroupName, sharedSetup?.id, currentScenarioDelegate.id, currentScenarioStartTime)
        )
    }

    private fun publishScenarioFinishedExecution() {
        val duration = Duration.between(currentScenarioStartTime, LocalDateTime.now())
        EventBus.instance.publish(
            EndScenarioExecutionEvent(
                sharedSetup?.id,
                currentScenarioDelegate.id,
                currentScenarioStartTime + duration,
                duration,
                currentScenarioDelegate.result
            )
        )
    }
}
