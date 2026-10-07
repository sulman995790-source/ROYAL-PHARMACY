package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineItem
import com.example.data.model.POItem
import com.example.data.model.PurchaseOrder
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenLight
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SmartRestockSuggestion(
  val medicine: MedicineItem,
  val dailyVelocity: Double, // units sold per day
  val daysRemaining: Int, // current stock / daily velocity
  val urgencyTier: RestockUrgency,
  val defaultSuggestedQty: Int,
  val estimatedCost: Double,
  val seasonalNote: String = "",
  val distributorName: String
)

enum class RestockUrgency {
  CRITICAL_STOCKOUT, // < 3 days remaining 🚨
  REORDER_SOON,      // 3-7 days remaining ⚠️
  SEASONAL_SURGE,    // Seasonal demand surge 🌧
  HEALTHY_BUFFER,    // 7-30 days 📦
  EXCESS_STOCK       // > 60 days 🧊
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartInventorySuggestionScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val medicines by viewModel.allMedicines.collectAsState()
  val suppliers by viewModel.allSuppliers.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()

  var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "CRITICAL", "REORDER", "SEASONAL", "EXCESS"
  var selectedSupplierFilter by remember { mutableStateOf("ALL") }

  // User-adjusted purchase quantities
  val customQuantities = remember { mutableStateMapOf<Long, Int>() }
  val selectedItemsForPO = remember { mutableStateMapOf<Long, Boolean>() }

  // Compute smart restocking suggestions from sales and stock data
  val suggestions = remember(medicines) {
    medicines.map { med ->
      // Simulate realistic daily sales run-rate (e.g. 0.5 to 12 packs/day) based on price and category
      val mockDailyRate = when {
        med.stockPacks <= 5 -> 3.5
        med.name.contains("Paracetamol", ignoreCase = true) || med.name.contains("Dolo", ignoreCase = true) -> 8.0
        med.name.contains("Azithral", ignoreCase = true) || med.name.contains("Augmentin", ignoreCase = true) -> 4.5
        med.name.contains("Pan", ignoreCase = true) || med.name.contains("Pantocid", ignoreCase = true) -> 6.0
        med.name.contains("Telma", ignoreCase = true) || med.name.contains("Glycomet", ignoreCase = true) -> 5.0
        med.name.contains("Cetirizine", ignoreCase = true) || med.name.contains("Montair", ignoreCase = true) -> 4.0
        else -> ((med.id % 4) + 1.2).coerceAtLeast(0.8)
      }

      val daysLeft = if (mockDailyRate > 0) (med.stockPacks / mockDailyRate).toInt() else 30

      val urgency = when {
        med.stockPacks == 0 || daysLeft <= 2 -> RestockUrgency.CRITICAL_STOCKOUT
        daysLeft in 3..7 -> RestockUrgency.REORDER_SOON
        med.name.contains("Paracetamol", ignoreCase = true) || med.name.contains("Cetirizine", ignoreCase = true) || med.name.contains("Cough", ignoreCase = true) -> RestockUrgency.SEASONAL_SURGE
        daysLeft > 60 -> RestockUrgency.EXCESS_STOCK
        else -> RestockUrgency.HEALTHY_BUFFER
      }

      // Calculate suggested order to cover 21 days + min safety stock
      val targetBufferQty = (mockDailyRate * 21).toInt().coerceAtLeast(med.minStockAlert * 2)
      val suggestedOrder = (targetBufferQty - med.stockPacks).coerceAtLeast(10)
      val unitRate = if (med.purchaseRate > 0) med.purchaseRate else med.mrp * 0.75

      val seasonal = when {
        med.name.contains("Paracetamol", ignoreCase = true) || med.name.contains("Dolo", ignoreCase = true) -> "Monsoon Viral Surge (+40% demand)"
        med.name.contains("Montair", ignoreCase = true) || med.name.contains("Cetirizine", ignoreCase = true) -> "Seasonal Allergy & Flu Wave"
        med.name.contains("Telma", ignoreCase = true) || med.name.contains("Glycomet", ignoreCase = true) -> "Chronic Monthly Refill Spike"
        else -> ""
      }

      val dist = med.manufacturer.ifBlank { "Cipla Healthcare Ltd" }

      SmartRestockSuggestion(
        medicine = med,
        dailyVelocity = mockDailyRate,
        daysRemaining = daysLeft,
        urgencyTier = urgency,
        defaultSuggestedQty = suggestedOrder,
        estimatedCost = suggestedOrder * unitRate,
        seasonalNote = seasonal,
        distributorName = dist
      )
    }
  }

