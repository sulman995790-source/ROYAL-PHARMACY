package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.MedicineBatchDetail
import com.example.data.model.MedicineItem
import com.example.service.DistributorExportService
import com.example.service.ExportFormat
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.BatchRiskTier
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun BatchTrackingScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val medicines by viewModel.allMedicines.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()
  
  val batchToReturn by viewModel.batchToReturn.collectAsState()
  val scannedBarcode by viewModel.scannedBarcodeForReturn.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedTab by remember { mutableIntStateOf(0) } // 0: All Batches, 1: Active In-Stock, 2: Critical Low, 3: Near Expiry, 4: Quarantined, 5: Clearance Priority
  var showAddBatchDialog by remember { mutableStateOf(false) }
  var batchToEdit by remember { mutableStateOf<MedicineBatchDetail?>(null) }
  var showQrForBatch by remember { mutableStateOf<MedicineBatchDetail?>(null) }
  var quarantinedBatchIds by remember { mutableStateOf(setOf<String>()) }
  var batchToDelete by remember { mutableStateOf<MedicineBatchDetail?>(null) }
  // var batchToReturn by remember { mutableStateOf<MedicineBatchDetail?>(null) } // Replaced by viewModel
  var generatedCreditNote by remember { mutableStateOf<com.example.viewmodel.DistributorCreditNote?>(null) }

  // Synthesize rich batch items from catalog
  val allBatches = remember(medicines, quarantinedBatchIds) {
    medicines.mapIndexed { idx, med ->
      val bNo = if (med.batchNumber.isNotBlank()) med.batchNumber else "RP-BT-${1000 + idx}"
      val isQ = quarantinedBatchIds.contains(bNo) || med.isExpired
      MedicineBatchDetail(
        id = "${med.id}_${bNo}",
        medicine = med,
        batchNumber = bNo,
        mfgDate = "02/2024",
        expiryDate = med.expiryDate,
        stockAvailable = med.stockPacks,
        initialPacks = med.stockPacks + med.dispenseCount,
        purchaseRate = med.purchaseRate,
        mrp = med.mrp,
        saleRate = med.saleRate,
        rackLocation = med.rackLocation.ifBlank { "Rack A-${(idx % 6) + 1}" },
        supplierName = med.manufacturer,
        isQuarantined = isQ,
        barcode = med.barcode.ifBlank { "890${(100000000..999999999).random()}" }
      )
    }
  }

  LaunchedEffect(scannedBarcode) {
      if (scannedBarcode != null) {
          val batch = allBatches.firstOrNull { it.barcode == scannedBarcode }
          if (batch != null) {
              viewModel.batchToReturn.value = batch
          } else {
              Toast.makeText(context, "Batch with barcode $scannedBarcode not found!", Toast.LENGTH_SHORT).show()
          }
          viewModel.scannedBarcodeForReturn.value = null
      }
  }

  val activeBatches = allBatches.filter { !it.isQuarantined && it.stockAvailable > 0 }
  val lowStockBatches = allBatches.filter { it.stockAvailable in 1..it.medicine.minStockAlert }
  val nearExpiryBatches = allBatches.filter { it.expiryDate.contains("26") || it.medicine.isExpired }
  val quarantinedBatches = allBatches.filter { it.isQuarantined }
  val clearanceBatches = allBatches.filter { it.expiryDate.contains("26") && it.mrp > 100 }

  val displayedBatches = when (selectedTab) {
    1 -> activeBatches
    2 -> lowStockBatches
    3 -> nearExpiryBatches
    4 -> quarantinedBatches
    5 -> clearanceBatches
    else -> allBatches
  }.filter {
    searchQuery.isBlank() ||
      it.medicine.name.contains(searchQuery, ignoreCase = true) ||
      it.batchNumber.contains(searchQuery, ignoreCase = true) ||
      it.supplierName.contains(searchQuery, ignoreCase = true) ||
      it.rackLocation.contains(searchQuery, ignoreCase = true)
  }

  val totalBatchValuation = allBatches.sumOf { it.stockAvailable * it.mrp }
  val totalPacksCount = allBatches.sumOf { it.stockAvailable }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // 1. Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalNavy)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { viewModel.navigateTo(Screen.STOCK) },
          modifier = Modifier.size(36.dp).testTag("btn_back_from_batch_tracker")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Batch Master & Tracking", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("${allBatches.size} Registered Batches • FIFO Traceability", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = {
            val exportItems = allBatches.map { b ->
              CartItem(
                medicineName = b.medicine.name,
                manufacturer = b.supplierName,
                category = b.medicine.category,
                batchNumber = b.batchNumber,
                expiryDate = b.expiryDate,
                quantity = b.stockAvailable,
                unitRate = b.purchaseRate,
                mrp = b.mrp,
                itemType = "BATCH_AUDIT"
              )
            }
            DistributorExportService.generateAndShareXls(
              context = context,
              cartItems = exportItems,
              distributorName = "Master Batch Ledger",
              pharmacyName = profile.businessName
            )
          },
          modifier = Modifier.testTag("btn_export_batch_xls")
        ) {
          Icon(Icons.Default.Download, contentDescription = "Export Excel", tint = Color.White)
        }

        IconButton(
          onClick = { viewModel.navigateTo(Screen.QUICK_SCAN) },
          modifier = Modifier.testTag("btn_scan_batch_barcode")
        ) {
          Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan 2D Barcode", tint = Color(0xFF67E8F9))
        }
      }
    }

    // 2. Summary Card
    Card(
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = CardDefaults.outlinedCardBorder(),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Total Batches Box
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFEDE9FE))
              .padding(10.dp)
          ) {
            Column {
              Text("ACTIVE BATCHES", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
              Text("${allBatches.size} Batches", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RoyalNavy)
              Text("$totalPacksCount Total Units", fontSize = 9.5.sp, color = TextMuted)
            }
          }

          // Near Expiry Box
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFFEF2F2))
              .padding(10.dp)
          ) {
            Column {
              Text("EXPIRY RADAR", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = StatusRed)
              Text("${nearExpiryBatches.size} Batches", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = StatusRed)
              Text("< 60 Days / Exp", fontSize = 9.5.sp, color = Color(0xFF991B1B))
            }
          }

          // Total Value Box
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFECFDF5))
              .padding(10.dp)
          ) {
            Column {
              Text("BATCH WORTH", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
              Text("₹${String.format(Locale.getDefault(), "%,.0f", totalBatchValuation)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = StatusGreen)
              Text("At MRP Value", fontSize = 9.5.sp, color = TextMuted)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by Batch No, Medicine, Rack or Supplier...", fontSize = 12.sp, color = TextMuted) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF8FAFC),
            unfocusedContainerColor = Color(0xFFF8FAFC),
            focusedBorderColor = RoyalMagenta,
            unfocusedBorderColor = CardBorder
          ),
          modifier = Modifier.fillMaxWidth().height(50.dp).testTag("input_batch_search")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Expiry Report & Financial Loss Module Card
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EventBusy, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Expiry & Financial Loss Report", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
              }
              val expiredOrNear = nearExpiryBatches
              val lossValue = expiredOrNear.sumOf { it.stockAvailable * it.purchaseRate }
              Text("Loss Est: ₹${String.format(Locale.getDefault(), "%,.0f", lossValue)}", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF991B1B))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Export comprehensive audit of expired & near-expiry batches with distributor contact info & purchase loss.", fontSize = 11.sp, color = Color(0xFF92400E))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              val formats = listOf("PDF" to ExportFormat.PDF, "CSV" to ExportFormat.CSV, "XLS" to ExportFormat.XLS, "XLSX" to ExportFormat.XLSX)
              formats.forEach { (label, format) ->
                OutlinedButton(
                  onClick = {
                    val expiredOrNear = nearExpiryBatches
                    val exportItems = expiredOrNear.map { b ->
                      CartItem(
                        medicineName = b.medicine.name,
                        manufacturer = "Distributor: ${b.supplierName}",
                        category = b.medicine.category,
                        batchNumber = b.batchNumber,
                        expiryDate = b.expiryDate,
                        quantity = b.stockAvailable,
                        unitRate = b.purchaseRate,
                        mrp = b.mrp,
                        itemType = "EXPIRY_LOSS_REPORT"
                      )
                    }
                    DistributorExportService.exportBatchItems(
                      context = context,
                      cartItems = exportItems,
                      format = format,
                      distributorName = "Expiry & Loss Audit Ledger",
                      pharmacyName = profile.businessName
                    )
                    Toast.makeText(context, "Expiry Report exported as $label!", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.weight(1f).height(32.dp),
                  contentPadding = PaddingValues(0.dp)
                ) {
                  Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "All Batches (${allBatches.size})",
            "In Stock (${activeBatches.size})",
            "Critical Low (${lowStockBatches.size})",
            "Near Expiry (${nearExpiryBatches.size})",
            "Quarantined (${quarantinedBatches.size})",
            "Clearance Priority"
          ).forEachIndexed { idx, title ->
            FilterChip(
              selected = selectedTab == idx,
              onClick = { selectedTab = idx },
              label = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalNavy,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }
    }

    // 3. Batches List
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (displayedBatches.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.Storage, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
              Spacer(modifier = Modifier.height(10.dp))
              Text("No medicine batches found", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("Try searching another batch number, medicine name, or rack location.", fontSize = 11.sp, color = TextMuted)
            }
          }
        }
      } else {
        items(displayedBatches) { batch ->
          val isExpiredBatch = batch.medicine.isExpired || batch.expiryDate.contains("24") || batch.expiryDate.contains("25")
          val isNearExpiryBatch = batch.expiryDate.contains("26")

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (batch.isQuarantined) Color(0xFFFFF1F2) else Color.White
            ),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(
                if (batch.isQuarantined) Color(0xFFFECACA) else CardBorder
              )
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_batch_${batch.batchNumber}")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              // Top Row: Batch Number + Status Badge
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (batch.isQuarantined) StatusRed else RoyalNavy)
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = "BATCH: ${batch.batchNumber}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White,
                      fontFamily = FontFamily.Monospace
                    )
                  }

                  IconButton(
                    onClick = {
                      clipboardManager.setText(AnnotatedString(batch.batchNumber))
                      Toast.makeText(context, "Batch #${batch.batchNumber} copied", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(14.dp))
                  }
                }

                // Color-coded Status Pill
                when {
                  batch.isQuarantined -> {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF3E8FF))
                        .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text("QUARANTINED", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7E22CE))
                    }
                  }
                  isExpiredBatch -> {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFEE2E2))
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text("EXPIRED", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                    }
                  }
                  isNearExpiryBatch -> {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text("< 30 DAYS (NEAR EXP)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                    }
                  }
                  else -> {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDCFCE7))
                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Text("ACTIVE (${batch.expiryDate})", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Medicine Info
              Text(
                text = batch.medicine.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "${batch.medicine.composition.ifBlank { "Formulation" }} • Mfg: ${batch.supplierName}",
                fontSize = 11.sp,
                color = TextMuted
              )

              Spacer(modifier = Modifier.height(10.dp))
              Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
              Spacer(modifier = Modifier.height(10.dp))

              // 4 Metrics Grid
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("STOCK AVAILABLE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text("${batch.stockAvailable} Packs", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (batch.stockAvailable <= batch.medicine.minStockAlert) StatusRed else TextDark)
                  Text("Total ${batch.initialPacks} received", fontSize = 9.sp, color = TextMuted)
                }

                Column {
                  Text("PURCHASE RATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text("₹${String.format(Locale.getDefault(), "%.2f", batch.purchaseRate)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Cost Price", fontSize = 9.sp, color = TextMuted)
                }

                Column {
                  Text("RETAIL MRP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text("₹${String.format(Locale.getDefault(), "%.2f", batch.mrp)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                  Text("Sale ₹${String.format(Locale.getDefault(), "%.2f", batch.saleRate)}", fontSize = 9.sp, color = TextMuted)
                }

                Column {
                  Text("STORAGE RACK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text(batch.rackLocation, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                  Text(batch.medicine.scheduleDrug, fontSize = 9.sp, color = TextMuted)
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              
              // Batch Timeline
              Text("BATCH LIFECYCLE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              Spacer(modifier = Modifier.height(4.dp))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  listOf("Received", "Stocked", "Quarantined", "Returned").forEachIndexed { idx, label ->
                      Column(horizontalAlignment = Alignment.CenterHorizontally) {
                          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (idx <= 1) StatusGreen else Color.LightGray))
                          Text(label, fontSize = 8.sp, color = TextMuted)
                      }
                  }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Action Buttons Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedButton(
                  onClick = { batchToEdit = batch },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp),
                  modifier = Modifier.weight(1f).height(38.dp).testTag("btn_adjust_batch_${batch.batchNumber}")
                ) {
                  Icon(Icons.Default.Edit, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("Qty", fontSize = 11.sp, color = RoyalNavy)
                }

                // Return to Distributor Button for Expired/Near-Expiry Batches
                if (isExpiredBatch || isNearExpiryBatch) {
                  Button(
                    onClick = { viewModel.batchToReturn.value = batch },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.weight(1.2f).height(38.dp).testTag("btn_return_distributor_${batch.batchNumber}")
                  ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Return", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                  }
                }

                // Batch QR Button
                IconButton(
                  onClick = { showQrForBatch = batch },
                  modifier = Modifier.size(38.dp).border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                ) {
                  Icon(Icons.Default.QrCode, contentDescription = "Batch QR", tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                }

                // Quick Delete Icon Button
                IconButton(
                  onClick = { batchToDelete = batch },
                  modifier = Modifier.size(38.dp).border(1.dp, CardBorder, RoundedCornerShape(8.dp)).testTag("btn_delete_batch_${batch.batchNumber}")
                ) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete Batch", tint = StatusRed, modifier = Modifier.size(18.dp))
                }

                if (batch.isQuarantined) {
                  Button(
                    onClick = {
                      quarantinedBatchIds = quarantinedBatchIds - batch.batchNumber
                      Toast.makeText(context, "Batch #${batch.batchNumber} un-quarantined for billing", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(38.dp)
                  ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Release", fontSize = 11.sp, color = Color.White)
                  }
                } else {
                  Button(
                    onClick = {
                      quarantinedBatchIds = quarantinedBatchIds + batch.batchNumber
                      Toast.makeText(context, "Batch #${batch.batchNumber} quarantined & stopped from billing", Toast.LENGTH_LONG).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(38.dp).testTag("btn_quarantine_${batch.batchNumber}")
                  ) {
                    Icon(Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Hold", fontSize = 11.sp, color = Color.White)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Adjust Stock Dialog
  if (batchToEdit != null) {
    var qtyInput by remember { mutableStateOf("${batchToEdit!!.stockAvailable}") }
    var rackInput by remember { mutableStateOf(batchToEdit!!.rackLocation) }

    AlertDialog(
      onDismissRequest = { batchToEdit = null },
      title = {
        Text("Adjust Batch #${batchToEdit!!.batchNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Medicine: ${batchToEdit!!.medicine.name}", fontSize = 12.sp, color = TextMuted)
          OutlinedTextField(
            value = qtyInput,
            onValueChange = { qtyInput = it },
            label = { Text("Available Stock Packs") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = rackInput,
            onValueChange = { rackInput = it },
            label = { Text("Rack / Storage Location") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val newQty = qtyInput.toIntOrNull() ?: batchToEdit!!.stockAvailable
            viewModel.updateMedicine(
              batchToEdit!!.medicine.copy(
                stockPacks = newQty,
                rackLocation = rackInput
              )
            )
            Toast.makeText(context, "Batch #${batchToEdit!!.batchNumber} updated to $newQty packs", Toast.LENGTH_SHORT).show()
            batchToEdit = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { batchToEdit = null }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showQrForBatch != null) {
    val batch = showQrForBatch!!
    val qrPayload = remember(batch) {
      com.example.util.MedicineQrPayload(
        id = batch.medicine.id,
        name = batch.medicine.name,
        batch = batch.batchNumber,
        exp = batch.expiryDate,
        mrp = batch.mrp,
        saleRate = if (batch.saleRate > 0) batch.saleRate else batch.medicine.saleRate,
        rack = batch.rackLocation.ifBlank { batch.medicine.rackLocation },
        barcode = batch.medicine.barcode
      ).toJsonString()
    }
    val qrBitmap = remember(batch) { com.example.util.QrCodeGeneratorUtil.generateQrBitmap(qrPayload, 320) }

    AlertDialog(
      onDismissRequest = { showQrForBatch = null },
      title = {
        Text("Batch #${batch.batchNumber} QR Label", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          androidx.compose.foundation.Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "Batch QR Code",
            modifier = Modifier.size(200.dp).border(1.dp, Color.LightGray)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(batch.medicine.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
          Text("Batch: ${batch.batchNumber} | Exp: ${batch.expiryDate}", fontSize = 12.sp, color = TextMuted)
          Text("Price: ₹${if (batch.saleRate > 0) batch.saleRate else batch.mrp}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            "Scan this QR code at POS counter or tap below to auto-fill medicine details, batch number, expiry date, and price.",
            fontSize = 11.sp,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.handleScannedBarcode(qrPayload)
            Toast.makeText(context, "Added ${batch.medicine.name} (Batch: ${batch.batchNumber}) to POS Bill!", Toast.LENGTH_SHORT).show()
            showQrForBatch = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
        ) {
          Text("Add To POS Bill", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showQrForBatch = null }) {
          Text("Close", color = TextMuted)
        }
      }
    )
  }

  // Delete Batch Confirmation Dialog
  if (batchToDelete != null) {
    val b = batchToDelete!!
    AlertDialog(
      onDismissRequest = { batchToDelete = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRed)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Delete Batch #${b.batchNumber}?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Text("Are you sure you want to permanently remove batch '${b.batchNumber}' for '${b.medicine.name}' from active batch records?", fontSize = 13.sp, color = TextDark)
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteMedicineBatch(b.medicine.id, b.batchNumber)
            Toast.makeText(context, "Batch #${b.batchNumber} deleted", Toast.LENGTH_SHORT).show()
            batchToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
        ) {
          Text("Delete Record", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { batchToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Return to Distributor & Credit Note Dialog
  if (batchToReturn != null) {
    val b = batchToReturn!!
    var returnQtyInput by remember { mutableStateOf("${b.stockAvailable}") }
    var supplierInput by remember { mutableStateOf(b.supplierName) }
    var returnReasonInput by remember { mutableStateOf("Expired Stock Return") }

    val qty = returnQtyInput.toIntOrNull() ?: b.stockAvailable
    val totalCredit = qty * b.purchaseRate

    AlertDialog(
      onDismissRequest = { viewModel.batchToReturn.value = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = RoyalMagenta)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Return Batch #${b.batchNumber} to Supplier", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Medicine: ${b.medicine.name} (Exp: ${b.expiryDate})", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextDark)

          OutlinedTextField(
            value = supplierInput,
            onValueChange = { supplierInput = it },
            label = { Text("Distributor / Supplier Name*") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_return_distributor")
          )

          OutlinedTextField(
            value = returnQtyInput,
            onValueChange = { returnQtyInput = it.filter { char -> char.isDigit() } },
            label = { Text("Return Quantity (Packs)*") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_return_qty")
          )

          OutlinedTextField(
            value = returnReasonInput,
            onValueChange = { returnReasonInput = it },
            label = { Text("Return Reason") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFFEF2F2))
              .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Column {
              Text("CREDIT NOTE REFUND AMOUNT", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = StatusRed)
              Text("₹${String.format(Locale.getDefault(), "%,.2f", totalCredit)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StatusRed)
              Text("Calculated at purchase cost ₹${b.purchaseRate} / pack", fontSize = 10.sp, color = TextMuted)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (supplierInput.isBlank() || qty <= 0) {
              Toast.makeText(context, "Please enter valid distributor name and return quantity", Toast.LENGTH_SHORT).show()
              return@Button
            }
            val creditNote = viewModel.returnBatchToDistributor(
              medicineId = b.medicine.id,
              batchNumber = b.batchNumber,
              distributorName = supplierInput,
              returnQty = qty,
              reason = returnReasonInput,
              creditAmount = totalCredit
            )
            viewModel.batchToReturn.value = null
            generatedCreditNote = creditNote
            Toast.makeText(context, "Return logged & Credit Note #${creditNote.creditNoteNumber} issued!", Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
          modifier = Modifier.testTag("btn_confirm_batch_return")
        ) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Generate Credit Note")
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.batchToReturn.value = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Credit Note Voucher Modal
  if (generatedCreditNote != null) {
    val cn = generatedCreditNote!!
    AlertDialog(
      onDismissRequest = { generatedCreditNote = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = StatusGreen)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Credit Note Voucher", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Voucher #: ${cn.creditNoteNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
            Text(cn.dateCreated, fontSize = 10.sp, color = TextMuted)
          }
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
          Text("Supplier: ${cn.distributorName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
          Text("Returned Item: ${cn.medicineName} (Batch #${cn.batchNumber})", fontSize = 12.sp, color = TextDark)
          Text("Quantity Returned: ${cn.returnedPacks} Packs @ ₹${cn.purchaseRate}/pack", fontSize = 12.sp, color = TextMuted)
          Text("Reason: ${cn.returnReason}", fontSize = 11.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFECFDF5))
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("CREDIT REFUND TOTAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
            Text("₹${String.format(Locale.getDefault(), "%,.2f", cn.totalCreditAmount)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = StatusGreen)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            clipboardManager.setText(AnnotatedString("CREDIT NOTE: ${cn.creditNoteNumber}\nDistributor: ${cn.distributorName}\nItem: ${cn.medicineName} (Batch #${cn.batchNumber})\nReturned: ${cn.returnedPacks} Packs\nCredit Amount: ₹${cn.totalCreditAmount}"))
            Toast.makeText(context, "Credit Note Voucher #${cn.creditNoteNumber} copied to clipboard", Toast.LENGTH_SHORT).show()
            generatedCreditNote = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
        ) {
          Text("Copy Voucher & Close")
        }
      }
    )
  }
}
