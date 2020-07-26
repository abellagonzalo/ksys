package abellagonzalo.events

import abellagonzalo.scenarios.Outcome
import java.time.LocalDateTime

data class EndScenarioEvent(val time: LocalDateTime, val scenarioId: String, val outcome: Outcome)
