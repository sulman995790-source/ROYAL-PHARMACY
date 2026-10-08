package com.example.data.model

data class MedicineBatchDetail(
  val id: String,
  val medicine: MedicineItem,
  val batchNumber: String,
  val mfgDate: String = "01/2024",
  val expiryDate: String,
  val stockAvailable: Int,
  val initialPacks: Int,
  val purchaseRate: Double,
  val mrp: Double,
  val saleRate: Double,
  val rackLocation: String,
  val supplierName: String,
  val isQuarantined: Boolean = false,
  val barcode: String = ""
)
