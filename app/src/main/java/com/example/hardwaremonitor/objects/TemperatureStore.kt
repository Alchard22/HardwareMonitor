package com.example.hardwaremonitor.objects

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object TemperatureStore {
    val temperature: MutableState<Float?> = mutableStateOf(null)
    val cpuTemperature: MutableState<Float?> = mutableStateOf(null)
    val batteryTemperature: MutableState<Float?> = mutableStateOf(null)
}