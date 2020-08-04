package abellagonzalo.fakes

import abellagonzalo.EventBus
import org.junit.jupiter.api.Assertions
import kotlin.reflect.KClass

class SpyListener(val eventBus: EventBus) {
    private val list = mutableListOf<Any>()

    fun <T: Any> subscription(): (T) -> Unit {
        return { list.add(it) }
    }

    fun <T: Any> event(klass: KClass<T>, zeroIndex: Int = 0): T {
        return list.filterIsInstance(klass.java)[zeroIndex]
    }
}

inline fun <reified T : Any> SpyListener.event(zeroIndex: Int = 0): T {
    return event(T::class, zeroIndex)
}

inline fun <reified T : Any> SpyListener.assertEquals(expectedEvent: T, zeroIndex: Int = 0) {
    Assertions.assertEquals(expectedEvent, event<T>(zeroIndex))
}
