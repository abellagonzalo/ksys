package abellagonzalo.providers

import java.time.LocalDateTime

class TimeProviderImpl : TimeProvider {
    override fun now(): LocalDateTime = LocalDateTime.now()
}
