package abellagonzalo.commands

import picocli.CommandLine

class KsysFactory(private val providers: Map<Class<*>, () -> Any>) :
    CommandLine.IFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <K : Any?> create(cls: Class<K>?): K {
        return providers[cls!!]?.invoke() as K ?: kotlin.run {
            val constructor = cls.constructors.maxBy { it.parameterCount }!!
            val initargs = constructor.parameterTypes.map { create(it) }.toTypedArray()
            cls.getDeclaredConstructor(*constructor.parameterTypes).newInstance(*initargs)
        }
    }
}