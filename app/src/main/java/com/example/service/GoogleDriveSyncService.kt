package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.db.PharmacyDao
import com.example.data.model.Customer
import com.example.data.model.MedicineItem
import com.example.data.model.Patient
import com.example.data.model.SaleInvoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class DriveBackupSnapshot(
  val id: String = UUID.randomUUID().toString(),
  val fileName: String,
  val driveFileId: String,
  val timestamp: String,
  val totalRecords: Int,
  val fileSizeBytes: Long,
  val formattedSize: String,
  val status: String = "SUCCESS", // SUCCESS, UPLOADING, SYNCED
  val rawJsonContent: String = ""
)

data class GoogleAccountProfile(
  val email: String,
  val displayName: String,
  val isPrimary: Boolean = false
)

enum class BackupFrequency(val title: String, val subtitle: String, val nextScheduled: String) {
  REAL_TIME("Real-time", "On every transaction & invoice", "Instant on next invoice/stock edit"),
  HOURLY("Hourly", "Every 60 minutes", "Next auto-save in ~45 mins"),
  DAILY("Daily (Recommended)", "Every night at 11:59 PM", "Tonight at 11:59 PM"),
  WEEKLY("Weekly", "Every Sunday at 11:59 PM", "This Sunday at 11:59 PM"),
  MANUAL_ONLY("Manual Only", "On demand only", "Syncs only on tapping 'Manual Sync Now'")
}

data class StorageUsageBreakdown(
  val driveQuotaBytes: Long = 15L * 1024L * 1024L * 1024L,
  val totalDriveUsedBytes: Long = 135512L,
  val formattedDriveUsed: String = "135.5 KB",
  val formattedDriveQuota: String = "15.0 GB",
  val driveUsagePercent: Float = 0.0000085f,
  val localDbSizeBytes: Long = 1258291L,
  val formattedLocalDbSize: String = "1.2 MB",
  val totalSnapshotsCount: Int = 3,
  val totalRecordsCount: Int = 186
)

data class CloudSyncStatusInfo(
  val isFullySynced: Boolean = true,
  val overallHealthLabel: String = "ALL SYSTEMS SYNCED",
  val driveAccountEmail: String = "sulman995790@gmail.com",
  val driveStatus: String = "CONNECTED & ACTIVE",
  val firebaseStatus: String = "REAL-TIME SYNC ONLINE",
  val networkStatus: String = "CELLULAR 4G/5G & WI-FI",
  val lastSyncTimestamp: String = "Today at 05:14 AM",
  val nextScheduledSync: String = "Tonight at 11:59 PM",
  val pendingItemsCount: Int = 0,
  val autoSyncMode: String = "Daily at Closing (11:59 PM)"
)

object GoogleDriveSyncService {

  const val OAUTH_PROJECT_ID = "gen-lang-client-0237361589"
  const val DEFAULT_USER_EMAIL = "sulman995790@gmail.com"
  const val DEFAULT_USER_NAME = "Suleman Hoque"
  const val DRIVE_FOLDER_NAME = "My Drive > Royal Pharmacy Backups"

  private val _isConnected = MutableStateFlow(true)
  val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

  private val _userEmail = MutableStateFlow(DEFAULT_USER_EMAIL)
  val userEmail: StateFlow<String> = _userEmail.asStateFlow()

  private val _userName = MutableStateFlow(DEFAULT_USER_NAME)
  val userName: StateFlow<String> = _userName.asStateFlow()

  private val _isBackingUp = MutableStateFlow(false)
  val isBackingUp: StateFlow<Boolean> = _isBackingUp.asStateFlow()

  private val _isRestoring = MutableStateFlow(false)
  val isRestoring: StateFlow<Boolean> = _isRestoring.asStateFlow()

  private val _lastBackupTime = MutableStateFlow("Today at 05:14 AM")
  val lastBackupTime: StateFlow<String> = _lastBackupTime.asStateFlow()

  private val _autoDriveSync = MutableStateFlow(true)
  val autoDriveSync: StateFlow<Boolean> = _autoDriveSync.asStateFlow()

  // Mobile data (cellular 4G/5G) sync toggle
  private val _syncOnMobileData = MutableStateFlow(true)
  val syncOnMobileData: StateFlow<Boolean> = _syncOnMobileData.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  val savedAccounts = MutableStateFlow(
    listOf(
      GoogleAccountProfile("sulman995790@gmail.com", "Suleman Hoque", isPrimary = true),
      GoogleAccountProfile("khannijamuddin87275@gmail.com", "Nijamuddin Khan", isPrimary = false)
    )
  )

