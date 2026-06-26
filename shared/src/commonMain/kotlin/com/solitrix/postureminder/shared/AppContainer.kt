package com.solitrix.postureminder.shared

import com.solitrix.postureminder.shared.di.appModules
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin { modules(appModules) }
}
