package abellagonzalo.teardown

import abellagonzalo.EventBus
import abellagonzalo.events.PushCleanEvent
import abellagonzalo.subscribe
import abellagonzalo.unsubscribe
import java.util.*

class CleanerManager(private val eventBus: EventBus) {
    private val stack = Stack<() -> Unit>()

    init {
        eventBus.subscribe(this::handlePushCleanEvent)
    }

    private fun handlePushCleanEvent(event: PushCleanEvent) {
        stack.push(event.action)
    }

    fun emptyStack() {
        while (stack.isNotEmpty())
            stack.pop().invoke()
        eventBus.unsubscribe(this::handlePushCleanEvent)
    }
}
