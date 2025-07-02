package org.drakejin.carparazzi.entity

import org.drakejin.carparazzi.entity.generated.tables.pojos.Users
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.OffsetDateTime
import java.util.*

class UserTest {

    @Test
    fun `Users 엔티티 생성 테스트`() {
        // Given
        val userId = UUID.randomUUID()
        val email = "test@example.com"
        val nickname = "testuser"
        val createdAt = OffsetDateTime.now()
        val status = "ACTIVE"

        // When
        val user = Users(
            userId,
            email,
            nickname,
            createdAt,
            null,
            status
        )

        // Then
        assertEquals(userId, user.userId)
        assertEquals(email, user.email)
        assertEquals(nickname, user.nickname)
        assertEquals(status, user.status)
        assertEquals(createdAt, user.createdAt)
        assertNull(user.lastLoginAt)
    }

    @Test
    fun `Users 엔티티 필수 필드만으로 생성 테스트`() {
        // Given
        val userId = UUID.randomUUID()
        val email = "test@example.com"
        val createdAt = OffsetDateTime.now()
        val status = "ACTIVE"

        // When
        val user = Users(
            userId,
            email,
            null,
            createdAt,
            null,
            status
        )

        // Then
        assertEquals(userId, user.userId)
        assertEquals(email, user.email)
        assertNull(user.nickname)
        assertEquals(status, user.status)
        assertEquals(createdAt, user.createdAt)
        assertNull(user.lastLoginAt)
    }

    @Test
    fun `Users 엔티티 복사 생성자 테스트`() {
        // Given
        val originalUser = Users(
            UUID.randomUUID(),
            "original@example.com",
            "original",
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            "ACTIVE"
        )

        // When
        val copiedUser = Users(originalUser)

        // Then
        assertEquals(originalUser.userId, copiedUser.userId)
        assertEquals(originalUser.email, copiedUser.email)
        assertEquals(originalUser.nickname, copiedUser.nickname)
        assertEquals(originalUser.createdAt, copiedUser.createdAt)
        assertEquals(originalUser.lastLoginAt, copiedUser.lastLoginAt)
        assertEquals(originalUser.status, copiedUser.status)
    }

    @Test
    fun `Users 엔티티 equals 테스트`() {
        // Given
        val userId = UUID.randomUUID()
        val email = "test@example.com"
        val nickname = "testuser"
        val createdAt = OffsetDateTime.now()
        val status = "ACTIVE"

        val user1 = Users(userId, email, nickname, createdAt, null, status)
        val user2 = Users(userId, email, nickname, createdAt, null, status)
        val user3 = Users(UUID.randomUUID(), email, nickname, createdAt, null, status)

        // When & Then
        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun `Users 엔티티 toString 테스트`() {
        // Given
        val userId = UUID.randomUUID()
        val email = "test@example.com"
        val nickname = "testuser"
        val createdAt = OffsetDateTime.now()
        val status = "ACTIVE"

        val user = Users(userId, email, nickname, createdAt, null, status)

        // When
        val toString = user.toString()

        // Then
        assertTrue(toString.contains("Users"))
        assertTrue(toString.contains(userId.toString()))
        assertTrue(toString.contains(email))
        assertTrue(toString.contains(nickname))
        assertTrue(toString.contains(status))
    }
}
