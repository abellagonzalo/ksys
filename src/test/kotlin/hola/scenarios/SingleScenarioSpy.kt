package hola.scenarios

open class SingleScenarioSpy : Scenario() {

    override val id: String = this::class.simpleName!!.toLowerCase()

    override val description: String = """
        Check the order of the methods called.
    """.trimIndent()

    override fun setup() {
        addCalls("setup")
    }

    override fun execute() {
        addCalls("execute")
    }

    override fun validate() {
        addCalls("validate")
    }

    private fun addCalls(method: String) {
        addCall(this::class, method)
        clean { addCall(this::class, "clean.$method") }
    }
}