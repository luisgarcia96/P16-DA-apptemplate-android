package com.openclassrooms.arista.domain.usecase

import com.openclassrooms.arista.data.repository.ExerciseRepository
import com.openclassrooms.arista.domain.model.Exercise
import com.openclassrooms.arista.domain.model.ExerciseCategory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

class DeleteExerciseUseCaseTest {

    @Mock
    private lateinit var exerciseRepository: ExerciseRepository

    private lateinit var useCase: DeleteExerciseUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = DeleteExerciseUseCase(exerciseRepository)
    }

    @Test
    fun `execute delegates delete to repository`() = runBlocking {
        val exercise = Exercise(
            id = 9,
            startTime = LocalDateTime.now(),
            duration = 12,
            category = ExerciseCategory.Walking,
            intensity = 3
        )

        useCase.execute(exercise)

        Mockito.verify(exerciseRepository).deleteExercise(exercise)
    }
}
