package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BusinessProfile
import com.example.data.model.CategoryReorderThreshold
import com.example.data.model.Customer
import com.example.data.model.Distributor
import com.example.data.model.Doctor
import com.example.data.model.MedicineItem
import com.example.data.model.Patient
import com.example.data.model.PurchaseInvoice
import com.example.data.model.PurchaseOrder
import com.example.data.model.SaleInvoice
import com.example.data.model.Supplier
import com.example.data.model.SyncQueueItem
import com.example.data.model.UdharTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {
  // Medicines / Stock
  @Query("SELECT * FROM medicines ORDER BY name ASC")
  fun getAllMedicines(): Flow<List<MedicineItem>>

  @Query("SELECT * FROM medicines WHERE name LIKE '%' || :query || '%' OR manufacturer LIKE '%' || :query || '%' OR composition LIKE '%' || :query || '%' OR saltMolecule LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%'")
  fun searchMedicines(query: String): Flow<List<MedicineItem>>

  @Query("SELECT * FROM medicines WHERE saltMolecule = :salt OR composition LIKE '%' || :salt || '%' ORDER BY saleRate ASC")
  fun getSubstitutesForSalt(salt: String): Flow<List<MedicineItem>>

  @Query("SELECT * FROM medicines WHERE barcode = :barcode LIMIT 1")
  suspend fun findMedicineByBarcode(barcode: String): MedicineItem?

  @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
  suspend fun getMedicineById(id: Long): MedicineItem?

  @Query("SELECT * FROM medicines WHERE LOWER(name) = LOWER(:name) LIMIT 1")
  suspend fun getMedicineByName(name: String): MedicineItem?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMedicine(medicine: MedicineItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMedicines(medicines: List<MedicineItem>)

  @Update
  suspend fun updateMedicine(medicine: MedicineItem)

  @Delete
  suspend fun deleteMedicine(medicine: MedicineItem)

  @Query("DELETE FROM medicines WHERE id = :id")
  suspend fun deleteMedicineById(id: Long)

  @Query("UPDATE medicines SET stockPacks = CASE WHEN stockPacks >= :qty THEN stockPacks - :qty ELSE 0 END WHERE id = :id")
  suspend fun reduceStockQuantity(id: Long, qty: Int)

  @Query("UPDATE medicines SET stockPacks = stockPacks - :qty, dispenseCount = dispenseCount + :qty WHERE id = :id AND stockPacks >= :qty")
  suspend fun deductStock(id: Long, qty: Int): Int

  @Query("UPDATE medicines SET rackLocation = :rackLocation WHERE id = :id")
  suspend fun updateMedicineRack(id: Long, rackLocation: String)

  @Query("UPDATE medicines SET stockPacks = stockPacks + :qty WHERE id = :id")
  suspend fun addStock(id: Long, qty: Int)

  // Customers & Udhar
  @Query("SELECT * FROM customers ORDER BY balanceReceivable DESC, name ASC")
  fun getAllCustomers(): Flow<List<Customer>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomer(customer: Customer): Long

  @Update
  suspend fun updateCustomer(customer: Customer)

  @Query("UPDATE customers SET balanceReceivable = balanceReceivable + :amount WHERE id = :customerId")
  suspend fun addUdharToCustomer(customerId: Long, amount: Double)

  @Query("UPDATE customers SET balanceReceivable = CASE WHEN balanceReceivable >= :amount THEN balanceReceivable - :amount ELSE 0.0 END WHERE id = :customerId")
  suspend fun deductUdharFromCustomer(customerId: Long, amount: Double)

  @Query("UPDATE customers SET loyaltyPoints = CASE WHEN loyaltyPoints + :points >= 0 THEN loyaltyPoints + :points ELSE 0 END WHERE id = :customerId")
  suspend fun adjustCustomerLoyaltyPoints(customerId: Long, points: Int)

  @Query("UPDATE patients SET loyaltyPoints = CASE WHEN loyaltyPoints + :points >= 0 THEN loyaltyPoints + :points ELSE 0 END WHERE id = :patientId")
  suspend fun adjustPatientLoyaltyPoints(patientId: Long, points: Int)

  // Udhar Transactions
  @Query("SELECT * FROM udhar_transactions ORDER BY id DESC")
  fun getAllUdharTransactions(): Flow<List<UdharTransaction>>

  @Query("SELECT * FROM udhar_transactions WHERE customerId = :customerId ORDER BY id DESC")
  fun getUdharTransactionsForCustomer(customerId: Long): Flow<List<UdharTransaction>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUdharTransaction(transaction: UdharTransaction): Long

  // Patients
  @Query("SELECT * FROM patients ORDER BY name ASC")
  fun getAllPatients(): Flow<List<Patient>>

  @Query("SELECT * FROM patients WHERE name LIKE '%' || :query || '%' OR contactNumber LIKE '%' || :query || '%'")
  fun searchPatients(query: String): Flow<List<Patient>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPatient(patient: Patient): Long

  @Update
  suspend fun updatePatient(patient: Patient)

  @Delete
  suspend fun deletePatient(patient: Patient)

  // Suppliers
  @Query("SELECT * FROM suppliers ORDER BY name ASC")
  fun getAllSuppliers(): Flow<List<Supplier>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSupplier(supplier: Supplier): Long

  @Update
  suspend fun updateSupplier(supplier: Supplier)

  @Delete
  suspend fun deleteSupplier(supplier: Supplier)

  // Purchase Orders
  @Query("SELECT * FROM purchase_orders ORDER BY id DESC")
  fun getAllPurchaseOrders(): Flow<List<PurchaseOrder>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPurchaseOrder(po: PurchaseOrder): Long

  @Update
  suspend fun updatePurchaseOrder(po: PurchaseOrder)

  @Delete
  suspend fun deletePurchaseOrder(po: PurchaseOrder)

  // Distributors
  @Query("SELECT * FROM distributors ORDER BY name ASC")
  fun getAllDistributors(): Flow<List<Distributor>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDistributor(distributor: Distributor): Long

  // Sales
  @Query("SELECT * FROM sale_invoices ORDER BY id DESC")
  fun getAllSales(): Flow<List<SaleInvoice>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSale(sale: SaleInvoice): Long

  // Purchases
  @Query("SELECT * FROM purchase_invoices ORDER BY id DESC")
  fun getAllPurchases(): Flow<List<PurchaseInvoice>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPurchase(purchase: PurchaseInvoice): Long

  @Update
  suspend fun updatePurchase(purchase: PurchaseInvoice)

  @Delete
  suspend fun deletePurchase(purchase: PurchaseInvoice)

  @Query("UPDATE purchase_invoices SET status = :status WHERE id = :id")
  suspend fun updatePurchaseStatus(id: Long, status: String)

  // Doctors
  @Query("SELECT * FROM doctors ORDER BY name ASC")
  fun getAllDoctors(): Flow<List<Doctor>>

  @Query("SELECT * FROM doctors WHERE name LIKE '%' || :query || '%' OR specialty LIKE '%' || :query || '%' OR clinicHospital LIKE '%' || :query || '%'")
  fun searchDoctors(query: String): Flow<List<Doctor>>

  @Query("SELECT * FROM doctors WHERE LOWER(name) = LOWER(:name) LIMIT 1")
  suspend fun getDoctorByName(name: String): Doctor?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDoctor(doctor: Doctor): Long

  @Update
  suspend fun updateDoctor(doctor: Doctor)

  @Delete
  suspend fun deleteDoctor(doctor: Doctor)

  @Query("DELETE FROM doctors WHERE id = :id")
  suspend fun deleteDoctorById(id: Long)

  @Query("UPDATE doctors SET prescriptionCount = prescriptionCount + 1 WHERE LOWER(name) = LOWER(:name)")
  suspend fun incrementDoctorPrescriptionCount(name: String)

  // Distributors update/delete
  @Update
  suspend fun updateDistributor(distributor: Distributor)

  @Delete
  suspend fun deleteDistributor(distributor: Distributor)

  @Query("SELECT * FROM distributors WHERE LOWER(name) = LOWER(:name) LIMIT 1")
  suspend fun getDistributorByName(name: String): Distributor?

  // Business Profile
  @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
  fun getBusinessProfile(): Flow<BusinessProfile?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBusinessProfile(profile: BusinessProfile)

  // Essential & Life-Saving Low Stock Monitoring
  @Query("SELECT * FROM medicines WHERE (isEssential = 1 OR isLifeSaving = 1) AND stockPacks <= minStockAlert ORDER BY stockPacks ASC, name ASC")
  fun getCriticalLowStockMedicines(): Flow<List<MedicineItem>>

  @Query("SELECT * FROM medicines WHERE (isEssential = 1 OR isLifeSaving = 1) AND stockPacks <= minStockAlert ORDER BY stockPacks ASC, name ASC")
  suspend fun getCriticalLowStockMedicinesList(): List<MedicineItem>

  @Query("SELECT * FROM medicines WHERE isEssential = 1 OR isLifeSaving = 1 ORDER BY name ASC")
  fun getAllEssentialMedicines(): Flow<List<MedicineItem>>

  @Query("UPDATE medicines SET isEssential = :isEssential, isLifeSaving = :isLifeSaving, minStockAlert = :safetyThreshold WHERE id = :id")
  suspend fun updateEssentialSafetyThreshold(id: Long, isEssential: Boolean, isLifeSaving: Boolean, safetyThreshold: Int)

  @Query("UPDATE medicines SET minStockAlert = :globalThreshold WHERE (isEssential = 1 OR isLifeSaving = 1)")
  suspend fun updateGlobalSafetyThreshold(globalThreshold: Int)

  // Sync Queue (Local-First Offline -> Firebase Sync)
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSyncQueueItem(item: SyncQueueItem): Long

  @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY timestamp ASC")
  fun getPendingSyncItems(): Flow<List<SyncQueueItem>>

  @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY timestamp ASC")
  suspend fun getPendingSyncItemsList(): List<SyncQueueItem>

  @Query("UPDATE sync_queue SET status = 'SYNCED' WHERE id = :id")
  suspend fun markSyncItemCompleted(id: Long)

  @Query("UPDATE sync_queue SET status = 'FAILED', errorMessage = :error, retryCount = retryCount + 1 WHERE id = :id")
  suspend fun markSyncItemFailed(id: Long, error: String)

  @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
  suspend fun clearSyncedQueue()

  @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
  fun getPendingSyncCount(): Flow<Int>
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategoryThreshold(threshold: CategoryReorderThreshold)

  @Query("SELECT * FROM category_reorder_thresholds")
  fun getAllCategoryThresholds(): Flow<List<CategoryReorderThreshold>>
}
