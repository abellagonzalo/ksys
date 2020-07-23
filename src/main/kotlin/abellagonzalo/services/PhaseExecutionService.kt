package abellagonzalo.services

import abellagonzalo.commands.ResultCode
import abellagonzalo.commands.ResultCode.*
import abellagonzalo.commands.ScenarioPhase
import abellagonzalo.commands.SkippedException
import abellagonzalo.delegates.ScenarioDelegate2
import abellagonzalo.publishers.StartEndPhasePublisher

interface PhaseExecutionService {
    fun execute(phase: ScenarioPhase, delegate: ScenarioDelegate2): ResultCode
    fun skip(phase: ScenarioPhase): ResultCode
    fun dryRun(phase: ScenarioPhase): ResultCode
}

class PhaseExecutionServiceImpl(private val startEndPhasePublisher: StartEndPhasePublisher) :
    PhaseExecutionService {

    override fun execute(phase: ScenarioPhase, delegate: ScenarioDelegate2): ResultCode {

        startEndPhasePublisher.start(phase)

        val (result, exception) = try {
            when (phase) {
                ScenarioPhase.SETUP -> delegate.setup()
                ScenarioPhase.EXECUTE -> delegate.execute()
                ScenarioPhase.VALIDATE -> delegate.validate()
                else -> throw PhaseNotSupported(phase)
            }
            PASSED to null

        } catch (ex: PhaseNotSupported) {
            throw ex

        } catch (ex: SkippedException) {
            SKIPPED to ex

        } catch (ex: Exception) {
            FAILED to ex
        }

        startEndPhasePublisher.end(result, exception)

        return result
    }

    override fun skip(phase: ScenarioPhase): ResultCode {
        TODO("Not yet implemented")
    }

    override fun dryRun(phase: ScenarioPhase): ResultCode {
        TODO("Not yet implemented")
    }
}

class PhaseNotSupported(val phase: ScenarioPhase) : Exception("Phase $phase is not supported.")
