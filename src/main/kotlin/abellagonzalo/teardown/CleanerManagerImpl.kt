package abellagonzalo.teardown

import abellagonzalo.EventBus
import abellagonzalo.events.PushCleanEvent
import abellagonzalo.scenarios.Outcome
import abellagonzalo.subscribe
import abellagonzalo.unsubscribe
import java.util.*

class CleanerManagerImpl(private val eventBus: EventBus) :
    CleanerManager {
    override fun createNew(): ScenarioCleaner {
        return ScenarioCleanerImpl()
    }

    inner class ScenarioCleanerImpl : ScenarioCleaner {
        private val stack = Stack<() -> Unit>()

        init {
            eventBus.subscribe(this::handlePushCleanEvent)
        }

        private fun handlePushCleanEvent(event: PushCleanEvent) {
            stack.push(event.action)
        }

        override fun emptyStack(): Outcome {
            while (stack.isNotEmpty())
                stack.pop().invoke()
            eventBus.unsubscribe(this::handlePushCleanEvent)
            return Outcome.PASSED
        }
    }
}