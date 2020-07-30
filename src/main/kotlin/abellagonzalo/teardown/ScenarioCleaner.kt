package abellagonzalo.teardown

import abellagonzalo.scenarios.Outcome

interface ScenarioCleaner {
    fun emptyStack() : Outcome
}