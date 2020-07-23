package abellagonzalo.events

import abellagonzalo.commands.ResultCode
import java.time.Duration
import java.time.LocalDateTime

data class EndScenarioExecutionEvent(
    val sharedSetupId: String?,
    val scenarioId: String,
    val time: LocalDateTime,
    val executionTime: Duration,
    val resultCode: ResultCode
)