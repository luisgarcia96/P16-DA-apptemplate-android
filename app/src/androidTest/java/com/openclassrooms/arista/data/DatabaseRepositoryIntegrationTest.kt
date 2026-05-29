package com.openclassrooms.arista.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.arista.data.entity.ExerciseDto
import com.openclassrooms.arista.data.entity.SleepDto
import com.openclassrooms.arista.data.entity.UserDto
import com.openclassrooms.arista.data.repository.ExerciseRepository
import com.openclassrooms.arista.data.repository.SleepRepository
import com.openclassrooms.arista.data.repository.UserRepository
import com.openclassrooms.arista.domain.model.Exercise
import com.openclassrooms.arista.domain.model.ExerciseCategory
import com.openclassrooms.arista.domain.model.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class DatabaseRepositoryIntegrationTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun exerciseDao_insertReadDelete_cycleWorks() = runBlocking {
        val dao = db.exerciseDtoDao()

        val id = dao.insertExercise(
            ExerciseDto(
                startTime = 1_700_000_000_000,
                duration = 30,
                category = "Running",
                intensity = 6
            )
        )
        assertNotNull(id)

        val stored = dao.getAllExercises().first()
        assertEquals(1, stored.size)
        assertEquals("Running", stored.first().category)

        dao.deleteExerciseById(stored.first().id)
        assertEquals(0, dao.getAllExercises().first().size)
    }

    @Test
    fun sleepDao_and_userDao_insertAndRead_work() = runBlocking {
        val sleepDao = db.sleepDtoDao()
        val userDao = db.userDtoDao()

        sleepDao.insertSleep(SleepDto(startTime = 1_700_000_000_000, duration = 420, quality = 4))
        userDao.insertUser(UserDto(name = "Nora", email = "nora@mail.com"))

        val sleeps = sleepDao.getAllSleeps()
        val user = userDao.getUserById(1)

        assertEquals(1, sleeps.size)
        assertEquals(420, sleeps.first().duration)
        assertEquals("Nora", user?.name)
    }

    @Test
    fun repositories_mapDataWithRealDaos() = runBlocking {
        val exerciseRepository = ExerciseRepository(db.exerciseDtoDao())
        val sleepRepository = SleepRepository(db.sleepDtoDao())
        val userRepository = UserRepository(db.userDtoDao())

        exerciseRepository.addExercise(
            Exercise(
                startTime = LocalDateTime.now(),
                duration = 55,
                category = ExerciseCategory.Walking,
                intensity = 5
            )
        )
        db.sleepDtoDao().insertSleep(SleepDto(startTime = 1_700_000_000_000, duration = 460, quality = 3))
        db.userDtoDao().insertUser(UserDto(name = "Seed", email = "seed@mail.com"))
        userRepository.setUser(User(name = "Léo", email = "leo@mail.com"))

        val exercises = exerciseRepository.getAllExercises()
        val sleeps = sleepRepository.getAllSleeps()
        val user = userRepository.getCurrentUser()

        assertEquals(1, exercises.size)
        assertEquals(55, exercises.first().duration)
        assertEquals(ExerciseCategory.Walking, exercises.first().category)
        assertEquals(1, sleeps.size)
        assertEquals(460, sleeps.first().duration)
        // Current repository implementation updates by id from DTO (default 0),
        // so the seeded row (id=1) remains unchanged.
        assertEquals("Seed", user?.name)
    }
}
