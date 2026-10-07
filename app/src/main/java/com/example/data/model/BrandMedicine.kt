package com.example.data.model

data class BrandMedicine(
  val id: String = java.util.UUID.randomUUID().toString(),
  val name: String,
  val brandName: String, // "Cipla", "IPCA", "GSK", "SUN PHARMA", etc.
  val saltComposition: String,
  val category: String, // "Tablet", "Capsule", "Injectable", "Syrup", "Ointment", "Inhaler"
  val strength: String = "",
  val packaging: String = "10 Tablets",
  val mrp: Double = 0.0,
  val isInjectable: Boolean = false,
  val isEssential: Boolean = false,
  val description: String = ""
)

data class CartItem(
  val id: String = java.util.UUID.randomUUID().toString(),
  val medicineName: String,
  val manufacturer: String = "",
  val composition: String = "",
  val batchNumber: String = "",
  val expiryDate: String = "",
  val quantity: Int = 10,
  val unitRate: Double = 0.0,
  val mrp: Double = 0.0,
  val itemType: String = "PURCHASE_ORDER", // "PURCHASE_ORDER" or "EXPIRY_RETURN"
  val category: String = "Tablet",
  val note: String = ""
) {
  val totalAmount: Double get() = quantity * (if (unitRate > 0) unitRate else mrp)
}
