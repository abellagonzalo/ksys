package abellagonzalo.scenarios

import abellagonzalo.commands.*
import abellagonzalo.events.EventBus
import abellagonzalo.events.LogMessageEvent
import abellagonzalo.events.ThreadCreatedEvent
import jdk.jfr.Event
import org.junit.jupiter.api.Test
import kotlin.reflect.full.primaryConstructor

class TheMostSimpleLogger {
    fun a(event: LogMessageEvent) {
        println("${event.time} [${event.level}] ${event.scenarioId}::${event.phase} ${event.message}")
    }

    fun b(event: StartScenarioExecutionEvent) {
        println("The path for the log file is ${event.sharedSetupId ?: "standalone"}/${event.scenarioId}.")
    }
}


class Draft {

    @Test
    fun first() {
        EventBus.instance.subscribe(ThreadCreatedEvent::class, ScenarioLogger.Companion::a)
        EventBus.instance.subscribe(StartScenarioPhaseEvent::class, ScenarioLogger.Companion::b)

        val logger = TheMostSimpleLogger()
        EventBus.instance.subscribe(LogMessageEvent::class, logger::a)
        EventBus.instance.subscribe(StartScenarioExecutionEvent::class, logger::b)

        val klass = ExampleSingleScenario::class
        val scenario = klass.primaryConstructor!!.call()
        val delegate = SingleScenarioDelegate(scenario)
        val unitsOfExecution = listOf(
            UnitOfExecution(null, listOf(delegate))
        )

        var counter = 1

        for (unitOfExecution in unitsOfExecution) {
            val thg = ThreadGroup("ksys-thread-group-$counter")
            val th = object : Thread(thg, "ksys-thread-$counter") {
                override fun run() {
                    unitOfExecution.execute(thg.name)
                }
            }
            th.start()
            th.join()
        }
    }
}
