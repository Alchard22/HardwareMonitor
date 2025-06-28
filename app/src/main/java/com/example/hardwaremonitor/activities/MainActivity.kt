package com.example.hardwaremonitor.activities

import TemperatureMonitor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hardwaremonitor.objects.TemperatureStore
import com.example.hardwaremonitor.ui.theme.HardwareMonitorTheme

class MainActivity : ComponentActivity() {
    private var temperatureMonitor: TemperatureMonitor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        temperatureMonitor = TemperatureMonitor(this)

        enableEdgeToEdge()
        setContent {
            HardwareMonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "",
                        modifier = Modifier.padding(innerPadding)
                    )
                    TemperatureDisplay()
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        temperatureMonitor?.start()
    }

    override fun onPause() {
        super.onPause()
        temperatureMonitor?.stop()
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HardwareMonitorTheme {
        Greeting("Android")
    }
}
@Composable
fun TemperatureDisplay() {
    val ambientTemp by TemperatureStore.temperature
    val batteryTemp by TemperatureStore.batteryTemperature
    val cpuTemp by TemperatureStore.cpuTemperature

    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .offset(y = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Temperature Readings", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
//            TemperatureGauge(ambientTemp, hotThreshold = 35f, maxTemp = 60f)
            TemperatureGauge("Battery Temperature",batteryTemp, hotThreshold = 45f, maxTemp = 60f)
            TemperatureGauge("CPU Temperature", cpuTemp, hotThreshold = 60f, maxTemp = 100f)
        }
    }
}


@Composable
fun TemperatureGauge(
    title: String,
    temperature: Float?,
    hotThreshold: Float = 40f,
    maxTemp: Float = 80f,
    modifier: Modifier = Modifier
) {
    val minTemp = 15f
    val displayTemp = temperature ?: 0f
    val progress = ((displayTemp - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f)

    val gaugeColor = lerp(
        start = Color.Blue,
        stop = Color.Red,
        fraction = ((displayTemp - minTemp) / (hotThreshold - minTemp)).coerceIn(0f, 1f)
    )
    Column {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Justify)
        Box(
            modifier = modifier
                .size(150.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = gaugeColor,
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )
            }
            Text(
                text = if (temperature != null) "${displayTemp}°C" else "--",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}
