package abellagonzalo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import picocli.CommandLine.IFactory

class KsysFactoryTests {

    private val factory = KsysFactory()

    @Test
    fun `Instantiate class with no args`() {
        class A
        factory.create(A::class.java)
    }

    @Test
    fun `Instantiate class with args`() {
        class A
        class B
        class C(a: A, b: B)
        factory.create(C::class.java)
    }

    interface IntA

    @Test
    fun `Instantiate implementation for interface`() {
        class ImplA : IntA
        factory.bind(IntA::class.java).to(ImplA::class.java)
        assertTrue(factory.create(IntA::class.java) is ImplA)
    }

    @Test
    fun `Instantiate with custom factory`() {
        class ImplA : IntA

        val instance = ImplA()
        factory.bind(IntA::class.java).to { instance }
        assertTrue(factory.create(IntA::class.java) === instance)
    }

    @Test
    fun `Instantiate interface with no mapping`() {
        Assertions.assertThrows(NoConstructorException::class.java) {
            factory.create(IntA::class.java)
        }
    }
}

class NoConstructorException(val cls: Class<*>) : Exception("No constructors found for $cls")

class KsysFactory : IFactory {

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
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K : Any?> create(cls: Class<K>?): K {
        val mapping2 = map[cls!!] ?: { callConstructor(cls) }
        return mapping2() as K
    }

    private fun <K : Any?> callConstructor(cls: Class<K>): K {
        val constructor = cls.constructors.maxBy { it.parameterCount } ?: throw NoConstructorException(cls)
        val initargs = constructor.parameterTypes.map { create(it) }.toTypedArray()
        return cls.getDeclaredConstructor(*constructor.parameterTypes).newInstance(*initargs)
    }
}