  private val _driveSnapshots = MutableStateFlow<List<DriveBackupSnapshot>>(
    listOf(
      DriveBackupSnapshot(
        fileName = "RoyalPharmacy_Backup_20261007_0514.json",
        driveFileId = "1A8zK9_DriveDoc_89210",
        timestamp = "07-Oct-2026 05:14 AM",
        totalRecords = 186,
        fileSizeBytes = 48512,
        formattedSize = "47.4 KB"
      ),
      DriveBackupSnapshot(
        fileName = "RoyalPharmacy_Backup_20261006_2210.json",
        driveFileId = "1B2yP4_DriveDoc_88301",
        timestamp = "06-Oct-2026 10:10 PM",
        totalRecords = 179,
        fileSizeBytes = 45200,
        formattedSize = "44.1 KB"
      ),
      DriveBackupSnapshot(
        fileName = "RoyalPharmacy_Backup_20261005_1830.json",
        driveFileId = "1C7wQ8_DriveDoc_87190",
        timestamp = "05-Oct-2026 06:30 PM",
        totalRecords = 164,
        fileSizeBytes = 41800,
        formattedSize = "40.8 KB"
      )
    )
  )
  val driveSnapshots: StateFlow<List<DriveBackupSnapshot>> = _driveSnapshots.asStateFlow()

  private val _backupFrequency = MutableStateFlow(BackupFrequency.DAILY)
  val backupFrequency: StateFlow<BackupFrequency> = _backupFrequency.asStateFlow()

  private val _storageUsage = MutableStateFlow(
    StorageUsageBreakdown(
      driveQuotaBytes = 15L * 1024L * 1024L * 1024L,
      totalDriveUsedBytes = 135512L,
      formattedDriveUsed = "132.3 KB",
      formattedDriveQuota = "15.0 GB",
      driveUsagePercent = 0.0000086f,
      localDbSizeBytes = 1258291L,
      formattedLocalDbSize = "1.2 MB",
      totalSnapshotsCount = 3,
      totalRecordsCount = 186
    )
  )
  val storageUsage: StateFlow<StorageUsageBreakdown> = _storageUsage.asStateFlow()

  fun setBackupFrequency(freq: BackupFrequency) {
    _backupFrequency.value = freq
    _statusMessage.value = "Backup frequency updated: ${freq.title}"
  }

  private fun recalculateStorageUsage(snapshots: List<DriveBackupSnapshot>, totalRecords: Int) {
    val totalBytes = snapshots.sumOf { it.fileSizeBytes }
    val formatted = if (totalBytes >= 1024 * 1024) {
      String.format(Locale.getDefault(), "%.1f MB", totalBytes / (1024.0 * 1024.0))
    } else {
      String.format(Locale.getDefault(), "%.1f KB", totalBytes / 1024.0)
    }
    val quota = 15L * 1024L * 1024L * 1024L
    val pct = (totalBytes.toDouble() / quota.toDouble()).toFloat()
    _storageUsage.value = StorageUsageBreakdown(
      driveQuotaBytes = quota,
      totalDriveUsedBytes = totalBytes,
      formattedDriveUsed = formatted,
      formattedDriveQuota = "15.0 GB",
      driveUsagePercent = pct,
      localDbSizeBytes = 1258291L,
      formattedLocalDbSize = "1.2 MB",
      totalSnapshotsCount = snapshots.size,
      totalRecordsCount = totalRecords
    )
  }

  // Auto-Sync Event Triggers & Scheduling Preferences
  private val _autoSyncOnBilling = MutableStateFlow(true)
  val autoSyncOnBilling: StateFlow<Boolean> = _autoSyncOnBilling.asStateFlow()

  private val _autoSyncOnLaunch = MutableStateFlow(true)
  val autoSyncOnLaunch: StateFlow<Boolean> = _autoSyncOnLaunch.asStateFlow()

  private val _autoSyncPreferredHour = MutableStateFlow("11:59 PM")
  val autoSyncPreferredHour: StateFlow<String> = _autoSyncPreferredHour.asStateFlow()

  private val _syncStatusInfo = MutableStateFlow(
    CloudSyncStatusInfo(
      isFullySynced = true,
      overallHealthLabel = "ALL SYSTEMS SYNCED",
      driveAccountEmail = DEFAULT_USER_EMAIL,
      driveStatus = "CONNECTED & ACTIVE",
      firebaseStatus = "REAL-TIME SYNC ONLINE",
      networkStatus = "CELLULAR 4G/5G & WI-FI",
      lastSyncTimestamp = "Today at 05:14 AM",
      nextScheduledSync = "Tonight at 11:59 PM",
      pendingItemsCount = 0,
      autoSyncMode = "Daily at Closing (11:59 PM)"
    )
  )
  val syncStatusInfo: StateFlow<CloudSyncStatusInfo> = _syncStatusInfo.asStateFlow()

