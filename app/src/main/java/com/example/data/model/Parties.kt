package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "customers",
  indices = [
    Index(value = ["name"]),
    Index(value = ["phone"])
  ]
)
data class Customer(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val phone: String,
  val email: String = "",
  val address: String = "",
  val type: String = "Individual", // Individual, Clinic, Hospital
  val doctorName: String = "",
  val balanceReceivable: Double = 0.0,
  val lastTxnDate: String = "",
  val loyaltyPoints: Int = 0,
  val loyaltyTier: String = "Bronze", // Bronze, Silver, Gold, Platinum
  val totalPurchasesAmt: Double = 0.0
)

@Entity(tableName = "distributors")
data class Distributor(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val phone: String = "",
  val email: String = "",
  val gstin: String = "",
  val dlNumber: String = "",
  val address: String = "",
  val balancePayable: Double = 0.0,
  val lastTxnDate: String = "",
  val isGstRegistered: Boolean = true
)
