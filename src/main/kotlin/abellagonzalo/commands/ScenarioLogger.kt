package abellagonzalo.commands

import abellagonzalo.events.EventBus
import abellagonzalo.events.LogMessageEvent
import abellagonzalo.events.ThreadCreatedEvent
import java.time.LocalDateTime

class ScenarioLogger(private val id: String, private val phase: ScenarioPhase) {

    companion object {

        val current: ScenarioLogger
            get() {
                val id = ids[Thread.currentThread().threadGroup.name]!!
                return ScenarioLogger(id, phases[id]!!)
            }

        private val ids = mutableMapOf<String, String>()
        private val phases = mutableMapOf<String, ScenarioPhase>()

        fun a(event: ThreadCreatedEvent) {
            ids[event.threadGroupId] = event.sharedSetupOrScenarioId
        }

        fun b(event: StartScenarioPhaseEvent) {
            phases[event.scenarioId] = event.phase
        }
    }

    fun info(message: String) {
        EventBus.instance.publish(
            LogMessageEvent(id, phase, LocalDateTime.now(),  LogLevel.INFO, message)
        )
    }
}