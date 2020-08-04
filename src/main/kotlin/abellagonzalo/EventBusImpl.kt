package abellagonzalo

import java.io.Closeable
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class EventBusImpl : EventBus {
    val subscribers = mutableListOf<(Any) -> Unit>()

    override fun <T : Any> publish(kls: KClass<T>, event: T) {
        for (subscriber in subscribers)
            subscriber(event)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> subscribe(kls: KClass<T>, action: (T) -> Unit): Closeable {
        val subscription = { any: Any -> if (any::class.isSubclassOf(kls)) action(any as T) }
        subscribers += subscription
        return Closeable { subscribers.remove(subscription) }
    }
}