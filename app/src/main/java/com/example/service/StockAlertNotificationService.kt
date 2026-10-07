package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.model.MedicineItem

object StockAlertNotificationService {
  const val CHANNEL_ID = "essential_medicine_alerts"
  const val CHANNEL_NAME = "Essential & Life-Saving Stock Alerts"
  const val CHANNEL_DESC = "Push notifications when essential or life-saving medicines fall below safety threshold"

  const val CHANNEL_ID_EXPIRY = "short_expiry_alerts"
  const val CHANNEL_NAME_EXPIRY = "Short Expiry Alerts (60 Days)"
  const val CHANNEL_DESC_EXPIRY = "Push notifications when medicines are within 60 days of expiry date"

  // Action intents for direct navigation
  const val ACTION_VIEW_STOCK = "com.example.ACTION_VIEW_STOCK"
  const val ACTION_CREATE_PO = "com.example.ACTION_CREATE_PO"
  const val ACTION_ADD_TO_CART = "com.example.ACTION_ADD_TO_CART"
  const val EXTRA_TARGET_SCREEN = "extra_target_screen"
  const val EXTRA_MEDICINE_ID = "extra_medicine_id"

  // Cooldown tracker: Medicine ID -> timestamp of last notification
  private val lastAlertTimestamps = mutableMapOf<Long, Long>()
  private val lastExpiryAlertTimestamps = mutableMapOf<Long, Long>()
  private const val ALERT_COOLDOWN_MS = 10 * 60 * 1000L // 10 minutes cooldown per item unless stock changed

