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

class AddNewExerciseUseCaseTest {

    @Mock
    private lateinit var exerciseRepository: ExerciseRepository

    private lateinit var useCase: AddNewExerciseUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = AddNewExerciseUseCase(exerciseRepository)
    }

    @Test
    fun `execute delegates add to repository`() = runBlocking {
        val exercise = Exercise(
            startTime = LocalDateTime.now(),
            duration = 25,
            category = ExerciseCategory.Running,
            intensity = 7
        )

        useCase.execute(exercise)

        Mockito.verify(exerciseRepository).addExercise(exercise)
    }
}
