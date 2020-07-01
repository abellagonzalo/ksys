package hola.services

import hola.scenarios.ParamScenario
import hola.scenarios.Scenario
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.KClass

interface ScenarioClassScanner {
    fun findAll(): List<KClass<*>>
}

class DefaultScenarioClassScanner : ScenarioClassScanner {
    override fun findAll(): List<KClass<*>> {
        val reflections = Reflections(
            SubTypesScanner(true), "hola"
        )
        val set =
            reflections.getSubTypesOf(Scenario::class.java) +
                    reflections.getSubTypesOf(ParamScenario::class.java)
        return set.map { it.kotlin }
    }
}