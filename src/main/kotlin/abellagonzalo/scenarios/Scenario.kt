package abellagonzalo.scenarios

abstract class Scenario {
    abstract val id: String
    abstract val execute: () -> Unit
}