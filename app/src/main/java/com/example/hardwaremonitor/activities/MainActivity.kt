package com.example.hardwaremonitor.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hardwaremonitor.objects.TemperatureStore
import com.example.hardwaremonitor.ui.theme.HardwareMonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HardwareMonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    TemperatureDisplay()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
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

    Text("Ambient: $ambientTemp °C\nBattery: $batteryTemp °C\nCPU: $cpuTemp °C", modifier = Modifier.padding(20.dp).offset(y = 20.dp))
}