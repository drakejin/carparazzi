package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.entity.generated.tables.pojos.Users
import java.time.OffsetDateTime
import java.util.*

data class UserDto(
    val userId: UUID,
    val email: String,
    val nickname: String?,
    val createdAt: OffsetDateTime,
    val lastLoginAt: OffsetDateTime?,
    val status: UserStatus
) {
    enum class UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    companion object {
        fun fromEntity(entity: Users): UserDto {
            return UserDto(
                userId = entity.userId,
                email = entity.email,
                nickname = entity.nickname,
                createdAt = entity.createdAt,
                lastLoginAt = entity.lastLoginAt,
                status = UserStatus.valueOf(entity.status)
            )
        }
    }
}
