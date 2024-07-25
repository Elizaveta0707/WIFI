package com.example.qr
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.ScanResult
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
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
    }
}