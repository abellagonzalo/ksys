package abellagonzalo.teardown

import abellagonzalo.EventBus
import abellagonzalo.events.PushCleanEvent
import abellagonzalo.publish

class Cleaner(private val eventBus: EventBus) {
    companion object {
        val current: Cleaner
            get() = Cleaner(EventBus.current)
    }

    fun clean(action: () -> Unit) {
        eventBus.publish(PushCleanEvent(action))
    }
}
