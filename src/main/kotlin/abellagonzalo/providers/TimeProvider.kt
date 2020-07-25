package abellagonzalo.providers

import java.time.LocalDateTime

interface TimeProvider {
    fun now(): LocalDateTime
}
