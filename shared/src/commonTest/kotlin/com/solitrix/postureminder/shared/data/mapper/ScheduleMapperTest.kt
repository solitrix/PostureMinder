package com.solitrix.postureminder.shared.data.mapper

import com.solitrix.postureminder.shared.data.entity.Set as SetEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class ScheduleMapperTest {

    @Test
    fun `given Set entity when toDomain then id and name are mapped correctly`() {
        val entity = SetEntity(name = "Evening Stretches", id = 9L)

        val domain = entity.toDomain()

        assertEquals(9L, domain.id)
        assertEquals("Evening Stretches", domain.name)
    }

    @Test
    fun `given Set entity with default id when toDomain then default id zero is preserved`() {
        val entity = SetEntity(name = "Work")

        val domain = entity.toDomain()

        assertEquals(0L, domain.id)
        assertEquals("Work", domain.name)
    }
}
