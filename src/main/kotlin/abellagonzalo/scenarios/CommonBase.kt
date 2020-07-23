package abellagonzalo.scenarios

import abellagonzalo.logging.ScenarioCleaner
import abellagonzalo.logging.ScenarioLogger

abstract class CommonBase {
    abstract val id: String
    abstract val description: String

    val logger: ScenarioLogger
        get() = ScenarioLogger.current

    fun clean(action: () -> Unit) {
        ScenarioCleaner.current.always(action)
    }
}