package abellagonzalo.services

import abellagonzalo.scenarios.ScenarioMarker
import abellagonzalo.scenarios.SharedSetupMarker
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.KClass

interface ClassScanner {
    fun findAll(): Set<KClass<*>>
}

class ReflectionsClassScanner {
    fun findAll(): Set<KClass<*>> {
        val reflections = Reflections(
            SubTypesScanner(true), "abellagonzalo"
        )
        return reflections.getSubTypesOf(SharedSetupMarker::class.java).map { it.kotlin }.toSet() +
                reflections.getSubTypesOf(ScenarioMarker::class.java).map { it.kotlin }.toSet()
    }
}