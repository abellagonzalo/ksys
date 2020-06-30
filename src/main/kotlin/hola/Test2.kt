package hola

class Test2SharedScenario : SharedScenario() {

    override val id = "shared-scenario-01"

    override val description = """
        This is the description for the shared scenario.
    """.trimIndent()

    override fun setup() {
        logger.info("This is the setup of the shared-scenario-01")
    }

    override fun validate() {
    }

    inner class Test2 : Scenario() {
        override val id = "test2"

        override val description: String = """
            This is the description of the test2.
        """.trimIndent()

        override fun setup() {

        }

        override fun validate() {

        }

        override fun execute() {
            logger.info("This is the execute of the test2")
        }
    }
}