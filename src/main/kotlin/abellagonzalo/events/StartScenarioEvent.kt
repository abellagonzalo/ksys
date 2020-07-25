package abellagonzalo.events

import java.time.LocalDateTime

data class StartScenarioEvent(val time: LocalDateTime, val scenarioId: String)
