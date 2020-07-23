package abellagonzalo.scenarios

import abellagonzalo.commands.ResultCode
import abellagonzalo.commands.ResultCode.*
import abellagonzalo.commands.ScenarioPhase
import abellagonzalo.commands.ScenarioPhase.*
import abellagonzalo.delegates.ScenarioDelegate2
import abellagonzalo.publishers.StartScenarioPublisher
import abellagonzalo.services.PhaseExecutionService
import abellagonzalo.services.ScenarioExecutionService
import abellagonzalo.services.ScenarioExecutionServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito
import org.mockito.Mockito.*


class ScenarioExecutionServiceTests {
    private val scenarioId = "test-scenario-id"

    private val mockDelegate = mock(ScenarioDelegate2::class.java)

    private val mockPhaseExecutionService = mock(PhaseExecutionService::class.java)
    private val mockStartScenarioPublisher = mock(StartScenarioPublisher::class.java)

    private val scenarioExecutionService: ScenarioExecutionService =
        ScenarioExecutionServiceImpl(
            mockPhaseExecutionService,
            mockStartScenarioPublisher
        )

    @BeforeEach
    fun beforeEach() {
        `when`(mockDelegate.id).thenReturn(scenarioId)

        `when`(mockPhaseExecutionService.execute(SETUP, mockDelegate)).thenReturn(PASSED)
        `when`(mockPhaseExecutionService.execute(EXECUTE, mockDelegate)).thenReturn(PASSED)
        `when`(mockPhaseExecutionService.execute(VALIDATE, mockDelegate)).thenReturn(PASSED)

        `when`(mockPhaseExecutionService.skip(any())).thenReturn(SKIPPED)
    }

    @Test
    fun `When execution starts then start scenario event is published`() {
        scenarioExecutionService.execute(mockDelegate)
        verify(mockStartScenarioPublisher, times(1)).start(scenarioId)
    }

    @Nested
    inner class WhenSetupIsFailedTests {

        lateinit var result: ResultCode

        @BeforeEach
        fun beforeEach() {
            `when`(mockPhaseExecutionService.execute(SETUP, mockDelegate)).thenReturn(FAILED)
            result = scenarioExecutionService.execute(mockDelegate)
        }

        @Test
        fun `When setup FAILED then execute is SKIPPED`() {
            verify(mockPhaseExecutionService, times(1)).skip(EXECUTE)
            verify(mockPhaseExecutionService, never()).execute(EXECUTE, mockDelegate)
        }

        @Test
        fun `When setup FAILED then validate is SKIPPED`() {
            verify(mockPhaseExecutionService, times(1)).skip(VALIDATE)
            verify(mockPhaseExecutionService, never()).execute(VALIDATE, mockDelegate)
        }

        @Test
        fun `When setup FAILED then the result is FAILED`() {
            assertEquals(FAILED, result)
        }

        @Test
        fun `When setup FAILED then FAILED is published`() {
            verify(mockStartScenarioPublisher, times(1)).end(FAILED)
        }
    }

    @Nested
    inner class WhenExecuteIsFailedTests {

        lateinit var result: ResultCode

        @BeforeEach
        fun beforeEach() {
            `when`(mockPhaseExecutionService.execute(EXECUTE, mockDelegate)).thenReturn(FAILED)
            result = scenarioExecutionService.execute(mockDelegate)
        }

        @Test
        fun `When execute FAILED then validate is SKIPPED`() {
            verify(mockPhaseExecutionService, times(1)).skip(VALIDATE)
            verify(mockPhaseExecutionService, never()).execute(VALIDATE, mockDelegate)
        }

        @Test
        fun `When execute FAILED then the result is FAILED`() {
            assertEquals(FAILED, result)
        }

        @Test
        fun `When execute FAILED then FAILED is published`() {
            verify(mockStartScenarioPublisher, times(1)).end(FAILED)
        }
    }

    @Nested
    inner class WhenSetupIsPassedTests {

        @Test
        fun `When setup is PASSED then execute is called`() {
            verify(mockPhaseExecutionService, times(1)).execute(EXECUTE, mockDelegate)
        }

        @Test
        fun `When setup is PASSED then validate is called`() {
            verify(mockPhaseExecutionService, times(1)).execute(VALIDATE, mockDelegate)
        }
    }

    @ParameterizedTest
    @CsvSource("EXECUTE", "VALIDATE")
    fun `When setup is PASSED then the next phases are called`(phase: ScenarioPhase) {
        scenarioExecutionService.execute(mockDelegate)
        verify(mockPhaseExecutionService, times(1)).execute(phase, mockDelegate)
    }

    @ParameterizedTest
    @CsvSource("EXECUTE", "VALIDATE")
    fun `When setup is SKIPPED then the next phases are called`(phase: ScenarioPhase) {
        `when`(mockPhaseExecutionService.execute(EXECUTE, mockDelegate)).thenReturn(SKIPPED)
        scenarioExecutionService.execute(mockDelegate)
        verify(mockPhaseExecutionService, times(1)).execute(phase, mockDelegate)
    }

    @Test
    fun `When execute is PASSED then validate is called`() {
        scenarioExecutionService.execute(mockDelegate)
        verify(mockPhaseExecutionService, times(1)).execute(VALIDATE, mockDelegate)
    }

    @Test
    fun `When execute is SKIPPED then validate is called`() {
        `when`(mockPhaseExecutionService.execute(VALIDATE, mockDelegate)).thenReturn(SKIPPED)
        scenarioExecutionService.execute(mockDelegate)
        verify(mockPhaseExecutionService, times(1)).execute(VALIDATE, mockDelegate)
    }
}
