package hola

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

class SingleScenarioTests {
    @Test
    fun first() {
        ksys("list", providers = defaultServices + mapOf(
            ScenarioClassScanner::class.java to {
                object : ScenarioClassScanner {
                    override fun findAll() = listOf(Scenario1::class)
                }
            }
        ))
    }
}