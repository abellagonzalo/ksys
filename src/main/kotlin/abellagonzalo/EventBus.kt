package abellagonzalo

import kotlin.reflect.KClass

interface EventBus {

    companion object {
        lateinit var current: EventBus
            private set

        fun createSingleton(): EventBus {
            current = EventBusImpl()
            return current
        }
    }

    fun <T : Any> subscribe(eventType: KClass<out T>, handler: (T) -> Unit)
    fun <T : Any> unsubscribe(eventType: KClass<out T>, handler: (T) -> Unit)
    fun <T : Any> publish(event: T)
}

class EventBusImpl : EventBus {
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

    override fun <T : Any> unsubscribe(eventType: KClass<out T>, handler: (T) -> Unit) {
        val list = receivers.remove(eventType)
        if (list == null || !list.remove(handler))
            throw Exception("Could not find the handler specified.")
    }
}

inline fun <reified T : Any> EventBus.subscribe(noinline handler: (T) -> Unit) {
    subscribe(T::class, handler)
}

inline fun <reified T : Any> EventBus.unsubscribe(noinline handler: (T) -> Unit) {
    unsubscribe(T::class, handler)
}