  // Filtered List
  val filteredSuggestions = remember(suggestions, selectedFilter, selectedSupplierFilter) {
    suggestions.filter { item ->
      val matchesFilter = when (selectedFilter) {
        "CRITICAL" -> item.urgencyTier == RestockUrgency.CRITICAL_STOCKOUT
        "REORDER" -> item.urgencyTier == RestockUrgency.REORDER_SOON
        "SEASONAL" -> item.urgencyTier == RestockUrgency.SEASONAL_SURGE
        "EXCESS" -> item.urgencyTier == RestockUrgency.EXCESS_STOCK
        else -> item.urgencyTier != RestockUrgency.EXCESS_STOCK && item.urgencyTier != RestockUrgency.HEALTHY_BUFFER
      }

      val matchesSupplier = if (selectedSupplierFilter == "ALL") true else item.distributorName.contains(selectedSupplierFilter, ignoreCase = true)

      matchesFilter && matchesSupplier
    }
  }

  // Calculate totals
  val criticalCount = suggestions.count { it.urgencyTier == RestockUrgency.CRITICAL_STOCKOUT }
  val reorderCount = suggestions.count { it.urgencyTier == RestockUrgency.REORDER_SOON }
  val seasonalCount = suggestions.count { it.urgencyTier == RestockUrgency.SEASONAL_SURGE }

  val totalSelectedCost = filteredSuggestions.filter { selectedItemsForPO[it.medicine.id] != false }.sumOf { item ->
    val qty = customQuantities[item.medicine.id] ?: item.defaultSuggestedQty
    val rate = if (item.medicine.purchaseRate > 0) item.medicine.purchaseRate else item.medicine.mrp * 0.75
    qty * rate
  }

