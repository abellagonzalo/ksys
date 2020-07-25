package abellagonzalo

import org.junit.jupiter.api.Assertions
import kotlin.reflect.KClass

class SpyListener {
    private val list = mutableListOf<Any>()

    fun <T: Any> subscription(klass: KClass<T>) : (T) -> Unit {
        return { list.add(it) }
    }

    fun <T: Any> event(klass: KClass<T>, zeroIndex: Int = 0): T {
        return list.filterIsInstance(klass.java)[zeroIndex]
    }
}

inline fun <reified T : Any> SpyListener.subscription() : (T) -> Unit {
    return subscription(T::class)
}

inline fun <reified T : Any> SpyListener.event(zeroIndex: Int = 0): T {
    return event(T::class, zeroIndex)
}

inline fun <reified T : Any> SpyListener.assertEquals(expectedEvent: T, zeroIndex: Int = 0) {
    Assertions.assertEquals(expectedEvent, event<T>(zeroIndex))
}
