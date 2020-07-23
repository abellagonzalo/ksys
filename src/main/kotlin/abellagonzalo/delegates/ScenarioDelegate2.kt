package abellagonzalo.delegates

interface Details {
    val id: String
    val description: String
    val parameters: List<*>
}

interface ScenarioDelegate2 : Details {
    fun setup()
    fun validate()
    fun execute()
    fun teardown()
}