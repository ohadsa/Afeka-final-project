package com.example.final_project_afeka.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

class BumpDetector(val context: Context, private val onBumpDetected: OnBumpDetect) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastUpdateTime: Long = 0
    private val shakeThreshold = 5f // Adjust this value based on the desired sensitivity

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {

            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastUpdateTime > 100) {
                    lastUpdateTime = currentTime

                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH

                    if (acceleration > shakeThreshold) {
                        Toast.makeText(context , "acc = $acceleration" , Toast.LENGTH_SHORT).show()
                        onBumpDetected.invoke()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle accuracy changes if needed
    }
}

interface OnBumpDetect {
    operator fun invoke()
}