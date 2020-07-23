package abellagonzalo.scenarios

import abellagonzalo.scenarios.CommonBase
import abellagonzalo.scenarios.SharedSetupMarker

abstract class SingleSharedSetup : CommonBase(),
    SharedSetupMarker {
    abstract val setup: () -> Unit
    abstract val validate: () -> Unit
}