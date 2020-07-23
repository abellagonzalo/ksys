package abellagonzalo.services

interface ExecutionIdService {
    val executionId: String
}

class ExecutionIdServiceImpl : ExecutionIdService {
    override val executionId: String
        get() = Thread.currentThread().threadGroup.name
}