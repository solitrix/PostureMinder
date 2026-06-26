package com.solitrix.postureminder.shared.domain.usecase

import com.solitrix.postureminder.shared.domain.repository.ReminderRepository

class ToggleRemindersUseCase(private val reminderRepository: ReminderRepository) {
    operator fun invoke() = reminderRepository.toggle()
}
