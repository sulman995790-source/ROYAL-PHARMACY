package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.db.PharmacyDao
import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Distributor
import com.example.data.model.MedicineItem
import com.example.data.model.Patient
import com.example.data.model.PurchaseInvoice
import com.example.data.model.PurchaseOrder
import com.example.data.model.SaleInvoice
import com.example.data.model.Supplier
import com.example.data.model.UdharTransaction
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupMetadata(
  val appName: String = "ROYAL PHARMACY ERP",
  val version: String = "2.4.0",
  val timestamp: Long = System.currentTimeMillis(),
  val formattedDate: String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
  val medicineCount: Int = 0,
  val salesCount: Int = 0,
  val purchaseCount: Int = 0,
  val customerCount: Int = 0,
  val patientCount: Int = 0,
  val supplierCount: Int = 0,
  val purchaseOrderCount: Int = 0,
  val totalStockValue: Double = 0.0,
  val totalSalesRevenue: Double = 0.0
)

data class RestoreResult(
  val isSuccess: Boolean,
  val message: String,
  val medicinesRestored: Int = 0,
  val salesRestored: Int = 0,
  val customersRestored: Int = 0,
  val suppliersRestored: Int = 0,
  val patientsRestored: Int = 0
)

data class LocalBackupItem(
  val fileName: String,
  val filePath: String,
  val fileSizeFormatted: String,
  val modifiedDate: String,
  val recordCountText: String
)

object BackupRestoreManager {

