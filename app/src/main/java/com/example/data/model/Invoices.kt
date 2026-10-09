package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

data class BillItem(
  val medicineId: Long = 0,
  val medicineName: String,
  val batchNumber: String = "",
  val expiryDate: String = "",
  val packQty: Int = 1,
  val mrp: Double = 0.0,
  val rate: Double = 0.0,
  val purchaseRate: Double = 0.0,
  val discountPercent: Double = 0.0,
  val gstPercent: Double = 12.0,
  val total: Double = 0.0,
  val isPaid: Boolean = false
)

@Entity(
  tableName = "sale_invoices",
  indices = [
    Index(value = ["invoiceNumber"]),
    Index(value = ["invoiceDate"]),
    Index(value = ["customerId"])
  ]
)
data class SaleInvoice(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val invoiceNumber: String,
  val invoiceDate: String,
  val billingTo: String = "Cash Sale", // "Cash Sale" or "Customer"
  val customerId: Long? = null,
  val customerName: String = "Cash Sale",
  val customerPhone: String = "",
  val doctorName: String = "",
  val saleType: String = "Invoice", // "Invoice" or "Delivery Challan"
  val itemsJson: String = "", // serialized items
  val totalItemsCount: Int = 1,
  val subtotal: Double = 0.0,
  val discountTotal: Double = 0.0,
  val gstTotal: Double = 0.0,
  val grandTotal: Double = 0.0,
  val paymentMode: String = "Cash", // Cash, UPI, Card, Credit
  val isPaid: Boolean = true,
  val loyaltyPointsEarned: Int = 0,
  val loyaltyPointsRedeemed: Int = 0,
  val loyaltyDiscountAmt: Double = 0.0
)

@Entity(tableName = "purchase_invoices")
data class PurchaseInvoice(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val invoiceNumber: String,
  val invoiceDate: String,
  val distributorId: Long? = null,
  val distributorName: String,
  val distributorGstin: String = "",
  val itemsCount: Int = 1,
  val totalAmount: Double = 0.0,
  val paidAmount: Double = 0.0,
  val status: String = "Unpaid" // Paid, Unpaid, Partial
)

@Entity(tableName = "business_profile")
data class BusinessProfile(
  @PrimaryKey
  val id: Long = 1,
  val businessName: String = "ROYAL PHARMACY",
  val ayushmanHfrId: String = "",
  val ownerName: String = "Suleman Hoque",
  val phone: String = "9957905450",
  val email: String = "sulman995790@gmail.com",
  val drugLicenseForm20: String = "DL-20B-784146-2024",
  val form20Expiry: String = "31-12-2028",
  val drugLicenseForm21: String = "DL-21B-784146-2024",
  val form21Expiry: String = "31-12-2028",
  val entityType: String = "Proprietorship",
  val isTurnoverBelowGstLimit: Boolean = false,
  val gstin: String = "18AABCR1234M1Z5",
  val pan: String = "AABCR1234M",
  val tradeName: String = "ROYAL PHARMACY & SURGICALS",
  val isCompositionScheme: Boolean = false,
  val addressLine1: String = "Darrang, Assam - 784146",
  val addressLine2: String = "Hospital Road, Near Civil Hospital",
  val addressLine3: String = "Darrang, Assam - 784146",
  val bankName: String = "State Bank of India",
  val bankAccountNumber: String = "XXXX-XXXX-8821",
  val bankIfsc: String = "SBIN0001234",
  val bankUpiId: String = "royalpharmacy@okaxis",
  val timings: String = "08:00 AM - 10:30 PM",
  val walletBalance: Double = 0.0,
  val businessFrontImageUri: String = "",
  val avatarImageUri: String = "",
  val form20ImageUri: String = "",
  val form21ImageUri: String = "",
  val latitude: Double = 26.4385,
  val longitude: Double = 92.0305
) {
  val dlNumber: String
    get() = drugLicenseForm20.ifBlank { drugLicenseForm21 }
}
