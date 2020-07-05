package hola

interface ScenarioInstance {
    val id: String
    val sharedId: String
    val scenarioDescription: String
    val sharedScenarioDescription: String?
    val params: List<*>

    val logger: SimpleLogger
    fun clean(fn: () -> Unit)

    fun sharedSetup()
    fun sharedValidate()
    fun sharedTeardown()

    fun setup()
    fun execute()
    fun validate()
    fun teardown()

    val exception: Exception?
    val status: TestOutcome
}