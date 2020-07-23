package abellagonzalo.logging

import abellagonzalo.commands.StartPhaseEvent
import abellagonzalo.events.EventBus
import abellagonzalo.events.LogMessageEvent
import abellagonzalo.events.StartScenarioExecutionEvent

class LoggerSink {

    private val currentScenarioInExecutor =
        mutableMapOf<String, StartScenarioExecutionEvent>()
    private val currentPhaseInScenario = mutableMapOf<String, StartPhaseEvent>()

    fun subscribe(eventBus: EventBus) {
        eventBus.subscribe(LogMessageEvent::class, this::handleLogMessageEvents)
        eventBus.subscribe(StartScenarioExecutionEvent::class, this::handleStartScenarioExecutionEvent)
        eventBus.subscribe(StartPhaseEvent::class, this::handleStartPhaseEvent)
    }

    private fun handleLogMessageEvents(event: LogMessageEvent) {
        val executionEvent = currentScenarioInExecutor[event.executorId]!!
        val phaseEvent = currentPhaseInScenario[executionEvent.scenarioId]!!
        println("${event.time} [${event.level}] ${phaseEvent.sharedSetupId}::${phaseEvent.scenarioId} ${event.message}")
    }

    private fun handleStartScenarioExecutionEvent(event: StartScenarioExecutionEvent) {
        currentScenarioInExecutor[event.executorId] = event
    }

    private fun handleStartPhaseEvent(event: StartPhaseEvent) {
        currentPhaseInScenario[event.scenarioId] = event
    }
}