package abellagonzalo.events

import java.time.LocalDateTime

data class EndScenarioEvent(val time: LocalDateTime, val scenarioId: String)
