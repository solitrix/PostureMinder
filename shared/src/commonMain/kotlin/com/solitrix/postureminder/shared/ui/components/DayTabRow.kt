package com.solitrix.postureminder.shared.ui.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.solitrix.postureminder.shared.util.localizedName
import kotlinx.datetime.DayOfWeek

private val WORK_WEEK = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
)

@Composable
fun DayTabRow(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    TabRow(selectedTabIndex = selectedDay) {
        WORK_WEEK.forEachIndexed { index, day ->
            Tab(
                selected = selectedDay == index,
                onClick = { onDaySelected(index) },
                text = { Text(day.localizedName(short = true), fontSize = 13.sp) }
            )
        }
    }
}
