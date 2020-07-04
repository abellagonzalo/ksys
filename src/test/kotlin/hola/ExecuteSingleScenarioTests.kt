package hola

import hola.ExecuteSingleScenarioTests.SharedScenario1.Scenario2
import hola.scenarios.SharedScenarioSpy
import hola.scenarios.SingleScenarioSpy
import hola.scenarios.callsFor
import hola.scenarios.clearCalls
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Test


class ExecuteSingleScenarioTests {

    class Scenario1 : SingleScenarioSpy()

    class SharedScenario1 : SharedScenarioSpy() {
        inner class Scenario2 : SingleScenarioSpy()
    }

    @AfterEach
    fun afterEach() = clearCalls()

    @Test
    fun `Execute a single scenario`() {
        ksys("run", "all", providers = withScenarios(Scenario1::class))
        val expected = listOf(
            "setup", "execute", "validate", "clean.validate", "clean.execute", "clean.setup")
        assertLinesMatch(expected, callsFor(Scenario1::class))
    }

    @Test
    fun `Execute single scenario within a shared scenario`() {
        ksys("run", "all", providers = withScenarios(Scenario2::class))
        val expected = listOf(
            "shared.setup", "shared.validate", "clean.shared.validate", "clean.shared.setup",
            "setup", "execute", "validate", "clean.validate", "clean.execute", "clean.setup")
        assertLinesMatch(expected, callsFor(SharedScenario1::class, Scenario2::class))
    }
}
