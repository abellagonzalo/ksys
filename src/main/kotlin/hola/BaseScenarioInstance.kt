package hola

import hola.scenarios.BaseScenario
import hola.scenarios.SharedScenario

abstract class BaseScenarioInstance<T : BaseScenario>(
    val scenario: T,
    val sharedScenario: SharedScenario?
) : ScenarioInstance {

    override val sharedId: String
        get() = sharedScenario?.id ?: id

    override val scenarioDescription: String = scenario.description

    override val sharedScenarioDescription: String? = sharedScenario?.description

    override var exception: Exception? = null
        protected set

    override val status: TestOutcome
        get() = if (exception == null) TestOutcome.PASSED else TestOutcome.FAILED

    override val logger: SimpleLogger
        get() = scenario.logger

    override fun clean(fn: () -> Unit) {
        scenario.clean(fn)
    }

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