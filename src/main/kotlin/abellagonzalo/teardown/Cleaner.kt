package abellagonzalo.teardown

import abellagonzalo.EventBus
import abellagonzalo.events.PushCleanEvent

class Cleaner(private val eventBus: EventBus) {
    companion object {
        val current: Cleaner = Cleaner(EventBus.current)
    }

    fun clean(action: () -> Unit) {
        eventBus.publish(PushCleanEvent(action))
    }
}
