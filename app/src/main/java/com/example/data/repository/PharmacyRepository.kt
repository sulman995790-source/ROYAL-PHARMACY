package com.example.data.repository

import com.example.data.db.PharmacyDao
import com.example.data.model.BusinessProfile
import com.example.data.model.Customer
import com.example.data.model.Distributor
import com.example.data.model.Doctor
import com.example.data.model.MedicineItem
import com.example.data.model.Patient
import com.example.data.model.PurchaseInvoice
import com.example.data.model.PurchaseOrder
import com.example.data.model.SaleInvoice
import com.example.data.model.Supplier
import com.example.data.model.UdharTransaction
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PharmacyRepository(private val dao: PharmacyDao) {
  val allMedicines: Flow<List<MedicineItem>> = dao.getAllMedicines()
  val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()
  val allDistributors: Flow<List<Distributor>> = dao.getAllDistributors()
  val allDoctors: Flow<List<Doctor>> = dao.getAllDoctors()
  val allSales: Flow<List<SaleInvoice>> = dao.getAllSales()
  val allPurchases: Flow<List<PurchaseInvoice>> = dao.getAllPurchases()
  val businessProfile: Flow<BusinessProfile?> = dao.getBusinessProfile()
  val allPatients: Flow<List<Patient>> = dao.getAllPatients()
  val allSuppliers: Flow<List<Supplier>> = dao.getAllSuppliers()
  val allPurchaseOrders: Flow<List<PurchaseOrder>> = dao.getAllPurchaseOrders()
  val allUdharTransactions: Flow<List<UdharTransaction>> = dao.getAllUdharTransactions()
  val criticalLowStockMedicines: Flow<List<MedicineItem>> = dao.getCriticalLowStockMedicines()
  val allEssentialMedicines: Flow<List<MedicineItem>> = dao.getAllEssentialMedicines()
  val pendingSyncCount: Flow<Int> = dao.getPendingSyncCount()

  private suspend fun queueSync(entityType: String, entityId: Long, action: String, json: org.json.JSONObject) {
    try {
      dao.insertSyncQueueItem(
        com.example.data.model.SyncQueueItem(
          entityType = entityType,
          entityId = entityId,
          action = action,
          payloadJson = json.toString(),
          timestamp = System.currentTimeMillis()
        )
      )
    } catch (_: Exception) {}
  }

  suspend fun getCriticalLowStockMedicinesList(): List<MedicineItem> = dao.getCriticalLowStockMedicinesList()

  suspend fun updateEssentialSafetyThreshold(id: Long, isEssential: Boolean, isLifeSaving: Boolean, threshold: Int) {
    dao.updateEssentialSafetyThreshold(id, isEssential, isLifeSaving, threshold)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("isEssential", isEssential)
      put("isLifeSaving", isLifeSaving)
      put("minStockAlert", threshold)
    }
    queueSync("MEDICINE", id, "UPDATE_THRESHOLD", json)
  }

  suspend fun updateGlobalSafetyThreshold(globalThreshold: Int) {
    dao.updateGlobalSafetyThreshold(globalThreshold)
    val json = org.json.JSONObject().apply {
      put("globalSafetyThreshold", globalThreshold)
    }
    queueSync("CONFIG", 1L, "UPDATE", json)
  }

  fun searchMedicines(query: String): Flow<List<MedicineItem>> = dao.searchMedicines(query)

  fun getSubstitutesForSalt(salt: String): Flow<List<MedicineItem>> = dao.getSubstitutesForSalt(salt)

  suspend fun findMedicineByBarcode(barcode: String): MedicineItem? = dao.findMedicineByBarcode(barcode)

  suspend fun insertMedicine(medicine: MedicineItem): Long {
    val id = dao.insertMedicine(medicine)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("name", medicine.name)
      put("manufacturer", medicine.manufacturer)
      put("composition", medicine.composition)
      put("saltMolecule", medicine.saltMolecule)
      put("stockPacks", medicine.stockPacks)
      put("mrp", medicine.mrp)
      put("saleRate", medicine.saleRate)
      put("purchaseRate", medicine.purchaseRate)
      put("isEssential", medicine.isEssential)
      put("isLifeSaving", medicine.isLifeSaving)
      put("minStockAlert", medicine.minStockAlert)
    }
    queueSync("MEDICINE", id, "INSERT", json)
    return id
  }

  suspend fun updateMedicine(medicine: MedicineItem) {
    dao.updateMedicine(medicine)
    val json = org.json.JSONObject().apply {
      put("id", medicine.id)
      put("name", medicine.name)
      put("stockPacks", medicine.stockPacks)
      put("mrp", medicine.mrp)
      put("saleRate", medicine.saleRate)
      put("purchaseRate", medicine.purchaseRate)
      put("rackLocation", medicine.rackLocation)
      put("isEssential", medicine.isEssential)
      put("isLifeSaving", medicine.isLifeSaving)
      put("minStockAlert", medicine.minStockAlert)
    }
    queueSync("MEDICINE", medicine.id, "UPDATE", json)
  }

  suspend fun updateMedicineRack(id: Long, rackLocation: String) {
    dao.updateMedicineRack(id, rackLocation)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("rackLocation", rackLocation)
    }
    queueSync("MEDICINE", id, "UPDATE_RACK", json)
  }

  suspend fun deleteMedicine(medicine: MedicineItem) {
    dao.deleteMedicine(medicine)
    queueSync("MEDICINE", medicine.id, "DELETE", org.json.JSONObject().put("id", medicine.id))
  }

  suspend fun deleteMedicineById(id: Long) {
    dao.deleteMedicineById(id)
    queueSync("MEDICINE", id, "DELETE", org.json.JSONObject().put("id", id))
  }

  suspend fun reduceStockQuantity(id: Long, qty: Int) {
    dao.reduceStockQuantity(id, qty)
    queueSync("MEDICINE", id, "UPDATE_STOCK", org.json.JSONObject().put("id", id).put("reducedBy", qty))
  }

  suspend fun addStock(medicineId: Long, qty: Int) {
    dao.addStock(medicineId, qty)
    queueSync("MEDICINE", medicineId, "UPDATE_STOCK", org.json.JSONObject().put("id", medicineId).put("addedStock", qty))
  }

  suspend fun deductStock(medicineId: Long, qty: Int): Boolean {
    val success = dao.deductStock(medicineId, qty) > 0
    if (success) {
      queueSync("MEDICINE", medicineId, "UPDATE_STOCK", org.json.JSONObject().put("id", medicineId).put("deductedStock", qty))
    }
    return success
  }

  // Customers & Udhar
  suspend fun insertCustomer(customer: Customer): Long {
    val id = dao.insertCustomer(customer)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("name", customer.name)
      put("phone", customer.phone)
      put("email", customer.email)
    }
    queueSync("CUSTOMER", id, "INSERT", json)
    return id
  }

  suspend fun updateCustomer(customer: Customer) {
    dao.updateCustomer(customer)
    val json = org.json.JSONObject().apply {
      put("id", customer.id)
      put("name", customer.name)
      put("phone", customer.phone)
      put("balanceReceivable", customer.balanceReceivable)
      put("loyaltyPoints", customer.loyaltyPoints)
    }
    queueSync("CUSTOMER", customer.id, "UPDATE", json)
  }

  suspend fun adjustCustomerLoyaltyPoints(customerId: Long, deltaPoints: Int) {
    dao.adjustCustomerLoyaltyPoints(customerId, deltaPoints)
    val json = org.json.JSONObject().apply {
      put("id", customerId)
      put("deltaLoyaltyPoints", deltaPoints)
    }
    queueSync("CUSTOMER", customerId, "UPDATE_LOYALTY", json)
  }

  suspend fun recordUdharCredit(customerId: Long, customerName: String, customerPhone: String, amount: Double, note: String, invoiceNo: String) {
    dao.addUdharToCustomer(customerId, amount)
    val dateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val txn = UdharTransaction(
      customerId = customerId,
      customerName = customerName,
      customerPhone = customerPhone,
      date = dateStr,
      type = "CREDIT_GIVEN",
      amount = amount,
      balanceAfter = 0.0,
      note = note,
      invoiceNumber = invoiceNo
    )
    val txnId = dao.insertUdharTransaction(txn)
    val json = org.json.JSONObject().apply {
      put("id", txnId)
      put("customerId", customerId)
      put("customerName", customerName)
      put("type", "CREDIT_GIVEN")
      put("amount", amount)
      put("date", dateStr)
      put("invoiceNumber", invoiceNo)
    }
    queueSync("UDHAR", txnId, "INSERT", json)
  }

  suspend fun recordUdharPayment(customerId: Long, customerName: String, customerPhone: String, amount: Double, note: String) {
    dao.deductUdharFromCustomer(customerId, amount)
    val dateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val txn = UdharTransaction(
      customerId = customerId,
      customerName = customerName,
      customerPhone = customerPhone,
      date = dateStr,
      type = "PAYMENT_RECEIVED",
      amount = amount,
      balanceAfter = 0.0,
      note = note
    )
    val txnId = dao.insertUdharTransaction(txn)
    val json = org.json.JSONObject().apply {
      put("id", txnId)
      put("customerId", customerId)
      put("customerName", customerName)
      put("type", "PAYMENT_RECEIVED")
      put("amount", amount)
      put("date", dateStr)
    }
    queueSync("UDHAR", txnId, "INSERT", json)
  }

  fun getUdharForCustomer(customerId: Long): Flow<List<UdharTransaction>> = dao.getUdharTransactionsForCustomer(customerId)

  // Patients
  fun searchPatients(query: String): Flow<List<Patient>> = dao.searchPatients(query)
  suspend fun insertPatient(patient: Patient): Long {
    val id = dao.insertPatient(patient)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("name", patient.name)
      put("contactNumber", patient.contactNumber)
      put("knownAllergies", patient.knownAllergies)
    }
    queueSync("PATIENT", id, "INSERT", json)
    return id
  }
  suspend fun updatePatient(patient: Patient) {
    dao.updatePatient(patient)
    val json = org.json.JSONObject().apply {
      put("id", patient.id)
      put("name", patient.name)
      put("contactNumber", patient.contactNumber)
      put("loyaltyPoints", patient.loyaltyPoints)
      put("nextRefillDueDate", patient.nextRefillDueDate)
    }
    queueSync("PATIENT", patient.id, "UPDATE", json)
  }

  suspend fun adjustPatientLoyaltyPoints(patientId: Long, deltaPoints: Int) {
    dao.adjustPatientLoyaltyPoints(patientId, deltaPoints)
    val json = org.json.JSONObject().apply {
      put("id", patientId)
      put("deltaLoyaltyPoints", deltaPoints)
    }
    queueSync("PATIENT", patientId, "UPDATE_LOYALTY", json)
  }

  suspend fun deletePatient(patient: Patient) {
    dao.deletePatient(patient)
    queueSync("PATIENT", patient.id, "DELETE", org.json.JSONObject().put("id", patient.id))
  }

  // Suppliers
  suspend fun insertSupplier(supplier: Supplier): Long {
    val id = dao.insertSupplier(supplier)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("name", supplier.name)
      put("phone", supplier.phone)
      put("gstin", supplier.gstin)
    }
    queueSync("SUPPLIER", id, "INSERT", json)
    return id
  }
  suspend fun updateSupplier(supplier: Supplier) {
    dao.updateSupplier(supplier)
    val json = org.json.JSONObject().apply {
      put("id", supplier.id)
      put("name", supplier.name)
      put("phone", supplier.phone)
    }
    queueSync("SUPPLIER", supplier.id, "UPDATE", json)
  }
  suspend fun deleteSupplier(supplier: Supplier) {
    dao.deleteSupplier(supplier)
    queueSync("SUPPLIER", supplier.id, "DELETE", org.json.JSONObject().put("id", supplier.id))
  }

  // Purchase Orders & Receiving Stock
  suspend fun insertPurchaseOrder(po: PurchaseOrder): Long = dao.insertPurchaseOrder(po)
  suspend fun updatePurchaseOrder(po: PurchaseOrder) = dao.updatePurchaseOrder(po)
  suspend fun deletePurchaseOrder(po: PurchaseOrder) = dao.deletePurchaseOrder(po)

  suspend fun receiveStockFromPO(poId: Long, invoiceNumber: String, itemsToReceive: List<Pair<Long, Int>>) {
    for ((medId, qty) in itemsToReceive) {
      if (medId > 0 && qty > 0) {
        dao.addStock(medId, qty)
        queueSync("MEDICINE", medId, "UPDATE_STOCK", org.json.JSONObject().put("id", medId).put("addedStock", qty).put("poId", poId))
      }
    }
  }

  // Sales & Purchases
  suspend fun createSale(sale: SaleInvoice, itemQuantities: List<Pair<Long, Int>>): Long {
    val id = dao.insertSale(sale)
    for ((medId, qty) in itemQuantities) {
      if (medId > 0 && qty > 0) {
        dao.deductStock(medId, qty)
      }
    }
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("invoiceNumber", sale.invoiceNumber)
      put("customerName", sale.customerName)
      put("grandTotal", sale.grandTotal)
      put("paymentMode", sale.paymentMode)
      put("invoiceDate", sale.invoiceDate)
      put("itemCount", itemQuantities.size)
    }
    queueSync("SALE", id, "INSERT", json)
    return id
  }

  suspend fun createPurchase(purchase: PurchaseInvoice): Long {
    val id = dao.insertPurchase(purchase)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("invoiceNumber", purchase.invoiceNumber)
      put("distributorName", purchase.distributorName)
      put("totalAmount", purchase.totalAmount)
      put("invoiceDate", purchase.invoiceDate)
      put("status", purchase.status)
    }
    queueSync("PURCHASE", id, "INSERT", json)
    return id
  }

  suspend fun updatePurchase(purchase: PurchaseInvoice) {
    dao.updatePurchase(purchase)
    val json = org.json.JSONObject().apply {
      put("id", purchase.id)
      put("status", purchase.status)
      put("totalAmount", purchase.totalAmount)
    }
    queueSync("PURCHASE", purchase.id, "UPDATE", json)
  }

  suspend fun updatePurchaseStatus(id: Long, status: String) {
    dao.updatePurchaseStatus(id, status)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("status", status)
    }
    queueSync("PURCHASE", id, "UPDATE_STATUS", json)
  }

  suspend fun deletePurchase(purchase: PurchaseInvoice) {
    dao.deletePurchase(purchase)
    queueSync("PURCHASE", purchase.id, "DELETE", org.json.JSONObject().put("id", purchase.id))
  }

  // Doctor Operations
  fun searchDoctors(query: String): Flow<List<Doctor>> = dao.searchDoctors(query)
  suspend fun getDoctorByName(name: String): Doctor? = dao.getDoctorByName(name)

  suspend fun insertDoctor(doctor: Doctor): Long {
    val id = dao.insertDoctor(doctor)
    val json = org.json.JSONObject().apply {
      put("id", id)
      put("name", doctor.name)
      put("specialty", doctor.specialty)
      put("clinicHospital", doctor.clinicHospital)
      put("phone", doctor.phone)
      put("registrationNo", doctor.registrationNo)
    }
    queueSync("DOCTOR", id, "INSERT", json)
    return id
  }

  suspend fun updateDoctor(doctor: Doctor) {
    dao.updateDoctor(doctor)
    val json = org.json.JSONObject().apply {
      put("id", doctor.id)
      put("name", doctor.name)
      put("specialty", doctor.specialty)
      put("phone", doctor.phone)
    }
    queueSync("DOCTOR", doctor.id, "UPDATE", json)
  }

  suspend fun deleteDoctor(doctor: Doctor) {
    dao.deleteDoctor(doctor)
    queueSync("DOCTOR", doctor.id, "DELETE", org.json.JSONObject().put("id", doctor.id))
  }

  suspend fun deleteDoctorById(id: Long) {
    dao.deleteDoctorById(id)
    queueSync("DOCTOR", id, "DELETE", org.json.JSONObject().put("id", id))
  }

  suspend fun incrementDoctorPrescriptionCount(name: String) {
    dao.incrementDoctorPrescriptionCount(name)
  }

  suspend fun insertDistributor(distributor: Distributor): Long = dao.insertDistributor(distributor)
  suspend fun updateDistributor(distributor: Distributor) = dao.updateDistributor(distributor)
  suspend fun deleteDistributor(distributor: Distributor) = dao.deleteDistributor(distributor)
  suspend fun getDistributorByName(name: String): Distributor? = dao.getDistributorByName(name)

  suspend fun updateBusinessProfile(profile: BusinessProfile) = dao.insertBusinessProfile(profile)
}
