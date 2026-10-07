package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "medicines",
  indices = [
    Index(value = ["name"]),
    Index(value = ["barcode"]),
    Index(value = ["category"])
  ]
)
data class MedicineItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val manufacturer: String,
  val composition: String = "",
  val saltMolecule: String = "", // e.g., "Paracetamol 650mg", "Amoxicillin + Clavulanic Acid"
  val category: String = "Tablet", // Tablet, Syrup, Injection, Ointment, Powder, OTC
  val barcode: String = "",
  val hsnCode: String = "3004",
  val batchNumber: String = "",
  val expiryDate: String = "12/26",
  val isExpired: Boolean = false,
  val stockPacks: Int = 1,
  val mrp: Double = 0.0,
  val purchaseRate: Double = 0.0,
  val saleRate: Double = 0.0,
  val gstPercent: Double = 12.0,
  val rackLocation: String = "Rack A-1",
  val scheduleDrug: String = "Schedule H", // Schedule H, H1, OTC
  val isGeneric: Boolean = false,
  val minStockAlert: Int = 5,
  val isEssential: Boolean = false,
  val isLifeSaving: Boolean = false,
  val dispenseCount: Int = 0
)
