package abellagonzalo

class NoConstructorException(val cls: Class<*>) : Exception("No constructors found for $cls")