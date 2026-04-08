package dev.game2048.app.ui.modifiers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.game2048.app.domain.model.Direction

fun Modifier.onSensorTilt(
    enabled: Boolean = true,
    threshold: Float = 4.0f,
    onMoveDetected: (Direction) -> Unit
): Modifier = composed {
    if (!enabled) return@composed this

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnMove by rememberUpdatedState(onMoveDetected)
    var lastMoveTime by remember { mutableLongStateOf(0L) }

    DisposableEffect(lifecycleOwner) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            ?: return@DisposableEffect onDispose {}

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val now = System.currentTimeMillis()
                if (now - lastMoveTime < 500) return

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
                    currentOnMove(it)
                    lastMoveTime = now
                }
            }

            @Suppress("EmptyFunctionBlock")
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME ->
                    sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
                Lifecycle.Event.ON_PAUSE ->
                    sensorManager.unregisterListener(listener)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    this
}
