package abellagonzalo.services

import abellagonzalo.commands.ResultCode
import abellagonzalo.commands.ScenarioPhase
import abellagonzalo.delegates.ScenarioDelegate2
import abellagonzalo.publishers.StartScenarioPublisher

interface ScenarioExecutionService {
    fun execute(delegate: ScenarioDelegate2): ResultCode
    fun skip(): ResultCode
    fun dryRun(): ResultCode
}

class ScenarioExecutionServiceImpl(
    private val phaseExecutionService: PhaseExecutionService,
    private val startScenarioPublisher: StartScenarioPublisher
) : ScenarioExecutionService {

    override fun execute(delegate: ScenarioDelegate2): ResultCode {

        startScenarioPublisher.start(delegate.id)

        var setup = phaseExecutionService.execute(ScenarioPhase.SETUP, delegate)

        val execute =
            if (setup == ResultCode.FAILED) phaseExecutionService.skip(
                ScenarioPhase.EXECUTE
            )
            else phaseExecutionService.execute(ScenarioPhase.EXECUTE, delegate)

        val validate =
            if (setup == ResultCode.FAILED || execute == ResultCode.FAILED) phaseExecutionService.skip(
                ScenarioPhase.VALIDATE
            )
            else phaseExecutionService.execute(ScenarioPhase.VALIDATE, delegate)

        val allSkipped = listOf(setup, execute, validate).all { it == ResultCode.SKIPPED }
        val teardown =
            if (allSkipped) phaseExecutionService.skip(ScenarioPhase.TEARDOWN)
            else phaseExecutionService.execute(ScenarioPhase.TEARDOWN, delegate)

        val results = listOf(setup, execute, validate, teardown)

        val result =
            results.firstOrNull { it == ResultCode.FAILED }
                ?: results.firstOrNull { it == ResultCode.PASSED }
                ?: ResultCode.SKIPPED

        startScenarioPublisher.end(result)

        return result
    }

    override fun skip(): ResultCode {
        TODO("Not yet implemented")
    }

    override fun dryRun(): ResultCode {
        TODO("Not yet implemented")
    }
}