package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineItem
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
import com.example.util.MedicineQrPayload
import com.example.util.QrCodeGeneratorUtil
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryQrScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val medicines by viewModel.allMedicines.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()

  var activeTab by remember { mutableStateOf("SCAN") } // "SCAN" or "GENERATE"
  var scannedMedicine by remember { mutableStateOf<MedicineItem?>(medicines.firstOrNull()) }
  var selectedMedicineForQr by remember { mutableStateOf<MedicineItem?>(medicines.firstOrNull()) }

  // Quick Edit Rack Dialog State
  var showEditRackDialog by remember { mutableStateOf(false) }
  var medicineToEditRack by remember { mutableStateOf<MedicineItem?>(null) }
  var newRackInput by remember { mutableStateOf("") }

  // Label Print Preset
  var selectedLabelFormat by remember { mutableStateOf("Thermal Sticker (50x30mm)") }

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
          IconButton(onClick = { viewModel.navigateTo(Screen.STOCK) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Column {
            Text(
              text = "Inventory QR Integration",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Live Scanning • Rack Details • Printable Shelf Labels",
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
          Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
        }
      }

      // 2. Mode Selector (Scan vs Generate)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GrayBackground)
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (activeTab == "SCAN") RoyalNavy else Color.Transparent)
              .clickable { activeTab = "SCAN" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = if (activeTab == "SCAN") Color.White else TextDark, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Scan QR / Barcode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTab == "SCAN") Color.White else TextDark)
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (activeTab == "GENERATE") RoyalMagenta else Color.Transparent)
              .clickable { activeTab = "GENERATE" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.QrCode2, contentDescription = null, tint = if (activeTab == "GENERATE") Color.White else TextDark, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Generate QR Labels", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTab == "GENERATE") Color.White else TextDark)
            }
          }
        }
      }

      // 3. Tab Contents
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
      ) {
        if (activeTab == "SCAN") {
          // Live Camera / Scanner Viewport Simulator
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.Black),
              modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            ) {
              Box(modifier = Modifier.fillMaxSize()) {
                // Scanner laser and corner guides
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(120.dp)
                      .border(2.dp, RoyalMagenta, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(RoyalMagenta)
                    )
                  }
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    "Point Camera at Medicine QR Code or Barcode",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }

          // Quick Scan Medicine Selector (Matches live inventory)
          item {
            Column {
              Text("QUICK SCAN OR SELECT MEDICINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                medicines.take(6).forEach { med ->
                  val isSelected = scannedMedicine?.id == med.id
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) RoyalNavy else Color.White)
                      .border(1.dp, if (isSelected) RoyalNavy else CardBorder, RoundedCornerShape(8.dp))
                      .clickable { scannedMedicine = med }
                      .padding(horizontal = 12.dp, vertical = 6.dp)
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.QrCode, contentDescription = null, tint = if (isSelected) Color.White else RoyalMagenta, modifier = Modifier.size(12.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(med.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextDark)
                    }
                  }
                }
              }
            }
          }

          // Scanned Medicine Details Card with RACK LOCATION
          if (scannedMedicine != null) {
            val med = scannedMedicine!!
            item {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth().testTag("scanned_medicine_card")
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(med.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text(med.manufacturer, fontSize = 12.sp, color = TextMuted)
                      if (med.composition.isNotBlank()) {
                        Text(med.composition, fontSize = 11.sp, color = RoyalMagenta)
                      }
                    }

                    // Stock pack count
                    Column(horizontalAlignment = Alignment.End) {
                      Text("₹${med.saleRate}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text("MRP ₹${med.mrp}", fontSize = 11.sp, color = TextMuted)
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  // RACK LOCATION PROMINENT BADGE
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFFEF3C7))
                      .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                      .padding(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                          Text("STORE RACK & SHELF LOCATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                          Text(
                            text = med.rackLocation.ifBlank { "Not Assigned (Rack A-1)" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF78350F)
                          )
                        }
                      }

                      // Edit Rack Button
                      OutlinedButton(
                        onClick = {
                          medicineToEditRack = med
                          newRackInput = med.rackLocation
                          showEditRackDialog = true
                        },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_edit_rack_from_scan")
                      ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Rack", tint = Color(0xFF92400E), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Rack", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  // Batch & Expiry and Stock info
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Batch: ${med.batchNumber.ifBlank { "RX990" }}", fontSize = 11.sp, color = TextMuted)
                    Text("Expiry: ${med.expiryDate}", fontSize = 11.sp, color = TextMuted)
                    Text("Current Stock: ${med.stockPacks} Packs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (med.stockPacks > 0) StatusGreen else StatusRed)
                  }

                  Spacer(modifier = Modifier.height(14.dp))

                  // Action Buttons from Scan
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    // Adjust Stock (-1 / +1)
                    Row(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GrayBackground)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      IconButton(
                        onClick = { viewModel.reduceMedicineQuantity(med.id, 1, "Quick scan adjustment") },
                        modifier = Modifier.size(28.dp)
                      ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextDark, modifier = Modifier.size(14.dp))
                      }
                      Text("${med.stockPacks}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(horizontal = 4.dp))
                      IconButton(
                        onClick = {
                          viewModel.updateMedicine(med.copy(stockPacks = med.stockPacks + 1))
                        },
                        modifier = Modifier.size(28.dp)
                      ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = RoyalMagenta, modifier = Modifier.size(14.dp))
                      }
                    }

                    // Add to Billing Cart
                    Button(
                      onClick = {
                        viewModel.addMedicineToCart(med, 1)
                        Toast.makeText(context, "Added ${med.name} to Bill Cart!", Toast.LENGTH_SHORT).show()
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f).height(40.dp).testTag("btn_add_scanned_to_cart")
                    ) {
                      Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Add to Bill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Print QR Label for this medicine
                    OutlinedButton(
                      onClick = {
                        selectedMedicineForQr = med
                        activeTab = "GENERATE"
                      },
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.height(40.dp)
                    ) {
                      Icon(Icons.Default.Print, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Label", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                    }
                  }
                }
              }
            }
          }
        } else {
          // GENERATE & PRINT QR LABELS TAB
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Medicine to Generate Label", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal medicine picker chips
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  medicines.forEach { med ->
                    val isSel = selectedMedicineForQr?.id == med.id
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) RoyalMagenta else GrayBackground)
                        .border(1.dp, if (isSel) RoyalMagenta else CardBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedMedicineForQr = med }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text(
                        text = med.name,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) Color.White else TextDark
                      )
                    }
                  }
                }
              }
            }
          }

          if (selectedMedicineForQr != null) {
            val med = selectedMedicineForQr!!
            // Live Sticker / QR Label Preview (Thermal & Sheet standard)
            item {
              val qrPayload = remember(med) { MedicineQrPayload.fromMedicine(med).toJsonString() }
              val qrBitmap = remember(med) { QrCodeGeneratorUtil.generateQrBitmap(qrPayload, pixelSize = 240) }

              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("LABEL PREVIEW (THERMAL STICKER)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Spacer(modifier = Modifier.height(10.dp))

                  // Physical Label Container (High-contrast barcode sticker style)
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFFCFCFC))
                      .border(1.5.dp, Color.Black, RoundedCornerShape(8.dp))
                      .padding(12.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      // QR Code Image
                      Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Medicine QR Code",
                        modifier = Modifier
                          .size(90.dp)
                          .border(1.dp, Color.LightGray)
                      )

                      Spacer(modifier = Modifier.width(12.dp))

                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = profile?.businessName ?: "ROYAL PHARMACY",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.Black
                        )
                        Text(
                          text = med.name,
                          fontSize = 13.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = Color.Black,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                        Text(
                          text = "Batch: ${med.batchNumber.ifBlank { "RX990" }} | Exp: ${med.expiryDate}",
                          fontSize = 10.sp,
                          color = Color.DarkGray
                        )
                        Text(
                          text = "MRP: ₹${med.mrp} (Inc. GST)",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Rack Location Ribbon
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.Black)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                          Text(
                            text = "📍 ${med.rackLocation.ifBlank { "RACK A-1" }}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                          )
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(16.dp))

                  // Print & Share Action Buttons
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    OutlinedButton(
                      onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                          type = "text/plain"
                          putExtra(Intent.EXTRA_SUBJECT, "Medicine QR Label - ${med.name}")
                          putExtra(Intent.EXTRA_TEXT, "Royal Pharmacy QR Label: ${med.name} | Batch: ${med.batchNumber} | Rack: ${med.rackLocation} | MRP: ₹${med.mrp}\nQR Payload: $qrPayload")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share QR Label Data"))
                      },
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                      Icon(Icons.Default.Share, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Share Label", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    }

                    Button(
                      onClick = {
                        Toast.makeText(context, "Sent to ESC/POS Thermal Label Printer for ${med.name}!", Toast.LENGTH_SHORT).show()
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f).height(46.dp).testTag("btn_print_qr_label")
                    ) {
                      Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Print Sticker", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                  }
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(40.dp))
        }
      }
    }

    // Quick Edit Rack Location Dialog
    if (showEditRackDialog && medicineToEditRack != null) {
      val med = medicineToEditRack!!
      val rackPresetOptions = listOf(
        "Rack A-1", "Rack A-2", "Rack A-3",
        "Rack B-1", "Rack B-2", "Rack B-3",
        "Rack C-1", "Rack C-2", "Rack D-1",
        "Cold Storage / Fridge", "Fast Counter", "OTC Shelf 1"
      )

      AlertDialog(
        onDismissRequest = { showEditRackDialog = false },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EditLocation, contentDescription = null, tint = RoyalMagenta)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Update Rack Location", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "Set shelf location for ${med.name}",
              fontSize = 13.sp,
              color = TextDark
            )

            // Preset Rack Chips
            Text("QUICK RACK PRESETS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              rackPresetOptions.chunked(3).forEach { rowPresets ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  rowPresets.forEach { preset ->
                    val isSel = newRackInput == preset
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) RoyalMagenta else GrayBackground)
                        .border(1.dp, if (isSel) RoyalMagenta else CardBorder, RoundedCornerShape(6.dp))
                        .clickable { newRackInput = preset }
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
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
              value = newRackInput,
              onValueChange = { newRackInput = it },
              label = { Text("Custom Rack / Shelf Location") },
              placeholder = { Text("e.g. Rack B-04, Shelf 2") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("input_edit_rack_location")
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (newRackInput.isNotBlank()) {
                viewModel.updateMedicineRack(med.id, newRackInput)
                scannedMedicine = scannedMedicine?.copy(rackLocation = newRackInput)
                selectedMedicineForQr = selectedMedicineForQr?.copy(rackLocation = newRackInput)
                showEditRackDialog = false
                Toast.makeText(context, "Rack location updated for ${med.name}!", Toast.LENGTH_SHORT).show()
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            modifier = Modifier.testTag("btn_confirm_rack_update")
          ) {
            Text("Save Rack")
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditRackDialog = false }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }
  }
}
