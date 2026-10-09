package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import com.example.util.MedicineQrPayload
import com.example.util.QrCodeGeneratorUtil
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineItem
import com.example.service.InvoicePrinterService
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenLight
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val medicines by viewModel.allMedicines.collectAsState()
  val criticalMedicines by viewModel.criticalLowStockMedicines.collectAsState()
  var searchQuery by remember { mutableStateOf(viewModel.globalSearchQuery.value) }
  var filterCategory by remember { mutableStateOf("All") }
  var filterRack by remember { mutableStateOf("All Racks") }

  var showAddOptionsSheet by remember { mutableStateOf(false) }
  var showManualAddDialog by remember { mutableStateOf(false) }
  var medicineToDelete by remember { mutableStateOf<MedicineItem?>(null) }
  var medicineToEditRack by remember { mutableStateOf<MedicineItem?>(null) }
  var medicineToEditReorder by remember { mutableStateOf<MedicineItem?>(null) }
  var medicineForQrBatch by remember { mutableStateOf<MedicineItem?>(null) }
  var medicineForThermalBarcode by remember { mutableStateOf<MedicineItem?>(null) }
  var medicineToEditDetails by remember { mutableStateOf<MedicineItem?>(null) }

  val searchTokens = searchQuery.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
  val filteredMedicines = medicines.filter { item ->
    val textToSearch = "${item.name} ${item.manufacturer} ${item.composition} ${item.barcode} ${item.rackLocation}".lowercase(Locale.ROOT)
    val matchesSearch = searchTokens.isEmpty() || searchTokens.all { token -> textToSearch.contains(token.lowercase(Locale.ROOT)) }

    val matchesCategory = when (filterCategory) {
      "In Stock" -> item.stockPacks > 0
      "Stock Out" -> item.stockPacks == 0
      "Critical & Life-Saving" -> (item.isEssential || item.isLifeSaving) && item.stockPacks <= item.minStockAlert
      "Essential" -> item.isEssential || item.isLifeSaving
      "Expiring" -> item.expiryDate.contains("26") || item.isExpired
      "Schedule H" -> item.scheduleDrug.contains("H")
      else -> true
    }

    val matchesRack = when (filterRack) {
      "All Racks" -> true
      "Rack A" -> item.rackLocation.contains("Rack A", ignoreCase = true)
      "Rack B" -> item.rackLocation.contains("Rack B", ignoreCase = true)
      "Rack C" -> item.rackLocation.contains("Rack C", ignoreCase = true)
      "Cold Storage / Fridge" -> item.rackLocation.contains("Fridge", ignoreCase = true) || item.rackLocation.contains("Cold", ignoreCase = true)
      "Counter / OTC" -> item.rackLocation.contains("Counter", ignoreCase = true) || item.rackLocation.contains("OTC", ignoreCase = true)
      else -> true
    }

    matchesSearch && matchesCategory && matchesRack
  }

  val totalStockVal = medicines.filter { !it.isExpired }.sumOf { it.stockPacks * it.saleRate }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Header (View Stocks & Quick Stations)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "View Stocks",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = "${medicines.size} Items | ${String.format(Locale.getDefault(), "₹%.2f", totalStockVal)}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Inventory QR Station Button
          Button(
            onClick = { viewModel.navigateTo(Screen.INVENTORY_QR) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 4.dp).testTag("btn_stock_qr_station")
          ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("QR Station", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.BATCH_TRACKING) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 4.dp).testTag("btn_stock_batch_tracking")
          ) {
            Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Batches", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.STOCK_TRANSFER) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 4.dp).testTag("btn_stock_transfer")
          ) {
            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Transfer", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.CRITICAL_STOCK_ALERTS) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 4.dp).testTag("btn_stock_low_alert")
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Alerts", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 2. Search Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = {
            searchQuery = it
            viewModel.globalSearchQuery.value = it
          },
          placeholder = { Text("Search by name, salt, rack (e.g. Rack A)...", fontSize = 13.sp, color = TextMuted) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
          },
          trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = {
                  searchQuery = ""
                  viewModel.globalSearchQuery.value = ""
                }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                }
              }
              IconButton(onClick = { viewModel.navigateTo(Screen.INVENTORY_QR) }) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR", tint = RoyalMagenta)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RoyalMagenta,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = GrayBackground,
            unfocusedContainerColor = GrayBackground
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_stock_search")
        )
      }

      // 3. Category Filter Chips (Row 1)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("All", "In Stock", "Stock Out", "Critical & Life-Saving", "Essential", "Expiring", "Schedule H").forEach { cat ->
          val isSelected = filterCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { filterCategory = cat },
            label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalNavy,
              selectedLabelColor = Color.White,
              containerColor = GrayBackground,
              labelColor = TextDark
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) RoyalNavy else CardBorder,
              selectedBorderColor = RoyalNavy
            )
          )
        }
      }

      // 4. Physical Rack Location Filter Chips (Row 2 - NEW RACK ORGANIZER)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(14.dp))
        Text("RACK:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))

        listOf("All Racks", "Rack A", "Rack B", "Rack C", "Cold Storage / Fridge", "Counter / OTC").forEach { rackOpt ->
          val isSelected = filterRack == rackOpt
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) Color(0xFFB45309) else Color(0xFFFEF3C7))
              .border(1.dp, if (isSelected) Color(0xFFB45309) else Color(0xFFFDE68A), RoundedCornerShape(6.dp))
              .clickable { filterRack = rackOpt }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = rackOpt,
              fontSize = 10.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Color(0xFF78350F)
            )
          }
        }
      }

      // 5. Stock Items List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
      ) {
        items(filteredMedicines, key = { it.id }) { med ->
          StockItemCard(
            item = med,
            onStockAdjust = { delta ->
              if (delta < 0 && med.stockPacks <= 0) {
                // Cannot reduce below 0
              } else {
                viewModel.updateMedicine(med.copy(stockPacks = (med.stockPacks + delta).coerceAtLeast(0)))
              }
            },
            onAddToCart = {
              viewModel.addMedicineToCart(med, 1)
              Toast.makeText(context, "Added ${med.name} to bill!", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = {
              medicineToDelete = med
            },
            onEditRack = {
              medicineToEditRack = med
            },
            onEditReorderLevel = {
              medicineToEditReorder = med
            },
            onGenerateQrBatch = {
              medicineForQrBatch = med
            },
            onPrintThermalBarcode = {
              medicineForThermalBarcode = med
            },
            onEditMedicineDetails = {
              medicineToEditDetails = med
            }
          )
        }

        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }

    // Floating Button: "+ Add Item"
    Button(
      onClick = { showAddOptionsSheet = true },
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_add_stock_item")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Add Item", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }

    // 6. Add Item 2-Options Bottom Sheet
    if (showAddOptionsSheet) {
      val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
      ModalBottomSheet(
        onDismissRequest = { showAddOptionsSheet = false },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .padding(bottom = 24.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Add Item to Stock",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            IconButton(onClick = { showAddOptionsSheet = false }) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Option 1: Fill item details manually (WITH RACK LOCATION)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
              .clickable {
                showAddOptionsSheet = false
                showManualAddDialog = true
              }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RoyalMagentaLight),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.EditNote, contentDescription = null, tint = RoyalMagenta)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Fill item details manually",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "Fill name, MRP, batch, and Rack/Shelf details",
                fontSize = 12.sp,
                color = TextMuted
              )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextLight, modifier = Modifier.size(14.dp))
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Option 2: Scan QR / Barcode to autofill
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
              .clickable {
                showAddOptionsSheet = false
                viewModel.navigateTo(Screen.INVENTORY_QR)
              }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0F2FE)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF0284C7))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Scan QR / Barcode & Assign Rack",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFFEBEE))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("Try QR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                }
              }
              Text(
                text = "Instant camera recognition & shelf assignment",
                fontSize = 12.sp,
                color = TextMuted
              )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextLight, modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    // 7. Manual Add Medicine Dialog WITH RACK LOCATION FIELD
    if (showManualAddDialog) {
      ManualAddMedicineDialog(
        onDismiss = { showManualAddDialog = false },
        onSave = { newMed ->
          viewModel.addNewMedicine(newMed)
          showManualAddDialog = false
          Toast.makeText(context, "Added ${newMed.name} to ${newMed.rackLocation}!", Toast.LENGTH_SHORT).show()
        }
      )
    }

    // 8. Quick Edit Rack Location Dialog
    if (medicineToEditRack != null) {
      val med = medicineToEditRack!!
      var updatedRack by remember { mutableStateOf(med.rackLocation) }
      val rackPresets = listOf(
        "Rack A-1", "Rack A-2", "Rack A-3",
        "Rack B-1", "Rack B-2", "Rack B-3",
        "Rack C-1", "Rack C-2", "Cold Storage / Fridge", "OTC Counter"
      )

      AlertDialog(
        onDismissRequest = { medicineToEditRack = null },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFB45309))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Rack Location", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(med.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("Select preset or type custom shelf location:", fontSize = 11.sp, color = TextMuted)

            // Rack preset pills
            rackPresets.chunked(3).forEach { rowPresets ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                rowPresets.forEach { preset ->
                  val isSel = updatedRack == preset
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isSel) Color(0xFFB45309) else GrayBackground)
                      .border(1.dp, if (isSel) Color(0xFFB45309) else CardBorder, RoundedCornerShape(6.dp))
                      .clickable { updatedRack = preset }
                      .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = preset,
                      fontSize = 10.sp,
                      fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSel) Color.White else TextDark,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
              value = updatedRack,
              onValueChange = { updatedRack = it },
              label = { Text("Rack / Shelf Position") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.updateMedicineRack(med.id, updatedRack)
              medicineToEditRack = null
              Toast.makeText(context, "Rack location updated to $updatedRack!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309))
          ) {
            Text("Save Rack")
          }
        },
        dismissButton = {
          TextButton(onClick = { medicineToEditRack = null }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // 8b. Quick Edit Reorder Threshold Dialog
    if (medicineToEditReorder != null) {
      val med = medicineToEditReorder!!
      var updatedThresholdText by remember { mutableStateOf(med.minStockAlert.toString()) }
      val presetLevels = listOf(2, 5, 8, 10, 15, 20, 50, 100)

      AlertDialog(
        onDismissRequest = { medicineToEditReorder = null },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFB45309))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Customize Reorder Threshold", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(med.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("Set the minimum stock level at which reorder alerts will trigger for this medicine.", fontSize = 12.sp, color = TextMuted)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              items(presetLevels) { preset ->
                FilterChip(
                  selected = updatedThresholdText == preset.toString(),
                  onClick = { updatedThresholdText = preset.toString() },
                  label = { Text("$preset units", fontSize = 11.sp) }
                )
              }
            }

            OutlinedTextField(
              value = updatedThresholdText,
              onValueChange = { updatedThresholdText = it.filter { char -> char.isDigit() } },
              label = { Text("Reorder Level (packs / units)") },
              singleLine = true,
              keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val newThreshold = updatedThresholdText.toIntOrNull() ?: med.minStockAlert
              if (newThreshold >= 1) {
                viewModel.updateMedicineSafetyThreshold(med.id, med.isEssential, med.isLifeSaving, newThreshold)
                medicineToEditReorder = null
                Toast.makeText(context, "Reorder level for ${med.name} set to $newThreshold packs!", Toast.LENGTH_SHORT).show()
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309))
          ) {
            Text("Save Threshold")
          }
        },
        dismissButton = {
          TextButton(onClick = { medicineToEditReorder = null }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // 9. Delete / Reduce Stock Dialog
    if (medicineToDelete != null) {
      val med = medicineToDelete!!
      DeleteStockDialog(
        item = med,
        onDismiss = { medicineToDelete = null },
        onDeleteFully = {
          viewModel.deleteMedicineById(med.id)
          medicineToDelete = null
        },
        onReduceQuantity = { qty, reason ->
          viewModel.reduceMedicineQuantity(med.id, qty, reason)
          medicineToDelete = null
        }
      )
    }

    // 10. Generate QR Batch Printable Dialog Modal
    if (medicineForQrBatch != null) {
      QrBatchModalDialog(
        item = medicineForQrBatch!!,
        onDismiss = { medicineForQrBatch = null }
      )
    }

    // 11. Generate & Print Thermal Barcode Label Dialog (HSN Code)
    if (medicineForThermalBarcode != null) {
      val med = medicineForThermalBarcode!!
      ThermalBarcodeLabelDialog(
        item = med,
        onDismiss = { medicineForThermalBarcode = null },
        onPrint = {
          InvoicePrinterService.printThermalBarcodeLabel(context, med)
          medicineForThermalBarcode = null
        }
      )
    }

    // 12. Individual Medicine Edit/View Modal with BATCH DETAILS Section
    if (medicineToEditDetails != null) {
      val med = medicineToEditDetails!!
      MedicineDetailEditDialog(
        item = med,
        onDismiss = { medicineToEditDetails = null },
        onSave = { updatedMed ->
          viewModel.updateMedicine(updatedMed)
          medicineToEditDetails = null
          Toast.makeText(context, "Saved ${updatedMed.name} and Batch Details!", Toast.LENGTH_SHORT).show()
        }
      )
    }
  }
}

