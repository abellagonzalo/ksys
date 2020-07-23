package abellagonzalo.scenarios

import abellagonzalo.commands.ResultCode
import abellagonzalo.commands.ResultCode.*
import abellagonzalo.commands.ScenarioPhase.*
import abellagonzalo.commands.SkippedException
import abellagonzalo.delegates.ScenarioDelegate2
import abellagonzalo.publishers.StartEndPhasePublisher
import abellagonzalo.services.PhaseExecutionService
import abellagonzalo.services.PhaseExecutionServiceImpl
import abellagonzalo.services.PhaseNotSupported
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class PhaseExecutionServiceTests {

    private val mockDelegate = mock(ScenarioDelegate2::class.java)

    private val mockStartEndPhasePublisher = mock(StartEndPhasePublisher::class.java)

    private val phaseExecutionService: PhaseExecutionService =
        PhaseExecutionServiceImpl(mockStartEndPhasePublisher)

    @Test
    fun `Invoke SETUP phase`() {
        phaseExecutionService.execute(SETUP, mockDelegate)
        verify(mockStartEndPhasePublisher, times(1)).start(SETUP)
        verify(mockDelegate, times(1)).setup()
    }

    @Test
    fun `Invoke EXECUTE phase`() {
        phaseExecutionService.execute(EXECUTE, mockDelegate)
        verify(mockStartEndPhasePublisher, times(1)).start(EXECUTE)
        verify(mockDelegate, times(1)).execute()
    }

    @Test
    fun `Invoke VALIDATE phase`() {
        phaseExecutionService.execute(VALIDATE, mockDelegate)
        verify(mockStartEndPhasePublisher, times(1)).start(VALIDATE)
        verify(mockDelegate, times(1)).validate()
    }

    @Test
    fun `Phase execution returns PASSED`() {
        executeTest(PASSED, null)
    }

    @Test
    fun `When phase execution throws an exception returns FAILED`() {
        executeTest(FAILED, Exception("The scenario has thrown an exception"))
    }

    @Test
    fun `When execution throws a SkippedException return SKIPPED`() {
        executeTest(SKIPPED, SkippedException("Scenario skipped for some reason"))
    }

    private fun executeTest(result: ResultCode, exception: Exception?) {
        if (exception != null) `when`(mockDelegate.setup()).then { throw exception }
        assertEquals(result, SETUP)
        verify(mockStartEndPhasePublisher, times(1)).end(result, exception)
    }

    @Test
    fun `TEARDOWN phase is not supported`() {
        assertThrows(PhaseNotSupported::class.java) {
            phaseExecutionService.execute(TEARDOWN, mockDelegate)
        }
    }
}
