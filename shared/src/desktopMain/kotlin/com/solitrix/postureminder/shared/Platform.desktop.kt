package com.solitrix.postureminder.shared

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage

actual fun platform() = "Desktop"

// Lazily created tray icon used for macOS notifications via java.awt.SystemTray.
private val macTrayIcon: TrayIcon? by lazy {
    if (!SystemTray.isSupported()) return@lazy null
    val img = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
    TrayIcon(img, "PostureMinder").also {
        it.isImageAutoSize = true
        SystemTray.getSystemTray().add(it)
    }
}

actual fun notify(message: String) {
    val os = System.getProperty("os.name").lowercase()
    Thread {
        try {
            when {
                "mac" in os -> macNotify(message)
                "win" in os -> windowsNotify(message)
                else        -> linuxNotify(message)
            }
        } catch (_: Exception) { /* best-effort; don't crash the app */ }
    }.apply { isDaemon = true }.start()
}

private fun macNotify(message: String) {
    macTrayIcon?.displayMessage("PostureMinder", message, TrayIcon.MessageType.NONE)
}

private fun windowsNotify(message: String) {
    val safe = message.replace("'", "''")
    val ps = """
        [void][Windows.UI.Notifications.ToastNotificationManager,Windows.UI.Notifications,ContentType=WindowsRuntime]
        ${'$'}xml   = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent('ToastText02')
        ${'$'}text  = ${'$'}xml.GetElementsByTagName('text')
        ${'$'}text[0].InnerText = 'PostureMinder'
        ${'$'}text[1].InnerText = '$safe'
        ${'$'}toast = [Windows.UI.Notifications.ToastNotification]::new(${'$'}xml)
        [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('PostureMinder').Show(${'$'}toast)
    """.trimIndent()
    ProcessBuilder("powershell", "-NonInteractive", "-Command", ps).start().waitFor()
}

private fun linuxNotify(message: String) {
    ProcessBuilder("notify-send", "PostureMinder", message).start().waitFor()
}