@Composable
fun StockItemCard(
  item: MedicineItem,
  onStockAdjust: (Int) -> Unit,
  onAddToCart: () -> Unit,
  onDeleteClick: () -> Unit,
  onEditRack: () -> Unit,
  onEditReorderLevel: () -> Unit,
  onGenerateQrBatch: () -> Unit,
  onPrintThermalBarcode: () -> Unit = {},
  onEditMedicineDetails: () -> Unit = {}
) {
  val initials = item.name.take(2).uppercase()
  val isInStock = item.stockPacks > 0
  val isLowStock = item.stockPacks <= item.minStockAlert

  val cardContainerColor = when {
    item.stockPacks == 0 -> Color(0xFFFFF1F2)
    isLowStock -> Color(0xFFFFFBEB)
    else -> Color.White
  }

  val cardBorderColor = when {
    item.stockPacks == 0 -> Color(0xFFFECACA)
    isLowStock -> Color(0xFFFDE68A)
    else -> CardBorder
  }

  val animatedContainerColor by animateColorAsState(
    targetValue = cardContainerColor,
    label = "cardBgColor"
  )
  val animatedBorderColor by animateColorAsState(
    targetValue = cardBorderColor,
    label = "cardBorderColor"
  )

  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = animatedContainerColor),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(animatedBorderColor)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("stock_item_${item.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      // Initials Square
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (isLowStock) Color(0xFFFEF3C7) else RoyalMagentaLight),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = initials,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (isLowStock) Color(0xFFD97706) else RoyalMagenta
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.name,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.clickable { onEditMedicineDetails() }
        )
        Text(
          text = item.manufacturer,
          fontSize = 11.sp,
          color = TextMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Stock Status Pill & RACK BADGE ROW
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (isInStock) (if (isLowStock) Color(0xFFFEF3C7) else StatusGreenLight) else StatusRedLight)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (!isInStock) "Stock Out" else if (isLowStock) "Low Stock" else "In Stock",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (!isInStock) StatusRed else if (isLowStock) Color(0xFFB45309) else StatusGreen
            )
          }

          if (isLowStock && isInStock) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFEF3C7))
                .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "⚠️ REORDER (Min ${item.minStockAlert})",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF92400E)
              )
            }
          }

          // Clickable REORDER THRESHOLD BADGE (Allows Editing Reorder Threshold directly from list)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (isLowStock) Color(0xFFFEF3C7) else Color(0xFFF1F5F9))
              .border(1.dp, if (isLowStock) Color(0xFFF59E0B) else Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
              .clickable { onEditReorderLevel() }
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = if (isLowStock) Color(0xFFB45309) else TextMuted, modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Min: ${item.minStockAlert}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLowStock) Color(0xFF78350F) else TextDark
              )
              Spacer(modifier = Modifier.width(3.dp))
              Icon(Icons.Default.Edit, contentDescription = null, tint = if (isLowStock) Color(0xFFB45309) else TextMuted, modifier = Modifier.size(9.dp))
            }
          }

          // Prominent Clickable RACK BADGE (Allows Editing directly from list)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFFEF3C7))
              .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(4.dp))
              .clickable { onEditRack() }
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = item.rackLocation.ifBlank { "Rack A-1" },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78350F)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(9.dp))
            }
          }
        }

        if (item.isLifeSaving || item.isEssential) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(if (item.isLifeSaving) Color(0xFFFEE2E2) else Color(0xFFFEF3C7))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (item.isLifeSaving) "LIFE-SAVING" else "ESSENTIAL",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (item.isLifeSaving) StatusRed else Color(0xFFD97706)
              )
            }
            if (item.stockPacks <= item.minStockAlert) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🚨 Min: ${item.minStockAlert}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = StatusRed
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Expiry clock and Batch
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = TextLight,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = item.expiryDate,
            fontSize = 11.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.width(10.dp))
          Icon(
            imageVector = Icons.Default.Inventory2,
            contentDescription = null,
            tint = TextLight,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          AnimatedContent(
            targetState = item.stockPacks,
            transitionSpec = { (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut()) },
            label = "stockPacksAnim"
          ) { count ->
            Text(
              text = "$count Packs",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = if (count <= item.minStockAlert) StatusRed else TextDark
            )
          }
        }
      }

      // Price & Quick Actions
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "₹${item.mrp}",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Decrement button
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(GrayBackground)
              .border(1.dp, CardBorder, CircleShape)
              .clickable { onStockAdjust(-1) },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextDark, modifier = Modifier.size(14.dp))
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Increment button
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(RoyalMagentaLight)
              .clickable { onStockAdjust(1) },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", tint = RoyalMagenta, modifier = Modifier.size(14.dp))
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Add to Billing Cart icon button
          IconButton(
            onClick = onAddToCart,
            modifier = Modifier.size(26.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingCart,
              contentDescription = "Add to bill",
              tint = RoyalNavy,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Generate QR Batch Button
          IconButton(
            onClick = onGenerateQrBatch,
            modifier = Modifier.size(26.dp).testTag("btn_qr_batch_${item.id}")
          ) {
            Icon(
              imageVector = Icons.Default.QrCode2,
              contentDescription = "Generate QR Batch Label",
              tint = RoyalMagenta,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Generate & Print Thermal Barcode Label Button (HSN code)
          IconButton(
            onClick = onPrintThermalBarcode,
            modifier = Modifier.size(26.dp).testTag("btn_thermal_barcode_${item.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Print,
              contentDescription = "Generate & Print Thermal Barcode Label (HSN: ${item.hsnCode})",
              tint = Color(0xFF4F46E5),
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Edit Medicine & Batch Details Button
          IconButton(
            onClick = onEditMedicineDetails,
            modifier = Modifier.size(26.dp).testTag("btn_edit_medicine_${item.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Medicine Details and Batches",
              tint = Color(0xFF2563EB),
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Delete / Reduce Stock Action Button
          IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(26.dp).testTag("btn_delete_product_${item.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete product options",
              tint = StatusRed,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun DeleteStockDialog(
  item: MedicineItem,
  onDismiss: () -> Unit,
  onDeleteFully: () -> Unit,
  onReduceQuantity: (Int, String) -> Unit
) {
  var deleteMode by remember { mutableStateOf("QUANTITY") }
  var qtyText by remember { mutableStateOf("1") }
  var reasonText by remember { mutableStateOf("Expired Stock Write-Off") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Manage Stock Removal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = item.name,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
        Text("Current stock: ${item.stockPacks} pack(s)", fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(4.dp))

        // Option 1: Reduce / Delete by Quantity
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { deleteMode = "QUANTITY" }
        ) {
          androidx.compose.material3.RadioButton(
            selected = deleteMode == "QUANTITY",
            onClick = { deleteMode = "QUANTITY" }
          )
          Column {
            Text("Delete / Write-off by Quantity", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("Reduce specific number of packs", fontSize = 11.sp, color = TextMuted)
          }
        }

        if (deleteMode == "QUANTITY") {
          OutlinedTextField(
            value = qtyText,
            onValueChange = { qtyText = it },
            label = { Text("Packs to Remove") },
            modifier = Modifier.fillMaxWidth().testTag("input_reduce_qty")
          )
          OutlinedTextField(
            value = reasonText,
            onValueChange = { reasonText = it },
            label = { Text("Reason (e.g. Expired, Damaged, Return)") },
            modifier = Modifier.fillMaxWidth().testTag("input_reduce_reason")
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Option 2: Delete Product Fully
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { deleteMode = "FULLY" }
        ) {
          androidx.compose.material3.RadioButton(
            selected = deleteMode == "FULLY",
            onClick = { deleteMode = "FULLY" }
          )
          Column {
            Text("Delete Product Fully", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusRed)
            Text("Permanently removes from pharmacy database", fontSize = 11.sp, color = TextMuted)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (deleteMode == "FULLY") {
            onDeleteFully()
          } else {
            val q = qtyText.toIntOrNull() ?: 1
            onReduceQuantity(q, reasonText)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
        modifier = Modifier.testTag("btn_confirm_stock_delete")
      ) {
        Text(if (deleteMode == "FULLY") "Delete Fully" else "Confirm Removal")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}

@Composable
fun ManualAddMedicineDialog(
  onDismiss: () -> Unit,
  onSave: (MedicineItem) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var manufacturer by remember { mutableStateOf("") }
  var composition by remember { mutableStateOf("") }
  var mrp by remember { mutableStateOf("") }
  var saleRate by remember { mutableStateOf("") }
  var purchaseRate by remember { mutableStateOf("") }
  var batchNumber by remember { mutableStateOf("") }
  var expiryDate by remember { mutableStateOf("12/27") }
  var stockPacks by remember { mutableStateOf("10") }
  var rackLocation by remember { mutableStateOf("Rack A-1") }
  var barcode by remember { mutableStateOf("890" + (10000000..99999999).random()) }

  val rackPresetOptions = listOf(
    "Rack A-1", "Rack A-2", "Rack B-1", "Rack B-2", "Rack C-1", "Cold Storage", "OTC Counter"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Add Medicine to Stock", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Medicine Name*") },
            placeholder = { Text("e.g. Dolo 650mg") },
            modifier = Modifier.fillMaxWidth().testTag("input_medicine_name")
          )
        }
        item {
          OutlinedTextField(
            value = manufacturer,
            onValueChange = { manufacturer = it },
            label = { Text("Manufacturer / Brand*") },
            placeholder = { Text("e.g. Micro Labs") },
            modifier = Modifier.fillMaxWidth().testTag("input_medicine_mfg")
          )
        }
        item {
          OutlinedTextField(
            value = composition,
            onValueChange = { composition = it },
            label = { Text("Salt Composition / Molecule") },
            placeholder = { Text("e.g. Paracetamol 650mg") },
            modifier = Modifier.fillMaxWidth()
          )
        }

        // RACK LOCATION SELECTION & EDITING IN ADD ITEM OPTION
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFFEF3C7))
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("RACK & SHELF LOCATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
            }
            Spacer(modifier = Modifier.height(6.dp))

            // Preset Rack Pills
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              rackPresetOptions.forEach { preset ->
                val isSel = rackLocation == preset
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSel) Color(0xFFB45309) else Color.White)
                    .border(1.dp, if (isSel) Color(0xFFB45309) else CardBorder, RoundedCornerShape(6.dp))
                    .clickable { rackLocation = preset }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = preset,
                    fontSize = 10.5.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSel) Color.White else TextDark
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
              value = rackLocation,
              onValueChange = { rackLocation = it },
              label = { Text("Rack Position") },
              placeholder = { Text("e.g. Rack A-02, Shelf 3") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("input_medicine_rack")
            )
          }
        }

        item {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = mrp,
              onValueChange = { mrp = it },
              label = { Text("MRP (₹)*") },
              modifier = Modifier.weight(1f).testTag("input_medicine_mrp")
            )
            OutlinedTextField(
              value = saleRate,
              onValueChange = { saleRate = it },
              label = { Text("Sale Rate (₹)") },
              modifier = Modifier.weight(1f)
            )
          }
        }
        item {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = batchNumber,
              onValueChange = { batchNumber = it },
              label = { Text("Batch No") },
              placeholder = { Text("DL2401") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = expiryDate,
              onValueChange = { expiryDate = it },
              label = { Text("Expiry (MM/YY)") },
              modifier = Modifier.weight(1f)
            )
          }
        }
        item {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = stockPacks,
              onValueChange = { stockPacks = it },
              label = { Text("Initial Stock (Packs)") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = barcode,
              onValueChange = { barcode = it },
              label = { Text("Barcode") },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val mrpVal = mrp.toDoubleOrNull() ?: 100.0
            val sRate = saleRate.toDoubleOrNull() ?: mrpVal
            val pRate = purchaseRate.toDoubleOrNull() ?: (sRate * 0.8)
            val packs = stockPacks.toIntOrNull() ?: 1
            onSave(
              MedicineItem(
                name = name,
                manufacturer = manufacturer.ifBlank { "Royal Generic" },
                composition = composition,
                mrp = mrpVal,
                saleRate = sRate,
                purchaseRate = pRate,
                batchNumber = batchNumber.ifBlank { "RX990" },
                expiryDate = expiryDate,
                stockPacks = packs,
                rackLocation = rackLocation.ifBlank { "Rack A-1" },
                barcode = barcode
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        modifier = Modifier.testTag("btn_save_medicine")
      ) {
        Text("Save Item")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}

@Composable
fun QrBatchModalDialog(
  item: MedicineItem,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val payload = MedicineQrPayload.fromMedicine(item)
  val payloadJson = payload.toJsonString()

  val qrMatrix = remember(payloadJson) { QrCodeGeneratorUtil.generateQrMatrix(payloadJson, 25) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.QrCode2, contentDescription = null, tint = RoyalNavy)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Printable QR Batch Label", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(item.name, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
            Text("${item.manufacturer} • ${item.category}", fontSize = 11.sp, color = Color(0xFF94A3B8))

            Spacer(modifier = Modifier.height(10.dp))

            // Render QR Matrix
            Box(
              modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(8.dp),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val matrixLen = qrMatrix.size
                val cellWidth = this.size.width / matrixLen
                val cellHeight = this.size.height / matrixLen

                for (r in 0 until matrixLen) {
                  for (c in 0 until matrixLen) {
                    if (qrMatrix[r][c]) {
                      drawRect(
                        color = Color(0xFF0F172A),
                        topLeft = androidx.compose.ui.geometry.Offset(c * cellWidth, r * cellHeight),
                        size = androidx.compose.ui.geometry.Size(cellWidth, cellHeight)
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Batch: ${payload.batch}", fontSize = 11.sp, color = Color(0xFFE2E8F0), fontWeight = FontWeight.Bold)
              Text("Exp: ${payload.exp}", fontSize = 11.sp, color = Color(0xFFF472B6), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("MRP: ₹${item.mrp}", fontSize = 11.sp, color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
              Text("Rack: ${payload.rack}", fontSize = 11.sp, color = Color(0xFFCBD5E1))
            }
          }
        }

        Text(
          "Scan this QR label with terminal camera or barcode scanner to auto-populate batch details and stock.",
          fontSize = 10.5.sp,
          color = TextMuted,
          textAlign = TextAlign.Center
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          Toast.makeText(context, "Sending QR Batch Label (${item.name}) to Thermal/PDF Printer...", Toast.LENGTH_LONG).show()
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Print QR Batch Label", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close", color = TextMuted)
      }
    }
  )
}

@Composable
fun ThermalBarcodeLabelDialog(
  item: MedicineItem,
  onDismiss: () -> Unit,
  onPrint: () -> Unit
) {
  val hsn = item.hsnCode.ifBlank { "3004" }
  val batch = item.batchNumber.ifBlank { "RX-2027" }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF4F46E5))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Thermal Barcode Sticker", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Thermal Label Preview Card
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0F172A))
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("ROYAL PHARMACY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black, letterSpacing = 1.sp)
            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, textAlign = TextAlign.Center)
            Text(
              item.composition.ifBlank { item.saltMolecule.ifBlank { item.category } },
              fontSize = 10.sp,
              color = Color.DarkGray,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Barcode Drawing (Visual Code 39 Pattern for HSN Code)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val totalBars = 36
                val barWidth = size.width / (totalBars * 1.5f)
                val hsnDigits = hsn.filter { it.isDigit() }.ifBlank { "3004" }
                for (i in 0 until totalBars) {
                  val digit = hsnDigits[i % hsnDigits.length].digitToIntOrNull() ?: 3
                  val isThick = (digit + i) % 3 == 0
                  val currentWidth = if (isThick) barWidth * 1.8f else barWidth * 0.8f
                  val x = i * (barWidth * 1.5f)
                  drawRect(
                    color = Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(x, 0f),
                    size = androidx.compose.ui.geometry.Size(currentWidth, size.height)
                  )
                }
              }
            }

            Text(
              text = "HSN CODE: $hsn",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
              color = Color.Black,
              modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, Color.Black)
                .padding(4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Batch: $batch", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              Text("Exp: ${item.expiryDate}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, start = 4.dp, end = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("MRP: ₹${item.mrp}", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              Text(item.rackLocation.ifBlank { "Rack A-1" }, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }
        }

        Text(
          text = "Standard 58mm x 40mm thermal sticker roll ready for ESC/POS and Android system print spooler.",
          fontSize = 10.5.sp,
          color = TextMuted,
          textAlign = TextAlign.Center
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onPrint,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
      ) {
        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Print Label")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close", color = TextDark)
      }
    }
  )
}
