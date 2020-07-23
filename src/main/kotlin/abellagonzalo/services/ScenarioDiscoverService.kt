package abellagonzalo.services

import kotlin.reflect.KClass

interface ScenarioDiscoverService {
    fun findCandidates(): List<SetupAndScenarios>
}