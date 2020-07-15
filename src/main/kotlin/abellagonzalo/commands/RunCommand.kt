package abellagonzalo.commands

import abellagonzalo.commands.ResultCode.*
import abellagonzalo.events.EventBus
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.full.primaryConstructor

interface ScenarioMarker

abstract class CommonBase {
    abstract val id: String
    abstract val description: String

    val logger: ScenarioLogger
        get() = ScenarioLogger.current

    fun clean(action: () -> Unit) {
        // Should add action on top of the current stack
        println("Calling clean")
    }
}

abstract class SingleScenario : CommonBase(), ScenarioMarker {
    abstract val setup: () -> Unit
    abstract val execute: () -> Unit
    abstract val validate: () -> Unit
}

data class Params1<T0>(val param0: T0)

abstract class ParamScenario1<T0> : CommonBase(), ScenarioMarker {
    abstract val parameters: List<Params1<T0>>

    abstract val setup: (T0) -> Unit
    abstract val execute: (T0) -> Unit
    abstract val validate: (T0) -> Unit
}

class SkippedException(message: String) : Exception(message)

interface SharedSetupDelegate {
    val id: String
    val description: String
    val parameters: List<*>

    // Maybe this is not necessary, results can be published
    // through the event bus and gathered somewhere else
    val result: ResultCode
    val summary: Map<String, ResultCode>
    val exception: Exception?

    fun setup()
    fun validate()
    fun teardown()
}

interface ScenarioDelegate : SharedSetupDelegate {
    fun execute()
}


data class StartScenarioPhaseEvent(
    val scenarioId: String,
    val phase: ScenarioPhase,
    val time: LocalDateTime
)

data class EndScenarioPhaseEvent(
    val scenarioId: String,
    val phase: ScenarioPhase,
    val time: LocalDateTime,
    val executionTime: Duration,
    val result: ResultCode,
    val exception: Exception?
)

abstract class BaseScenarioDelegate : ScenarioDelegate {

    override val result: ResultCode
        get() = if (summary.values.contains(FAILED)) FAILED else PASSED

    override val summary: Map<String, ResultCode> = emptyMap()

    override var exception: Exception? = null
        protected set

    override fun teardown() {
        // Emtpy the stack of the cleaner
    }

    protected fun executePhase(phase: ScenarioPhase, fn: () -> Unit) {
        // if previous phase is failed, say on data bus
        //   Finished $scenario.id, phase: SETUP, resultCode: SKIPPED, reason: Previous phase failed, timeOfExexution: ZERO

        val startTime = LocalDateTime.now()

        try {
            EventBus.instance.publish(
                StartScenarioPhaseEvent(id, phase, startTime)
            )

            fn()

            val endTime = LocalDateTime.now()
            EventBus.instance.publish(
                EndScenarioPhaseEvent(id, phase, endTime, Duration.between(startTime, endTime), PASSED, null)
            )

        } catch (skipped: SkippedException) {
            val endTime = LocalDateTime.now()
            EventBus.instance.publish(
                EndScenarioPhaseEvent(id, phase, endTime, Duration.between(startTime, endTime), SKIPPED, skipped)
            )

        } catch (ex: Exception) {
            exception = ex
            val endTime = LocalDateTime.now()
            EventBus.instance.publish(
                EndScenarioPhaseEvent(id, phase, endTime, Duration.between(startTime, endTime), SKIPPED, ex)
            )
        }
    }
}

class ParamScenario1Delegate<T0>(
    private val scenario: ParamScenario1<T0>,
    private val index: Int
) : BaseScenarioDelegate() {

    override val id: String = scenario.id + ":$index"

    override val description: String = scenario.description

    override val parameters: List<*> = with(scenario.parameters[index - 1]) { listOf(param0) }

    override fun setup() {
        executePhase(ScenarioPhase.SETUP) {
            scenario.setup(scenario.parameters[index - 1].param0)
        }
    }

    override fun execute() {
        executePhase(ScenarioPhase.EXECUTE) {
            scenario.execute(scenario.parameters[index - 1].param0)
        }
    }

    override fun validate() {
        executePhase(ScenarioPhase.VALIDATE) {
            scenario.validate(scenario.parameters[index - 1].param0)
        }
    }
}

class SingleScenarioDelegate(private val scenario: SingleScenario) : BaseScenarioDelegate() {

    override val id: String = scenario.id

    override val description: String = scenario.description

    override val parameters: List<*> = emptyList<Unit>()

    override fun setup() {
        executePhase(ScenarioPhase.SETUP, scenario.setup)
    }

    override fun execute() {
        executePhase(ScenarioPhase.EXECUTE, scenario.execute)
    }

    override fun validate() {
        executePhase(ScenarioPhase.VALIDATE, scenario.validate)
    }
}

@Command(name = "run")
class RunCommand : BaseCommand() {

    @Parameters(index = "0", defaultValue = "all")
    var filter: String = ""

    override fun call(): Int {
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

        return 0
    }
}
