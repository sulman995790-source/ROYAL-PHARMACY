package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineItem
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class AdditionalBatchRecord(
  var id: Long = System.currentTimeMillis() + (0..999).random(),
  var batchNumber: String,
  var expiryDate: String,
  var manufacturer: String,
  var rackLocation: String,
  var stock: String
)

data class StockAdjustmentLogItem(
  val timestamp: String,
  val type: String,
  val quantityChange: String,
  val remainingStock: Int,
  val staffName: String
)

/**
 * Individual medicine edit/view modal in the Drug Database (Stock Screen).
 * Features:
 * - Tab 0: Edit Details & Multiple Batch Records ("Add Batch" functionality)
 * - Tab 1: Stock Adjustment History logs for this specific medicine
 */
@Composable
fun MedicineDetailEditDialog(
  item: MedicineItem,
  onDismiss: () -> Unit,
  onSave: (MedicineItem) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Edit & Batches, 1: Stock Adjustment History

  var name by remember { mutableStateOf(item.name) }
  var composition by remember { mutableStateOf(item.composition.ifBlank { item.saltMolecule }) }
  var hsnCode by remember { mutableStateOf(item.hsnCode.ifBlank { "3004" }) }
  var category by remember { mutableStateOf(item.category) }
  var mrpText by remember { mutableStateOf(if (item.mrp > 0) item.mrp.toString() else "35.0") }
  var reorderLevelText by remember { mutableStateOf(item.minStockAlert.toString()) }

  // Primary Batch Section State
  var batchNumber by remember {
    mutableStateOf(item.batchNumber.ifBlank { "BX-" + (1000..9999).random() })
  }
  var expiryDate by remember {
    mutableStateOf(item.expiryDate.ifBlank { "2027-12-31" })
  }
  var manufacturer by remember {
    mutableStateOf(item.manufacturer.ifBlank { "Cipla Healthcare Ltd" })
  }
  var rackLocation by remember {
    mutableStateOf(item.rackLocation.ifBlank { "Rack A-1" })
  }
  var stockPacksText by remember {
    mutableStateOf(item.stockPacks.toString())
  }

  // Additional Batch Records list ("Add Batch" requirement)
  var additionalBatches by remember {
    mutableStateOf(listOf<AdditionalBatchRecord>())
  }

  // Sample Stock Adjustment History logs for this specific medicine
  val stockAdjustmentLogs = remember {
    listOf(
      StockAdjustmentLogItem("2026-10-08 14:22", "Initial Stock Inflow", "+${item.stockPacks.coerceAtLeast(10)} units", item.stockPacks.coerceAtLeast(10), "Suleman Hoque (Owner)"),
      StockAdjustmentLogItem("2026-10-06 09:15", "Distributor Restock PO", "+25 units", item.stockPacks.coerceAtLeast(10) + 25, "Supplier Delivery"),
      StockAdjustmentLogItem("2026-10-02 16:40", "POS Counter Dispense", "-4 units", (item.stockPacks.coerceAtLeast(10) + 25) - 4, "Staff POS Register")
    )
  }

  val mfgPresets = listOf("Cipla Healthcare", "Sun Pharma", "Abbott", "Mankind Pharma", "Dr. Reddy's", "Micro Labs")
  val rackPresets = listOf("Rack A-1", "Rack A-2", "Rack B-1", "Rack B-3", "Cold Storage Shelf 1")
  val categoryPresets = listOf("Tablet", "Syrup", "Injection", "Ointment", "Antibiotic", "Analgesic", "OTC")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(RoyalMagenta.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Medication, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Edit Medicine & Batch Management", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text(item.name, fontSize = 11.sp, color = TextMuted)
            }
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs: Edit & Batches vs Stock Adjustment History
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFFF1F5F9),
          indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
              TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = RoyalMagenta
              )
            }
          },
          divider = {}
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Details & Batches", fontSize = 11.5.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Stock History (${stockAdjustmentLogs.size})", fontSize = 11.5.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 520.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (selectedTab == 0) {
          // Top Banner Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFDF2F8))
              .border(1.dp, Color(0xFFFBCFE8), RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(name.ifBlank { "Medicine Name" }, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = RoyalMagenta)
                Text(composition.ifBlank { "Generic Salt / Formula" }, fontSize = 11.sp, color = Color(0xFF831843))
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Current MRP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("₹$mrpText", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RoyalMagenta)
              }
            }
          }

          // Section 1: General Medicine Information
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "GENERAL MEDICINE INFORMATION",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF475569)
            )

            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Brand Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("input_edit_medicine_name")
            )

            OutlinedTextField(
              value = composition,
              onValueChange = { composition = it },
              label = { Text("Generic Composition / Salt") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = mrpText,
                onValueChange = { mrpText = it },
                label = { Text("MRP (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
              )

              OutlinedTextField(
                value = reorderLevelText,
                onValueChange = { reorderLevelText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Reorder Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
              )

              OutlinedTextField(
                value = hsnCode,
                onValueChange = { hsnCode = it },
                label = { Text("HSN Code") },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
            }

            // Category Chips
            Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              categoryPresets.forEach { cat ->
                FilterChip(
                  selected = category.equals(cat, ignoreCase = true),
                  onClick = { category = cat },
                  label = { Text(cat, fontSize = 10.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalNavy,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }
          }

          // =====================================================================
          // SECTION 2: PRIMARY BATCH DETAILS
          // =====================================================================
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFEEF2FF))
              .border(1.5.dp, Color(0xFFC7D2FE), RoundedCornerShape(12.dp))
              .padding(12.dp)
              .testTag("section_batch_details")
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFF4F46E5)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text("Primary Batch Details", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E1B4B))
                    Text("Batch #1 (Active Inventory Batch)", fontSize = 10.sp, color = Color(0xFF4338CA))
                  }
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4338CA))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text("PRIMARY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }

              OutlinedTextField(
                value = batchNumber,
                onValueChange = { batchNumber = it.uppercase(Locale.getDefault()) },
                label = { Text("Batch Number *") },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E1B4B)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color(0xFF4F46E5),
                  unfocusedBorderColor = Color(0xFFC7D2FE),
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().testTag("input_batch_number")
              )

              // Expiry Date Field & Presets
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                  value = expiryDate,
                  onValueChange = { expiryDate = it },
                  label = { Text("Expiry Date (YYYY-MM-DD or MM/YY) *") },
                  leadingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                  },
                  singleLine = true,
                  textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B)
                  ),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4F46E5),
                    unfocusedBorderColor = Color(0xFFC7D2FE),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("input_batch_expiry_date")
                )

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text("Presets:", fontSize = 10.sp, color = TextMuted)
                  listOf(6 to "+6 Mo", 12 to "+1 Yr", 24 to "+2 Yrs").forEach { (months, label) ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(4.dp))
                        .clickable {
                          val cal = Calendar.getInstance()
                          cal.add(Calendar.MONTH, months)
                          expiryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
                    }
                  }
                }
              }

              // Manufacturer Field & Suggestion Chips
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                  value = manufacturer,
                  onValueChange = { manufacturer = it },
                  label = { Text("Manufacturer *") },
                  leadingIcon = {
                    Icon(Icons.Default.Domain, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                  },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4F46E5),
                    unfocusedBorderColor = Color(0xFFC7D2FE),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("input_batch_manufacturer")
                )

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  mfgPresets.forEach { mfg ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(4.dp))
                        .clickable { manufacturer = mfg }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(mfg, fontSize = 10.sp, color = Color(0xFF334155))
                    }
                  }
                }
              }

              // Storage Rack Location Field & Presets
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                  value = rackLocation,
                  onValueChange = { rackLocation = it },
                  label = { Text("Storage Rack Location *") },
                  leadingIcon = {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                  },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4F46E5),
                    unfocusedBorderColor = Color(0xFFC7D2FE),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.fillMaxWidth().testTag("input_batch_rack_location")
                )

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  rackPresets.forEach { rack ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(4.dp))
                        .clickable { rackLocation = rack }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(rack, fontSize = 10.sp, color = Color(0xFF334155))
                    }
                  }
                }
              }

              // Allocated Stock for Primary Batch
              OutlinedTextField(
                value = stockPacksText,
                onValueChange = { stockPacksText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Allocated Stock for Primary Batch (units/packs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color(0xFF4F46E5),
                  unfocusedBorderColor = Color(0xFFC7D2FE),
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().testTag("input_batch_stock")
              )
            }
          }

          // =====================================================================
          // SECTION 3: 'ADD BATCH' FUNCTIONALITY FOR MULTIPLE BATCH RECORDS
          // =====================================================================
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "ADDITIONAL BATCH RECORDS (${additionalBatches.size})",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF475569)
                )
                Text("Manage multiple manufacturer batches for this medicine", fontSize = 10.sp, color = TextMuted)
              }

              OutlinedButton(
                onClick = {
                  val newBatchNum = "BX-" + (1000..9999).random()
                  val cal = Calendar.getInstance()
                  cal.add(Calendar.MONTH, 12)
                  val defaultExp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                  additionalBatches = additionalBatches + AdditionalBatchRecord(
                    batchNumber = newBatchNum,
                    expiryDate = defaultExp,
                    manufacturer = manufacturer,
                    rackLocation = rackLocation,
                    stock = "10"
                  )
                },
                modifier = Modifier.testTag("btn_add_additional_batch")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = RoyalMagenta)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Batch", fontSize = 11.sp, color = RoyalMagenta)
              }
            }

            // Render each additional batch record card
            additionalBatches.forEachIndexed { index, batchRecord ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFF8FAFC))
                  .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                  .padding(10.dp)
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("Batch #${index + 2}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    IconButton(
                      onClick = {
                        additionalBatches = additionalBatches.filterIndexed { idx, _ -> idx != index }
                      },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = "Delete Batch", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedTextField(
                      value = batchRecord.batchNumber,
                      onValueChange = { newVal ->
                        additionalBatches = additionalBatches.mapIndexed { idx, b ->
                          if (idx == index) b.copy(batchNumber = newVal.uppercase(Locale.getDefault())) else b
                        }
                      },
                      label = { Text("Batch #") },
                      singleLine = true,
                      modifier = Modifier.weight(1.2f)
                    )

                    OutlinedTextField(
                      value = batchRecord.expiryDate,
                      onValueChange = { newVal ->
                        additionalBatches = additionalBatches.mapIndexed { idx, b ->
                          if (idx == index) b.copy(expiryDate = newVal) else b
                        }
                      },
                      label = { Text("Expiry") },
                      singleLine = true,
                      modifier = Modifier.weight(1.3f)
                    )

                    OutlinedTextField(
                      value = batchRecord.stock,
                      onValueChange = { newVal ->
                        additionalBatches = additionalBatches.mapIndexed { idx, b ->
                          if (idx == index) b.copy(stock = newVal.filter { c -> c.isDigit() }) else b
                        }
                      },
                      label = { Text("Qty") },
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      singleLine = true,
                      modifier = Modifier.weight(0.8f)
                    )
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedTextField(
                      value = batchRecord.manufacturer,
                      onValueChange = { newVal ->
                        additionalBatches = additionalBatches.mapIndexed { idx, b ->
                          if (idx == index) b.copy(manufacturer = newVal) else b
                        }
                      },
                      label = { Text("Manufacturer") },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                      value = batchRecord.rackLocation,
                      onValueChange = { newVal ->
                        additionalBatches = additionalBatches.mapIndexed { idx, b ->
                          if (idx == index) b.copy(rackLocation = newVal) else b
                        }
                      },
                      label = { Text("Rack") },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                  }
                }
              }
            }
          }

        } else {
          // =====================================================================
          // TAB 1: STOCK ADJUSTMENT HISTORY
          // =====================================================================
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Stock Adjustment History Log", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(RoyalMagenta.copy(alpha = 0.1f))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("${stockAdjustmentLogs.size} Events", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
              }
            }

            Text(
              "Complete audit trail of stock additions, POS dispenses, and distributor purchase orders for ${item.name}.",
              fontSize = 11.sp,
              color = TextMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            stockAdjustmentLogs.forEach { log ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFF8FAFC))
                  .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                  .padding(12.dp)
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(log.type, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    Text(log.quantityChange, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (log.quantityChange.startsWith("+")) Color(0xFF16A34A) else Color(0xFFDC2626))
                  }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Resulting Stock: ${log.remainingStock} packs", fontSize = 10.5.sp, color = TextDark)
                    Text(log.timestamp, fontSize = 10.sp, color = TextMuted)
                  }
                  Text("Staff: ${log.staffName}", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          // Sum up primary stock + additional batches stock
          val primaryStock = stockPacksText.toIntOrNull() ?: item.stockPacks
          val additionalStockSum = additionalBatches.sumOf { it.stock.toIntOrNull() ?: 0 }
          val totalStock = primaryStock + additionalStockSum

          val updated = item.copy(
            name = name.trim().ifBlank { item.name },
            composition = composition.trim(),
            saltMolecule = composition.trim(),
            hsnCode = hsnCode.trim().ifBlank { "3004" },
            category = category,
            mrp = mrpText.toDoubleOrNull() ?: item.mrp,
            minStockAlert = reorderLevelText.toIntOrNull() ?: item.minStockAlert,
            batchNumber = batchNumber.trim().uppercase(Locale.getDefault()),
            expiryDate = expiryDate.trim(),
            manufacturer = manufacturer.trim(),
            rackLocation = rackLocation.trim(),
            stockPacks = totalStock
          )
          onSave(updated)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        modifier = Modifier.testTag("btn_save_medicine_and_batch")
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Medicine & Batch")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}
