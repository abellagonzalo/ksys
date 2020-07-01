package hola.services

import hola.*
import hola.scenarios.ParamScenario
import hola.scenarios.Scenario
import hola.scenarios.SharedScenario
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

class ScenarioInstantiator(private val scanner: ScenarioClassScanner) {
    fun instantiateAll(): Map<String, List<ScenarioInstance>> {
        val all = scanner.findAll()
        return groupScenarioInstances(all)
    }

    private fun groupScenarioInstances(all: List<KClass<*>>): Map<String, List<ScenarioInstance>> {
        val singleScenarioInstances = all
            .filter { it.isFinal && it.isSubclassOf(Scenario::class) }
            .filterIsInstance<KClass<Scenario>>()
            .map { newSingleScenarioInstance(it) }

        val paramScenarioInstances = all
            .filter { it.isFinal && it.isSubclassOf(ParamScenario::class) }
            .filterIsInstance<KClass<ParamScenario>>()
            .map { newParamScenarioInstance(it) }
            .flatten()

        return (singleScenarioInstances + paramScenarioInstances)
            .sortedBy { it.id }
            .groupBy { it.sharedId }
    }

    private fun <T : Scenario> newSingleScenarioInstance(scenarioClass: KClass<T>): SingleScenarioInstance {
        val pair = scenarioClass.createPair()
        return SingleScenarioInstance(pair.first, pair.second)
    }

    private fun <T : ParamScenario> newParamScenarioInstance(scenarioClass: KClass<T>): List<ParamScenarioInstance> {
        val pair = scenarioClass.createPair()
        return pair.first.parameters.indices.map {
            ParamScenarioInstance(it, pair.first, pair.second)
        }
    }

    private fun <T : Any> KClass<T>.createPair(): Pair<T, SharedScenario?> {
        val shared = if (this.isInner) getSharedScenarioFor(this) else null
        val scenario = if (shared == null) this.primaryConstructor!!.call()
        else this.primaryConstructor!!.call(shared)
        return scenario to shared
    }

    private fun getSharedScenarioFor(klass: KClass<*>): SharedScenario? {
        val key = klass.java.name.substringBeforeLast('$')
        return sharedScenarios2.getOrPut(key) {
            val a = Class.forName(key).kotlin
            if (!a.isFinal) throw Exception("Outer class of scenario ${klass.qualifiedName} must be final.")
            if (!a.isSubclassOf(SharedScenario::class)) return null
            a.primaryConstructor!!.call() as SharedScenario
        }
    }

    private val sharedScenarios2 = mutableMapOf<String, SharedScenario>()
}



