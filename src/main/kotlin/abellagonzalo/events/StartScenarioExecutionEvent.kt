package abellagonzalo.events

import java.time.LocalDateTime

data class StartScenarioExecutionEvent(
    val executorId: String,
    val sharedSetupId: String?,
    val scenarioId: String,
    val time: LocalDateTime
)