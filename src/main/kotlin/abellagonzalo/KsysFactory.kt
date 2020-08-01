package abellagonzalo

import abellagonzalo.KsysFactory.Binder
import picocli.CommandLine

inline fun <reified K : Any?> KsysFactory.bind(): Binder<K> {
    return bind(K::class.java)
}

class KsysFactory : CommandLine.IFactory {

    private val map = mutableMapOf<Class<*>, () -> Any>()

    fun <K : Any?> bind(cls: Class<K>): Binder<K> {
        return Binder(cls)
    }

    inner class Binder<in K>(private val cls: Class<K>) {
        fun <U : K> to(fn: () -> U) {
            map[cls] = { fn() as Any }
        }

        fun <U : K> to(toCls: Class<U>) {
            map[cls] = { callConstructor(toCls) as Any }
        }

        inline fun <reified U : K> to() {
            to(U::class.java)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K : Any?> create(cls: Class<K>?): K {
        val fn = map[cls!!] ?: { callConstructor(cls) }
        return fn() as K
    }

    private fun <K : Any?> callConstructor(cls: Class<K>): K {
        val constructor = cls.constructors.maxBy { it.parameterCount } ?: throw NoConstructorException(
            cls
        )
        val initargs = constructor.parameterTypes.map { create(it) }.toTypedArray()
        return cls.getDeclaredConstructor(*constructor.parameterTypes).newInstance(*initargs)
    }
}