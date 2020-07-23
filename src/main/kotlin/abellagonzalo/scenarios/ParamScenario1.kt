package abellagonzalo.scenarios

abstract class ParamScenario1<T0> : CommonBase(),
    ScenarioMarker {
    abstract val parameters: List<Params1<T0>>

    abstract val setup: (T0) -> Unit
    abstract val execute: (T0) -> Unit
    abstract val validate: (T0) -> Unit
}