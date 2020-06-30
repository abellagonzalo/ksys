package hola

class Test1 : Scenario() {

    override val id = "test1"

    override val description: String = """
        This is the name of the test
        
        Here I can write a description of what the test is doing using markdown.
        This description is shown when displaying details of the test.
        
        #tag1 #tag2
    """.trimIndent()

    override fun setup() {
        logger.info("This is my setup")
    }

    override fun execute() {
        logger.info("This is my test")
    }

    override fun validate() {
        logger.info("This is the validation")
    }
}


//class SharingScenario : SharedScenario() {
//    override val description: String = """
//        My description for the sharing scenario.
//    """.trimIndent()
//
//    override fun setup() {
//        info("This is the sharing scenario setup.")
//    }
//
//    inner class Scene1 : Scenario() {
//        override val description: String = """
//            This is the description for scene 1.
//        """.trimIndent()
//
//        override fun setup() {
//            info("This is the setup for the scene1.")
//        }
//
//        override fun test() {
//            info("This is the test for the scene1.")
//        }
//
//    }
//}