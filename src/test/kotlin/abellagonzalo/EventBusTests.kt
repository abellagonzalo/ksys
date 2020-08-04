package abellagonzalo

import org.junit.jupiter.api.Test

class EventBusTests {

    private val ksysFactory = KsysFactory().apply {
        bind<EventBus>().to<EventBusImpl>()
    }

    private val eventBus = ksysFactory.create<EventBus>()

    @Test
    fun `I can publish an event`() {
        eventBus.publish("My event")
    }

    @Test
    fun `I receive an event when I subscribe`() {
        lateinit var theString: String
        eventBus.subscribe(String::class) { str: String -> theString = str }
        eventBus.publish("My string")
        assertEquals("My string", theString)
    }

    @Test
    fun `When I unsubscribe I stop receiving events`() {
        lateinit var theString: String
        val subscription = eventBus.subscribe { str: String -> theString = str }

        eventBus.publish("str1")
        subscription.close()
        eventBus.publish("str2")

        assertEquals("str1", theString)
    }

    @Test
    fun `When I subscribe with a base class I receive events of subclasses as well`() {
        abstract class Base
        class A : Base()
        class B : Base()

        val expectedEventA = A()
        val expectedEventB = B()

        val actualEvents = mutableListOf<Base>()
        eventBus.subscribe { base: Base -> actualEvents.add(base) }

        eventBus.publish(expectedEventA)
        eventBus.publish(expectedEventB)
        assertEquals(listOf(expectedEventA, expectedEventB), actualEvents)
   }
}
