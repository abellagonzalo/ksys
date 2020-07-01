package hola

import hola.scenarios.BaseScenario
import hola.scenarios.ParamScenario
import hola.scenarios.Scenario
import hola.scenarios.SharedScenario

enum class TestOutcome {
    NOT_VERIFIED, PASSED, FAILED, SKIPPED
}

data class TestResult(val outcome: TestOutcome)

interface ScenarioInstance {
    val id: String
    val sharedId: String
    val scenarioDescription: String
    val sharedScenarioDescription: String?
    val params: List<*>

    fun sharedSetup()
    fun sharedValidate()
    fun sharedTeardown()

    fun setup()
    fun execute()
    fun validate()
    fun teardown()

    val exception: Exception?
    val status: TestOutcome
}

abstract class BaseScenarioInstance<T : BaseScenario>(
    protected val scenario: T,
    protected val sharedScenario: SharedScenario?
) : ScenarioInstance {

    override val sharedId: String
        get() = sharedScenario?.id ?: id

    override val scenarioDescription: String = scenario.description

    override val sharedScenarioDescription: String? = sharedScenario?.description

    override var exception: Exception? = null
        protected set

    override val status: TestOutcome
        get() = if (exception == null) TestOutcome.PASSED else TestOutcome.FAILED

    override fun sharedSetup() {
        if (sharedScenario == null) return
        Logging.registerLogger(
            sharedScenario::class.java.name, sharedScenario::setup.name, null, sharedScenario.id
        ).use {
            exceptionSafe(sharedScenario::setup)
        }
    }

    override fun sharedValidate() {
        if (sharedScenario == null) return
        Logging.registerLogger(
            sharedScenario::class.java.name, sharedScenario::validate.name, null, sharedScenario.id
        ).use {
            exceptionSafe(sharedScenario::validate)
        }
    }

    override fun sharedTeardown() {
        if (sharedScenario == null) return
        Logging.registerLogger(
            sharedScenario::class.java.name, sharedScenario::teardown.name, null, sharedScenario.id
        ).use {
            sharedScenario.teardown()
        }
    }

    override fun setup() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::setup.name, id, sharedScenario?.id
        ).use {
            exceptionSafe(scenario::setup)
        }
    }

    override fun validate() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::validate.name, id, sharedScenario?.id
        ).use {
            exceptionSafe(scenario::validate)
        }
    }

    override fun teardown() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::teardown.name, id, sharedScenario?.id
        ).use {
            scenario.teardown()
        }
    }

    protected fun exceptionSafe(action: () -> Unit) {
        if (exception != null) return
        try {
            action()
        } catch (ex: Exception) {
            exception = ex
        }
    }
}

class SingleScenarioInstance(scenario: Scenario, sharedScenario: SharedScenario?) :
    BaseScenarioInstance<Scenario>(scenario, sharedScenario) {

    override val id = scenario.id

    override val params = emptyList<Any>()

    override fun execute() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::execute.name, id, sharedScenario?.id
        ).use {
            exceptionSafe(scenario::execute)
        }
    }
}

class ParamScenarioInstance(private val index: Int, scenario: ParamScenario, sharedScenario: SharedScenario?) :
    BaseScenarioInstance<ParamScenario>(scenario, sharedScenario) {

    override val id = scenario.id + "_" + index.toString().padStart(4, '0')

    override val params = scenario.parameters[index]

    override fun execute() {
        Logging.registerLogger(
            scenario::class.java.name, "execute", id, sharedScenario?.id
        ).use {
            exceptionSafe { scenario.executeWithParam(index) }
        }
    }
}

class ScenarioRunner {
    fun run(instances: List<ScenarioInstance>) {
        instances.forEachIndexed { index, scenarioInstance ->
            if (index == 0) {
                scenarioInstance.sharedSetup()
                scenarioInstance.sharedValidate()
            }

            scenarioInstance.setup()
            scenarioInstance.execute()
            scenarioInstance.validate()
            scenarioInstance.teardown()

            if (index == instances.size - 1)
                scenarioInstance.sharedTeardown()

            println("Scenario ${scenarioInstance.id}: ${scenarioInstance.status}")
            if (scenarioInstance.status == TestOutcome.FAILED)
                scenarioInstance.exception!!.printStackTrace(System.out)
        }
    }

    fun setupOnly(instances: List<ScenarioInstance>) {
        if (instances.size > 1) throw Exception("Only one test can run when using --setup-only")
        val scenarioInstance = instances.first()
        scenarioInstance.sharedSetup()
        scenarioInstance.sharedValidate()
        scenarioInstance.setup()
    }
}