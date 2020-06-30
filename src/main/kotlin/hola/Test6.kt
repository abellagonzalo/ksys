package hola

class Test6 : ParamScenario2<Int, String>() {

    override val id = "test6"

    override val parameters: List<List<*>> = listOf(
        listOf(3, "three"),
        listOf(4, "four")
    )

    override val description: String = """
        This is the descripton for Test6.
    """.trimIndent()

    override fun setup() {
        Logging.current.info("This is Test6 setup.")
    }

    override fun validate() {
    }

    override fun execute(param0: Int, param1: String) {
        Logging.current.info("This is Test6 wit $param0 and $param1")
    }
}