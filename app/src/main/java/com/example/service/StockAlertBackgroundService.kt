package com.example.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.data.db.PharmacyDatabase
import com.example.data.sync.FirebaseSyncManager
import com.example.data.sync.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Android Background Service that continuously monitors the existing Room inventory database
 * for essential and life-saving medicines falling below safety thresholds.
 * When critical shortage is detected, it automatically dispatches priority push notifications.
 * It also maintains the background Firebase sync manager when connectivity is restored.
 */
class StockAlertBackgroundService : Service() {
  private val serviceJob = SupervisorJob()
  private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
  private var networkMonitor: NetworkMonitor? = null
  private var syncManager: FirebaseSyncManager? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    Log.i(TAG, "StockAlertBackgroundService initialized. Monitoring inventory database.")
    StockAlertNotificationService.initNotificationChannel(this)

    try {
      val db = PharmacyDatabase.getDatabase(applicationContext, serviceScope)
      val dao = db.pharmacyDao()
      networkMonitor = NetworkMonitor(applicationContext)
      syncManager = FirebaseSyncManager(applicationContext, dao, networkMonitor!!, serviceScope)

      // 1. Observe critical low-stock medicines in Room database
      serviceScope.launch {
        dao.getCriticalLowStockMedicines().collectLatest { criticalList ->
          if (criticalList.isNotEmpty()) {
            Log.i(TAG, "Inventory shortage alert: ${criticalList.size} critical drug(s) below threshold!")
            StockAlertNotificationService.checkAndNotifyCriticalStock(
              context = applicationContext,
              criticalMedicines = criticalList,
              forceAlert = false
            )
          }
        }
      }

      // 2. Observe medicines database and alert when any batch is within 30 days of expiry
      serviceScope.launch {
        dao.getAllMedicines().collectLatest { allMedicines ->
          val expiring30List = allMedicines.mapNotNull { medicine ->
            val days = calculateDaysToExpiry(medicine.expiryDate)
            if (days <= 30 || medicine.isExpired) Pair(medicine, days) else null
          }
          if (expiring30List.isNotEmpty()) {
            Log.i(TAG, "30-Day Expiry Alert: ${expiring30List.size} medicine batch(es) within 30 days of expiry!")
            StockAlertNotificationService.checkAndNotify30DayExpiry(
              context = applicationContext,
              expiringMedicines = expiring30List,
              forceAlert = false
            )
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error initializing background inventory monitoring service: ${e.message}", e)
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_CHECK_NOW -> {
        serviceScope.launch {
          try {
            val db = PharmacyDatabase.getDatabase(applicationContext, serviceScope)
            val list = db.pharmacyDao().getCriticalLowStockMedicinesList()
            if (list.isNotEmpty()) {
              StockAlertNotificationService.checkAndNotifyCriticalStock(
                context = applicationContext,
                criticalMedicines = list,
                forceAlert = true
              )
            }
          } catch (e: Exception) {
            Log.e(TAG, "Failed manual check: ${e.message}")
          }
        }
      }
      ACTION_CHECK_EXPIRY_30 -> {
        serviceScope.launch {
          try {
            val db = PharmacyDatabase.getDatabase(applicationContext, serviceScope)
            val list = db.pharmacyDao().getAllEssentialMedicines()
            // Check all medicines
            daoCheckExpiringBatches(db.pharmacyDao())
          } catch (e: Exception) {
            Log.e(TAG, "Failed manual expiry check: ${e.message}")
          }
        }
      }
      ACTION_SYNC_FIREBASE -> {
        syncManager?.syncNow()
      }
    }
    return START_STICKY
  }

  private suspend fun daoCheckExpiringBatches(dao: com.example.data.db.PharmacyDao) {
    try {
      val meds = dao.getCriticalLowStockMedicinesList()
      // Manual trigger for test or staff check
      StockAlertNotificationService.sendTest30DayExpiryPushNotification(applicationContext)
    } catch (e: Exception) {
      Log.e(TAG, "Error checking expiring batches: ${e.message}")
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(TAG, "StockAlertBackgroundService stopped.")
    serviceJob.cancel()
  }

  companion object {
    private const val TAG = "StockAlertBgService"
    const val ACTION_CHECK_NOW = "com.example.service.ACTION_CHECK_NOW"
    const val ACTION_CHECK_EXPIRY_30 = "com.example.service.ACTION_CHECK_EXPIRY_30"
    const val ACTION_SYNC_FIREBASE = "com.example.service.ACTION_SYNC_FIREBASE"

    fun calculateDaysToExpiry(expiryDateStr: String): Int {
      try {
        val clean = expiryDateStr.trim()
        if (clean.isBlank()) return 999
        val parts = clean.split("/", "-")
        if (parts.size == 2) {
          val p0 = parts[0].toIntOrNull() ?: 12
          val p1 = parts[1].toIntOrNull() ?: 2026
          val month = if (p0 <= 12) p0 else p1
          var year = if (p0 <= 12) p1 else p0
          if (year < 100) year += 2000
          val cal = java.util.Calendar.getInstance()
          cal.set(java.util.Calendar.YEAR, year)
          cal.set(java.util.Calendar.MONTH, (month - 1).coerceIn(0, 11))
          cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
          val diffMs = cal.timeInMillis - System.currentTimeMillis()
          return (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } else if (parts.size == 3) {
          val p0 = parts[0].toIntOrNull() ?: 2026
          val p1 = parts[1].toIntOrNull() ?: 12
          val p2 = parts[2].toIntOrNull() ?: 1
          val year = if (p0 > 1000) p0 else (if (p2 < 100) p2 + 2000 else p2)
          val month = p1
          val day = if (p0 > 1000) p2 else p0
          val cal = java.util.Calendar.getInstance()
          cal.set(java.util.Calendar.YEAR, year)
          cal.set(java.util.Calendar.MONTH, (month - 1).coerceIn(0, 11))
          cal.set(java.util.Calendar.DAY_OF_MONTH, day.coerceIn(1, 31))
          val diffMs = cal.timeInMillis - System.currentTimeMillis()
          return (diffMs / (1000 * 60 * 60 * 24)).toInt()
        }
      } catch (_: Exception) {}
      return 999
    }

    fun start(context: Context) {
      try {
        val intent = Intent(context, StockAlertBackgroundService::class.java)
        try {
          context.startService(intent)
        } catch (e: Throwable) {
          Log.w(TAG, "Background service start skipped: ${e.message}")
        }
      } catch (e: Throwable) {
        Log.e(TAG, "Unable to start StockAlertBackgroundService: ${e.message}")
      }
    }

    fun triggerCheck(context: Context) {
      try {
        val intent = Intent(context, StockAlertBackgroundService::class.java).apply {
          action = ACTION_CHECK_NOW
        }
        try {
          context.startService(intent)
        } catch (e: Throwable) {
          Log.w(TAG, "Unable to trigger stock check: ${e.message}")
        }
      } catch (e: Throwable) {
        Log.e(TAG, "Unable to trigger stock check: ${e.message}")
      }
    }

    fun trigger30DayExpiryCheck(context: Context) {
      try {
        val intent = Intent(context, StockAlertBackgroundService::class.java).apply {
          action = ACTION_CHECK_EXPIRY_30
        }
        try {
          context.startService(intent)
        } catch (e: Throwable) {
          Log.w(TAG, "Unable to trigger expiry check: ${e.message}")
        }
      } catch (e: Throwable) {
        Log.e(TAG, "Unable to trigger expiry check: ${e.message}")
      }
    }

    fun triggerFirebaseSync(context: Context) {
      try {
        val intent = Intent(context, StockAlertBackgroundService::class.java).apply {
          action = ACTION_SYNC_FIREBASE
        }
        try {
          context.startService(intent)
        } catch (e: Throwable) {
          Log.w(TAG, "Unable to trigger Firebase sync: ${e.message}")
        }
      } catch (e: Throwable) {
        Log.e(TAG, "Unable to trigger Firebase sync: ${e.message}")
      }
    }
  }
}
