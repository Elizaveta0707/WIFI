package com.example.qr
import android.content.Context
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.ScanResult
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity() {

    private companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var scanner: GSMScanner
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanner = GSMScanner(this)
        resultTextView = findViewById(R.id.scanResultsTextView)

        val scanButton: Button = findViewById(R.id.startScanButton)
        scanButton.setOnClickListener { startScanning() }
    }

    private fun startScanning() {
        Log.d("MainActivity", "Start scanning button clicked.")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            scanGsmCells()
        }
    }

    private fun scanGsmCells() {
        scanner.scanCells { result ->
            runOnUiThread {
                resultTextView.text = result
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanGsmCells()
            } else {
                Log.d("MainActivity", "Location permission denied.")
                resultTextView.text = "Location permission denied."
            }
        }
    }
    /*
    FireBase
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var featureStatusTextView: TextView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация Firebase
        FirebaseApp.initializeApp(this)

        // Получаем экземпляр FirebaseRemoteConfig
        remoteConfig = FirebaseRemoteConfig.getInstance()

        // Связываем элементы интерфейса
        featureStatusTextView = findViewById(R.id.featureStatusTextView)
        refreshButton = findViewById(R.id.refreshButton)

        // Устанавливаем настройки Remote Config
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(5) //  на 5 секунд для тестирования
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Убираем установку значений по умолчанию

        // Получаем параметры при запуске
        fetchAndActivateConfig()

        // Обработчик нажатия на кнопку
        refreshButton.setOnClickListener {
            fetchAndActivateConfig()
        }
    }

    private fun fetchAndActivateConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i("MainActivity", "Fetch successful, last flag: ${task.result}")
                    updateFeatureStatus()
                } else {
                    Log.e("MainActivity", "Fetch failed: ${task.exception?.message}")
                }
            }
    }

    private fun updateFeatureStatus() {
        val featureToggle = remoteConfig.getBoolean("flag")
        Log.i("MainActivity", "Flag  value: $featureToggle")

        // Обновляем текст в TextView
        featureStatusTextView.text = if (featureToggle) {
            Log.i("MainActivity", "Flag true!")
            "Flag true!"
        } else {
            Log.i("MainActivity", "Flag false!")
            "Flag false!"
        }
    }
*/

    /*
    WIFIScanner
    private lateinit var wifiScanner: WiFiScanner
    private lateinit var scanResultsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiScanner = WiFiScanner(this)

        scanResultsTextView = findViewById(R.id.scanResultsTextView)

        val startScanButton: Button = findViewById(R.id.startScanButton)

        startScanButton.setOnClickListener {
            if (checkPermissions()) {
                wifiScanner.startScan()
            } else {
                requestPermissions()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                wifiScanner.startScan()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun displayScanResults(scannedResults: List<ScanResult>) {
        val results = scannedResults.joinToString(separator = "\n") { "SSID: ${it.SSID}, Signal strength: ${it.level}" }
        scanResultsTextView.text = results
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
    }*/
}