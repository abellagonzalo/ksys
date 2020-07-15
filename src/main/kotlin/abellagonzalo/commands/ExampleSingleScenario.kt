package abellagonzalo.commands


class ExampleSingleScenario : SingleScenario() {
    override val id: String
        get() = "my-unique-id"

    override val description: String
        get() = "This is a description of my scenario"

    override val setup = {
        logger.info("I am execute the setup of $id")
        ScenarioLogger.current.info("Calling the logger from anywhere.")
    }

    override val execute = {
        logger.info("I am execute the execute of $id")
    }

    override val validate = {
        logger.info("I am execute the validate of $id")
    }
}