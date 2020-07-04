package hola

import hola.scenarios.Param1ScenarioSpy
import hola.scenarios.Param2ScenarioSpy
import hola.scenarios.callsFor
import hola.scenarios.clearCalls
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.Test

class ExecuteParamScenariosTests {

    class Scenario1 : Param1ScenarioSpy<String>() {
        override val parameters: List<List<*>>
            get() = listOf(listOf("one"), listOf("two"))
    }

    class Scenario2 : Param2ScenarioSpy<Int, String>() {
        override val parameters: List<List<*>>
            get() = listOf(listOf(1, "one"), listOf(2, "two"))
    }

    @AfterEach
    fun afterEach() = clearCalls()

    @Test
    fun `Execute single param1 scenario`() {
        ksys("run", "all", providers = withScenarios(Scenario1::class))
        val expected = listOf(
            "setup", "execute.one", "validate", "clean.validate", "clean.execute.one", "clean.setup",
            "setup", "execute.two", "validate", "clean.validate", "clean.execute.two", "clean.setup"
        )
        assertLinesMatch(expected, callsFor(Scenario1::class))
    }

    @Test
    fun `Execute single param2 scenario`() {
        ksys("run", "all", providers = withScenarios(Scenario2::class))
        val expected = listOf(
            "setup", "execute.1.one", "validate", "clean.validate", "clean.execute.1.one", "clean.setup",
            "setup", "execute.2.two", "validate", "clean.validate", "clean.execute.2.two", "clean.setup"
        )
        assertLinesMatch(expected, callsFor(Scenario2::class))
    }
}
