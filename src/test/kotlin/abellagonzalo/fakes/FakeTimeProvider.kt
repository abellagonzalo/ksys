package abellagonzalo.fakes

import abellagonzalo.providers.TimeProvider
import java.time.Duration
import java.time.LocalDateTime

class FakeTimeProvider(private val times: List<LocalDateTime>) : TimeProvider {

    constructor(vararg shifts: Duration) : this(LocalDateTime.now(), *shifts)

    constructor(initial: LocalDateTime, vararg shifts: Duration)
            : this(listOf(initial) + shifts.map { initial + it })

    private val iterator = times.iterator()

    override fun now(): LocalDateTime {
        return iterator.next()
    }

    operator fun get(index: Int): LocalDateTime {
        return times[index]
    }
}