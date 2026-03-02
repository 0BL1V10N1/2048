package dev.game2048.app.ui.modifiers

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.game2048.app.data.models.Direction
import kotlin.math.abs

fun Modifier.onSwipe(threshold: Float = 100f, onSwipeDetected: (Direction) -> Unit): Modifier =
    this.pointerInput(Unit) {
        var totalDragX = 0f
        var totalDragY = 0f

        detectDragGestures(
            onDragStart = {
                totalDragX = 0f
                totalDragY = 0f
            },
            onDrag = { change, dragAmount ->
                change.consume()
                totalDragX += dragAmount.x
                totalDragY += dragAmount.y
            },
            onDragEnd = {
                val absX = abs(totalDragX)
                val absY = abs(totalDragY)

                if (maxOf(absX, absY) > threshold) {
                    if (absX > absY) {
                        onSwipeDetected(if (totalDragX > 0) Direction.RIGHT else Direction.LEFT)
                    } else {
                        onSwipeDetected(if (totalDragY > 0) Direction.DOWN else Direction.UP)
                    }
                }
            }
        )
    }
