package abellagonzalo.scenarios

import abellagonzalo.scenarios.CommonBase
import abellagonzalo.scenarios.ScenarioMarker

abstract class SingleScenario : CommonBase(),
    ScenarioMarker {
    abstract val setup: () -> Unit
    abstract val execute: () -> Unit
    abstract val validate: () -> Unit
}