package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.ActiveScheduleRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveActiveScheduleUseCase(private val activeScheduleRepository: ActiveScheduleRepository) {
    operator fun invoke(): StateFlow<Long> = activeScheduleRepository.activeScheduleId
}