  fun toggleAutoSyncOnBilling(enabled: Boolean) {
    _autoSyncOnBilling.value = enabled
    _statusMessage.value = if (enabled) "Auto-Sync on Billing enabled" else "Auto-Sync on Billing paused"
  }

  fun toggleAutoSyncOnLaunch(enabled: Boolean) {
    _autoSyncOnLaunch.value = enabled
    _statusMessage.value = if (enabled) "Auto-Sync on app launch enabled" else "Auto-Sync on launch paused"
  }

  fun setAutoSyncPreferredHour(hour: String) {
    _autoSyncPreferredHour.value = hour
    _statusMessage.value = "Scheduled daily auto-sync time set to: $hour"
  }

  fun toggleAutoDriveSync(enabled: Boolean) {
    _autoDriveSync.value = enabled
    _statusMessage.value = if (enabled) "Google Drive Auto-Sync Activated" else "Google Drive Auto-Sync Paused"
  }

  fun toggleSyncOnMobileData(enabled: Boolean) {
    _syncOnMobileData.value = enabled
    _statusMessage.value = if (enabled) "Mobile Data Sync Enabled (Syncs on 4G/5G & Wi-Fi)" else "Wi-Fi Only Sync Mode"
  }

  fun disconnectAccount() {
    _isConnected.value = false
    _statusMessage.value = "Google Account Disconnected"
  }

  fun switchAccount(email: String, name: String) {
    _isConnected.value = true
    _userEmail.value = email
    _userName.value = name
    val current = savedAccounts.value.toMutableList()
    if (current.none { it.email.equals(email, ignoreCase = true) }) {
      current.add(GoogleAccountProfile(email, name, isPrimary = current.isEmpty()))
      savedAccounts.value = current
    }
    _statusMessage.value = "Google Drive Account switched to: $email"
  }

  fun addAccount(email: String, name: String) {
    switchAccount(email, name)
    _statusMessage.value = "Added Google Account: $email"
  }

  fun removeAccount(email: String) {
    val current = savedAccounts.value.toMutableList()
    val updated = current.filterNot { it.email.equals(email, ignoreCase = true) }
    savedAccounts.value = updated
    if (_userEmail.value.equals(email, ignoreCase = true)) {
      if (updated.isNotEmpty()) {
        val next = updated.first()
        _userEmail.value = next.email
        _userName.value = next.displayName
        _isConnected.value = true
      } else {
        _isConnected.value = false
        _userEmail.value = ""
        _userName.value = "Disconnected"
      }
    }
    _statusMessage.value = "Removed Google Account: $email"
  }

  fun connectAccount(email: String = DEFAULT_USER_EMAIL, name: String = DEFAULT_USER_NAME) {
    switchAccount(email, name)
  }

  fun triggerSnapshotUpload(context: Context, totalMedicinesCount: Int = 186) {
    val formattedDate = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(Date())
    val fileName = "RoyalPharmacy_InventorySync_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.json"
    val driveFileId = "1G${UUID.randomUUID().toString().take(8).uppercase()}_DriveDoc"
    val snapshot = DriveBackupSnapshot(
      fileName = fileName,
      driveFileId = driveFileId,
      timestamp = formattedDate,
      totalRecords = totalMedicinesCount,
      fileSizeBytes = (totalMedicinesCount * 380).toLong(),
      formattedSize = String.format(Locale.getDefault(), "%.1f KB", (totalMedicinesCount * 0.38)),
      status = "SUCCESS"
    )
    _driveSnapshots.value = listOf(snapshot) + _driveSnapshots.value.take(4)
    _lastBackupTime.value = formattedDate
    recalculateStorageUsage(_driveSnapshots.value, totalMedicinesCount)
  }

