package com.openclassrooms.arista.domain.model

import com.openclassrooms.arista.data.entity.ExerciseDto
import com.openclassrooms.arista.data.entity.SleepDto
import com.openclassrooms.arista.data.entity.UserDto
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class DomainModelConversionTest {

    @Test
    fun `exercise toDto converts enum and fields`() {
        val exercise = Exercise(
            id = 42,
            startTime = LocalDateTime.of(2026, 5, 28, 8, 30),
            duration = 30,
            category = ExerciseCategory.Riding,
            intensity = 8
        )

        val dto = exercise.toDto()

        assertEquals(30, dto.duration)
        assertEquals("Riding", dto.category)
        assertEquals(8, dto.intensity)
        assertEquals(exercise.startTime.toEpochSecond(ZoneOffset.UTC), dto.startTime)
    }

    @Test
    fun `exercise fromDto converts timestamp and enum`() {
        val millis = 1_700_000_000_000
        val dto = ExerciseDto(
            id = 5,
            startTime = millis,
            duration = 44,
            category = "Swimming",
            intensity = 6
        )

        val model = Exercise.fromDto(dto)

        assertEquals(5L, model.id)
        assertEquals(44, model.duration)
        assertEquals(ExerciseCategory.Swimming, model.category)
        assertEquals(6, model.intensity)
        assertEquals(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()),
            model.startTime
        )
    }

    @Test
    fun `sleep fromDto converts dto`() {
        val millis = 1_700_000_000_000
        val dto = SleepDto(id = 2, startTime = millis, duration = 410, quality = 3)

        val model = Sleep.fromDto(dto)

        assertEquals(410, model.duration)
        assertEquals(3, model.quality)
        assertEquals(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()),
            model.startTime
        )
    }

    @Test
    fun `user toDto and fromDto keep values`() {
        val user = User(name = "Lina", email = "lina@mail.com")

        val dto = user.toDto()
        val remapped = User.fromDto(UserDto(id = 1, name = dto.name, email = dto.email))

        assertEquals("Lina", dto.name)
        assertEquals("lina@mail.com", dto.email)
        assertEquals(user, remapped)
    }
}
