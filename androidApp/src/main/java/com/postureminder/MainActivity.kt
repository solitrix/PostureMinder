package com.postureminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.solitrix.postureminder.shared.data.database.appContext
import com.solitrix.postureminder.shared.initKoin
import com.solitrix.postureminder.shared.ui.components.PostureApp
import com.solitrix.postureminder.shared.ui.theme.PostureminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appContext = applicationContext
        initKoin()

        setContent {
            PostureminderTheme {
                PostureApp(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                )
            }
        }
    }
}
