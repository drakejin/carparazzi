package org.drakejin.carparazzi.controller.auth

import kotlinx.coroutines.runBlocking
import org.drakejin.carparazzi.controller.auth.dto.UserInfoResponseDto
import org.drakejin.carparazzi.controller.common.dto.ApiResponse
import org.drakejin.carparazzi.domain.usecase.UserUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/auth")
open class AuthController(
    private val userUseCase: UserUseCase
) {

    @GetMapping("/me")
    suspend fun getCurrentUserInfo(
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<*>> {
        val userUuid = try {
            UUID.fromString(userId)
        } catch (e: IllegalArgumentException) {
            val errorResponse = ApiResponse<Any>(
                success = false,
                data = null,
                error = org.drakejin.carparazzi.controller.common.dto.ErrorDetail(
                    code = "BAD_REQUEST",
                    message = "Invalid user ID format"
                )
            )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
        }

        val user = userUseCase.getUserInfo(userUuid)

        return if (user != null) {
            val response = UserInfoResponseDto(
                userId = user.userId,
                email = user.email,
                nickname = user.nickname ?: "",
                status = user.status,
                createdAt = user.createdAt,
                lastLoginAt = user.lastLoginAt
            )

            val successResponse = ApiResponse(
                success = true,
                data = response
            )
            ResponseEntity.ok(successResponse)
        } else {
            val errorResponse = ApiResponse<Any>(
                success = false,
                data = null,
                error = org.drakejin.carparazzi.controller.common.dto.ErrorDetail(
                    code = "NOT_FOUND",
                    message = "User not found"
                )
            )
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        }
    }
}
