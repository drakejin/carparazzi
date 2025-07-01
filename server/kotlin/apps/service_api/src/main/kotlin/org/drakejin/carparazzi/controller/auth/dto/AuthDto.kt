package org.drakejin.carparazzi.controller.auth.dto

import java.time.Instant
import java.util.*

// Response DTOs
data class UserInfoResponseDto(
    val userId: UUID,
    val email: String,
    val nickname: String,
    val status: UserStatus,
    val createdAt: Instant,
    val lastLoginAt: Instant?
)

enum class UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}
