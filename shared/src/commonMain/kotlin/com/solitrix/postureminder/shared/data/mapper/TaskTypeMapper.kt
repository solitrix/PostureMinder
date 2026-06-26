package com.solitrix.postureminder.shared.data.mapper

import com.solitrix.postureminder.shared.data.entity.TaskType
import com.solitrix.postureminder.shared.domain.model.TaskTypeModel

fun TaskType.toDomain() = TaskTypeModel(id = id, name = name, colorRgb = colorRgb)