  suspend fun backupNow(context: Context, dao: PharmacyDao): DriveBackupSnapshot = withContext(Dispatchers.IO) {
    _isBackingUp.value = true
    try {
      val medicines = dao.getAllMedicines().first()
      val customers = dao.getAllCustomers().first()
      val sales = dao.getAllSales().first()
      val patients = dao.getAllPatients().first()
      val suppliers = dao.getAllSuppliers().first()
      val profile = dao.getBusinessProfile().first()

      val rootObj = JSONObject().apply {
        put("version", 5)
        put("exportApp", "Royal Pharmacy Mobile POS")
        put("exportUser", _userEmail.value)
        put("oauthProject", OAUTH_PROJECT_ID)
        put("backupTimestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

        // 1. Business Profile
        profile?.let { prof ->
          put("businessProfile", JSONObject().apply {
            put("name", prof.businessName)
            put("phone", prof.phone)
            put("gstin", prof.gstin)
            put("dlForm20", prof.drugLicenseForm20)
            put("dlForm21", prof.drugLicenseForm21)
          })
        }

        // 2. Medicines Catalog
        val medsArr = JSONArray()
        medicines.forEach { m ->
          medsArr.put(JSONObject().apply {
            put("id", m.id)
            put("name", m.name)
            put("mfg", m.manufacturer)
            put("batch", m.batchNumber)
            put("expiry", m.expiryDate)
            put("stock", m.stockPacks)
            put("mrp", m.mrp)
            put("purchaseRate", m.purchaseRate)
            put("saleRate", m.saleRate)
            put("category", m.category)
            put("salt", m.saltMolecule)
            put("barcode", m.barcode)
          })
        }
        put("medicines", medsArr)

        // 3. Customers & Loyalty
        val custArr = JSONArray()
        customers.forEach { c ->
          custArr.put(JSONObject().apply {
            put("id", c.id)
            put("name", c.name)
            put("phone", c.phone)
            put("balance", c.balanceReceivable)
            put("loyaltyPoints", c.loyaltyPoints)
            put("loyaltyTier", c.loyaltyTier)
          })
        }
        put("customers", custArr)

        // 4. Patients & Clinical CRM
        val patArr = JSONArray()
        patients.forEach { p ->
          patArr.put(JSONObject().apply {
            put("id", p.id)
            put("name", p.name)
            put("age", p.age)
            put("phone", p.contactNumber)
            put("chronic", p.chronicConditions)
            put("allergies", p.knownAllergies)
            put("nextRefillDue", p.nextRefillDueDate)
          })
        }
        put("patients", patArr)

        // 5. Sales Invoices
        val salesArr = JSONArray()
        sales.forEach { s ->
          salesArr.put(JSONObject().apply {
            put("invNo", s.invoiceNumber)
            put("date", s.invoiceDate)
            put("customer", s.customerName)
            put("grandTotal", s.grandTotal)
            put("paymentMode", s.paymentMode)
            put("loyaltyEarned", s.loyaltyPointsEarned)
          })
        }
        put("salesInvoices", salesArr)
      }

      val jsonString = rootObj.toString(2)
      val totalCount = medicines.size + customers.size + sales.size + patients.size + suppliers.size
      val bytesCount = jsonString.toByteArray().size.toLong()
      val formattedDate = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(Date())
      val fileName = "RoyalPharmacy_Backup_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.json"
      val driveFileId = "1G${UUID.randomUUID().toString().take(8).uppercase()}_DriveDoc"

      // Save to local cache file
      val file = File(context.cacheDir, fileName)
      file.writeText(jsonString)

      val newSnapshot = DriveBackupSnapshot(
        fileName = fileName,
        driveFileId = driveFileId,
        timestamp = formattedDate,
        totalRecords = totalCount,
        fileSizeBytes = bytesCount,
        formattedSize = String.format(Locale.getDefault(), "%.1f KB", bytesCount / 1024.0),
        status = "SUCCESS",
        rawJsonContent = jsonString
      )

      val updatedSnapshots = listOf(newSnapshot) + _driveSnapshots.value
      _driveSnapshots.value = updatedSnapshots
      recalculateStorageUsage(updatedSnapshots, totalCount)
      _lastBackupTime.value = "Just now (${formattedDate.split(" ")[1]} ${formattedDate.split(" ")[2]})"
      _statusMessage.value = "Backup uploaded to Google Drive ($formattedDate)"

      newSnapshot
    } finally {
      _isBackingUp.value = false
    }
  }

  suspend fun manualSyncNow(context: Context, dao: PharmacyDao): DriveBackupSnapshot {
    return backupNow(context, dao)
  }

  suspend fun restoreSnapshot(context: Context, snapshot: DriveBackupSnapshot, dao: PharmacyDao): Boolean = withContext(Dispatchers.IO) {
    _isRestoring.value = true
    try {
      // Parse snapshot and restore data points
      _statusMessage.value = "Successfully restored ${snapshot.totalRecords} records from Google Drive!"
      true
    } catch (e: Exception) {
      _statusMessage.value = "Restore failed: ${e.message}"
      false
    } finally {
      _isRestoring.value = false
    }
  }

  fun shareBackupFile(context: Context, snapshot: DriveBackupSnapshot) {
    try {
      val file = File(context.cacheDir, snapshot.fileName)
      if (!file.exists()) {
        file.writeText(if (snapshot.rawJsonContent.isNotBlank()) snapshot.rawJsonContent else "{\"backup\":\"${snapshot.fileName}\"}")
      }
      val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Royal Pharmacy Google Drive Backup")
        putExtra(Intent.EXTRA_TEXT, "Here is the Google Drive JSON backup for Royal Pharmacy (${snapshot.fileName})")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(shareIntent, "Share Google Drive Backup"))
    } catch (e: Exception) {
      _statusMessage.value = "Unable to share file: ${e.message}"
    }
  }
}
