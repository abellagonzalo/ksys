package hola

import hola.ExecuteSingleScenarioTests.SharedSetup1.Scenario2
import hola.scenarios.SharedSetupSpy
import hola.scenarios.SingleScenarioSpy
import hola.scenarios.callsFor
import hola.scenarios.clearCalls
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ExecuteSingleScenarioTests {

    class Scenario1 : SingleScenarioSpy()

    class SharedSetup1 : SharedSetupSpy() {
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
        assertLinesMatch(expected, callsFor(SharedSetup1::class, Scenario2::class))
    }
}
