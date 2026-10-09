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
            Log.w(TAG, "Inventory shortage alert: ${criticalList.size} critical drug(s) below threshold!")
            StockAlertNotificationService.checkAndNotifyCriticalStock(
              context = applicationContext,
              criticalMedicines = criticalList,
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
      ACTION_SYNC_FIREBASE -> {
        syncManager?.syncNow()
      }
    }
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(TAG, "StockAlertBackgroundService stopped.")
    serviceJob.cancel()
  }

  companion object {
    private const val TAG = "StockAlertBgService"
    const val ACTION_CHECK_NOW = "com.example.service.ACTION_CHECK_NOW"
    const val ACTION_SYNC_FIREBASE = "com.example.service.ACTION_SYNC_FIREBASE"

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
