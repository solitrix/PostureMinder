package com.solitrix.postureminder.shared.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val StandColor = Color(0xFF4CAF50)
val SitColor = Color(0xFF2196F3)
val ReclineColor = Color(0xFFFF9800)

// Ordered 0xRRGGBB palette used when auto-assigning colours to task types.
val taskTypeColorPalette = listOf(
    0x4CAF50,  // green
    0x2196F3,  // blue
    0xFF9800,  // orange
    0x9C27B0,  // purple
    0xF44336,  // red
    0x00BCD4,  // cyan
    0x795548,  // brown
    0x607D8B,  // blue-grey
    0xE91E63,  // pink
    0x3F51B5,  // indigo
    0x009688,  // teal
    0xFFC107,  // amber
    0xFF5722,  // deep orange
    0x673AB7,  // deep purple
    0x000000,  // black
)

/** Pick a colour for a new task type given how many types already exist. */
fun pickTaskTypeColorRgb(existingCount: Int): Int =
    taskTypeColorPalette[existingCount % taskTypeColorPalette.size]

/** Build a Compose Color from a stored 0xRRGGBB integer (alpha forced to 0xFF). */
fun postureColorFromRgb(rgb: Int): Color = Color(0xFF000000L or rgb.toLong())

/** Name-based fallback — still used for placed Task pills which store the type name string. */
fun postureColor(typeName: String): Color = when (typeName) {
    "Stand" -> StandColor
    "Sit" -> SitColor
    "Recline" -> ReclineColor
    else -> postureColorFromRgb(
        taskTypeColorPalette[(typeName.hashCode().toLong() and 0x7FFFFFFF).toInt() % taskTypeColorPalette.size]
    )
}

fun postureLabel(typeName: String): String = typeName
