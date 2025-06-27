import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.util.Log
import com.example.hardwaremonitor.objects.TemperatureStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class TemperatureMonitor(private val context: Context) {

    private var monitoringJob: Job? = null
    private val checkIntervalMs = 1000L // You can make this user-configurable

    fun start() {
        monitoringJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                checkTemperatures()
                delay(checkIntervalMs)
            }
        }
    }

    fun stop() {
        monitoringJob?.cancel()
    }

    private fun checkTemperatures() {
        // Check battery temp
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val batteryTemp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)?.toFloat()?.div(10)

        TemperatureStore.batteryTemperature.value = batteryTemp

        // CPU temp
        val cpuTemp = readCpuTemp()
        TemperatureStore.cpuTemperature.value = cpuTemp

//        // Optional: Post notification if over threshold
//        if ((batteryTemp ?: 0f) > 45f) {
//            sendOverheatNotification(batteryTemp!!)
//        }
    }

    private fun readCpuTemp(): Float? {
        return try {
            val file = File("/sys/class/thermal/thermal_zone0/temp")
            file.readText().trim().toFloat() / 1000f
        } catch (e: Exception) {
            null
        }
    }

//    private fun sendOverheatNotification(temp: Float) {
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel("overheat", "Overheat Warnings", NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(context, "overheat")
//            .setSmallIcon(android.R.drawable.stat_notify_error)
//            .setContentTitle("Battery Overheating")
//            .setContentText("Battery temperature is $temp°C!")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        notificationManager.notify(1, notification)
//    }
}

