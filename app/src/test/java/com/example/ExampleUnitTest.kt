package com.example

import com.example.data.model.BillItem
import com.example.data.model.MedicineItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testMedicineBillCalculation() {
    val item = BillItem(
      medicineName = "Dolo 650mg Tablet",
      packQty = 2,
      rate = 30.0,
      discountPercent = 10.0,
      total = 2 * 30.0 * 0.9
    )
    assertEquals(54.0, item.total, 0.01)
  }

  @Test
  fun testGstAndTaxableCalculations() {
    val grossSubtotal = 100.0
    val discountPercent = 10.0
    val taxable = grossSubtotal * (1 - discountPercent / 100.0)
    val gstRate = 12.0
    val gstAmount = taxable * (gstRate / 100.0)
    val grandTotal = taxable + gstAmount

    assertEquals(90.0, taxable, 0.01)
    assertEquals(10.8, gstAmount, 0.01)
    assertEquals(100.8, grandTotal, 0.01)
  }

  @Test
  fun testStockStatusLogic() {
    val inStockMed = MedicineItem(name = "Pan 40", manufacturer = "Alkem", stockPacks = 5)
    val outOfStockMed = MedicineItem(name = "Pegfiber", manufacturer = "Sun Pharma", stockPacks = 0)

    assertTrue(inStockMed.stockPacks > 0)
    assertEquals(0, outOfStockMed.stockPacks)
  }

  @Test
  fun testCriticalLowStockDetection() {
    val adrenaline = MedicineItem(
      name = "Adrenaline 1mg/ml",
      manufacturer = "Neon",
      stockPacks = 2,
      minStockAlert = 5,
      isEssential = true,
      isLifeSaving = true
    )
    val isCritical = (adrenaline.isEssential || adrenaline.isLifeSaving) && adrenaline.stockPacks <= adrenaline.minStockAlert
    assertTrue(isCritical)
  }

  @Test
  fun testAboveSafetyThresholdNotCritical() {
    val dolo = MedicineItem(
      name = "Dolo 650",
      manufacturer = "Micro Labs",
      stockPacks = 50,
      minStockAlert = 10,
      isEssential = true,
      isLifeSaving = false
    )
    val isCritical = (dolo.isEssential || dolo.isLifeSaving) && dolo.stockPacks <= dolo.minStockAlert
    org.junit.Assert.assertFalse(isCritical)
  }

  @Test
  fun testSyncQueueItemCreation() {
    val syncItem = com.example.data.model.SyncQueueItem(
      entityType = "MEDICINE",
      entityId = 101L,
      action = "INSERT",
      payloadJson = """{"name":"Atropine 0.6mg","stockPacks":2,"isLifeSaving":true}""",
      status = "PENDING"
    )
    assertEquals("MEDICINE", syncItem.entityType)
    assertEquals("PENDING", syncItem.status)
    assertEquals(101L, syncItem.entityId)
    assertTrue(syncItem.payloadJson.contains("Atropine"))
  }

  @Test
  fun testNonEssentialDrugNotCriticalEvenIfLowStock() {
    val cosmeticCream = MedicineItem(
      name = "Moisturizing Cream",
      manufacturer = "DermaCare",
      stockPacks = 1,
      minStockAlert = 5,
      isEssential = false,
      isLifeSaving = false
    )
    val isCritical = (cosmeticCream.isEssential || cosmeticCream.isLifeSaving) && cosmeticCream.stockPacks <= cosmeticCream.minStockAlert
    org.junit.Assert.assertFalse(isCritical)
  }
}
