import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
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

        val baseIcon = rememberVectorPainter(Icons.Filled.Accessibility)
        val trayIcon = remember(baseIcon) {
            object : Painter() {
                override val intrinsicSize: Size get() = baseIcon.intrinsicSize
                override fun DrawScope.onDraw() {
                    with(baseIcon) { draw(size, colorFilter = ColorFilter.tint(Color.White)) }
                }
            }
        }

        Tray(
            icon = trayIcon,
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
