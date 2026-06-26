package com.solitrix.postureminder.shared.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solitrix.postureminder.shared.ui.state.LocalDragState
import com.solitrix.postureminder.shared.ui.theme.postureColor
import com.solitrix.postureminder.shared.ui.theme.postureColorFromRgb
import kotlin.math.roundToInt

@Composable
fun DragOverlay() {
    var overlayOrigin by remember { mutableStateOf(Offset.Zero) }
    val dragState = LocalDragState.current.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { overlayOrigin = it.positionInWindow() }
    ) {
        if (dragState != null) {
            Surface(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragState.currentOffset.x - dragState.anchorOffset.x - overlayOrigin.x).roundToInt(),
                            (dragState.currentOffset.y - dragState.anchorOffset.y - overlayOrigin.y).roundToInt()
                        )
                    }
                    .width(120.dp)
                    .height(PILL_HEIGHT_DP.dp),
                shape = RoundedCornerShape(percent = 50),
                color = (if (dragState.typeColorRgb != null) postureColorFromRgb(dragState.typeColorRgb)
                         else postureColor(dragState.typeName)).copy(alpha = 0.7f),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = dragState.typeName,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
