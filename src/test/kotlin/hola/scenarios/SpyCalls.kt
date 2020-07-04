package hola.scenarios

import kotlin.reflect.KClass

private val calls = mutableMapOf<String, MutableList<String>>()

fun callsFor(vararg klasses: KClass<*>): List<String> {
    return klasses.map { calls[it.qualifiedName!!]!! }.flatten()
}

fun addCall(klass: KClass<*>, method: String) {
    calls.getOrPut(klass.qualifiedName!!) { mutableListOf() }.add(method)
}

fun clearCalls() {
    calls.clear()
}
