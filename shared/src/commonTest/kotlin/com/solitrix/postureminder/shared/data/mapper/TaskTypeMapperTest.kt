package com.solitrix.postureminder.shared.data.mapper

import com.solitrix.postureminder.shared.data.entity.TaskType
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskTypeMapperTest {

    @Test
    fun `given TaskType entity when toDomain then all fields are mapped correctly`() {
        val entity = TaskType(id = 3L, name = "Recline", colorRgb = 0x2196F3)

        val domain = entity.toDomain()

        assertEquals(3L, domain.id)
        assertEquals("Recline", domain.name)
        assertEquals(0x2196F3, domain.colorRgb)
    }

    @Test
    fun `given TaskType with default color when toDomain then default color is preserved`() {
        val entity = TaskType(id = 1L, name = "Stand")

        val domain = entity.toDomain()

        assertEquals(0x4CAF50, domain.colorRgb)
    }
}