  suspend fun generateBackupJson(dao: PharmacyDao): String {
    val medicines = dao.getAllMedicines().first()
    val sales = dao.getAllSales().first()
    val purchases = dao.getAllPurchases().first()
    val customers = dao.getAllCustomers().first()
    val patients = dao.getAllPatients().first()
    val suppliers = dao.getAllSuppliers().first()
    val purchaseOrders = dao.getAllPurchaseOrders().first()
    val distributors = dao.getAllDistributors().first()
    val udharTransactions = dao.getAllUdharTransactions().first()
    val profile = dao.getBusinessProfile().first()

    val root = JSONObject()
    root.put("app", "ROYAL PHARMACY ERP")
    root.put("schemaVersion", 2)
    root.put("timestamp", System.currentTimeMillis())
    root.put("createdAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

    // Metadata
    val meta = JSONObject().apply {
      put("medicineCount", medicines.size)
      put("salesCount", sales.size)
      put("purchaseCount", purchases.size)
      put("customerCount", customers.size)
      put("patientCount", patients.size)
      put("supplierCount", suppliers.size)
      put("poCount", purchaseOrders.size)
      put("totalStockValue", medicines.sumOf { it.stockPacks * it.saleRate })
      put("totalRevenue", sales.sumOf { it.grandTotal })
    }
    root.put("metadata", meta)

    // Medicines array
    val medArray = JSONArray()
    medicines.forEach { m ->
      medArray.put(JSONObject().apply {
        put("id", m.id)
        put("name", m.name)
        put("manufacturer", m.manufacturer)
        put("composition", m.composition)
        put("saltMolecule", m.saltMolecule)
        put("category", m.category)
        put("barcode", m.barcode)
        put("hsnCode", m.hsnCode)
        put("batchNumber", m.batchNumber)
        put("expiryDate", m.expiryDate)
        put("isExpired", m.isExpired)
        put("stockPacks", m.stockPacks)
        put("mrp", m.mrp)
        put("purchaseRate", m.purchaseRate)
        put("saleRate", m.saleRate)
        put("gstPercent", m.gstPercent)
        put("rackLocation", m.rackLocation)
        put("scheduleDrug", m.scheduleDrug)
        put("isGeneric", m.isGeneric)
        put("minStockAlert", m.minStockAlert)
        put("isEssential", m.isEssential)
        put("isLifeSaving", m.isLifeSaving)
        put("dispenseCount", m.dispenseCount)
      })
    }
    root.put("medicines", medArray)

    // Sales Invoices
    val salesArray = JSONArray()
    sales.forEach { s ->
      salesArray.put(JSONObject().apply {
        put("id", s.id)
        put("invoiceNumber", s.invoiceNumber)
        put("invoiceDate", s.invoiceDate)
        put("billingTo", s.billingTo)
        put("customerName", s.customerName)
        put("customerPhone", s.customerPhone)
        put("doctorName", s.doctorName)
        put("saleType", s.saleType)
        put("itemsJson", s.itemsJson)
        put("totalItemsCount", s.totalItemsCount)
        put("subtotal", s.subtotal)
        put("discountTotal", s.discountTotal)
        put("gstTotal", s.gstTotal)
        put("grandTotal", s.grandTotal)
        put("paymentMode", s.paymentMode)
        put("isPaid", s.isPaid)
        put("loyaltyPointsEarned", s.loyaltyPointsEarned)
        put("loyaltyPointsRedeemed", s.loyaltyPointsRedeemed)
        put("loyaltyDiscountAmt", s.loyaltyDiscountAmt)
      })
    }
    root.put("sales", salesArray)

    // Customers
    val custArray = JSONArray()
    customers.forEach { c ->
      custArray.put(JSONObject().apply {
        put("id", c.id)
        put("name", c.name)
        put("phone", c.phone)
        put("email", c.email)
        put("address", c.address)
        put("type", c.type)
        put("doctorName", c.doctorName)
        put("balanceReceivable", c.balanceReceivable)
        put("loyaltyPoints", c.loyaltyPoints)
        put("loyaltyTier", c.loyaltyTier)
        put("totalPurchasesAmt", c.totalPurchasesAmt)
      })
    }
    root.put("customers", custArray)

    // Patients
    val patientArray = JSONArray()
    patients.forEach { p ->
      patientArray.put(JSONObject().apply {
        put("id", p.id)
        put("name", p.name)
        put("age", p.age)
        put("gender", p.gender)
        put("contactNumber", p.contactNumber)
        put("address", p.address)
        put("medicalHistory", p.medicalHistory)
        put("knownAllergies", p.knownAllergies)
        put("preferredDoctor", p.preferredDoctor)
        put("chronicConditions", p.chronicConditions)
        put("lastRefillDate", p.lastRefillDate)
        put("nextRefillDueDate", p.nextRefillDueDate)
        put("refillCycleDays", p.refillCycleDays)
        put("loyaltyPoints", p.loyaltyPoints)
      })
    }
    root.put("patients", patientArray)

    // Suppliers
    val supArray = JSONArray()
    suppliers.forEach { sup ->
      supArray.put(JSONObject().apply {
        put("id", sup.id)
        put("name", sup.name)
        put("companyName", sup.companyName)
        put("contactPerson", sup.contactPerson)
        put("phone", sup.phone)
        put("email", sup.email)
        put("gstin", sup.gstin)
        put("drugLicenseNo", sup.drugLicenseNo)
        put("address", sup.address)
        put("paymentTermsDays", sup.paymentTermsDays)
        put("outstandingPayable", sup.outstandingPayable)
      })
    }
    root.put("suppliers", supArray)

    // Purchases
    val purchaseArray = JSONArray()
    purchases.forEach { p ->
      purchaseArray.put(JSONObject().apply {
        put("id", p.id)
        put("invoiceNumber", p.invoiceNumber)
        put("distributorName", p.distributorName)
        put("distributorGstin", p.distributorGstin)
        put("invoiceDate", p.invoiceDate)
        put("itemsCount", p.itemsCount)
        put("totalAmount", p.totalAmount)
        put("paidAmount", p.paidAmount)
        put("status", p.status)
      })
    }
    root.put("purchases", purchaseArray)

    // Business Profile
    if (profile != null) {
      root.put("profile", JSONObject().apply {
        put("businessName", profile.businessName)
        put("ownerName", profile.ownerName)
        put("phone", profile.phone)
        put("email", profile.email)
        put("gstin", profile.gstin)
        put("drugLicenseForm20", profile.drugLicenseForm20)
        put("drugLicenseForm21", profile.drugLicenseForm21)
        put("addressLine1", profile.addressLine1)
        put("addressLine2", profile.addressLine2)
        put("addressLine3", profile.addressLine3)
      })
    }

    return root.toString(2)
  }

  fun saveBackupToLocalFile(context: Context, jsonString: String): File {
    val backupDir = File(context.filesDir, "backups")
    if (!backupDir.exists()) backupDir.mkdirs()

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File(backupDir, "RoyalPharmacy_Backup_$timeStamp.json")
    FileOutputStream(file).use { out ->
      out.write(jsonString.toByteArray(Charsets.UTF_8))
    }
    return file
  }

  fun shareBackupFile(context: Context, file: File) {
    try {
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_SUBJECT, "Royal Pharmacy Database Backup (${file.name})")
        putExtra(Intent.EXTRA_TEXT, "Attached full system backup for Royal Pharmacy ERP.\nGenerated: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())}")
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      val chooser = Intent.createChooser(intent, "Share Database Backup")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (_: Exception) {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Royal Pharmacy Backup JSON")
        putExtra(Intent.EXTRA_TEXT, file.readText())
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(Intent.createChooser(intent, "Share Database Backup"))
    }
  }

  fun parseBackupMetadata(jsonString: String): BackupMetadata? {
    return try {
      val root = JSONObject(jsonString)
      val appName = root.optString("app", "ROYAL PHARMACY ERP")
      val version = root.optString("version", "2.4.0")
      val ts = root.optLong("timestamp", System.currentTimeMillis())
      val formatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ts))

      val meta = root.optJSONObject("metadata")
      val medArray = root.optJSONArray("medicines")
      val salesArray = root.optJSONArray("sales")
      val custArray = root.optJSONArray("customers")
      val patientArray = root.optJSONArray("patients")
      val supArray = root.optJSONArray("suppliers")
      val purArray = root.optJSONArray("purchases")

      BackupMetadata(
        appName = appName,
        version = version,
        timestamp = ts,
        formattedDate = formatted,
        medicineCount = meta?.optInt("medicineCount") ?: (medArray?.length() ?: 0),
        salesCount = meta?.optInt("salesCount") ?: (salesArray?.length() ?: 0),
        purchaseCount = meta?.optInt("purchaseCount") ?: (purArray?.length() ?: 0),
        customerCount = meta?.optInt("customerCount") ?: (custArray?.length() ?: 0),
        patientCount = meta?.optInt("patientCount") ?: (patientArray?.length() ?: 0),
        supplierCount = meta?.optInt("supplierCount") ?: (supArray?.length() ?: 0),
        purchaseOrderCount = meta?.optInt("poCount") ?: 0,
        totalStockValue = meta?.optDouble("totalStockValue", 0.0) ?: 0.0,
        totalSalesRevenue = meta?.optDouble("totalRevenue", 0.0) ?: 0.0
      )
    } catch (e: Exception) {
      null
    }
  }

  suspend fun restoreDatabase(
    dao: PharmacyDao,
    jsonString: String,
    cleanOverwrite: Boolean = false
  ): RestoreResult {
    return try {
      val root = JSONObject(jsonString)
      val medArray = root.optJSONArray("medicines") ?: JSONArray()
      val salesArray = root.optJSONArray("sales") ?: JSONArray()
      val custArray = root.optJSONArray("customers") ?: JSONArray()
      val patientArray = root.optJSONArray("patients") ?: JSONArray()
      val supArray = root.optJSONArray("suppliers") ?: JSONArray()
      val profileObj = root.optJSONObject("profile")

      var medRestored = 0
      for (i in 0 until medArray.length()) {
        val o = medArray.getJSONObject(i)
        val med = MedicineItem(
          name = o.optString("name", "Unknown Medicine"),
          manufacturer = o.optString("manufacturer", "Pharma Ltd"),
          composition = o.optString("composition", ""),
          saltMolecule = o.optString("saltMolecule", ""),
          category = o.optString("category", "Tablet"),
          barcode = o.optString("barcode", ""),
          hsnCode = o.optString("hsnCode", "3004"),
          batchNumber = o.optString("batchNumber", "B100"),
          expiryDate = o.optString("expiryDate", "12/26"),
          isExpired = o.optBoolean("isExpired", false),
          stockPacks = o.optInt("stockPacks", 10),
          mrp = o.optDouble("mrp", 50.0),
          purchaseRate = o.optDouble("purchaseRate", 35.0),
          saleRate = o.optDouble("saleRate", 45.0),
          gstPercent = o.optDouble("gstPercent", 12.0),
          rackLocation = o.optString("rackLocation", "Rack A-1"),
          scheduleDrug = o.optString("scheduleDrug", "Schedule H"),
          isGeneric = o.optBoolean("isGeneric", false),
          minStockAlert = o.optInt("minStockAlert", 5),
          isEssential = o.optBoolean("isEssential", false),
          isLifeSaving = o.optBoolean("isLifeSaving", false),
          dispenseCount = o.optInt("dispenseCount", 0)
        )
        dao.insertMedicine(med)
        medRestored++
      }

      var salesRestored = 0
      for (i in 0 until salesArray.length()) {
        val o = salesArray.getJSONObject(i)
        val sale = SaleInvoice(
          invoiceNumber = o.optString("invoiceNumber", "INV-RESTORED-$i"),
          invoiceDate = o.optString("invoiceDate", "01-01-2026"),
          billingTo = o.optString("billingTo", "Customer"),
          customerName = o.optString("customerName", "Walk-in"),
          customerPhone = o.optString("customerPhone", ""),
          doctorName = o.optString("doctorName", ""),
          saleType = o.optString("saleType", "Invoice"),
          itemsJson = o.optString("itemsJson", ""),
          totalItemsCount = o.optInt("totalItemsCount", 1),
          subtotal = o.optDouble("subtotal", 100.0),
          discountTotal = o.optDouble("discountTotal", 0.0),
          gstTotal = o.optDouble("gstTotal", 12.0),
          grandTotal = o.optDouble("grandTotal", 112.0),
          paymentMode = o.optString("paymentMode", "Cash"),
          isPaid = o.optBoolean("isPaid", true),
          loyaltyPointsEarned = o.optInt("loyaltyPointsEarned", 0),
          loyaltyPointsRedeemed = o.optInt("loyaltyPointsRedeemed", 0),
          loyaltyDiscountAmt = o.optDouble("loyaltyDiscountAmt", 0.0)
        )
        dao.insertSale(sale)
        salesRestored++
      }

      var custRestored = 0
      for (i in 0 until custArray.length()) {
        val o = custArray.getJSONObject(i)
        val cust = Customer(
          name = o.optString("name", "Customer"),
          phone = o.optString("phone", ""),
          email = o.optString("email", ""),
          address = o.optString("address", ""),
          type = o.optString("type", "Individual"),
          doctorName = o.optString("doctorName", ""),
          balanceReceivable = o.optDouble("balanceReceivable", 0.0),
          loyaltyPoints = o.optInt("loyaltyPoints", 0),
          loyaltyTier = o.optString("loyaltyTier", "Bronze"),
          totalPurchasesAmt = o.optDouble("totalPurchasesAmt", 0.0)
        )
        dao.insertCustomer(cust)
        custRestored++
      }

      var patientRestored = 0
      for (i in 0 until patientArray.length()) {
        val o = patientArray.getJSONObject(i)
        val patient = Patient(
          name = o.optString("name", "Patient"),
          age = o.optInt("age", 40),
          gender = o.optString("gender", "Male"),
          contactNumber = o.optString("contactNumber", "9876543210"),
          address = o.optString("address", ""),
          medicalHistory = o.optString("medicalHistory", "Hypertension"),
          knownAllergies = o.optString("knownAllergies", "None"),
          preferredDoctor = o.optString("preferredDoctor", "Dr. Sen"),
          chronicConditions = o.optString("chronicConditions", "Hypertension"),
          lastRefillDate = o.optString("lastRefillDate", "01-01-2026"),
          nextRefillDueDate = o.optString("nextRefillDueDate", "31-01-2026"),
          refillCycleDays = o.optInt("refillCycleDays", 30),
          loyaltyPoints = o.optInt("loyaltyPoints", 100)
        )
        dao.insertPatient(patient)
        patientRestored++
      }

      var supRestored = 0
      for (i in 0 until supArray.length()) {
        val o = supArray.getJSONObject(i)
        val sup = Supplier(
          name = o.optString("name", "Supplier"),
          companyName = o.optString("companyName", "Distributors Ltd"),
          contactPerson = o.optString("contactPerson", "Manager"),
          phone = o.optString("phone", "9876543210"),
          email = o.optString("email", ""),
          gstin = o.optString("gstin", ""),
          drugLicenseNo = o.optString("drugLicenseNo", ""),
          address = o.optString("address", ""),
          paymentTermsDays = o.optInt("paymentTermsDays", 30),
          outstandingPayable = o.optDouble("outstandingPayable", 0.0)
        )
        dao.insertSupplier(sup)
        supRestored++
      }

      if (profileObj != null) {
        val profile = BusinessProfile(
          id = 1,
          businessName = profileObj.optString("businessName", "ROYAL PHARMACY"),
          ownerName = profileObj.optString("ownerName", "Suleman Hoque"),
          phone = profileObj.optString("phone", "9957905450"),
          email = profileObj.optString("email", "sulman995790@gmail.com"),
          gstin = profileObj.optString("gstin", "18AABCR1234M1Z5"),
          drugLicenseForm20 = profileObj.optString("drugLicenseForm20", "DL-20B-784146-2024"),
          drugLicenseForm21 = profileObj.optString("drugLicenseForm21", "DL-21B-784146-2024"),
          addressLine1 = profileObj.optString("addressLine1", "Darrang, Assam - 784146"),
          addressLine2 = profileObj.optString("addressLine2", "Hospital Road"),
          addressLine3 = profileObj.optString("addressLine3", "Darrang, Assam - 784146")
        )
        dao.insertBusinessProfile(profile)
      }

      RestoreResult(
        isSuccess = true,
        message = "Restore successfully completed!",
        medicinesRestored = medRestored,
        salesRestored = salesRestored,
        customersRestored = custRestored,
        suppliersRestored = supRestored,
        patientsRestored = patientRestored
      )
    } catch (e: Exception) {
      RestoreResult(
        isSuccess = false,
        message = "Restore failed: ${e.localizedMessage ?: "Invalid JSON backup file"}"
      )
    }
  }

  fun getSavedLocalBackups(context: Context): List<LocalBackupItem> {
    val backupDir = File(context.filesDir, "backups")
    if (!backupDir.exists()) return emptyList()

    return backupDir.listFiles { file -> file.extension.equals("json", ignoreCase = true) }
      ?.sortedByDescending { it.lastModified() }
      ?.map { f ->
        val sizeKb = (f.length() / 1024.0)
        val formattedSize = if (sizeKb > 1024) String.format(Locale.getDefault(), "%.1f MB", sizeKb / 1024.0) else String.format(Locale.getDefault(), "%.1f KB", sizeKb)
        val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(f.lastModified()))

        LocalBackupItem(
          fileName = f.name,
          filePath = f.absolutePath,
          fileSizeFormatted = formattedSize,
          modifiedDate = date,
          recordCountText = "Encrypted Local JSON Snapshot"
        )
      } ?: emptyList()
  }
}
