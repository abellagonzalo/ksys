package abellagonzalo.scenarios

import abellagonzalo.teardown.Cleaner

abstract class Scenario {
    abstract val id: String
    abstract val execute: () -> Unit

    fun clean(action: () -> Unit) {
        Cleaner.current.clean(action)
    }
}
