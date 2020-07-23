package abellagonzalo.commands

import abellagonzalo.commands.ResultCode.*
import abellagonzalo.commands.ScenarioPhase.SETUP
import abellagonzalo.commands.ScenarioPhase.VALIDATE
import abellagonzalo.events.EventBus
import abellagonzalo.logging.ScenarioCleaner
import abellagonzalo.scenarios.ParamScenario1
import abellagonzalo.scenarios.SingleScenario
import abellagonzalo.scenarios.SingleSharedSetup
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.full.primaryConstructor

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

class SingleSharedSetupDelegate(private val sharedSetup: SingleSharedSetup) : SharedSetupDelegate {
    override val id: String = sharedSetup.id

    override val description: String = sharedSetup.description

    override val parameters: List<*> = emptyList<Unit>()

    override val result: ResultCode
        get() = TODO("Not yet implemented")
    override val summary: Map<String, ResultCode>
        get() = TODO("Not yet implemented")
    override val exception: Exception?
        get() = TODO("Not yet implemented")

    override fun setup() {
        executePhase(SETUP, sharedSetup.setup)
    }

    override fun validate() {
        executePhase(VALIDATE, sharedSetup.validate)
    }

    override fun teardown() {
        ScenarioCleaner.current.clear()
    }

    private fun executePhase(phase: ScenarioPhase, fn: () -> Unit) {
        val startTime = publishStartPhase(phase)
        try {
            fn()
            publishFinishPhase(startTime, phase, PASSED, null)
        } catch (ex: Exception) {
            publishFinishPhase(startTime, phase, FAILED, ex)
        }
    }

    private fun publishStartPhase(phase: ScenarioPhase): LocalDateTime {
        val startTime = LocalDateTime.now()
        EventBus.instance.publish(StartPhaseAnEvent(startTime, id, phase))
        return startTime
    }

    private fun publishFinishPhase(
        startTime: LocalDateTime,
        phase: ScenarioPhase,
        result: ResultCode,
        exception: Exception?
    ) {
        val endTime = LocalDateTime.now()
        EventBus.instance.publish(
            FinishedPhaseAnEvent(endTime, id, phase, Duration.between(startTime, endTime), result, exception)
        )
    }
}

interface AnEvent {
    val time: LocalDateTime
}

data class StartPhaseAnEvent(
    override val time: LocalDateTime,
    val id: String,
    val phase: ScenarioPhase
) : AnEvent

data class FinishedPhaseAnEvent(
    override val time: LocalDateTime,
    val id: String,
    val phase: ScenarioPhase,
    val duration: Duration,
    val result: ResultCode,
    val exception: Exception?
) : AnEvent

interface ScenarioDelegate : SharedSetupDelegate {
    fun execute()
}


data class StartPhaseEvent(
    val executionId: String,
    val sharedSetupId: String,
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
//            EventBus.instance.publish(
//                StartPhaseEvent(id, phase, startTime)
//            )

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

    override val parameters: List<*> =
        with(scenario.parameters[index - 1]) {
            listOf(param0)
        }

    override fun setup() {
        executePhase(SETUP) {
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
        executePhase(SETUP, scenario.setup)
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
