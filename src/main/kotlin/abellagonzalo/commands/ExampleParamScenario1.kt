package abellagonzalo.commands

import abellagonzalo.scenarios.ParamScenario1
import abellagonzalo.scenarios.Params1


class ExampleParamScenario1 : ParamScenario1<Int>() {
    override val id = "param-scenario1-01"

    override val description = "Description for $id"

    override val parameters: List<Params1<Int>> = listOf(
        Params1(1),
        Params1(2),
        Params1(3)
    )

    override val setup = fun(myInt: Int) {
        println("setup of $id with $myInt")
    }

    override val execute = fun(myInt: Int) {
        println("execute of $id with $myInt")
    }

    override val validate = fun(myInt: Int) {
        println("vaidate of $id with $myInt")
    }
}