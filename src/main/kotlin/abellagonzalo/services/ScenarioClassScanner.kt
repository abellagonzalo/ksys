package abellagonzalo.services

import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.KClass

interface ScenarioClassScanner {
    fun findAll(): List<KClass<*>>
}

class DefaultScenarioClassScanner : ScenarioClassScanner {
    override fun findAll(): List<KClass<*>> {
        return emptyList()
    }
}