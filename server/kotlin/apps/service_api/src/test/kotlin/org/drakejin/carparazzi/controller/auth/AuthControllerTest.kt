package org.drakejin.carparazzi.controller.auth

import io.mockk.coEvery
import io.mockk.mockk
import org.drakejin.carparazzi.domain.dto.UserDto
import org.drakejin.carparazzi.domain.usecase.UserUseCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.OffsetDateTime
import java.util.*

@WebFluxTest(AuthController::class)
class AuthControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var userUseCase: UserUseCase

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun userUseCase(): UserUseCase = mockk()
    }

    @Test
    fun `getCurrentUserInfo should return user information when user exists`() {
        // Given
        val userId = UUID.randomUUID()
        val userDto = UserDto(
            userId = userId,
            email = "user@example.com",
            nickname = "사용자닉네임",
            createdAt = OffsetDateTime.parse("2025-07-01T10:30:00Z"),
            lastLoginAt = OffsetDateTime.parse("2025-07-01T10:25:00Z"),
            status = UserDto.UserStatus.ACTIVE
        )

        coEvery { userUseCase.getUserInfo(userId) } returns userDto

        // When & Then
        webTestClient.get()
            .uri("/api/v1/auth/me")
            .header("X-User-ID", userId.toString())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.user_id").isEqualTo(userId.toString())
            .jsonPath("$.data.email").isEqualTo("user@example.com")
            .jsonPath("$.data.nickname").isEqualTo("사용자닉네임")
            .jsonPath("$.data.status").isEqualTo("ACTIVE")
            .jsonPath("$.data.created_at").isEqualTo("2025-07-01T10:30:00Z")
            .jsonPath("$.data.last_login_at").isEqualTo("2025-07-01T10:25:00Z")
            .jsonPath("$.error").isEmpty
            .jsonPath("$.timestamp").exists()
    }

    @Test
    fun `getCurrentUserInfo should return 404 when user does not exist`() {
        // Given
        val userId = UUID.randomUUID()
        coEvery { userUseCase.getUserInfo(userId) } returns null

        // When & Then
        webTestClient.get()
            .uri("/api/v1/auth/me")
            .header("X-User-ID", userId.toString())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `getCurrentUserInfo should return 400 when user ID is invalid`() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/auth/me")
            .header("X-User-ID", "invalid-uuid")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.success").isEqualTo(false)
            .jsonPath("$.error.code").isEqualTo("BAD_REQUEST")
            .jsonPath("$.error.message").isEqualTo("Invalid user ID format")
    }
}
