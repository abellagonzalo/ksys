package abellagonzalo.events

import abellagonzalo.logging.LogLevel
import java.time.LocalDateTime

data class LogMessageEvent(
    val executorId: String,
    val time: LocalDateTime,
    val level: LogLevel,
    val message: String
)