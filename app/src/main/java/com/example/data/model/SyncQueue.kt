package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val entityType: String, // "MEDICINE", "SALE", "CUSTOMER", "UDHAR", "PURCHASE", "PATIENT", "SUPPLIER"
  val entityId: Long,
  val action: String, // "INSERT", "UPDATE", "DELETE"
  val payloadJson: String,
  val timestamp: Long = System.currentTimeMillis(),
  val status: String = "PENDING", // "PENDING", "SYNCING", "SYNCED", "FAILED"
  val retryCount: Int = 0,
  val errorMessage: String? = null
)

data class SyncReport(
  val totalPending: Int = 0,
  val totalSynced: Int = 0,
  val lastSyncTime: String = "Never",
  val isOnline: Boolean = true,
  val isSyncing: Boolean = false,
  val lastError: String? = null
)
