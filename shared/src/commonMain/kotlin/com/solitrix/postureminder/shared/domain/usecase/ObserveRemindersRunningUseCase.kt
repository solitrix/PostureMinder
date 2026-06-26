package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveRemindersRunningUseCase(private val reminderRepository: ReminderRepository) {
    operator fun invoke(): StateFlow<Boolean> = reminderRepository.isRunning
}
