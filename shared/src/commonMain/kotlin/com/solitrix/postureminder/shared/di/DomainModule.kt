package com.solitrix.postureminder.shared.di

import com.solitrix.postureminder.shared.domain.usecase.AddScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.AddTaskTypeUseCase
import com.solitrix.postureminder.shared.domain.usecase.DeleteScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.MoveTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveActiveScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveRemindersRunningUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveScheduledTasksUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveSchedulesUseCase
import com.solitrix.postureminder.shared.domain.usecase.ObserveTaskTypesUseCase
import com.solitrix.postureminder.shared.domain.usecase.PlaceTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.RemoveTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.ResizeTaskTopUseCase
import com.solitrix.postureminder.shared.domain.usecase.ResizeTaskUseCase
import com.solitrix.postureminder.shared.domain.usecase.SetActiveScheduleUseCase
import com.solitrix.postureminder.shared.domain.usecase.ToggleRemindersUseCase
import com.solitrix.postureminder.shared.domain.usecase.UpdateTaskTypeUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { ObserveScheduledTasksUseCase(get()) }
    factory { PlaceTaskUseCase(get()) }
    factory { MoveTaskUseCase(get()) }
    factory { RemoveTaskUseCase(get()) }
    factory { ResizeTaskUseCase(get()) }
    factory { ResizeTaskTopUseCase(get()) }
    factory { ObserveTaskTypesUseCase(get()) }
    factory { AddTaskTypeUseCase(get()) }
    factory { UpdateTaskTypeUseCase(get()) }
    factory { ObserveSchedulesUseCase(get()) }
    factory { AddScheduleUseCase(get()) }
    factory { DeleteScheduleUseCase(get(), get()) }
    factory { ObserveActiveScheduleUseCase(get()) }
    factory { SetActiveScheduleUseCase(get()) }
    factory { ObserveRemindersRunningUseCase(get()) }
    factory { ToggleRemindersUseCase(get()) }
}
