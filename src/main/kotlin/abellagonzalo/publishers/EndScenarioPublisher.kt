package abellagonzalo.publishers

import abellagonzalo.scenarios.Outcome

interface EndScenarioPublisher {
    fun publishEnd(outcome: Outcome)
}