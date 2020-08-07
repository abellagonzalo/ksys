package abellagonzalo.scenarios

import abellagonzalo.Executable
import abellagonzalo.teardown.Cleaner

data class Tag(val value: String) {
    override fun equals(other: Any?): Boolean = when (other) {
        is String -> value.trim('#') == other.trim('#')
        is Tag -> equals(other.value)
        else -> false
    }
}

interface Details {
    val id: String
    val description: String
    val tags: List<Tag>
    val execute: () -> Unit
}

class MyScenario : TripleAScenario() {
    override val id = "my-scenario-{index}"

    override val description = """
        This is the description of my scenario. I can say whatever I want.
        The parameters for this scenario are {0}, {1} and {2}.
        
        #group1 #regression #octane-546
    """.trimIndent()

    val parameters = listOf(
        listOf("ENT 4.4", "hola", "adios"),
        listOf("ENT 4.5", "hola", "adios"),
        listOf("ENT 4.6", "hola", "adios")
    )

    inner class Phases : AAAPhases() {
        override val setup: () -> Unit
            get() = TODO("Not yet implemented")
        override val execute: () -> Unit
            get() = TODO("Not yet implemented")
        override val validate: () -> Unit
            get() = TODO("Not yet implemented")
    }
}

abstract class TripleAScenario {
    abstract val id: String
    abstract val description: String
}

abstract class AAAPhases {
    abstract val setup: () -> Unit
    abstract val execute: () -> Unit
    abstract val validate: () -> Unit
}

abstract class Scenario : Executable {

    fun clean(action: () -> Unit) {
        Cleaner.current.clean(action)
    }
}
