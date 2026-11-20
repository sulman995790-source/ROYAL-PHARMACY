package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.db.PharmacyDao
import com.example.data.model.MedicineItem
import com.example.data.model.SyncQueueItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class OperationType {
  CREATE, UPDATE, DELETE, LIST, GET, WRITE
}

class FirebaseSyncManager(
  private val context: Context,
  private val dao: PharmacyDao,
  private val networkMonitor: NetworkMonitor,
  private val scope: CoroutineScope
) {
  private val auth: FirebaseAuth by lazy { Firebase.auth }

  private val firestore: FirebaseFirestore? by lazy {
    try {
      val dbId = context.getString(R.string.firestore_database_id)
        .ifBlank { "ai-studio-android-royalpha-1aaf0481-70c1-4064-9bb9-5d4d765c6272" }
      Firebase.firestore(dbId)
    } catch (e: Throwable) {
      Log.e("FirebaseSyncManager", "Named Firestore initialization failed: ${e.message}", e)
      null
    }
  }

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  private val _lastSyncTimestamp = MutableStateFlow("Never")
  val lastSyncTimestamp: StateFlow<String> = _lastSyncTimestamp.asStateFlow()

  private val _syncStatusMessage = MutableStateFlow("Sign in with Google for Cloud Sync")
  val syncStatusMessage: StateFlow<String> = _syncStatusMessage.asStateFlow()

  private val _syncError = MutableStateFlow<String?>(null)
  val syncError: StateFlow<String?> = _syncError.asStateFlow()

  private val _syncHistory = MutableStateFlow<List<String>>(
    listOf("Local Room DB active: User-scoped Cloud Sync initialized")
  )
  val syncHistory: StateFlow<List<String>> = _syncHistory.asStateFlow()

  private val _autoSyncOnReconnect = MutableStateFlow(true)
  val autoSyncOnReconnect: StateFlow<Boolean> = _autoSyncOnReconnect.asStateFlow()

  private val activeListeners = mutableListOf<ListenerRegistration>()
  private var attachedUid: String? = null

  private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
    val uid = firebaseAuth.currentUser?.uid
    if (uid.isNullOrBlank()) {
      removeActiveListenersSync()
      _syncStatusMessage.value = "Local-First: Sign in with Google to sync"
    } else if (uid != attachedUid) {
      attachListenersForUser(uid)
      if (networkMonitor.isOnline.value && _autoSyncOnReconnect.value) {
        scope.launch { performSync() }
      }
    }
  }

  init {
    auth.addAuthStateListener(authStateListener)
    auth.currentUser?.uid?.let { attachListenersForUser(it) }

    scope.launch {
      var wasOnline = networkMonitor.isOnline.value
      networkMonitor.isOnline.collect { isOnline ->
        if (isOnline && !wasOnline && _autoSyncOnReconnect.value && auth.currentUser != null) {
          addLog("Network connection restored. Auto-syncing pending changes...")
          performSync()
        } else if (!isOnline) {
          addLog("Offline mode active. Changes queued in local Room DB.")
          _syncStatusMessage.value = "Offline: Saving to Room DB"
        }
        wasOnline = isOnline
      }
    }
  }

  private fun attachListenersForUser(uid: String) {
    val fs = firestore ?: return
    removeActiveListenersSync()
    attachedUid = uid
    _syncError.value = null
    _syncStatusMessage.value = "Cloud Sync Connected"

    val inventoryPath = "users/$uid/inventory"
    val invReg = fs.collection("users").document(uid).collection("inventory")
      .addSnapshotListener { snapshot, e ->
        if (e != null) {
          handleFirestoreError(e, OperationType.LIST, inventoryPath)
          return@addSnapshotListener
        }
        snapshot?.documentChanges?.forEach { change ->
          val data = change.document.data
          if (data["_lastUpdatedBy"] == "WEB_PORTAL") {
            scope.launch(Dispatchers.IO) {
              try {
                val item = mapToMedicineItem(data)
                if (item.name.isBlank()) return@launch
                val existing = dao.getMedicineByName(item.name)
                if (existing != null) {
                  val merged = existing.copy(
                    minStockAlert = (data["minStockAlert"] as? Number)?.toInt() ?: existing.minStockAlert,
                    manufacturer = (data["manufacturer"] as? String)?.takeIf { it.isNotBlank() } ?: existing.manufacturer,
                    composition = (data["composition"] as? String) ?: existing.composition,
                    saltMolecule = (data["saltMolecule"] as? String) ?: existing.saltMolecule,
                    category = (data["category"] as? String)?.takeIf { it.isNotBlank() } ?: existing.category,
                    hsnCode = (data["hsnCode"] as? String)?.takeIf { it.isNotBlank() } ?: existing.hsnCode,
                    batchNumber = (data["batchNumber"] as? String) ?: existing.batchNumber,
                    expiryDate = (data["expiryDate"] as? String)?.takeIf { it.isNotBlank() } ?: existing.expiryDate,
                    stockPacks = (data["stockPacks"] as? Number)?.toInt() ?: existing.stockPacks,
                    mrp = (data["mrp"] as? Number)?.toDouble() ?: existing.mrp,
                    purchaseRate = (data["purchaseRate"] as? Number)?.toDouble() ?: existing.purchaseRate,
                    saleRate = (data["saleRate"] as? Number)?.toDouble() ?: existing.saleRate,
                    gstPercent = (data["gstPercent"] as? Number)?.toDouble() ?: existing.gstPercent,
                    rackLocation = (data["rackLocation"] as? String)?.takeIf { it.isNotBlank() } ?: existing.rackLocation,
                    scheduleDrug = (data["scheduleDrug"] as? String)?.takeIf { it.isNotBlank() } ?: existing.scheduleDrug,
                    isEssential = (data["isEssential"] as? Boolean) ?: existing.isEssential,
                    isLifeSaving = (data["isLifeSaving"] as? Boolean) ?: existing.isLifeSaving
                  )
                  dao.insertMedicine(merged)
                  addLog("Cloud Sync: Updated ${merged.name} from Web")
                } else {
                  dao.insertMedicine(item)
                  addLog("Cloud Sync: Added ${item.name} from Web")
                }
              } catch (ex: Exception) {
                Log.w("FirebaseSyncManager", "Room merge skipped: ${ex.message}")
              }
            }
          }
        }
      }
    activeListeners.add(invReg)

    val configPath = "users/$uid/config/business_settings"
    val cfgReg = fs.collection("users").document(uid)
      .collection("config").document("business_settings")
      .addSnapshotListener { doc, e ->
        if (e != null) {
          handleFirestoreError(e, OperationType.GET, configPath)
          return@addSnapshotListener
        }
        val data = doc?.data ?: return@addSnapshotListener
        if (doc.exists() && data["_lastUpdatedBy"] == "WEB_PORTAL") {
          addLog("Cloud Sync: Business settings updated from Web")
        }
      }
    activeListeners.add(cfgReg)
  }

  private fun removeActiveListenersSync() {
    activeListeners.forEach { reg ->
      runCatching { reg.remove() }
    }
    activeListeners.clear()
    attachedUid = null
  }

  suspend fun detachListeners() = withContext(NonCancellable) {
    removeActiveListenersSync()
    _syncError.value = null
  }

  private fun handleFirestoreError(
    error: Throwable,
    operationType: OperationType,
    path: String?
  ) {
    val user = auth.currentUser
    val errJson = JSONObject().apply {
      put("error", error.message ?: error::class.java.simpleName)
      put("operationType", operationType.name)
      put("path", path ?: "")
      put("authInfo", JSONObject().apply {
        put("userId", user?.uid ?: "null")
        put("email", user?.email ?: "null")
        put("emailVerified", user?.isEmailVerified ?: false)
      })
    }.toString()
    Log.e("FirestoreError", errJson, error)
    _syncError.value = error.localizedMessage ?: "Firestore $operationType failed on $path"
    addLog("Sync Error ($operationType): ${error.localizedMessage ?: "Permission or network error"}")
  }

  private fun mapToMedicineItem(data: Map<String, Any>): MedicineItem {
    return MedicineItem(
      id = (data["localId"] as? Number)?.toLong() ?: 0L,
      name = data["name"] as? String ?: "",
      manufacturer = data["manufacturer"] as? String ?: "Generic",
      composition = data["composition"] as? String ?: "",
      saltMolecule = data["saltMolecule"] as? String ?: "",
      category = data["category"] as? String ?: "Tablet",
      hsnCode = data["hsnCode"] as? String ?: "3004",
      batchNumber = data["batchNumber"] as? String ?: "",
      expiryDate = data["expiryDate"] as? String ?: "12/27",
      stockPacks = ((data["stockPacks"] as? Number)?.toInt() ?: 1).coerceAtLeast(0),
      mrp = ((data["mrp"] as? Number)?.toDouble() ?: 0.0).coerceAtLeast(0.0),
      purchaseRate = ((data["purchaseRate"] as? Number)?.toDouble() ?: 0.0).coerceAtLeast(0.0),
      saleRate = ((data["saleRate"] as? Number)?.toDouble() ?: 0.0).coerceAtLeast(0.0),
      gstPercent = ((data["gstPercent"] as? Number)?.toDouble() ?: 12.0).coerceIn(0.0, 28.0),
      rackLocation = data["rackLocation"] as? String ?: "Rack A-1",
      scheduleDrug = data["scheduleDrug"] as? String ?: "Schedule H",
      minStockAlert = ((data["minStockAlert"] as? Number)?.toInt() ?: 5).coerceAtLeast(0),
      isEssential = data["isEssential"] as? Boolean ?: false,
      isLifeSaving = data["isLifeSaving"] as? Boolean ?: false
    )
  }

  fun setAutoSync(enabled: Boolean) {
    _autoSyncOnReconnect.value = enabled
    addLog(if (enabled) "Auto-sync on reconnect enabled" else "Auto-sync on reconnect paused")
  }

  fun clearSyncError() {
    _syncError.value = null
  }

  fun syncNow() {
    scope.launch {
      performSync()
    }
  }

  fun syncConfigChange(key: String, value: Any) {
    val allowedKeys = setOf("globalSafetyThreshold", "autoSyncEnabled", "businessName")
    if (key !in allowedKeys) return

    val uid = auth.currentUser?.uid ?: return
    val fs = firestore ?: return

    scope.launch(Dispatchers.IO) {
      val path = "users/$uid/config/business_settings"
      try {
        val docRef = fs.collection("users").document(uid)
          .collection("config").document("business_settings")
        val exists = docRef.get().await().exists()
        val payload = mutableMapOf<String, Any>(
          "userId" to uid,
          key to value,
          "_lastUpdatedBy" to "ANDROID_APP",
          "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!exists) {
          payload["createdAt"] = FieldValue.serverTimestamp()
          docRef.set(payload).await()
        } else {
          docRef.update(payload).await()
        }
        Log.i("FirebaseSyncManager", "Synced config change: $key")
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, path)
      }
    }
  }

  suspend fun performSync(): Boolean = withContext(Dispatchers.IO) {
    if (_isSyncing.value) return@withContext false

    val uid = auth.currentUser?.uid
    if (uid.isNullOrBlank()) {
      _syncStatusMessage.value = "Sign in with Google to sync to cloud"
      addLog("Sync skipped: Google Sign-In required before writing to Firestore.")
      return@withContext false
    }

    if (!networkMonitor.isOnline.value) {
      _syncStatusMessage.value = "Cannot sync: Device is offline"
      addLog("Sync skipped: Device is offline. Changes preserved locally in Room.")
      return@withContext false
    }

    val fs = firestore
    if (fs == null) {
      _syncError.value = "Firestore database unavailable"
      _syncStatusMessage.value = "Cloud Sync Unavailable"
      return@withContext false
    }

    _isSyncing.value = true
    _syncError.value = null
    _syncStatusMessage.value = "Syncing local changes to Firestore..."

    try {
      val pendingItems = dao.getPendingSyncItemsList()
      addLog("Starting sync: ${pendingItems.size} pending item(s)")

      var successCount = 0
      var failureCount = 0

      for (item in pendingItems) {
        val path = resolveDocumentPath(uid, item)
        try {
          syncItemToFirestore(fs, uid, item)
          dao.markSyncItemCompleted(item.id)
          successCount++
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          failureCount++
          handleFirestoreError(e, OperationType.WRITE, path)
        }
      }

      if (successCount > 0) {
        dao.clearSyncedQueue()
      }

      val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
      _lastSyncTimestamp.value = timeStr

      return@withContext if (failureCount == 0) {
        _syncStatusMessage.value = "Cloud Synced ($timeStr)"
        addLog("Sync complete ($timeStr): $successCount item(s) synced.")
        true
      } else {
        _syncStatusMessage.value = "Sync partial: $successCount synced, $failureCount failed"
        false
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, "users/$uid")
      _syncStatusMessage.value = "Sync failed: ${e.localizedMessage ?: "Unknown error"}"
      return@withContext false
    } finally {
      _isSyncing.value = false
    }
  }

  private fun resolveCollectionName(entityType: String): String = when (entityType) {
    "MEDICINE" -> "inventory"
    "SALE" -> "sales"
    "CUSTOMER" -> "customers"
    "UDHAR" -> "udhar_transactions"
    "PURCHASE" -> "purchases"
    "PATIENT" -> "patients"
    "SUPPLIER" -> "suppliers"
    "DOCTOR" -> "doctors"
    else -> "customers"
  }

  private fun resolveDocId(item: SyncQueueItem): String {
    val cleanType = item.entityType.lowercase().replace(Regex("[^a-z0-9._-]"), "_")
    val cleanId = item.entityId.coerceAtLeast(0L)
    return "${cleanType}_$cleanId".take(128)
  }

  private fun resolveDocumentPath(uid: String, item: SyncQueueItem): String {
    return "users/$uid/${resolveCollectionName(item.entityType)}/${resolveDocId(item)}"
  }

  private suspend fun syncItemToFirestore(fs: FirebaseFirestore, uid: String, item: SyncQueueItem) {
    val collection = resolveCollectionName(item.entityType)
    val docId = resolveDocId(item)
    val docRef = fs.collection("users").document(uid).collection(collection).document(docId)

    if (item.action == "DELETE") {
      docRef.delete().await()
      return
    }

    val snapshot = docRef.get().await()
    val isCreate = !snapshot.exists()

    val payload = when (item.entityType) {
      "MEDICINE" -> buildMedicinePayload(uid, item)
      "SALE" -> buildSalePayload(uid, item)
      else -> buildLedgerPayload(uid, item)
    }.toMutableMap()

    payload["updatedAt"] = FieldValue.serverTimestamp()
    if (isCreate) {
      payload["createdAt"] = FieldValue.serverTimestamp()
      docRef.set(payload).await()
    } else {
      docRef.update(payload).await()
    }
  }

  private suspend fun buildMedicinePayload(uid: String, item: SyncQueueItem): Map<String, Any> {
    val med = dao.getMedicineById(item.entityId)
    val parsed = jsonToMap(item.payloadJson)

    val name = (med?.name ?: parsed["name"] as? String ?: "Medicine #${item.entityId}")
      .trim().ifBlank { "Medicine #${item.entityId}" }.take(200)
    val stockPacks = (med?.stockPacks ?: (parsed["stockPacks"] as? Number)?.toInt() ?: 0)
      .coerceIn(0, 1_000_000)
    val mrp = (med?.mrp ?: (parsed["mrp"] as? Number)?.toDouble() ?: 0.0)
      .coerceIn(0.0, 1_000_000.0)

    val map = mutableMapOf<String, Any>(
      "userId" to uid,
      "localId" to item.entityId.coerceAtLeast(0L),
      "name" to name,
      "stockPacks" to stockPacks,
      "mrp" to mrp,
      "_lastUpdatedBy" to "ANDROID_APP"
    )

    if (med != null) {
      map["manufacturer"] = med.manufacturer.take(160)
      map["composition"] = med.composition.take(400)
      map["saltMolecule"] = med.saltMolecule.take(400)
      map["category"] = med.category.take(80)
      map["hsnCode"] = med.hsnCode.take(32)
      map["batchNumber"] = med.batchNumber.take(80)
      map["expiryDate"] = med.expiryDate.take(40)
      map["purchaseRate"] = med.purchaseRate.coerceIn(0.0, 1_000_000.0)
      map["saleRate"] = med.saleRate.coerceIn(0.0, 1_000_000.0)
      map["gstPercent"] = med.gstPercent.coerceIn(0.0, 28.0)
      map["rackLocation"] = med.rackLocation.take(120)
      map["scheduleDrug"] = med.scheduleDrug.take(40)
      map["isEssential"] = med.isEssential
      map["isLifeSaving"] = med.isLifeSaving
      map["minStockAlert"] = med.minStockAlert.coerceIn(0, 100_000)
    } else {
      (parsed["manufacturer"] as? String)?.let { map["manufacturer"] = it.take(160) }
      (parsed["composition"] as? String)?.let { map["composition"] = it.take(400) }
      (parsed["saltMolecule"] as? String)?.let { map["saltMolecule"] = it.take(400) }
      (parsed["category"] as? String)?.let { map["category"] = it.take(80) }
      (parsed["hsnCode"] as? String)?.let { map["hsnCode"] = it.take(32) }
      (parsed["batchNumber"] as? String)?.let { map["batchNumber"] = it.take(80) }
      (parsed["expiryDate"] as? String)?.let { map["expiryDate"] = it.take(40) }
      (parsed["purchaseRate"] as? Number)?.toDouble()?.let { map["purchaseRate"] = it.coerceIn(0.0, 1_000_000.0) }
      (parsed["saleRate"] as? Number)?.toDouble()?.let { map["saleRate"] = it.coerceIn(0.0, 1_000_000.0) }
      (parsed["gstPercent"] as? Number)?.toDouble()?.let { map["gstPercent"] = it.coerceIn(0.0, 28.0) }
      (parsed["rackLocation"] as? String)?.let { map["rackLocation"] = it.take(120) }
      (parsed["scheduleDrug"] as? String)?.let { map["scheduleDrug"] = it.take(40) }
      (parsed["isEssential"] as? Boolean)?.let { map["isEssential"] = it }
      (parsed["isLifeSaving"] as? Boolean)?.let { map["isLifeSaving"] = it }
      (parsed["minStockAlert"] as? Number)?.toInt()?.let { map["minStockAlert"] = it.coerceIn(0, 100_000) }
    }
    return map
  }

  private fun buildSalePayload(uid: String, item: SyncQueueItem): Map<String, Any> {
    val parsed = jsonToMap(item.payloadJson)
    val invNum = (parsed["invoiceNumber"] as? String)?.trim()?.ifBlank { null } ?: "INV-${item.entityId}"
    val grandTotal = ((parsed["grandTotal"] as? Number)?.toDouble() ?: 0.0).coerceIn(0.0, 10_000_000.0)
    val localId = (parsed["id"] as? Number)?.toLong()?.coerceAtLeast(0L) ?: item.entityId.coerceAtLeast(0L)

    val map = mutableMapOf<String, Any>(
      "userId" to uid,
      "localId" to localId,
      "invoiceNumber" to invNum.take(80),
      "grandTotal" to grandTotal,
      "_lastUpdatedBy" to "ANDROID_APP"
    )
    (parsed["customerName"] as? String)?.let { map["customerName"] = it.take(160) }
    (parsed["paymentMode"] as? String)?.let { map["paymentMode"] = it.take(40) }
    (parsed["invoiceDate"] as? String)?.let { map["invoiceDate"] = it.take(40) }
    (parsed["itemCount"] as? Number)?.toInt()?.let { map["itemCount"] = it.coerceIn(0, 10_000) }
    return map
  }

  private fun buildLedgerPayload(uid: String, item: SyncQueueItem): Map<String, Any> {
    val normalizedType = when (item.entityType) {
      "CUSTOMER", "UDHAR", "PURCHASE", "PATIENT", "SUPPLIER", "DOCTOR" -> item.entityType
      else -> "CUSTOMER"
    }
    return mapOf(
      "userId" to uid,
      "entityType" to normalizedType,
      "localId" to item.entityId.coerceAtLeast(0L),
      "action" to item.action.take(40),
      "payloadJson" to item.payloadJson.take(8000),
      "_lastUpdatedBy" to "ANDROID_APP"
    )
  }

  private fun jsonToMap(jsonString: String): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    try {
      if (jsonString.isNotBlank()) {
        val json = JSONObject(jsonString)
        val keys = json.keys()
        while (keys.hasNext()) {
          val key = keys.next()
          val value = json.opt(key)
          if (value != null && value != JSONObject.NULL) {
            map[key] = value
          }
        }
      }
    } catch (e: Exception) {
      map["rawPayload"] = jsonString.take(4000)
    }
    return map
  }

  private fun addLog(message: String) {
    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    val entry = "[$time] $message"
    _syncHistory.value = listOf(entry) + _syncHistory.value.take(25)
  }
}
