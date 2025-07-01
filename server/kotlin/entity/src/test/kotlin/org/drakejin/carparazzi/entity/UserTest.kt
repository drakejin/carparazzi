package org.drakejin.carparazzi.entity

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.*

class UserTest {

    @Test
    fun `User 엔티티 생성 테스트`() {
        // Given
        val email = "test@example.com"
        val nickname = "testuser"

        // When
        val user = User(
            email = email,
            nickname = nickname
        )

        // Then
        assertNotNull(user.userId)
        assertEquals(email, user.email)
        assertEquals(nickname, user.nickname)
        assertEquals(User.UserStatus.ACTIVE, user.status)
        assertNotNull(user.createdAt)
        assertNull(user.lastLoginAt)
    }

    @Test
    fun `User 엔티티 기본값 테스트`() {
        // Given & When
        val user = User(email = "test@example.com")

        // Then
        assertNull(user.nickname)
        assertEquals(User.UserStatus.ACTIVE, user.status)
        assertNotNull(user.userId)
        assertNotNull(user.createdAt)
    }

    @Test
    fun `UserStatus enum 테스트`() {
        // Given & When & Then
        assertEquals(3, User.UserStatus.values().size)
        assertTrue(User.UserStatus.values().contains(User.UserStatus.ACTIVE))
        assertTrue(User.UserStatus.values().contains(User.UserStatus.INACTIVE))
        assertTrue(User.UserStatus.values().contains(User.UserStatus.SUSPENDED))
    }
}
