package hola

fun main(args: Array<String>) {
    println("Args: ${args.toList()}")
    println()
    ksys(*args)
}