package hola.scenarios

abstract class Param1ScenarioSpy<T0> : ParamScenario1<T0>() {

    override val id: String = this::class.simpleName!!.toLowerCase()

    override val description: String = """
        Check the order of the methods called.
    """.trimIndent()

    override fun setup() {
        addCalls("setup")
    }

    override fun execute(param0: T0) {
        addCalls("execute.$param0")
    }

    override fun validate() {
        addCalls("validate")
    }

    private fun addCalls(method: String) {
        addCall(this::class, method)
        clean { addCall(this::class, "clean.$method") }
    }

}

abstract class Param2ScenarioSpy<T0, T1> : ParamScenario2<T0, T1>() {

    override val id: String = this::class.simpleName!!.toLowerCase()

    override val description: String = """
        Check the order of the methods called.
    """.trimIndent()

    override fun setup() {
        addCalls("setup")
    }

    override fun execute(param0: T0, param1: T1) {
        addCalls("execute.$param0.$param1")
    }

    override fun validate() {
        addCalls("validate")
    }

    private fun addCalls(method: String) {
        addCall(this::class, method)
        clean { addCall(this::class, "clean.$method") }
    }
}
