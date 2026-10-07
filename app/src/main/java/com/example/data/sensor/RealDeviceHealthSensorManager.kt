package com.example.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

class RealDeviceHealthSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val stepCounterSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private val accelerometerSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _realSteps = MutableStateFlow(0)
    val realSteps: StateFlow<Int> = _realSteps.asStateFlow()

    private var initialHardwareSteps: Float = -1f
    private var lastMagnitude: Float = 0f
    private var lastStepTimestamp: Long = 0

    private val appStartTime = System.currentTimeMillis()

    init {
        registerListeners()
    }

    private fun registerListeners() {
        if (stepDetectorSensor != null) {
            sensorManager?.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else if (stepCounterSensor != null) {
            sensorManager?.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else if (accelerometerSensor != null) {
            // Only fallback to accelerometer if no hardware step sensor is available
            sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_DETECTOR -> {
                _realSteps.value += 1
            }
            Sensor.TYPE_STEP_COUNTER -> {
                val count = event.values[0]
                if (initialHardwareSteps < 0) {
                    initialHardwareSteps = count
                }
                val delta = (count - initialHardwareSteps).toInt()
                if (delta >= 0) {
                    _realSteps.value = delta
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                // Fallback accelerometer peak detection for devices without hardware step detector
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta = magnitude - lastMagnitude
                lastMagnitude = magnitude

                val now = System.currentTimeMillis()
                if (delta > 3.5f && (now - lastStepTimestamp) > 350) {
                    lastStepTimestamp = now
                    if (stepDetectorSensor == null && stepCounterSensor == null) {
                        _realSteps.value += 1
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun getSessionScreenMinutes(): Int {
        val elapsedMillis = System.currentTimeMillis() - appStartTime
        return (elapsedMillis / 60000).toInt().coerceAtLeast(1)
    }

    fun resetStepsToZero() {
        initialHardwareSteps = -1f
        _realSteps.value = 0
    }

    fun unregister() {
        sensorManager?.unregisterListener(this)
    }
}
