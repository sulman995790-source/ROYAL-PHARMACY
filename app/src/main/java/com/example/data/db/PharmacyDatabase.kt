package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.example.data.model.SyncQueueItem
import com.example.data.model.UdharTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    MedicineItem::class,
    Customer::class,
    Distributor::class,
    SaleInvoice::class,
    PurchaseInvoice::class,
    BusinessProfile::class,
    Patient::class,
    Supplier::class,
    PurchaseOrder::class,
    UdharTransaction::class,
    SyncQueueItem::class,
    Doctor::class
  ],
  version = 8,
  exportSchema = false
)
abstract class PharmacyDatabase : RoomDatabase() {
  abstract fun pharmacyDao(): PharmacyDao

  companion object {
    @Volatile
    private var INSTANCE: PharmacyDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): PharmacyDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PharmacyDatabase::class.java,
          "royal_pharmacy_db"
        )
          .addCallback(PharmacyDatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class PharmacyDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialData(database.pharmacyDao())
        }
      }
    }

    suspend fun populateInitialData(dao: PharmacyDao) {
      // 1. Initial Business Profile
      dao.insertBusinessProfile(
        BusinessProfile(
          id = 1,
          businessName = "ROYAL PHARMACY",
          ayushmanHfrId = "HFR-892104",
          ownerName = "Suleman Hoque",
          phone = "9957905450",
          email = "sulman995790@gmail.com",
          drugLicenseForm20 = "DL-ASS-20B-10928",
          form20Expiry = "15-11-2027",
          drugLicenseForm21 = "DL-ASS-21B-10929",
          form21Expiry = "15-11-2027",
          entityType = "Proprietorship",
          isTurnoverBelowGstLimit = false,
          gstin = "18AABCR1234M1Z5",
          pan = "AABCR1234M",
          tradeName = "ROYAL PHARMACY & SURGICALS",
          isCompositionScheme = false,
          addressLine1 = "Darrang, Assam - 784146",
          addressLine2 = "Hospital Road, Near Civil Hospital",
          addressLine3 = "Darrang, Assam - 784146",
          timings = "08:00 AM - 10:30 PM",
          walletBalance = 0.0
        )
      )

      // 2. Comprehensive Indian Medicine Catalog with Salts, Brands & Generic Substitutes
      val medicines = listOf(
        // Paracetamol 650mg cluster (Matches Screenshot 1)
        MedicineItem(
          name = "Paracetamol 650mg (Pacimol)",
          manufacturer = "Ipca Laboratories",
          composition = "Paracetamol 650mg",
          saltMolecule = "Paracetamol 650mg",
          category = "Tablet",
          barcode = "8901112223399",
          hsnCode = "3004",
          batchNumber = "PC6501",
          expiryDate = "04/28",
          isExpired = false,
          stockPacks = 60,
          mrp = 26.0,
          purchaseRate = 16.0,
          saleRate = 22.0,
          gstPercent = 12.0,
          rackLocation = "Rack A-03",
          scheduleDrug = "OTC",
          isGeneric = true,
          dispenseCount = 140
        ),
        MedicineItem(
          name = "Calpol 650",
          manufacturer = "GlaxoSmithKline (GSK)",
          composition = "Paracetamol 650mg",
          saltMolecule = "Paracetamol 650mg",
          category = "Tablet",
          barcode = "8901112223377",
          hsnCode = "3004",
          batchNumber = "CP2409",
          expiryDate = "12/27",
          isExpired = false,
          stockPacks = 42,
          mrp = 33.5,
          purchaseRate = 24.0,
          saleRate = 29.5,
          gstPercent = 12.0,
          rackLocation = "Rack A-02",
          scheduleDrug = "OTC",
          isGeneric = false,
          dispenseCount = 185
        ),
        MedicineItem(
          name = "Dolo 650",
          manufacturer = "Micro Labs Ltd",
          composition = "Paracetamol 650mg",
          saltMolecule = "Paracetamol 650mg",
          category = "Tablet",
          barcode = "8901112223334",
          hsnCode = "3004",
          batchNumber = "DL6501",
          expiryDate = "08/27",
          isExpired = false,
          stockPacks = 85,
          mrp = 34.0,
          purchaseRate = 23.5,
          saleRate = 30.6,
          gstPercent = 12.0,
          rackLocation = "Rack A-01",
          scheduleDrug = "OTC",
          isGeneric = false,
          dispenseCount = 320
        ),

        // Amoxicillin + Clavulanate Cluster
        MedicineItem(
          name = "Amoxyclav 625 (Generic)",
          manufacturer = "Cipla Generics",
          composition = "Amoxicillin 500mg + Clavulanic Acid 125mg",
          saltMolecule = "Amoxicillin 500mg + Clavulanic Acid 125mg",
          category = "Tablet",
          barcode = "8903334445588",
          hsnCode = "3004",
          batchNumber = "AC6251",
          expiryDate = "05/28",
          isExpired = false,
          stockPacks = 30,
          mrp = 140.0,
          purchaseRate = 75.0,
          saleRate = 98.0,
          gstPercent = 12.0,
          rackLocation = "Rack A-04",
          scheduleDrug = "Schedule H1",
          isGeneric = true,
          dispenseCount = 85
        ),
        MedicineItem(
          name = "Augmentin 625 Duo",
          manufacturer = "GlaxoSmithKline",
          composition = "Amoxicillin 500mg + Clavulanic Acid 125mg",
          saltMolecule = "Amoxicillin 500mg + Clavulanic Acid 125mg",
          category = "Tablet",
          barcode = "8903334445556",
          hsnCode = "3004",
          batchNumber = "AG2410",
          expiryDate = "05/27",
          isExpired = false,
          stockPacks = 18,
          mrp = 201.70,
          purchaseRate = 155.0,
          saleRate = 195.0,
          gstPercent = 12.0,
          rackLocation = "Rack A-03",
          scheduleDrug = "Schedule H1",
          isGeneric = false,
          dispenseCount = 210
        ),
        MedicineItem(
          name = "Clavam 625",
          manufacturer = "Alkem Laboratories",
          composition = "Amoxicillin 500mg + Clavulanic Acid 125mg",
          saltMolecule = "Amoxicillin 500mg + Clavulanic Acid 125mg",
          category = "Tablet",
          barcode = "8903334445522",
          hsnCode = "3004",
          batchNumber = "CV9902",
          expiryDate = "09/27",
          isExpired = false,
          stockPacks = 25,
          mrp = 204.0,
          purchaseRate = 152.0,
          saleRate = 190.0,
          gstPercent = 12.0,
          rackLocation = "Rack A-05",
          scheduleDrug = "Schedule H1",
          isGeneric = false,
          dispenseCount = 140
        ),

        // Pantoprazole 40mg Cluster
        MedicineItem(
          name = "Generic Pantoprazole 40mg",
          manufacturer = "Zydus Cadila",
          composition = "Pantoprazole 40mg",
          saltMolecule = "Pantoprazole 40mg",
          category = "Tablet",
          barcode = "8902223334411",
          hsnCode = "3004",
          batchNumber = "PT4001",
          expiryDate = "02/28",
          isExpired = false,
          stockPacks = 50,
          mrp = 95.0,
          purchaseRate = 40.0,
          saleRate = 55.0,
          gstPercent = 12.0,
          rackLocation = "Rack B-01",
          scheduleDrug = "Schedule H",
          isGeneric = true,
          dispenseCount = 110
        ),
        MedicineItem(
          name = "Pan 40 Tablet",
          manufacturer = "Alkem Laboratories",
          composition = "Pantoprazole 40mg",
          saltMolecule = "Pantoprazole 40mg",
          category = "Tablet",
          barcode = "8902223334445",
          hsnCode = "3004",
          batchNumber = "PN4012",
          expiryDate = "11/26",
          isExpired = false,
          stockPacks = 24,
          mrp = 155.0,
          purchaseRate = 112.0,
          saleRate = 150.0,
          gstPercent = 12.0,
          rackLocation = "Rack A-2",
          scheduleDrug = "Schedule H",
          isGeneric = false,
          dispenseCount = 260
        ),
        MedicineItem(
          name = "Pantocid 40",
          manufacturer = "Sun Pharma",
          composition = "Pantoprazole 40mg",
          saltMolecule = "Pantoprazole 40mg",
          category = "Tablet",
          barcode = "8902223334499",
          hsnCode = "3004",
          batchNumber = "PC4091",
          expiryDate = "10/27",
          isExpired = false,
          stockPacks = 20,
          mrp = 165.0,
          purchaseRate = 118.0,
          saleRate = 145.0,
          gstPercent = 12.0,
          rackLocation = "Rack B-02",
          scheduleDrug = "Schedule H",
          isGeneric = false,
          dispenseCount = 175
        ),

        // Antibiotics & Chronic care
        MedicineItem(
          name = "Azithral 500 Tablet",
          manufacturer = "Alembic Pharma",
          composition = "Azithromycin 500mg",
          saltMolecule = "Azithromycin 500mg",
          category = "Tablet",
          barcode = "8904445556667",
          hsnCode = "3004",
          batchNumber = "AZ9901",
          expiryDate = "04/27",
          isExpired = false,
          stockPacks = 30,
          mrp = 119.50,
          purchaseRate = 88.0,
          saleRate = 115.0,
          gstPercent = 12.0,
          rackLocation = "Rack B-1",
          scheduleDrug = "Schedule H1",
          dispenseCount = 190
        ),
        MedicineItem(
          name = "Telma 40 Tablet",
          manufacturer = "Glenmark",
          composition = "Telmisartan 40mg",
          saltMolecule = "Telmisartan 40mg",
          category = "Tablet",
          barcode = "8907778889990",
          hsnCode = "3004",
          batchNumber = "TL4092",
          expiryDate = "09/27",
          isExpired = false,
          stockPacks = 25,
          mrp = 125.0,
          purchaseRate = 92.0,
          saleRate = 120.0,
          gstPercent = 12.0,
          rackLocation = "Rack C-2",
          scheduleDrug = "Schedule H",
          dispenseCount = 230
        ),
        MedicineItem(
          name = "Montair LC Tablet",
          manufacturer = "Cipla",
          composition = "Montelukast 10mg + Levocetirizine 5mg",
          saltMolecule = "Montelukast + Levocetirizine",
          category = "Tablet",
          barcode = "8906667778889",
          hsnCode = "3004",
          batchNumber = "ML8831",
          expiryDate = "12/26",
          isExpired = false,
          stockPacks = 20,
          mrp = 198.0,
          purchaseRate = 145.0,
          saleRate = 190.0,
          gstPercent = 12.0,
          rackLocation = "Rack A-4",
          scheduleDrug = "Schedule H",
          dispenseCount = 180
        ),
        MedicineItem(
          name = "Shelcal 500 Tablet",
          manufacturer = "Torrent Pharma",
          composition = "Calcium Carbonate 500mg + Vitamin D3 250IU",
          saltMolecule = "Calcium + Vitamin D3",
          category = "Tablet",
          barcode = "8905556667778",
          hsnCode = "3004",
          batchNumber = "SC5001",
          expiryDate = "01/27",
          isExpired = false,
          stockPacks = 15,
          mrp = 131.0,
          purchaseRate = 96.0,
          saleRate = 128.0,
          gstPercent = 12.0,
          rackLocation = "Rack B-3",
          scheduleDrug = "OTC",
          dispenseCount = 145
        ),
        MedicineItem(
          name = "Horlicks Mother Plus Vanila 500gm",
          manufacturer = "Hindustan Unilever Ltd",
          composition = "Nutritional Health Drink with Essential Micronutrients",
          saltMolecule = "Nutritional Supplement",
          category = "OTC",
          barcode = "8901030381001",
          hsnCode = "1901",
          batchNumber = "HL5009",
          expiryDate = "12/26",
          isExpired = false,
          stockPacks = 1,
          mrp = 579.0,
          purchaseRate = 480.0,
          saleRate = 579.0,
          gstPercent = 18.0,
          rackLocation = "Rack C-1",
          scheduleDrug = "OTC"
        ),
        MedicineItem(
          name = "Pegfiber Powder 154.812gm",
          manufacturer = "Sun Pharma",
          composition = "Polyethylene Glycol + Dietary Fiber",
          saltMolecule = "PEG + Fiber",
          category = "Powder",
          barcode = "8901117092102",
          hsnCode = "3004",
          batchNumber = "PF1542",
          expiryDate = "02/26",
          isExpired = false,
          stockPacks = 0,
          mrp = 309.38,
          purchaseRate = 245.0,
          saleRate = 309.38,
          gstPercent = 12.0,
          rackLocation = "Rack B-2",
          scheduleDrug = "Schedule H"
        ),
        MedicineItem(
          name = "Combiflam Tablet",
          manufacturer = "Sanofi India",
          composition = "Ibuprofen 400mg + Paracetamol 325mg",
          saltMolecule = "Ibuprofen + Paracetamol",
          category = "Tablet",
          barcode = "8909991112223",
          hsnCode = "3004",
          batchNumber = "CF3320",
          expiryDate = "10/24",
          isExpired = true,
          stockPacks = 3,
          mrp = 45.0,
          purchaseRate = 32.0,
          saleRate = 45.0,
          gstPercent = 12.0,
          rackLocation = "Rack X-Exp",
          scheduleDrug = "Schedule H"
        ),

        // Essential & Life-Saving Emergency Medicines with defined safety thresholds
        MedicineItem(
          name = "Adrenaline 1mg/ml Injection (Epinephrine)",
          manufacturer = "Harson Laboratories",
          composition = "Adrenaline Bitartrate 1mg/ml",
          saltMolecule = "Adrenaline (Epinephrine) 1mg",
          category = "Injection",
          barcode = "8901234000011",
          hsnCode = "3004",
          batchNumber = "ADR-902",
          expiryDate = "11/27",
          isExpired = false,
          stockPacks = 2, // BELOW threshold of 5!
          mrp = 85.0,
          purchaseRate = 50.0,
          saleRate = 75.0,
          gstPercent = 12.0,
          rackLocation = "Emergency Box-1",
          scheduleDrug = "Schedule H",
          isGeneric = true,
          minStockAlert = 5,
          isEssential = true,
          isLifeSaving = true,
          dispenseCount = 45
        ),
        MedicineItem(
          name = "Atropine Sulphate 0.6mg/ml Injection",
          manufacturer = "Neon Laboratories",
          composition = "Atropine Sulphate 0.6mg",
          saltMolecule = "Atropine Sulphate 0.6mg",
          category = "Injection",
          barcode = "8901234000022",
          hsnCode = "3004",
          batchNumber = "ATR-441",
          expiryDate = "08/27",
          isExpired = false,
          stockPacks = 3, // BELOW threshold of 8!
          mrp = 45.0,
          purchaseRate = 26.0,
          saleRate = 38.0,
          gstPercent = 12.0,
          rackLocation = "Emergency Box-1",
          scheduleDrug = "Schedule H",
          isGeneric = true,
          minStockAlert = 8,
          isEssential = true,
          isLifeSaving = true,
          dispenseCount = 60
        ),
        MedicineItem(
          name = "Sorbitrate 5mg (Nitroglycerin) Sublingual",
          manufacturer = "Abbott India",
          composition = "Isosorbide Dinitrate 5mg",
          saltMolecule = "Isosorbide Dinitrate 5mg",
          category = "Tablet",
          barcode = "8901234000033",
          hsnCode = "3004",
          batchNumber = "SBT-102",
          expiryDate = "12/27",
          isExpired = false,
          stockPacks = 4, // BELOW threshold of 10!
          mrp = 52.0,
          purchaseRate = 34.0,
          saleRate = 48.0,
          gstPercent = 12.0,
          rackLocation = "Rack E-01",
          scheduleDrug = "Schedule H",
          isGeneric = false,
          minStockAlert = 10,
          isEssential = true,
          isLifeSaving = true,
          dispenseCount = 110
        ),
        MedicineItem(
          name = "Human Actrapid Insulin 40 IU/ml",
          manufacturer = "Novo Nordisk",
          composition = "Soluble Insulin 40 IU/ml",
          saltMolecule = "Insulin Regular 40 IU",
          category = "Injection",
          barcode = "8901234000044",
          hsnCode = "3004",
          batchNumber = "ACT-778",
          expiryDate = "04/27",
          isExpired = false,
          stockPacks = 2, // BELOW threshold of 6!
          mrp = 185.0,
          purchaseRate = 135.0,
          saleRate = 175.0,
          gstPercent = 12.0,
          rackLocation = "Fridge Top Shelf",
          scheduleDrug = "Schedule H",
          isGeneric = false,
          minStockAlert = 6,
          isEssential = true,
          isLifeSaving = true,
          dispenseCount = 95
        ),
        MedicineItem(
          name = "Asthalin (Salbutamol) Inhaler 100mcg",
          manufacturer = "Cipla",
          composition = "Salbutamol 100mcg per actuation",
          saltMolecule = "Salbutamol 100mcg",
          category = "Inhaler",
          barcode = "8901234000055",
          hsnCode = "3004",
          batchNumber = "AST-301",
          expiryDate = "09/27",
          isExpired = false,
          stockPacks = 3, // BELOW threshold of 8!
          mrp = 158.0,
          purchaseRate = 110.0,
          saleRate = 148.0,
          gstPercent = 12.0,
          rackLocation = "Rack R-02",
          scheduleDrug = "Schedule H",
          isGeneric = false,
          minStockAlert = 8,
          isEssential = true,
          isLifeSaving = true,
          dispenseCount = 130
        ),
        MedicineItem(
          name = "Heparin Sodium 5000 IU/ml Injection",
          manufacturer = "Gland Pharma",
          composition = "Heparin Sodium 5000 IU/ml",
          saltMolecule = "Heparin Sodium 5000 IU",
          category = "Injection",
          barcode = "8901234000066",
          hsnCode = "3004",
          batchNumber = "HEP-502",
          expiryDate = "06/27",
          isExpired = false,
          stockPacks = 1, // BELOW threshold of 5!
          mrp = 240.0,
          purchaseRate = 170.0,
          saleRate = 220.0,
          gstPercent = 12.0,
          rackLocation = "Emergency Box-2",
          scheduleDrug = "Schedule H",
          isGeneric = true,
          minStockAlert = 5,
          isEssential = true,
          isLifeSaving = true,
          dispenseCount = 35
        ),
        MedicineItem(
          name = "Hydrocortisone 100mg Injection",
          manufacturer = "Pfizer",
          composition = "Hydrocortisone Sodium Succinate 100mg",
          saltMolecule = "Hydrocortisone 100mg",
          category = "Injection",
          barcode = "8901234000077",
          hsnCode = "3004",
          batchNumber = "HYD-119",
          expiryDate = "10/27",
          isExpired = false,
          stockPacks = 2, // BELOW threshold of 6!
          mrp = 65.0,
          purchaseRate = 40.0,
          saleRate = 58.0,
          gstPercent = 12.0,
          rackLocation = "Emergency Box-1",
          scheduleDrug = "Schedule H",
          isGeneric = false,
          minStockAlert = 6,
          isEssential = true,
          isLifeSaving = true,
          dispenseCount = 50
        )
      )
      dao.insertMedicines(medicines)

      // 3. Udhar Khata Customers (Matches Screenshot 3: ₹6,860.00 Total Outstanding!)
      val customers = listOf(
        Customer(
          id = 1,
          name = "Dr. Amit Patel (Clinic)",
          phone = "+91 98765 43210",
          email = "dr.amit@patelclinic.org",
          address = "Station Road, Near Maternity Hospital",
          type = "Clinic",
          doctorName = "Dr. Amit Patel, MD",
          balanceReceivable = 3850.0,
          lastTxnDate = "05 Oct 26"
        ),
        Customer(
          id = 2,
          name = "Rajesh Kumar (Gupta Ji)",
          phone = "+91 98234 11223",
          email = "rajesh.gupta@gmail.com",
          address = "Shop 12, Main Bazaar",
          type = "Individual",
          doctorName = "Dr. B. K. Sarma",
          balanceReceivable = 1420.0,
          lastTxnDate = "04 Oct 26"
        ),
        Customer(
          id = 3,
          name = "Meena Joshi",
          phone = "+91 97654 33221",
          email = "meena.joshi@gmail.com",
          address = "Lane 3, Teachers Colony",
          type = "Individual",
          doctorName = "Dr. Roy",
          balanceReceivable = 940.0,
          lastTxnDate = "02 Oct 26"
        ),
        Customer(
          id = 4,
          name = "Sunita Sharma",
          phone = "+91 94150 99881",
          email = "sunita.sharma@yahoo.com",
          address = "House 88, Civil Line",
          type = "Individual",
          doctorName = "Dr. Amit Patel",
          balanceReceivable = 650.0,
          lastTxnDate = "28 Sep 26"
        ),
        Customer(
          id = 5,
          name = "Sample Customer",
          phone = "+91 99998 88877",
          email = "sample@example.com",
          address = "Ward 4, Hospital Road",
          type = "Individual",
          doctorName = "Dr. Amit Roy, MBBS",
          balanceReceivable = 0.0,
          lastTxnDate = "04 Apr 26"
        )
      )
      for (cust in customers) {
        dao.insertCustomer(cust)
      }

      // 4. Initial Udhar Transactions
      dao.insertUdharTransaction(
        UdharTransaction(
          customerId = 1,
          customerName = "Dr. Amit Patel (Clinic)",
          customerPhone = "+91 98765 43210",
          date = "05-10-2026",
          type = "CREDIT_GIVEN",
          amount = 1850.0,
          balanceAfter = 3850.0,
          note = "Augmentin 625 Duo (x10 packs) credit billing",
          invoiceNumber = "INV-1092"
        )
      )
      dao.insertUdharTransaction(
        UdharTransaction(
          customerId = 2,
          customerName = "Rajesh Kumar (Gupta Ji)",
          customerPhone = "+91 98234 11223",
          date = "04-10-2026",
          type = "CREDIT_GIVEN",
          amount = 620.0,
          balanceAfter = 1420.0,
          note = "Telma 40 & Pan 40 monthly prescription",
          invoiceNumber = "INV-1088"
        )
      )

      // 5. Initial Suppliers
      val suppliers = listOf(
        Supplier(
          name = "Sun Pharma Distribution Hub",
          companyName = "Sun Pharmaceutical Industries Ltd",
          contactPerson = "Anupam Sen",
          phone = "+91 98765 43210",
          email = "orders.assam@sunpharma.com",
          gstin = "18AABCS9988K1Z3",
          drugLicenseNo = "DL-ASS-20B-7788",
          address = "GS Road Logistics Park, Guwahati",
          latitude = 26.1445,
          longitude = 91.7362,
          outstandingPayable = 957.0
        ),
        Supplier(
          name = "Cipla Regional Wholesale Agency",
          companyName = "Cipla Healthcare Ltd",
          contactPerson = "Rakesh Barua",
          phone = "+91 98640 12345",
          email = "orders@ciplaregional.com",
          gstin = "18AACCC4455D1ZQ",
          drugLicenseNo = "DL-ASS-20B-3344",
          address = "Paltan Bazaar Commercial Hub, Guwahati",
          latitude = 26.1805,
          longitude = 91.7539,
          outstandingPayable = 0.0
        ),
        Supplier(
          name = "MedPlus Pharma Logistics",
          companyName = "MedPlus Supply Chain",
          contactPerson = "Deepa Kalita",
          phone = "+91 94350 98765",
          email = "supply@medpluslogistics.in",
          gstin = "18AAPPL1234P1Z2",
          drugLicenseNo = "DL-ASS-20B-1122",
          address = "Near Central Warehouse, Mangaldai, Darrang",
          latitude = 26.4357,
          longitude = 92.0354,
          outstandingPayable = 1250.0
        )
      )
      for (sup in suppliers) {
        dao.insertSupplier(sup)
      }

      // 6. Initial Purchase Orders
      dao.insertPurchaseOrder(
        PurchaseOrder(
          poNumber = "PO-2026-101",
          supplierId = 1,
          supplierName = "Sun Pharma Distribution Hub",
          orderDate = "01-10-2026",
          expectedDeliveryDate = "06-10-2026",
          status = "RECEIVED",
          itemsJson = "Dolo 650mg (x50 packs), Pan 40 Tablet (x30 packs)",
          totalAmount = 4535.0,
          receivedDate = "03-10-2026",
          supplierInvoiceNumber = "SP-INV-889"
        )
      )
      dao.insertPurchaseOrder(
        PurchaseOrder(
          poNumber = "PO-2026-102",
          supplierId = 2,
          supplierName = "Cipla Regional Wholesale Agency",
          orderDate = "04-10-2026",
          expectedDeliveryDate = "08-10-2026",
          status = "ORDERED",
          itemsJson = "Montair LC (x40 packs), Amoxyclav 625 (x20 packs)",
          totalAmount = 7760.0
        )
      )

      // 7. Initial Patient Profiles
      val patients = listOf(
        Patient(
          name = "Ramesh Hazarika",
          dob = "14-03-1968",
          age = 58,
          gender = "Male",
          contactNumber = "9864112233",
          address = "Station Road, Ward 4, Mangaldai",
          medicalHistory = "Hypertension (Stage 2), Type 2 Diabetes Mellitus",
          knownAllergies = "Penicillin, Sulfa drugs (Severe rash)",
          currentMedications = "Telma 40 OD, Glycomet GP 1 BD",
          emergencyContact = "9864112234 (Son: Bikram)",
          createdDate = "12-01-2026"
        ),
        Patient(
          name = "Ananya Baruah",
          dob = "22-09-1992",
          age = 34,
          gender = "Female",
          contactNumber = "9706554433",
          address = "Teachers Colony, Darrang",
          medicalHistory = "Bronchial Asthma, Seasonal Allergic Rhinitis",
          knownAllergies = "Aspirin, NSAIDs (Causes Bronchospasm)",
          currentMedications = "Montair LC HS, Asthalin Inhaler PRN",
          emergencyContact = "9706554430 (Husband: Dipak)",
          createdDate = "05-02-2026"
        ),
        Patient(
          name = "Mohammed Ali",
          dob = "01-05-1959",
          age = 67,
          gender = "Male",
          contactNumber = "9954009988",
          address = "Civil Hospital Road, Darrang",
          medicalHistory = "Ischemic Heart Disease (Post-Stent), Hyperlipidemia",
          knownAllergies = "None reported",
          currentMedications = "Ecosprin 75mg, Atorva 20mg",
          emergencyContact = "9954009989 (Daughter: Fatima)",
          createdDate = "18-03-2026"
        )
      )
      for (pat in patients) {
        dao.insertPatient(pat)
      }

      // 8. Initial Sales
      dao.insertSale(
        SaleInvoice(
          invoiceNumber = "Invoice/1",
          invoiceDate = "07-04-2026",
          billingTo = "Cash Sale",
          customerName = "Cash Sale",
          itemsJson = "Dolo 650mg (x5), Combiflam (x2)",
          totalItemsCount = 2,
          subtotal = 300.0,
          gstTotal = 36.0,
          grandTotal = 300.0,
          paymentMode = "Cash",
          isPaid = true
        )
      )
      dao.insertSale(
        SaleInvoice(
          invoiceNumber = "Invoice/2",
          invoiceDate = "07-04-2026",
          billingTo = "Customer",
          customerName = "Sample Customer",
          customerPhone = "+91 99998 88877",
          doctorName = "Dr. Amit Roy",
          itemsJson = "Pan 40 Tablet (x1), Augmentin 625 (x1)",
          totalItemsCount = 2,
          subtotal = 297.0,
          gstTotal = 35.64,
          grandTotal = 297.0,
          paymentMode = "UPI",
          isPaid = true
        )
      )

      // 9. Initial Distributors
      val distributors = listOf(
        Distributor(
          id = 1,
          name = "Sun Pharma Distribution Hub",
          phone = "+91 98765 43210",
          email = "orders.assam@sunpharma.com",
          gstin = "18AABCS9988K1Z3",
          dlNumber = "DL-ASS-20B-7788",
          isGstRegistered = true,
          balancePayable = 957.0,
          lastTxnDate = "07-10-2026"
        ),
        Distributor(
          id = 2,
          name = "Cipla Regional Wholesale Agency",
          phone = "+91 98640 12345",
          email = "orders@ciplaregional.com",
          gstin = "18AACCC4455D1ZQ",
          dlNumber = "DL-ASS-20B-3344",
          isGstRegistered = true,
          balancePayable = 0.0,
          lastTxnDate = "04-10-2026"
        ),
        Distributor(
          id = 3,
          name = "MedPlus Pharma Logistics",
          phone = "+91 94350 98765",
          email = "supply@medpluslogistics.in",
          gstin = "18AAPPL1234P1Z2",
          dlNumber = "DL-ASS-20B-1122",
          isGstRegistered = true,
          balancePayable = 1250.0,
          lastTxnDate = "02-10-2026"
        ),
        Distributor(
          id = 4,
          name = "Ipca Laboratories Hub",
          phone = "+91 98540 22334",
          email = "dist.ipca@assampharma.in",
          gstin = "18AAACI3322K1Z9",
          dlNumber = "DL-ASS-20B-9988",
          isGstRegistered = true,
          balancePayable = 450.0,
          lastTxnDate = "01-10-2026"
        )
      )
      for (dist in distributors) {
        dao.insertDistributor(dist)
      }

      // 10. Initial Purchases
      dao.insertPurchase(
        PurchaseInvoice(
          id = 1,
          distributorName = "Sun Pharma Distribution Hub",
          distributorGstin = "18AABCS9988K1Z3",
          invoiceNumber = "PUR-2741",
          invoiceDate = "07-10-2026",
          totalAmount = 957.0,
          itemsCount = 3,
          status = "Unpaid"
        )
      )
      dao.insertPurchase(
        PurchaseInvoice(
          id = 2,
          distributorName = "MedPlus Pharma Logistics",
          distributorGstin = "18AAPPL1234P1Z2",
          invoiceNumber = "PUR-2690",
          invoiceDate = "02-10-2026",
          totalAmount = 1250.0,
          itemsCount = 5,
          status = "Paid"
        )
      )

      // 11. Initial Doctors Directory
      val initialDoctors = listOf(
        Doctor(
          name = "Dr. Amit Patel, MD",
          degree = "MBBS, MD (Medicine)",
          specialty = "General Physician",
          clinicHospital = "Patel Care Clinic, Hospital Road",
          phone = "+91 98765 43210",
          email = "dr.amit@patelclinic.org",
          registrationNo = "MCI-49201",
          address = "Hospital Road, Darrang",
          prescriptionCount = 54,
          commissionPercentage = 5.0,
          notes = "Prefers prescribing Azithral, Pan 40 and Augmentin"
        ),
        Doctor(
          name = "Dr. A. K. Sharma",
          degree = "MBBS, MS (General Surgery)",
          specialty = "Surgeon & Trauma Care",
          clinicHospital = "Civil Hospital Darrang",
          phone = "+91 94350 11223",
          email = "dr.aksharma@civildarrang.gov.in",
          registrationNo = "ASSAM-7821",
          address = "Civil Hospital Campus, Mangaldai",
          prescriptionCount = 38,
          commissionPercentage = 0.0,
          notes = "Civil Hospital Chief Medical Officer"
        ),
        Doctor(
          name = "Dr. P. Baruah, MD (Pediatrics)",
          degree = "MBBS, DCH, MD (Pediatrics)",
          specialty = "Pediatrician / Child Specialist",
          clinicHospital = "Care Child Clinic",
          phone = "+91 98640 99887",
          email = "drpbaruah@carechild.in",
          registrationNo = "MCI-33891",
          address = "Teachers Colony, Darrang",
          prescriptionCount = 27,
          commissionPercentage = 5.0,
          notes = "Specialist for pediatric suspensions and infant drops"
        ),
        Doctor(
          name = "Dr. N. Hazarika, MD (Cardiology)",
          degree = "MBBS, MD, DM (Cardiology)",
          specialty = "Cardiologist",
          clinicHospital = "Apex Heart & Medical Center",
          phone = "+91 99540 88776",
          email = "drhazarika@apexheart.com",
          registrationNo = "MCI-22198",
          address = "Station Road, Mangaldai",
          prescriptionCount = 22,
          commissionPercentage = 5.0,
          notes = "Prescribes Telma 40, Ecosprin, Rosuvas regularly"
        )
      )
      for (doc in initialDoctors) {
        dao.insertDoctor(doc)
      }
    }
  }
}
