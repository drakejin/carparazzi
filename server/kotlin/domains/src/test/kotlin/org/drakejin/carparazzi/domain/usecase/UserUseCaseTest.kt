package org.drakejin.carparazzi.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.drakejin.carparazzi.domain.dto.UserDto
import org.drakejin.carparazzi.domain.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.*

class UserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userUseCase: UserUseCase

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        userUseCase = UserUseCase(userRepository)
    }

    @Test
    fun `getUserInfo should return user and update last login time when user exists`() = runTest {
        // Given
        val userId = UUID.randomUUID()
        val userDto = UserDto(
            userId = userId,
            email = "test@example.com",
            nickname = "testuser",
            createdAt = OffsetDateTime.now().minusDays(30),
            lastLoginAt = OffsetDateTime.now().minusHours(1),
            status = UserDto.UserStatus.ACTIVE
        )

        coEvery { userRepository.findById(userId) } returns userDto
        coEvery { userRepository.updateLastLoginAt(userId) } returns Unit

        // When
        val result = userUseCase.getUserInfo(userId)

        // Then
        assertNotNull(result)
        assertEquals(userDto, result)
        coVerify { userRepository.findById(userId) }
        coVerify { userRepository.updateLastLoginAt(userId) }
    }

    @Test
    fun `getUserInfo should return null when user does not exist`() = runTest {
        // Given
        val userId = UUID.randomUUID()
        coEvery { userRepository.findById(userId) } returns null

        // When
        val result = userUseCase.getUserInfo(userId)

        // Then
        assertNull(result)
        coVerify { userRepository.findById(userId) }
        coVerify(exactly = 0) { userRepository.updateLastLoginAt(any()) }
    }

    @Test
    fun `getUserByEmail should return user when user exists`() = runTest {
        // Given
        val email = "test@example.com"
        val userDto = UserDto(
            userId = UUID.randomUUID(),
            email = email,
            nickname = "testuser",
            createdAt = OffsetDateTime.now().minusDays(30),
            lastLoginAt = OffsetDateTime.now().minusHours(1),
            status = UserDto.UserStatus.ACTIVE
        )

        coEvery { userRepository.findByEmail(email) } returns userDto

        // When
        val result = userUseCase.getUserByEmail(email)

        // Then
        assertNotNull(result)
        assertEquals(userDto, result)
        coVerify { userRepository.findByEmail(email) }
    }

    @Test
    fun `getUserByEmail should return null when user does not exist`() = runTest {
        // Given
        val email = "nonexistent@example.com"
        coEvery { userRepository.findByEmail(email) } returns null

        // When
        val result = userUseCase.getUserByEmail(email)

        // Then
        assertNull(result)
        coVerify { userRepository.findByEmail(email) }
    }
}
