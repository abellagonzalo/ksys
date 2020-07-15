package abellagonzalo.events

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class EventBus {
    companion object {
        val instance: EventBus =
            EventBus()
    }

    private val receivers: MutableMap<KClass<*>, MutableList<(Any) -> Unit>> = mutableMapOf()

    fun <T : Any> subscribe(eventType: KClass<out T>, handler: (T) -> Unit) {
        val list = receivers.getOrPut(eventType) { mutableListOf() }
        list.add { handler(it as T) }
    }

    fun <T : Any> publish(event: T) {
        for (receiver in receivers[event::class].orEmpty()) {
            receiver(event)
        }
    }
}