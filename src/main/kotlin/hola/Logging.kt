package hola

import hola.scenarios.ParamScenario
import hola.scenarios.Scenario
import hola.scenarios.SharedSetup
import java.io.Closeable
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime
import kotlin.reflect.full.isSubclassOf

// TODO - Make it generic to customize logger
object Logging {

    private val loggers = hashMapOf<Pair<String, String>, SimpleLogger>()

    val current: SimpleLogger
        get() {
            val stack = Thread.currentThread().stackTrace
            val element = stack.first {
                val candidate = Class.forName(it.className).kotlin
                if (candidate.java.isSynthetic) return@first false
                val isFinal = candidate.isFinal
                val isSharedScenario = candidate.isSubclassOf(SharedSetup::class)
                val isScenario = candidate.isSubclassOf(Scenario::class)
                val isParamScenario = candidate.isSubclassOf(ParamScenario::class)
                isFinal && (isSharedScenario || isScenario || isParamScenario)
            }
            val key = element.className to element.methodName
            return loggers[key] ?: throw Exception("Logger $key has not been registered.")
        }

    private val basedirs = hashMapOf<String, File>()

    private fun getOrCreateBaseDir(id: String) = basedirs.getOrPut(id) {
        File("output/$id").apply {
            deleteRecursively()
            mkdirs()
        }
    }

    private val deletedOnce = mutableSetOf<File>()

    internal fun registerLogger(
        className: String,
        methodName: String,
        scenarioId: String?,
        sharedScenarioId: String?
    ): Closeable {
        var file = File("output")
        if (sharedScenarioId != null)
            file = file.resolve(sharedScenarioId)
        if (scenarioId != null)
            file = file.resolve(scenarioId)
        file = file.resolve("$methodName.log")

        if (deletedOnce.add(file.parentFile))
            file.parentFile.mkdirs()

        file.createNewFile()
        val stdout = PrintStream(file)
        val logger = SimpleLogger(listOf(System.out, stdout))
        val key = className to methodName
        loggers.put(key, logger) ?: return Closeable {
            loggers.remove(key) ?: throw Exception("Logger $key cannot be removed because it does not exist.")
        }
        throw Exception("Logger $key already exists and cannot be overwritten")
    }
}

// TODO - use a proper logger class
class SimpleLogger(private val printStreams: List<PrintStream>) {

    fun info(str: String) = printAll("${LocalDateTime.now()} [ INFO] $str")

    fun warning(str: String) = printAll("${LocalDateTime.now()} [ WARN] $str")

    fun error(str: String) = printAll("${LocalDateTime.now()} [ERROR] $str")

    private fun printAll(str: String) = printStreams.forEach { it.println(str) }
}