package com.solitrix.postureminder.shared.di

import com.solitrix.postureminder.shared.ui.viewmodel.PostureViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        PostureViewModel(
            observeScheduledTasks = get(),
            placeTask = get(),
            moveTask = get(),
            removeTask = get(),
            resizeTask = get(),
            resizeTaskTop = get(),
            observeTaskTypes = get(),
            addTaskType = get(),
            updateTaskType = get(),
            observeSchedules = get(),
            addSchedule = get(),
            deleteSchedule = get(),
            observeActiveSchedule = get(),
            setActiveSchedule = get(),
            observeRemindersRunning = get(),
            toggleReminders = get(),
        )
    }
}

val appModules = listOf(dataModule, domainModule, uiModule)
