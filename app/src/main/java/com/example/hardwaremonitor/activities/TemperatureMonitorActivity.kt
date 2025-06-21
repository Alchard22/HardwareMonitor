import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.health.connect.datatypes.units.Temperature
import android.os.BatteryManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.hardwaremonitor.objects.TemperatureStore
import java.io.File

class TemperatureMonitorActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var tempSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        setUpBatteryTemperature(this)
    }


    override fun onResume() {
        super.onResume()
        tempSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        TemperatureStore.temperature.value = event?.values?.firstOrNull()
        TemperatureStore.cpuTemperature.value = readCpuTemp() ?: TemperatureStore.cpuTemperature.value
    }

    private fun setUpBatteryTemperature(context: Context) {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val batteryTemp = intent
            ?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            ?.toFloat()?.div(10)
        TemperatureStore.batteryTemperature.value = batteryTemp
    }


    private fun readCpuTemp(): Float? {
        return try {
            val tempFile = "/sys/class/thermal/thermal_zone0/temp"
            val tempStr = File(tempFile).readText().trim()
            tempStr.toFloat() / 1000f
        } catch (e: Exception) {
            null
        }
    }
}

