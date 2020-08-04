package abellagonzalo.teardown

import abellagonzalo.EventBus
import abellagonzalo.events.PushCleanEvent
import abellagonzalo.publish
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CleanerManagerTests {

    private val eventBus: EventBus = EventBus.createSingleton()
    private var cleanerManager: CleanerManager = CleanerManagerImpl(eventBus)

    @Test
    fun `Clean events are executed in LIFO`() {
        val list = mutableListOf<Int>()

        val a = cleanerManager.createNew()

        eventBus.publish(PushCleanEvent { list.add(1) })
        eventBus.publish(PushCleanEvent { list.add(2) })

        a.emptyStack()

        Assertions.assertEquals(listOf(2, 1), list)
    }
}
