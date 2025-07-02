package org.drakejin.carparazzi.controller.auth

import kotlinx.coroutines.runBlocking
import org.drakejin.carparazzi.controller.auth.dto.UserInfoResponseDto
import org.drakejin.carparazzi.controller.common.dto.ApiResponse
import org.drakejin.carparazzi.domain.usecase.UserUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userUseCase: UserUseCase
) {

    @GetMapping("/me")
    fun getCurrentUserInfo(
        @RequestHeader("X-User-ID") userId: String
    ): Mono<ApiResponse<UserInfoResponseDto>> {
        return Mono.fromCallable {
            UUID.fromString(userId)
        }
        .onErrorMap { ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format") }
        .flatMap { userUuid ->
            Mono.fromCallable {
                runBlocking { userUseCase.getUserInfo(userUuid) }
            }
        }
        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
        .map { user ->
            user ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

            val response = UserInfoResponseDto(
                userId = user.userId,
                email = user.email,
                nickname = user.nickname ?: "",
                status = user.status,
                createdAt = user.createdAt,
                lastLoginAt = user.lastLoginAt
            )

            ApiResponse(
                success = true,
                data = response
            )
        }
    }
}
