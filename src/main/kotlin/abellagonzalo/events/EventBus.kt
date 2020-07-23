package abellagonzalo.events

import kotlin.reflect.KClass

interface EventBusInterface {
    fun <T : Any> subscribe(eventType: KClass<out T>, handler: (T) -> Unit)
    fun <T : Any> publish(event: T)
}

@Suppress("UNCHECKED_CAST")
class EventBus : EventBusInterface {
    companion object {
        var instance: EventBus = EventBus()
            private set
    }

    private val receivers: MutableMap<KClass<*>, MutableList<(Any) -> Unit>> = mutableMapOf()

    override fun <T : Any> subscribe(eventType: KClass<out T>, handler: (T) -> Unit) {
        val list = receivers.getOrPut(eventType) { mutableListOf() }
        list.add { handler(it as T) }
    }

    override fun <T : Any> publish(event: T) {
        for (receiver in receivers[event::class].orEmpty()) {
            receiver(event)
        }
    }

    internal fun clear() {
        receivers.clear()
    }
}