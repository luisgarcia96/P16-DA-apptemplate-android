package com.openclassrooms.arista.domain.usecase

import com.openclassrooms.arista.data.repository.SleepRepository
import com.openclassrooms.arista.domain.model.Sleep
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

class GetAllSleepsUseCaseTest {

    @Mock
    private lateinit var sleepRepository: SleepRepository

    private lateinit var useCase: GetAllSleepsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = GetAllSleepsUseCase(sleepRepository)
    }

    @Test
    fun `execute returns repository sleeps`() = runBlocking {
        val sleeps = listOf(Sleep(LocalDateTime.now(), 480, 4))
        Mockito.`when`(sleepRepository.getAllSleeps()).thenReturn(sleeps)

        val result = useCase.execute()

        assertEquals(sleeps, result)
    }
}
