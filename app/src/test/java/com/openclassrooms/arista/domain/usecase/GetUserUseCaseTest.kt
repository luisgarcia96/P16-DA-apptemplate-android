package com.openclassrooms.arista.domain.usecase

import com.openclassrooms.arista.data.repository.UserRepository
import com.openclassrooms.arista.domain.model.User
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GetUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var useCase: GetUserUsecase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = GetUserUsecase(userRepository)
    }

    @Test
    fun `execute returns repository user`() = runBlocking {
        val user = User("Mia", "mia@mail.com")
        Mockito.`when`(userRepository.getCurrentUser()).thenReturn(user)

        val result = useCase.execute()

        assertEquals(user, result)
    }
}