  fun shareWhatsAppRestockOrder() {
    val selectedList = filteredSuggestions.filter { selectedItemsForPO[it.medicine.id] != false }
    if (selectedList.isEmpty()) {
      Toast.makeText(context, "No items selected for restock order", Toast.LENGTH_SHORT).show()
      return
    }

    val msg = buildString {
      append("📋 *${profile.businessName} — PURCHASE RESTOCK ORDER*\n")
      append("═══════════════════════════════\n")
      append("📅 Date: ${SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())}\n\n")
      append("📦 *Requested Medicines (${selectedList.size} items):*\n")
      selectedList.forEachIndexed { idx, item ->
        val qty = customQuantities[item.medicine.id] ?: item.defaultSuggestedQty
        append("${idx + 1}. *${item.medicine.name}* — ${qty} Packs (${item.medicine.rackLocation})\n")
      }
      append("\n💰 *Estimated Total Value:* ₹${String.format(Locale.getDefault(), "%.2f", totalSelectedCost)}\n")
      append("═══════════════════════════════\n")
      append("Please dispatch urgently. Generated via Royal Pharmacy ERP.")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, msg)
    }
    context.startActivity(Intent.createChooser(intent, "Send Restock PO via WhatsApp"))
  }

  fun createDraftPurchaseOrder() {
    val selectedList = filteredSuggestions.filter { selectedItemsForPO[it.medicine.id] != false }
    if (selectedList.isEmpty()) {
      Toast.makeText(context, "Select at least 1 item to draft PO", Toast.LENGTH_SHORT).show()
      return
    }

    val poItems = selectedList.map { item ->
      val qty = customQuantities[item.medicine.id] ?: item.defaultSuggestedQty
      val unitRate = if (item.medicine.purchaseRate > 0) item.medicine.purchaseRate else item.medicine.mrp * 0.75
      POItem(
        medicineId = item.medicine.id,
        medicineName = item.medicine.name,
        requestedPacks = qty,
        estimatedUnitRate = unitRate,
        batchNumber = item.medicine.batchNumber,
        expiryDate = item.medicine.expiryDate
      )
    }

    val po = PurchaseOrder(
      poNumber = "PO-AUTO-${System.currentTimeMillis() % 10000}",
      supplierId = 1L,
      supplierName = selectedList.firstOrNull()?.distributorName ?: "Central Distributor",
      orderDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
      expectedDeliveryDate = "Within 48 Hours",
      status = "ORDERED",
      totalAmount = totalSelectedCost,
      itemsJson = org.json.JSONArray().apply {
        poItems.forEach { p ->
          put(org.json.JSONObject().apply {
            put("medicineId", p.medicineId)
            put("medicineName", p.medicineName)
            put("requestedPacks", p.requestedPacks)
            put("estimatedUnitRate", p.estimatedUnitRate)
          })
        }
      }.toString()
    )

    viewModel.addPurchaseOrder(po)
    Toast.makeText(context, "Created Purchase Order: ${po.poNumber}", Toast.LENGTH_LONG).show()
    viewModel.navigateTo(Screen.PURCHASE_ORDERS)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Column {
            Text(
              text = "Smart Inventory Suggestions",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Velocity Run-Rate & AI Restock Radar",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(RoyalMagentaLight),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
        }
      }

      // 2. Summary KPI Ribbon
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Critical Stockout KPI
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text("Critical (<3d)", fontSize = 10.sp, color = TextMuted)
            Text("$criticalCount Items", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusRed)
          }
        }

        // Reorder Soon KPI
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text("Reorder (3-7d)", fontSize = 10.sp, color = TextMuted)
            Text("$reorderCount Items", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
          }
        }

        // Seasonal Surge KPI
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF93C5FD)),
          modifier = Modifier.weight(1f)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text("Seasonal Wave", fontSize = 10.sp, color = TextMuted)
            Text("$seasonalCount Items", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
          }
        }
      }

      // 3. Filter Tabs Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(
          "ALL" to "All Recommended (${criticalCount + reorderCount + seasonalCount})",
          "CRITICAL" to "🚨 Critical ($criticalCount)",
          "REORDER" to "⚠️ Reorder ($reorderCount)",
          "SEASONAL" to "🌧 Seasonal ($seasonalCount)",
          "EXCESS" to "🧊 Slow Moving"
        ).forEach { (id, label) ->
          val isSel = selectedFilter == id
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSel) RoyalNavy else GrayBackground)
              .border(1.dp, if (isSel) RoyalNavy else CardBorder, RoundedCornerShape(8.dp))
              .clickable { selectedFilter = id }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = label,
              fontSize = 11.5.sp,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
              color = if (isSel) Color.White else TextDark
            )
          }
        }
      }

      // 4. Suggestion List
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredSuggestions) { item ->
          val med = item.medicine
          val currentQty = customQuantities[med.id] ?: item.defaultSuggestedQty
          val isChecked = selectedItemsForPO[med.id] ?: true
          val unitCost = if (med.purchaseRate > 0) med.purchaseRate else med.mrp * 0.75

          val (urgencyColor, urgencyBg, urgencyText) = when (item.urgencyTier) {
            RestockUrgency.CRITICAL_STOCKOUT -> Triple(StatusRed, Color(0xFFFEF2F2), "CRITICAL STOCKOUT")
            RestockUrgency.REORDER_SOON -> Triple(Color(0xFFD97706), Color(0xFFFFFBEB), "REORDER SOON")
            RestockUrgency.SEASONAL_SURGE -> Triple(Color(0xFF2563EB), Color(0xFFEFF6FF), "SEASONAL SURGE")
            RestockUrgency.HEALTHY_BUFFER -> Triple(StatusGreen, Color(0xFFF0FDF4), "HEALTHY BUFFER")
            RestockUrgency.EXCESS_STOCK -> Triple(Color(0xFF64748B), Color(0xFFF1F5F9), "SLOW MOVING")
          }

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              // Row 1: Checkbox, Medicine Name, and Urgency Badge
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = isChecked,
                  onCheckedChange = { selectedItemsForPO[med.id] = it },
                  colors = CheckboxDefaults.colors(checkedColor = RoyalMagenta)
                )

                Column(modifier = Modifier.weight(1f)) {
                  Text(med.name, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text(
                    text = "${med.composition} • Rack: ${med.rackLocation}",
                    fontSize = 10.5.sp,
                    color = TextMuted
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(urgencyBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(urgencyText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = urgencyColor)
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              // Row 2: Stock Velocity & Days Left
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Current Stock", fontSize = 9.5.sp, color = TextMuted)
                  Text("${med.stockPacks} Packs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (med.stockPacks <= med.minStockAlert) StatusRed else TextDark)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Daily Velocity", fontSize = 9.5.sp, color = TextMuted)
                  Text(String.format(Locale.getDefault(), "%.1f packs/day", item.dailyVelocity), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text("Days Left", fontSize = 9.5.sp, color = TextMuted)
                  Text(
                    text = if (item.daysRemaining <= 0) "Stocked Out" else "${item.daysRemaining} Days",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = urgencyColor
                  )
                }
              }

              // Seasonal wave note
              if (item.seasonalNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(item.seasonalNote, fontSize = 10.5.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Medium)
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              HorizontalDivider(color = Color(0xFFF1F5F9))
              Spacer(modifier = Modifier.height(8.dp))

              // Row 3: Suggested Order Quantity & Stepper
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Suggested Order (21-day run)", fontSize = 10.sp, color = TextMuted)
                  Text("Est. Cost: ₹${String.format(Locale.getDefault(), "%.2f", currentQty * unitCost)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(4.dp)
                ) {
                  IconButton(
                    onClick = {
                      if (currentQty > 5) customQuantities[med.id] = currentQty - 5
                    },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                  }

                  Text(
                    text = "$currentQty Packs",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(horizontal = 8.dp)
                  )

                  IconButton(
                    onClick = {
                      customQuantities[med.id] = currentQty + 5
                    },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                  }
                }
              }
            }
          }
        }
      }

      // 5. Bottom Action Station (Auto-Draft PO & WhatsApp Share)
      Card(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Total PO Estimated Cost", fontSize = 11.sp, color = TextMuted)
              Text("₹${String.format(Locale.getDefault(), "%.2f", totalSelectedCost)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedButton(
                onClick = { shareWhatsAppRestockOrder() },
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
              ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("WhatsApp", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
              }

              Button(
                onClick = { createDraftPurchaseOrder() },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.testTag("btn_create_auto_po")
              ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text("Auto-Draft PO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      }
    }
  }
}
