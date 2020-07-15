package abellagonzalo.events

import abellagonzalo.commands.LogLevel
import abellagonzalo.commands.ScenarioPhase
import java.time.LocalDateTime

data class LogMessageEvent(
    val scenarioId: String,
    val phase: ScenarioPhase,
    val time: LocalDateTime,
    val level: LogLevel,
    val message: String
)