  fun initNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
      if (existing == null) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = CHANNEL_DESC
          enableLights(true)
          lightColor = android.graphics.Color.RED
          enableVibration(true)
          vibrationPattern = longArrayOf(0, 350, 150, 350)
        }
        notificationManager.createNotificationChannel(channel)
      }

      val existingExpiry = notificationManager.getNotificationChannel(CHANNEL_ID_EXPIRY)
      if (existingExpiry == null) {
        val expiryChannel = NotificationChannel(
          CHANNEL_ID_EXPIRY,
          CHANNEL_NAME_EXPIRY,
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = CHANNEL_DESC_EXPIRY
          enableLights(true)
          lightColor = android.graphics.Color.YELLOW
          enableVibration(true)
          vibrationPattern = longArrayOf(0, 250, 150, 250)
        }
        notificationManager.createNotificationChannel(expiryChannel)
      }
    }
  }

  fun sendCriticalStockPushNotification(
    context: Context,
    medicine: MedicineItem
  ) {
    initNotificationChannel(context)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Open MainActivity and go to Suppliers or Stock
    val contentIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_SCREEN, "CRITICAL_STOCK")
      putExtra(EXTRA_MEDICINE_ID, medicine.id)
    }
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      medicine.id.toInt(),
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Action 1: Create Purchase Order (PO)
    val poIntent = Intent(context, MainActivity::class.java).apply {
      action = ACTION_CREATE_PO
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_SCREEN, "SUPPLIERS")
      putExtra(EXTRA_MEDICINE_ID, medicine.id)
    }
    val poPendingIntent = PendingIntent.getActivity(
      context,
      (medicine.id + 10000).toInt(),
      poIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Action 2: View In Stock
    val stockIntent = Intent(context, MainActivity::class.java).apply {
      action = ACTION_VIEW_STOCK
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_SCREEN, "STOCK")
      putExtra(EXTRA_MEDICINE_ID, medicine.id)
    }
    val stockPendingIntent = PendingIntent.getActivity(
      context,
      (medicine.id + 20000).toInt(),
      stockIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val drugBadge = if (medicine.isLifeSaving) "LIFE-SAVING DRUG" else "ESSENTIAL MEDICINE"

    val bigText = buildString {
      append("🚨 URGENT PHARMACY SHORTAGE ALERT\n")
      append("${medicine.name} is below defined safety threshold!\n\n")
      append("• Current Stock: ${medicine.stockPacks} pack(s) remaining\n")
      append("• Safety Minimum: ${medicine.minStockAlert} pack(s)\n")
      append("• Classification: $drugBadge\n")
      append("• Salt: ${medicine.saltMolecule.ifBlank { medicine.composition }}\n")
      append("• Manufacturer: ${medicine.manufacturer}\n")
      append("• Location: ${medicine.rackLocation}\n\n")
      append("Immediate purchase order restock recommended to avoid stockout!")
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.stat_notify_error)
      .setContentTitle("🚨 CRITICAL STOCK: ${medicine.name}")
      .setContentText("Only ${medicine.stockPacks} pack(s) left! (Safety Threshold: ${medicine.minStockAlert})")
      .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setColor(0xFFD32F2F.toInt()) // Urgent Red
      .setContentIntent(contentPendingIntent)
      .setAutoCancel(true)
      .setSound(defaultSoundUri)
      .setVibrate(longArrayOf(0, 350, 150, 350))
      .addAction(
        android.R.drawable.ic_input_add,
        "Order Now",
        poPendingIntent
      )
      .addAction(
        android.R.drawable.ic_menu_view,
        "View Stock",
        stockPendingIntent
      )
      .build()

    notificationManager.notify(medicine.id.toInt(), notification)
    lastAlertTimestamps[medicine.id] = System.currentTimeMillis()
  }

  fun sendBatchCriticalNotification(
    context: Context,
    medicines: List<MedicineItem>
  ) {
    if (medicines.isEmpty()) return
    initNotificationChannel(context)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_SCREEN, "CRITICAL_STOCK")
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      999,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val inboxStyle = NotificationCompat.InboxStyle()
      .setBigContentTitle("⚠️ ${medicines.size} Critical Drugs Below Safety Level")
      .setSummaryText("Immediate Restock Required")

    medicines.take(5).forEach { med ->
      inboxStyle.addLine("${med.name}: ${med.stockPacks} left (Min: ${med.minStockAlert})")
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.stat_notify_error)
      .setContentTitle("⚠️ ${medicines.size} Critical Drugs Below Safety Level")
      .setContentText("Immediate restock needed for ${medicines.first().name} and others")
      .setStyle(inboxStyle)
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setColor(0xFFD32F2F.toInt())
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(99999, notification)
  }

  fun sendTestPushNotification(context: Context) {
    val sample = MedicineItem(
      id = 777,
      name = "Adrenaline 1mg/ml (Test Alert)",
      manufacturer = "Emergency Care Pharma",
      composition = "Adrenaline 1mg/ml",
      saltMolecule = "Adrenaline 1mg/ml",
      category = "Injection",
      stockPacks = 1,
      minStockAlert = 5,
      isEssential = true,
      isLifeSaving = true
    )
    sendCriticalStockPushNotification(context, sample)
  }

  fun checkAndNotifyCriticalStock(
    context: Context,
    criticalMedicines: List<MedicineItem>,
    forceAlert: Boolean = false
  ) {
    val now = System.currentTimeMillis()
    criticalMedicines.forEach { medicine ->
      val lastAlert = lastAlertTimestamps[medicine.id] ?: 0L
      val shouldAlert = forceAlert || (now - lastAlert > ALERT_COOLDOWN_MS)

      if (shouldAlert) {
        sendCriticalStockPushNotification(context, medicine)
      }
    }
  }

  fun sendShortExpiryPushNotification(
    context: Context,
    medicine: MedicineItem,
    daysRemaining: Int
  ) {
    initNotificationChannel(context)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Open MainActivity and go to Reports or Cart
    val contentIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_SCREEN, "REPORTS")
      putExtra(EXTRA_MEDICINE_ID, medicine.id)
    }
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      (medicine.id + 30000).toInt(),
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Action 1: Add to Return Cart
    val cartIntent = Intent(context, MainActivity::class.java).apply {
      action = ACTION_ADD_TO_CART
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_TARGET_SCREEN, "CART")
      putExtra(EXTRA_MEDICINE_ID, medicine.id)
    }
    val cartPendingIntent = PendingIntent.getActivity(
      context,
      (medicine.id + 40000).toInt(),
      cartIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val bigText = buildString {
      append("⏳ SHORT EXPIRY RETURN NOTICE (<60 DAYS)\n")
      append("${medicine.name} expires in $daysRemaining day(s)!\n\n")
      append("• Expiry Date: ${medicine.expiryDate}\n")
      append("• Batch Number: ${medicine.batchNumber.ifBlank { "B-7740" }}\n")
      append("• Remaining Stock: ${medicine.stockPacks} pack(s)\n")
      append("• Manufacturer: ${medicine.manufacturer}\n")
      append("• Rack Location: ${medicine.rackLocation}\n\n")
      append("Action: Add to Return Cart to generate XLS/PDF return debit note for distributor.")
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID_EXPIRY)
      .setSmallIcon(android.R.drawable.stat_notify_more)
      .setContentTitle("⏳ SHORT EXPIRY (<60d): ${medicine.name}")
      .setContentText("Expires in $daysRemaining days (${medicine.expiryDate}) • ${medicine.stockPacks} packs left")
      .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setColor(0xFFF57C00.toInt()) // Warning Amber/Orange
      .setContentIntent(contentPendingIntent)
      .setAutoCancel(true)
      .addAction(
        android.R.drawable.ic_input_add,
        "Add to Return Cart",
        cartPendingIntent
      )
      .addAction(
        android.R.drawable.ic_menu_agenda,
        "View Expiry List",
        contentPendingIntent
      )
      .build()

    notificationManager.notify((medicine.id + 50000).toInt(), notification)
    lastExpiryAlertTimestamps[medicine.id] = System.currentTimeMillis()
  }

  fun sendTestExpiryPushNotification(context: Context) {
    val sample = MedicineItem(
      id = 888,
      name = "Taxim 1g Inj (Batch TX-902)",
      manufacturer = "ALKEM",
      composition = "Cefotaxime 1g",
      expiryDate = "11/26",
      batchNumber = "TX-9021",
      stockPacks = 8,
      category = "Injection"
    )
    sendShortExpiryPushNotification(context, sample, 45)
  }

  fun checkAndNotifyShortExpiry(
    context: Context,
    expiringMedicines: List<Pair<MedicineItem, Int>>,
    forceAlert: Boolean = false
  ) {
    val now = System.currentTimeMillis()
    expiringMedicines.forEach { (medicine, daysRemaining) ->
      val lastAlert = lastExpiryAlertTimestamps[medicine.id] ?: 0L
      val shouldAlert = forceAlert || (now - lastAlert > ALERT_COOLDOWN_MS)

      if (shouldAlert) {
        sendShortExpiryPushNotification(context, medicine, daysRemaining)
      }
    }
  }
}
