package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "patients",
  indices = [
    Index(value = ["name"]),
    Index(value = ["contactNumber"])
  ]
)
data class Patient(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val dob: String = "",
  val age: Int = 0,
  val gender: String = "Male", // Male, Female, Other
  val contactNumber: String,
  val address: String = "",
  val medicalHistory: String = "", // e.g., "Hypertension, Type 2 Diabetes"
  val knownAllergies: String = "", // e.g., "Penicillin, Sulfa drugs"
  val currentMedications: String = "",
  val emergencyContact: String = "",
  val createdDate: String = "",
  val chronicConditions: String = "", // e.g. "Type 2 Diabetes, Hypertension"
  val lastRefillDate: String = "",
  val nextRefillDueDate: String = "", // e.g. "12-10-2026"
  val refillCycleDays: Int = 30,
  val preferredDoctor: String = "",
  val loyaltyPoints: Int = 0,
  val crmTag: String = "Chronic Patient" // "Chronic Patient", "VIP", "Senior Citizen", "Pediatric"
)

@Entity(tableName = "suppliers")
data class Supplier(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val companyName: String = "",
  val contactPerson: String = "",
  val phone: String,
  val email: String = "",
  val gstin: String = "",
  val drugLicenseNo: String = "",
  val address: String = "",
  val latitude: Double = 26.3534, // Default to Assam / Northeast coordinates
  val longitude: Double = 92.0528,
  val paymentTermsDays: Int = 30,
  val outstandingPayable: Double = 0.0
)

data class POItem(
  val medicineId: Long = 0,
  val medicineName: String,
  val requestedPacks: Int,
  val receivedPacks: Int = 0,
  val estimatedUnitRate: Double,
  val batchNumber: String = "",
  val expiryDate: String = ""
)

@Entity(tableName = "purchase_orders")
data class PurchaseOrder(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val poNumber: String, // e.g., "PO-2026-101"
  val supplierId: Long,
  val supplierName: String,
  val orderDate: String,
  val expectedDeliveryDate: String = "",
  val status: String = "ORDERED", // DRAFT, ORDERED, RECEIVED, CANCELLED
  val itemsJson: String = "", // serializable items
  val totalAmount: Double = 0.0,
  val receivedDate: String = "",
  val supplierInvoiceNumber: String = ""
)

@Entity(tableName = "udhar_transactions")
data class UdharTransaction(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val customerId: Long,
  val customerName: String,
  val customerPhone: String,
  val date: String,
  val type: String, // "CREDIT_GIVEN" or "PAYMENT_RECEIVED"
  val amount: Double,
  val balanceAfter: Double,
  val note: String = "",
  val invoiceNumber: String = ""
)

data class BankAccount(
  val id: Long = 0,
  val bankName: String,
  val accountNumber: String,
  val ifscCode: String,
  val branchName: String = "Main Branch",
  val accountType: String = "Current Account", // Current, Savings, Cash Credit (CC)
  val balance: Double = 0.0,
  val isPrimary: Boolean = false
)

data class UpiAccount(
  val id: Long = 0,
  val upiId: String, // e.g., "royalpharmacy@okaxis", "9957905450@ybl"
  val providerName: String = "Google Pay / Axis", // Google Pay, PhonePe, Paytm, BHIM
  val holderName: String = "ROYAL PHARMACY",
  val isPrimary: Boolean = true,
  val qrType: String = "Merchant QR",
  val linkedBank: String = "State Bank of India"
)

data class PrescriptionRecord(
  val id: Long = 0,
  val patientName: String,
  val patientAgeGender: String = "35 / Male",
  val patientPhone: String = "",
  val doctorName: String,
  val clinicName: String = "City Care Hospital",
  val prescriptionDate: String,
  val medicinesSummary: String,
  val diagnosisNotes: String = "",
  val status: String = "Active", // "Active", "Dispensed", "Refill Due"
  val refillDueDate: String = "",
  val imageUriString: String = ""
)

@Entity(
  tableName = "doctors",
  indices = [
    Index(value = ["name"]),
    Index(value = ["registrationNo"])
  ]
)
data class Doctor(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val degree: String = "MBBS, MD",
  val specialty: String = "General Medicine",
  val clinicHospital: String = "Civil Hospital Road",
  val phone: String = "+91 98640 11223",
  val email: String = "",
  val registrationNo: String = "MCI-48921",
  val address: String = "Darrang, Assam",
  val prescriptionCount: Int = 1,
  val commissionPercentage: Double = 0.0,
  val notes: String = "",
  val autoAddedFromRx: Boolean = false
)


