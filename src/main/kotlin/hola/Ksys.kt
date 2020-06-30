package hola

import hola.commands.ListCommand
import hola.commands.RunCommand
import hola.commands.ShowCommand
import picocli.CommandLine
import picocli.CommandLine.Command

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

val defaultServices: Map<Class<*>, () -> Any> = mapOf(
    ScenarioClassScanner::class.java to { DefaultScenarioClassScanner() }
)

fun ksys(vararg args: String, providers: Map<Class<*>, () -> Any> = defaultServices): Int {
    return CommandLine(Ksys(), KsysFactory(providers)).execute(*args)
}
