import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import com.solitrix.postureminder.shared.initKoin
import com.solitrix.postureminder.shared.ui.components.PostureApp

fun main() {
    initKoin()

    application {
        var isWindowVisible by remember { mutableStateOf(true) }
        val trayState = rememberTrayState()

        Tray(
            icon = rememberVectorPainter(Icons.Filled.Accessibility),
            state = trayState,
            tooltip = "PostureMinder",
            onAction = { isWindowVisible = true },
        ) {
            Item("Show", onClick = { isWindowVisible = true })
            Item("Hide", onClick = { isWindowVisible = false })
            Item("Quit", onClick = { exitApplication() })
        }

        Window(
            onCloseRequest = { isWindowVisible = false },
            title = "PostureMinder",
            visible = isWindowVisible,
        ) {
            PostureApp()
        }
    }
}
