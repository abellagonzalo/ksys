package hola

import hola.services.ScenarioClassScanner
import kotlin.reflect.KClass

fun withScenarios(vararg classes: KClass<*>): Map<Class<*>, () -> Any> {
    return defaultServices + mapOf(
        ScenarioClassScanner::class.java to {
            object : ScenarioClassScanner {
                override fun findAll() = classes.toList()
            }
        }
    )
}