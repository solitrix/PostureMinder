package com.solitrix.postureminder.shared.data.mapper

import com.solitrix.postureminder.shared.data.entity.Set as SetEntity
import com.solitrix.postureminder.shared.domain.model.ScheduleModel

fun SetEntity.toDomain() = ScheduleModel(id = id, name = name)
