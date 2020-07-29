package abellagonzalo

import org.junit.jupiter.api.Assertions

fun <T> assertEquals(expected: T, actual: T) {
    Assertions.assertEquals(expected, actual)
}