package com.example.qr
import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class WiFiScanner(private val context: Context) {

    private val wifiManager: WifiManager by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val wifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Обрабатываем результаты сканирования
            val results: List<ScanResult> = wifiManager.scanResults
            for (result in results) {
                Log.d("WiFiScanner", "Found WiFi network: ${result.SSID}, Signal strength: ${result.level}")
            }
            stopScan() // Отключаем приемник после получения результатов
        }
    }

    fun startScan() {
        // Проверяем разрешения
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        // Включаем WiFi, если он выключен
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
            Log.d("WiFiScanner", "WiFi was disabled.")
        }

        // Региструем приемник для получения результатов сканирования
        context.registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        // Начинаем сканирование
        if (wifiManager.startScan()) {
            Log.d("WiFiScanner", "Scanning for WiFi networks...")
        } else {
            Log.d("WiFiScanner", "Failed to start scan.")
        }
    }

    fun stopScan() {
        try {
            context.unregisterReceiver(wifiReceiver)
            Log.d("WiFiScanner", "Receiver unregistered successfully.")
        } catch (e: IllegalArgumentException) {
            Log.d("WiFiScanner", "Receiver was not registered.")
        }
    }
}