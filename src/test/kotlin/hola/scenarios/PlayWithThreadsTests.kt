package hola.scenarios

import hola.RunningScenarioLocator
import hola.ksys
import hola.withScenarios
import org.junit.jupiter.api.Test



class PlayWithThreadsTests {

    class Scenario1 : Scenario() {
        override val id: String = this::class.simpleName!!.toLowerCase()

        override val description: String = """
            Check the order of the methods called.
        """.trimIndent()

        override fun setup() {
            println("setup")
            RunningScenarioLocator.current.clean {
                println("Cleaning setup through locator")
            }
        }

        override fun execute() {
            println("execute")
        }

        override fun validate() {
            println("validate")
        }
    }

    @Test
    fun first() {
        ksys("run", "all", providers = withScenarios(Scenario1::class))
    }
}