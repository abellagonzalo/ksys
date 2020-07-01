package hola

import hola.SharedScenario1.Scenario3
import hola.SharedScenario1.Scenario4
import hola.SharedScenario2.Scenario5
import org.junit.jupiter.api.Test

class Scenario1 : Scenario() {
    override val id: String = "scenario_001"

    override val description: String = """
        Scenario1 description.
    """.trimIndent()

    override fun setup() {

    }

    override fun validate() {
    }

    override fun execute() {
        logger.info("Execute $id")
    }
}

class Scenario2 : ParamScenario1<Int>() {
    override val id: String = "scenario_002"

    override val description: String = """
        Scenario2 description.
    """.trimIndent()

    override val parameters: List<List<*>> = listOf(
        listOf(1), listOf(2), listOf(3)
    )

    override fun setup() {

    }

    override fun validate() {

    }

    override fun execute(param0: Int) {

    }
}


class SharedScenario1 : SharedScenario() {
    override val id: String = "shared_scenario_001"

    override val description: String = """
        Shared scenario 1 description.
    """.trimIndent()

    override fun setup() {

    }

    override fun validate() {

    }

    inner class Scenario3 : Scenario() {
        override val id: String = "scenario_003"

        override val description: String = """
            Description for scenario_003.
        """.trimIndent()

        override fun execute() {

        }


        override fun setup() {

        }

        override fun validate() {

        }
    }

    inner class Scenario4 : ParamScenario2<Int, Double>() {
        override val id: String = "scenario_004"

        override val description: String = """
            $id description.
        """.trimIndent()

        override val parameters: List<List<*>> = listOf(
            listOf(1, 1.1), listOf(2, 2.2)
        )

        override fun setup() {

        }

        override fun validate() {

        }


        override fun execute(param0: Int, param1: Double) {

        }

    }
}


open class RepeatingSharedScenario() : SharedScenario() {
    override val id: String = "shared_scenario_002"

    override val description: String = """
        Shared scenario 1 description.
    """.trimIndent()

    override fun setup() {

    }

    override fun validate() {

    }
}

class SharedScenario2 : RepeatingSharedScenario() {
    inner class Scenario5 : Scenario() {
        private val lastDigits = this::class.simpleName!!.takeLastWhile { it.isDigit() }

        override val id: String = "scenario_$lastDigits"

        override val description: String = """
            Description for $id.
        """.trimIndent()

        override fun execute() {

        }


        override fun setup() {

        }

        override fun validate() {

        }
    }
}


class SingleScenarioTests {
    @Test
    fun first() {
        ksys(
            "list", providers = withScenarios(
                Scenario1::class, Scenario2::class, Scenario3::class, Scenario4::class, Scenario5::class
            )
        )
    }
}
