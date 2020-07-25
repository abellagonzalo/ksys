package abellagonzalo

import kotlin.reflect.KClass

interface EventBus {
    fun <T : Any> subscribe(eventType: KClass<out T>, handler: (T) -> Unit)
    fun <T : Any> publish(event: T)
}

class EventBusImpl : EventBus {
    companion object {
        var instance: EventBus = EventBusImpl()
            private set
    }

    private val receivers = mutableMapOf<KClass<*>, MutableList<Any>>()

    override fun <T : Any> subscribe(eventType: KClass<out T>, handler: (T) -> Unit) {
        val list = receivers.getOrPut(eventType) { mutableListOf() }
        list.add(handler)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> publish(event: T) {
        for (receiver in receivers[event::class].orEmpty())
            (receiver as (T) -> Any).invoke(event)
    }
}

inline fun <reified T : Any> EventBus.subscribe(noinline handler: (T) -> Unit) {
    subscribe(T::class, handler)
}