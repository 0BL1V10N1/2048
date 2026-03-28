package dev.game2048.app.ui.modifiers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import dev.game2048.app.domain.model.Direction

fun Modifier.onSensorTilt(
    enabled: Boolean = true,
    threshold: Float = 4.0f,
    onMoveDetected: (Direction) -> Unit
): Modifier = composed {
    if (!enabled) return@composed this

    val context = LocalContext.current
    var lastMoveTime by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val now = System.currentTimeMillis()
                if (now - lastMoveTime < 500) return // prevent multiple movements

                val x = event.values[0]
                val y = event.values[1]

                val direction = when {
                    x < -threshold -> Direction.RIGHT
                    x > threshold -> Direction.LEFT
                    y < -threshold -> Direction.UP
                    y > threshold -> Direction.DOWN
                    else -> null
                }

                direction?.let {
                    onMoveDetected(it)
                    lastMoveTime = now
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    this
}
