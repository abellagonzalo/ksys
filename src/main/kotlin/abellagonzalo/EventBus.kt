package abellagonzalo

import java.io.Closeable
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
    fun <T : Any> publish(kls: KClass<T>, event: T)
    fun <T : Any> subscribe(kls: KClass<T>, action: (T) -> Unit): Closeable
}

inline fun <reified T : Any> EventBus.publish(event: T): Unit {
    publish(T::class, event)
}

inline fun <reified T : Any> EventBus.subscribe(noinline action: (T) -> Unit): Closeable {
    return subscribe(T::class, action)
}