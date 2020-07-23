package abellagonzalo.services

import abellagonzalo.scenarios.ScenarioMarker
import abellagonzalo.scenarios.SharedSetupMarker
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

data class SetupAndScenarios(val outer: KClass<*>, val inners: Set<KClass<*>>)

class NoSharedSetup

class ReflectionsScenarioDiscoverService(scanner: ClassScanner) : ScenarioDiscoverService {

    private val all by lazy { scanner.findAll() }

    override fun findCandidates(): List<SetupAndScenarios> {

        // Get all scenarios
        val scenarios = all
            .filter { it.isSubclassOf(ScenarioMarker::class) }
            .filter { it.isFinal }
            .toMutableList()

        val sharedSetup = all
            .filter { it.isSubclassOf(SharedSetupMarker::class) }
            .filter { it.isFinal }
            .toMutableSet()

        val list = mutableListOf<SetupAndScenarios>()

        while (scenarios.isNotEmpty()) {
            val candidate = scenarios.removeAt(0)

            if (!candidate.isInner) {
                list += SetupAndScenarios(NoSharedSetup::class, setOf(candidate))
                continue
            }

            // TODO - Add shared setups
        }


        return list
    }
}