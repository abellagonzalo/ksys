package hola

import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

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
        findAllScenariosClasses = { listOf(Scenario1::class) }
        ksys("list", providers = defaultProviders + mapOf(
            ScenarioClassScanner::class.java to {
                object : ScenarioClassScanner {
                    override fun findAll() = listOf(Scenario1::class)
                }
            }
        ))
    }
}