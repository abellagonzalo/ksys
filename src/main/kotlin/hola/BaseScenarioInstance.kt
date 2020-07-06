package hola

import hola.scenarios.BaseScenarioSetup
import hola.scenarios.SharedSetup

abstract class BaseScenarioInstance<T : BaseScenarioSetup>(
    val scenario: T,
    val sharedSetup: SharedSetup?
) : ScenarioInstance {

    override val sharedId: String
        get() = sharedSetup?.id ?: id

    override val scenarioDescription: String = scenario.description

    override val sharedScenarioDescription: String? = sharedSetup?.description

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
        if (sharedSetup == null) return
        Logging.registerLogger(
            sharedSetup::class.java.name, sharedSetup::setup.name, null, sharedSetup.id
        ).use {
            exceptionSafe(sharedSetup::setup)
        }
    }

    override fun sharedValidate() {
        if (sharedSetup == null) return
        Logging.registerLogger(
            sharedSetup::class.java.name, sharedSetup::validate.name, null, sharedSetup.id
        ).use {
            exceptionSafe(sharedSetup::validate)
        }
    }

    override fun sharedTeardown() {
        if (sharedSetup == null) return
        Logging.registerLogger(
            sharedSetup::class.java.name, sharedSetup::teardown.name, null, sharedSetup.id
        ).use {
            sharedSetup.teardown()
        }
    }

    override fun setup() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::setup.name, id, sharedSetup?.id
        ).use {
            exceptionSafe(scenario::setup)
        }
    }

    override fun validate() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::validate.name, id, sharedSetup?.id
        ).use {
            exceptionSafe(scenario::validate)
        }
    }

    override fun teardown() {
        Logging.registerLogger(
            scenario::class.java.name, scenario::teardown.name, id, sharedSetup?.id
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