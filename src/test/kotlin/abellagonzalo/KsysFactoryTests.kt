package abellagonzalo

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@Suppress("UNUSED_PARAMETER")
class KsysFactoryTests {

    private val factory = KsysFactory()

    @Test
    fun `Instantiate class with no args`() {
        class A
        factory.create(A::class.java)
    }

    @Test
    fun `Instantiate class with args`() {
        class A; class B; class C(a: A, b: B)
        factory.create(C::class.java)
    }

    interface IntA

    @Test
    fun `Instantiate implementation for interface`() {
        class ImplA : IntA
        factory.bind<IntA>().to<ImplA>()
        assertTrue(factory.create(IntA::class.java) is ImplA)
    }

    @Test
    fun `Instantiate with custom factory`() {
        class ImplA : IntA

        val instance = ImplA()
        factory.bind<IntA>().to { instance }
        assertTrue(factory.create(IntA::class.java) === instance)
    }

    @Test
    fun `Instantiate interface with no mapping`() {
        assertThrows(NoConstructorException::class.java) {
            factory.create(IntA::class.java)
        }
    }

    @Test
    fun `Instantiate using constructor with more parameters`() {
        class A; class B; class C(a: A) {
            constructor(a: A, b: B) : this(a) {
                secondConstructorCalled = true
            }

            var secondConstructorCalled: Boolean = false
                private set
        }
        assertTrue(factory.create(C::class.java).secondConstructorCalled)
    }
}

