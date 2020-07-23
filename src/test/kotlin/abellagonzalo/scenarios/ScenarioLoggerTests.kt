package abellagonzalo.scenarios

import abellagonzalo.logging.LogLevel.INFO
import abellagonzalo.logging.ScenarioLogger
import abellagonzalo.events.EventBus
import abellagonzalo.events.LogMessageEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ScenarioLoggerTests {

    lateinit var event: LogMessageEvent

    @BeforeEach
    fun beforeEach() {
        EventBus.instance.clear()
        EventBus.instance.subscribe(LogMessageEvent::class) { event = it }
    }

    @Test
    fun `Publish a log message`() {
        lateinit var before: LocalDateTime
        lateinit var after: LocalDateTime

        val th = object : Thread(ThreadGroup("executorId"), "thread-name") {
            override fun run() {
                before = LocalDateTime.now()
                ScenarioLogger.current.info("My test message")
                after = LocalDateTime.now()
            }
        }
        th.start()
        th.join()

        assertEquals("executorId", event.executorId)
        assertEquals(INFO, event.level)
        assertEquals("My test message", event.message)
        assertTrue(before < event.time)
        assertTrue(after > event.time)
    }
}