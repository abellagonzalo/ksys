package abellagonzalo.services

import java.time.LocalDateTime

interface TimeService {
    val now: LocalDateTime
}

class TimeServiceImpl : TimeService {
    override val now: LocalDateTime
        get() = LocalDateTime.now()
}