package hola

import hola.commands.ListCommand
import hola.commands.RunCommand
import hola.commands.ShowCommand
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

// [ ] Run test matching pattern
// [ ] Run a test with a setup
// [ ] Run just the setup of a test
//
// ./gradlew ksys run all
// ./gradlew ksys run test1
// ./gradlew ksys run test*
// ./gradlew ksys run <filter> [--dry-run]
// ./gradlew list [filter]
// ./gradlew show [filter]
// ./gradlew ksys --debug-jvm --- run Test1

@Command(
    name = "ksys", subcommands = [
        ListCommand::class,
        ShowCommand::class,
        RunCommand::class
    ]
)
class Ksys


private fun defaultFindAllScenarioClasses(): List<KClass<*>> {
    val reflections = Reflections(SubTypesScanner(true), "hola")
    val set =
        reflections.getSubTypesOf(Scenario::class.java) +
                reflections.getSubTypesOf(ParamScenario::class.java)
    return set.map { it.kotlin }
}

// Test hook. Should only be overwritten in tests
var findAllScenariosClasses: () -> List<KClass<*>> = ::defaultFindAllScenarioClasses

val defaultProviders: Map<Class<*>, () -> Any> = mapOf(
    ScenarioClassScanner::class.java to { DefaultScenarioClassScanner() }
)

fun ksys(vararg args: String, providers: Map<Class<*>, () -> Any> = defaultProviders): Int {
    return CommandLine(Ksys(), KsysFactory(providers)).execute(*args)
}

fun groupScenarioInstances(all: List<KClass<*>> = findAllScenariosClasses()): Map<String, List<ScenarioInstance>> {
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
