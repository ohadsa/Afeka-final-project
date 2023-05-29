package com.example.final_project_afeka.services.objects

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.final_project_afeka.utils.SharedPreferenceUtil

class BumpDetector(val context: Context, private val onBumpDetected: OnBumpDetect) :
    SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastUpdateTime: Long = 0

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle accuracy changes if needed
    }

    fun stopListening() {

        sensorManager.unregisterListener(this)

    }


    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val shakeThreshold = SharedPreferenceUtil.readSensitivity().value
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastUpdateTime > 100) {
                    lastUpdateTime = currentTime

                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val acceleration =
                        Math.sqrt((y * y  ).toDouble()) - SensorManager.GRAVITY_EARTH
                    if (acceleration > shakeThreshold) {
                        println("sense: ${SharedPreferenceUtil.readSensitivity().value}")
                        println("acceleration: $acceleration")
                        onBumpDetected.invoke(
                            onBumpDetected.getLocation().first,
                            onBumpDetected.getLocation().second
                        )
                    }
                }
            }
        }
    }

}

enum class Sensitivity (val value: Float, val text: String ){
    VerySensitive(1f, "Very sensative"), Sensitive(6f, "Sensative"), Normal(9f, "Normal"), Insensitive(14f , "Insensitive"), VeryInsensitive(21f,"Very Insensitive")
}



interface OnBumpDetect {
    operator fun invoke(latitude: Double, longitude: Double)
    fun getLocation(): Pair<Double, Double>
}