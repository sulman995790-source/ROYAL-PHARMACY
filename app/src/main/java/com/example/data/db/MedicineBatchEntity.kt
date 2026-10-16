package com.example.data.db

data class MedicineBatchEntity(
    val id: Long = 0,
    val brandName: String,
    val genericSalt: String,
    val batchNumber: String,
    val expiryYearMonth: String,
    val stockQuantity: Int,
    val sellingMrp: Double,
    val purchasePtr: Double = 0.0,
    val rackLocation: String = "R-A1"
)
