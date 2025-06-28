import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hardwaremonitor.objects.TemperatureStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.sql.Time
import java.util.Timer

class TemperatureMonitor(private val context: Context) {

    private var monitoringJob: Job? = null
    private val checkIntervalMs = 1000L // You can make this user-configurable
    private var lastOverheatNotificationTime = 0L
    private val notificationCooldownMillis = 60_000L

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
        val currentTime = System.currentTimeMillis()
        if ((batteryTemp ?: 0f) > 45f &&
            currentTime - lastOverheatNotificationTime > notificationCooldownMillis
        ) {
            lastOverheatNotificationTime = currentTime
            sendOverheatNotification(batteryTemp!!)
        }
    }

    private fun readCpuTemp(): Float? {
        return try {
            val file = File("/sys/class/thermal/thermal_zone0/temp")
            file.readText().trim().toFloat() / 1000f
        } catch (e: Exception) {
            null
        }
    }

    private fun sendOverheatNotification(temp: Float) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel("overheat", "Overheat Warnings", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, "overheat")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("Battery Hot")
            .setContentText("Battery: ${temp}°C.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()

        notificationManager.notify(1, notification)
    }
}

