package com.example.qr
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.telephony.NeighboringCellInfo
import android.util.Log
import android.os.Build
import androidx.core.app.ActivityCompat
class GSMScanner(private val context: Context) {
    private val TAG = "GSMScanner"

    fun scanCells(callback: (String) -> Unit) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Проверяем поддержку GSM
        if (telephonyManager.phoneType == TelephonyManager.PHONE_TYPE_GSM) {
            val resultBuilder = StringBuilder()

            // Проверяем разрешение
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    callback("Разрешение на доступ к местоположению не предоставлено.")
                    return
                }
            }

            // Если API уровень 29 или выше, используем allCellInfo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val cellInfoList: List<CellInfo>? = telephonyManager.allCellInfo

                // Обработка информации о клетках
                if (cellInfoList != null && cellInfoList.isNotEmpty()) {
                    for (cellInfo in cellInfoList) {
                        Log.d(TAG, "Detected CellInfo: $cellInfo")
                        val operatorCode = getOperatorCode(cellInfo)

                        // Проверяем, если операторский код скорее "00" или null
                        if (operatorCode == "00" || operatorCode == null) {
                            Log.d(TAG, "Пропускаем ячейку с кодом оператора $operatorCode")
                            continue
                        }

                        Log.d(TAG, "Operator Code: $operatorCode")
                        val operatorName = telephonyManager.networkOperatorName
                        val cellInfoString = when (cellInfo) {
                            is CellInfoGsm -> "GSM: ${cellInfo.cellIdentity.cid}, Signal: ${cellInfo.cellSignalStrength.dbm}, Operator: $operatorName (Code: $operatorCode)"
                            is CellInfoLte -> "LTE: ${cellInfo.cellIdentity.ci}, Signal: ${cellInfo.cellSignalStrength.dbm}, Operator: $operatorName (Code: $operatorCode)"
                            is CellInfoWcdma -> "WCDMA: ${cellInfo.cellIdentity.cid}, Signal: ${cellInfo.cellSignalStrength.dbm}, Operator: $operatorName (Code: $operatorCode)"
                            else -> "Unknown Cell Info"
                        }
                        resultBuilder.append("$cellInfoString\n")
                        Log.d(TAG, cellInfoString)
                    }
                } else {
                    resultBuilder.append("Нет информации о вышках.\n")
                    Log.d(TAG, "Нет информации о вышках.")
                }

            } else { // Для API уровня 28 и ниже, здесь можно использовать устаревший метод
                Log.d(TAG, "Данный метод будет использовать устаревший getNeighboringCellInfo()")
                // Здесь можно попробовать использовать getNeighboringCellInfo()
                // Это только для старых API и для примера
                // val neighboringCellInfo = telephonyManager.neighboringCellInfo  // Устаревший метод

                // Переменная для хранения информации о соседних клетках
                // if (neighboringCellInfo.isNotEmpty()) { ... }
            }

            callback(resultBuilder.toString())
        } else {
            callback("Телефон не поддерживает GSM\n")
        }
    }

    private fun getOperatorCode(cellInfo: CellInfo): String? {
        val mcc = when (cellInfo) {
            is CellInfoGsm -> cellInfo.cellIdentity.mccString
            is CellInfoLte -> cellInfo.cellIdentity.mccString
            is CellInfoWcdma -> cellInfo.cellIdentity.mccString
            else -> null
        }

        val mnc = when (cellInfo) {
            is CellInfoGsm -> cellInfo.cellIdentity.mncString
            is CellInfoLte -> cellInfo.cellIdentity.mncString
            is CellInfoWcdma -> cellInfo.cellIdentity.mncString
            else -> null
        }

        return if (!mcc.isNullOrEmpty() && !mnc.isNullOrEmpty()) {
            "$mcc$mnc"
        } else {
            null // Возвращаем null, если отсутствуют MCC или MNC
        }
    }
}