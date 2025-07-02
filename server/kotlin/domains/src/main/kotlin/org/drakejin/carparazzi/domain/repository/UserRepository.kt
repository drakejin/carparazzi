package org.drakejin.carparazzi.domain.repository

import org.drakejin.carparazzi.domain.dto.UserDto
import java.util.*

interface UserRepository {
    suspend fun findById(userId: UUID): UserDto?
    suspend fun findByEmail(email: String): UserDto?
    suspend fun updateLastLoginAt(userId: UUID)
}
