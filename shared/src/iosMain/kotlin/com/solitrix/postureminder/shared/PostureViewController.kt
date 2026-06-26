package com.solitrix.postureminder.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.solitrix.postureminder.shared.ui.components.PostureApp
import platform.UIKit.UIViewController

fun PostureViewController(): UIViewController =
    ComposeUIViewController {
        PostureApp(modifier = Modifier.fillMaxSize().safeDrawingPadding())
    }
