package abellagonzalo.logging

import abellagonzalo.logging.LogLevel.INFO
import abellagonzalo.events.EventBus
import abellagonzalo.events.LogMessageEvent
import java.time.LocalDateTime
import java.util.*

class ScenarioCleaner(private val executorId: String) {

    private val stack = Stack<() -> Unit>()

    companion object {
        val current: ScenarioCleaner = Thread.currentThread().threadGroup.name.let(::ScenarioCleaner)
    }

    fun always(fn: () -> Unit) {
        stack.push(fn)
    }

    fun clear() {
        while (stack.isNotEmpty())
            stack.pop().invoke()
    }
}

class ScenarioLogger(private val executorId: String) {

    companion object {
        val current: ScenarioLogger = Thread.currentThread().threadGroup.name.let(::ScenarioLogger)
    }

    fun info(message: String) {
        publish(INFO, message)
    }

    private fun publish(level: LogLevel, message: String) {
        EventBus.instance.publish(
            LogMessageEvent(executorId, LocalDateTime.now(), level, message)
        )
    }
}

