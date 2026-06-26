package com.solitrix.postureminder.shared.di

import com.solitrix.postureminder.shared.IReminderScheduler
import com.solitrix.postureminder.shared.buildReminderScheduler
import com.solitrix.postureminder.shared.data.database.createDatabase
import com.solitrix.postureminder.shared.data.repository.ActiveScheduleRepositoryImpl
import com.solitrix.postureminder.shared.data.repository.ReminderRepositoryImpl
import com.solitrix.postureminder.shared.data.repository.ScheduleRepositoryImpl
import com.solitrix.postureminder.shared.data.repository.TaskRepositoryImpl
import com.solitrix.postureminder.shared.data.repository.TaskTypeRepositoryImpl
import com.solitrix.postureminder.shared.domain.repository.ActiveScheduleRepository
import com.solitrix.postureminder.shared.domain.repository.ReminderRepository
import com.solitrix.postureminder.shared.domain.repository.ScheduleRepository
import com.solitrix.postureminder.shared.domain.repository.TaskRepository
import com.solitrix.postureminder.shared.domain.repository.TaskTypeRepository
import com.solitrix.postureminder.shared.reminder.ReminderEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val dataModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    single { createDatabase() }
    single { get<com.solitrix.postureminder.shared.data.database.AppDatabase>().getDao() }
    single { get<com.solitrix.postureminder.shared.data.database.AppDatabase>().getTaskTypeDao() }
    single { get<com.solitrix.postureminder.shared.data.database.AppDatabase>().getSetDao() }

    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<TaskTypeRepository> { TaskTypeRepositoryImpl(get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
    single<ActiveScheduleRepository> { ActiveScheduleRepositoryImpl(get(), get()) }

    single<IReminderScheduler> {
        val engine: ReminderEngine = get()
        buildReminderScheduler(get()) { engine.checkNow() }
    }
    single<ReminderRepository> {
        ReminderRepositoryImpl(get<IReminderScheduler>()).also { it.start() }
    }
    single {
        ReminderEngine(
            taskRepository = get(),
            activeScheduleRepository = get(),
        )
    }
}
