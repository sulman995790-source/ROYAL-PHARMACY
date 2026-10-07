package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import com.example.data.model.BusinessProfile
import com.example.data.model.SaleInvoice
import com.example.service.BluetoothPrinterDeviceInfo
import com.example.service.InvoiceCopyType
import com.example.service.InvoicePrintOptions
import com.example.service.InvoicePrinterService
import com.example.service.PrinterPaperSize
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@Composable
fun InvoicePrinterScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Bluetooth Permission Handling
  var hasBluetoothPermission by remember {
    mutableStateOf(
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
      } else {
        true
      }
    )
  }

  val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasBluetoothPermission = granted
    if (!granted) {
      Toast.makeText(context, "Bluetooth connection permission required for printing", Toast.LENGTH_LONG).show()
    }
  }

  val profile by viewModel.businessProfile.collectAsState()
  val allSales by viewModel.allSales.collectAsState()
  val lastGeneratedInvoice by viewModel.lastGeneratedInvoice.collectAsState()
  val selectedInvoiceFromVm by viewModel.selectedInvoiceForPrinting.collectAsState()

  // Selected invoice
  var selectedInvoice by remember(selectedInvoiceFromVm, lastGeneratedInvoice, allSales) {
    mutableStateOf(
      selectedInvoiceFromVm ?: lastGeneratedInvoice ?: allSales.firstOrNull() ?: SaleInvoice(
        id = 1,
        invoiceNumber = "INV-2026-0891",
        invoiceDate = "07-Oct-2026 05:30 PM",
        customerName = "Rahul Sharma",
        customerPhone = "9876543210",
        doctorName = "Dr. S. K. Baruah, MBBS, MD",
        subtotal = 640.0,
        gstTotal = 76.80,
        grandTotal = 716.80,
        paymentMode = "UPI / PhonePe",
        saleType = "Retail Cash Memo",
        itemsJson = "2x Augmentin 625 Duo @ ₹200.00\n1x Pan-D Capsule @ ₹140.00\n1x Dolo 650mg @ ₹30.00\n1x Becosules Z @ ₹70.00",
        loyaltyPointsEarned = 7,
        loyaltyPointsRedeemed = 0
      )
    )
  }

  // Print options
  var selectedPaperSize by remember { mutableStateOf(PrinterPaperSize.THERMAL_80MM) }
  var selectedCopyType by remember { mutableStateOf(InvoiceCopyType.ORIGINAL) }
  var includeGstin by remember { mutableStateOf(true) }
  var includeDrugLicense by remember { mutableStateOf(true) }
  var includeAyushmanHfr by remember { mutableStateOf(true) }
  var includeDoctorDetails by remember { mutableStateOf(true) }
  var includePharmacistSign by remember { mutableStateOf(true) }
  var includeLoyaltyRewards by remember { mutableStateOf(true) }
  var isBluetoothSimulating by remember { mutableStateOf(false) }

  var showEditInvoiceDetailsDialog by remember { mutableStateOf(false) }
  var showBluetoothPrinterPickerDialog by remember { mutableStateOf(false) }
  var pairedPrinters by remember { mutableStateOf<List<BluetoothPrinterDeviceInfo>>(emptyList()) }
  var selectedBluetoothPrinter by remember { mutableStateOf<BluetoothPrinterDeviceInfo?>(null) }

  val currentOptions = remember(
    selectedPaperSize,
    selectedCopyType,
    includeGstin,
    includeDrugLicense,
    includeAyushmanHfr,
    includeDoctorDetails,
    includePharmacistSign,
    includeLoyaltyRewards
  ) {
    InvoicePrintOptions(
      paperSize = selectedPaperSize,
      copyType = selectedCopyType,
      includeGstin = includeGstin,
      includeDrugLicense = includeDrugLicense,
      includeAyushmanHfr = includeAyushmanHfr,
      includeDoctorDetails = includeDoctorDetails,
      includePharmacistSignature = includePharmacistSign,
      includeLoyaltyRewards = includeLoyaltyRewards
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // 1. Top App Bar
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
          onClick = { viewModel.navigateTo(Screen.SALES) },
          modifier = Modifier.size(36.dp).testTag("btn_back_from_printer")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Smart Invoice Printer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("ESC/POS Thermal & GST A4/A5 Print Station", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
          onClick = { showEditInvoiceDetailsDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
          shape = RoundedCornerShape(16.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.padding(end = 6.dp).testTag("btn_edit_invoice_header_details")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Edit My Details", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        IconButton(
          onClick = {
            InvoicePrinterService.shareInvoiceOnWhatsApp(context, selectedInvoice, profile)
          },
          modifier = Modifier.testTag("btn_whatsapp_share")
        ) {
          Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color(0xFF4ADE80))
        }

        IconButton(
          onClick = {
            InvoicePrinterService.shareInvoiceDocument(context, selectedInvoice, profile, currentOptions)
          },
          modifier = Modifier.testTag("btn_share_pdf")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
        }
      }
    }

    // 2. Body with Options & Live Print Preview
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Invoice Quick Selector
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_invoice_selector")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Selected Tax Invoice:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFEDE9FE))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(selectedInvoice.invoiceNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recent Invoices quick pills
            if (allSales.isNotEmpty()) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                allSales.take(5).forEach { inv ->
                  val isSelected = inv.id == selectedInvoice.id
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) RoyalNavy else Color(0xFFF1F5F9))
                      .clickable { selectedInvoice = inv }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Column {
                      Text(
                        text = inv.invoiceNumber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextDark
                      )
                      Text(
                        text = "${inv.customerName} • ₹${String.format(Locale.getDefault(), "%.0f", inv.grandTotal)}",
                        fontSize = 9.5.sp,
                        color = if (isSelected) Color(0xFFE2E8F0) else TextMuted
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Printer Paper Format & Copy Type Selector
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_printer_format")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("1. Select Printer & Paper Format", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              PrinterPaperSize.values().forEach { size ->
                val isSelected = selectedPaperSize == size
                Card(
                  onClick = { selectedPaperSize = size },
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFEDE9FE) else Color(0xFFF8FAFC)
                  ),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                      if (isSelected) RoyalMagenta else CardBorder
                    )
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(
                      imageVector = if (size == PrinterPaperSize.THERMAL_80MM || size == PrinterPaperSize.THERMAL_58MM) Icons.Default.Receipt else Icons.Default.FormatAlignLeft,
                      contentDescription = null,
                      tint = if (isSelected) RoyalMagenta else TextMuted,
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = size.title,
                      fontSize = 10.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) RoyalMagenta else TextDark,
                      textAlign = TextAlign.Center
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Copy Type
            Text("2. Document Copy Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              InvoiceCopyType.values().forEach { copy ->
                FilterChip(
                  selected = selectedCopyType == copy,
                  onClick = { selectedCopyType = copy },
                  label = { Text(copy.label, fontSize = 10.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalNavy,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }
          }
        }
      }

      // Print Content Controls & Toggles
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("3. Header & Compliance Toggles", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Include DL Form 20B & 21B", fontSize = 12.sp, color = TextDark)
              Switch(
                checked = includeDrugLicense,
                onCheckedChange = { includeDrugLicense = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Include Ayushman Bharat HFR ID", fontSize = 12.sp, color = TextDark)
              Switch(
                checked = includeAyushmanHfr,
                onCheckedChange = { includeAyushmanHfr = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Include Pharmacist Signatory", fontSize = 12.sp, color = TextDark)
              Switch(
                checked = includePharmacistSign,
                onCheckedChange = { includePharmacistSign = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Include Customer Loyalty Rewards", fontSize = 12.sp, color = TextDark)
              Switch(
                checked = includeLoyaltyRewards,
                onCheckedChange = { includeLoyaltyRewards = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
              )
            }
          }
        }
      }

      // Live Interactive Print Preview Container
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_print_preview")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Print, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Live Document Preview (${selectedPaperSize.title})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
              Text(selectedCopyType.label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Realistic Paper Slip container
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                .padding(14.dp)
            ) {
              Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = profile.businessName,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TextDark,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = profile.addressLine1,
                  fontSize = 10.5.sp,
                  color = TextMuted,
                  textAlign = TextAlign.Center
                )
                Text(
                  text = "Ph: ${profile.phone} • Email: ${profile.email}",
                  fontSize = 10.sp,
                  color = TextMuted
                )

                if (includeDrugLicense) {
                  Text(
                    text = "DL Form 20B/21B: ${profile.drugLicenseForm20.ifBlank { "DL-ASS-20B-10928" }}",
                    fontSize = 9.sp,
                    color = TextMuted
                  )
                }
                if (includeGstin && profile.gstin.isNotBlank()) {
                  Text(
                    text = "GSTIN: ${profile.gstin} • PAN: ${profile.pan.ifBlank { "AABCR1234M" }}",
                    fontSize = 9.sp,
                    color = TextMuted
                  )
                }
                if (includeAyushmanHfr && profile.ayushmanHfrId.isNotBlank()) {
                  Text(
                    text = "Ayushman HFR ID: ${profile.ayushmanHfrId}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RoyalNavy
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                Spacer(modifier = Modifier.height(6.dp))

                // Meta
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Inv: ${selectedInvoice.invoiceNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                  Text("Date: ${selectedInvoice.invoiceDate}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Customer: ${selectedInvoice.customerName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                  Text("Mode: ${selectedInvoice.paymentMode}", fontSize = 11.sp, color = RoyalMagenta)
                }

                if (includeDoctorDetails && selectedInvoice.doctorName.isNotBlank()) {
                  Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Dr: ${selectedInvoice.doctorName}", fontSize = 10.sp, color = TextMuted)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                Spacer(modifier = Modifier.height(6.dp))

                // Items list
                Text(
                  text = selectedInvoice.itemsJson,
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  lineHeight = 16.sp,
                  modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                Spacer(modifier = Modifier.height(6.dp))

                // Totals
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Subtotal (Taxable):", fontSize = 11.sp, color = TextMuted)
                  Text("₹${String.format(Locale.getDefault(), "%.2f", selectedInvoice.subtotal)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("GST (CGST + SGST):", fontSize = 11.sp, color = TextMuted)
                  Text("₹${String.format(Locale.getDefault(), "%.2f", selectedInvoice.gstTotal)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                if (selectedInvoice.loyaltyPointsRedeemed > 0) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Loyalty Discount:", fontSize = 11.sp, color = StatusGreen)
                    Text("-₹${selectedInvoice.loyaltyPointsRedeemed}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF111827)))
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("NET TOTAL:", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                  Text(
                    "₹${String.format(Locale.getDefault(), "%.2f", selectedInvoice.grandTotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalMagenta,
                    fontFamily = FontFamily.Monospace
                  )
                }

                if (includePharmacistSign) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "Signatory: Suleman Hoque (Reg: 4146-AS)\n*Medicines once sold cannot be returned without cash memo*",
                    fontSize = 8.5.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                  )
                }
              }
            }
          }
        }
      }

      // Master Action Buttons
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Primary Android System Print Manager button
          Button(
            onClick = {
              InvoicePrinterService.printInvoiceViaSystem(
                context = context,
                invoice = selectedInvoice,
                profile = profile,
                options = currentOptions
              )
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_system_print")
          ) {
            Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Print Invoice (Android Print System)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          // Bluetooth Thermal POS Print button
          OutlinedButton(
            onClick = {
              if (!hasBluetoothPermission && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
              } else {
                pairedPrinters = InvoicePrinterService.getPairedBluetoothPrinters(context)
                showBluetoothPrinterPickerDialog = true
              }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("btn_bluetooth_thermal_print")
          ) {
            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send to Bluetooth Thermal POS (58mm/80mm)", color = RoyalMagenta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          // Row for WhatsApp & PDF
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                InvoicePrinterService.shareInvoiceOnWhatsApp(context, selectedInvoice, profile)
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
              modifier = Modifier.weight(1f).height(44.dp).testTag("btn_whatsapp_action")
            ) {
              Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("WhatsApp Bill", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                InvoicePrinterService.generateAndSharePdfInvoice(context, selectedInvoice, profile, currentOptions)
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).height(44.dp).testTag("btn_save_pdf_action")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Export PDF Invoice", color = RoyalNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Dialog 1: Edit Pharmacy & Invoice Header Details
  if (showEditInvoiceDetailsDialog) {
    EditPharmacyInvoiceDetailsDialog(
      profile = profile,
      invoice = selectedInvoice,
      onSave = { updatedProfile, newCustomerName, newDoctorName ->
        viewModel.saveBusinessProfile(updatedProfile)
        selectedInvoice = selectedInvoice.copy(
          customerName = newCustomerName,
          doctorName = newDoctorName
        )
        Toast.makeText(context, "Invoice details updated successfully!", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showEditInvoiceDetailsDialog = false }
    )
  }

  // Dialog 2: Bluetooth Thermal POS Printer Picker
  if (showBluetoothPrinterPickerDialog) {
    BluetoothPrinterPickerDialog(
      printers = pairedPrinters,
      onPrinterSelected = { printer ->
        selectedBluetoothPrinter = printer
        Toast.makeText(context, "Connecting to ${printer.name}...", Toast.LENGTH_SHORT).show()
        InvoicePrinterService.printInvoiceViaBluetooth(
          context = context,
          deviceAddress = printer.address,
          invoice = selectedInvoice,
          profile = profile,
          options = currentOptions
        ) { success, msg ->
          Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
      },
      onDismiss = { showBluetoothPrinterPickerDialog = false }
    )
  }
}

@Composable
fun EditPharmacyInvoiceDetailsDialog(
  profile: BusinessProfile,
  invoice: SaleInvoice,
  onSave: (BusinessProfile, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(profile.businessName) }
  var phone by remember { mutableStateOf(profile.phone) }
  var email by remember { mutableStateOf(profile.email) }
  var address1 by remember { mutableStateOf(profile.addressLine1) }
  var address2 by remember { mutableStateOf(profile.addressLine2) }
  var dl20 by remember { mutableStateOf(profile.drugLicenseForm20) }
  var dl21 by remember { mutableStateOf(profile.drugLicenseForm21) }
  var gstin by remember { mutableStateOf(profile.gstin) }
  var ayushmanHfr by remember { mutableStateOf(profile.ayushmanHfrId) }
  var bankName by remember { mutableStateOf(profile.bankName) }
  var bankAcc by remember { mutableStateOf(profile.bankAccountNumber) }
  var bankIfsc by remember { mutableStateOf(profile.bankIfsc) }
  var bankUpi by remember { mutableStateOf(profile.bankUpiId) }
  var ownerName by remember { mutableStateOf(profile.ownerName) }
  var customerName by remember { mutableStateOf(invoice.customerName) }
  var doctorName by remember { mutableStateOf(invoice.doctorName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Edit, contentDescription = null, tint = RoyalMagenta)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Edit Invoice & Pharmacy Details", fontSize = 17.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(360.dp)) {
        item {
          OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Pharmacy Business Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Phone Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = address1, onValueChange = { address1 = it }, label = { Text("Address Line 1 (City / Zip)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = address2, onValueChange = { address2 = it }, label = { Text("Address Line 2 (Locality)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = dl20, onValueChange = { dl20 = it }, label = { Text("Drug License Form 20B") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = dl21, onValueChange = { dl21 = it }, label = { Text("Drug License Form 21B") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = gstin, onValueChange = { gstin = it }, label = { Text("GSTIN Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = ayushmanHfr, onValueChange = { ayushmanHfr = it }, label = { Text("Ayushman HFR ID") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name (For Invoice)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = bankAcc, onValueChange = { bankAcc = it }, label = { Text("Bank Account No") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = bankIfsc, onValueChange = { bankIfsc = it }, label = { Text("Bank IFSC Code") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = bankUpi, onValueChange = { bankUpi = it }, label = { Text("UPI ID (For Payments)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("Pharmacist Signatory Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Customer Name on Invoice") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = doctorName, onValueChange = { doctorName = it }, label = { Text("Prescribing Doctor Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val updatedProfile = profile.copy(
            businessName = name,
            phone = phone,
            email = email,
            addressLine1 = address1,
            addressLine2 = address2,
            drugLicenseForm20 = dl20,
            drugLicenseForm21 = dl21,
            gstin = gstin,
            ayushmanHfrId = ayushmanHfr,
            bankName = bankName,
            bankAccountNumber = bankAcc,
            bankIfsc = bankIfsc,
            bankUpiId = bankUpi,
            ownerName = ownerName
          )
          onSave(updatedProfile, customerName, doctorName)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
      ) {
        Text("Save & Update Invoice")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun BluetoothPrinterPickerDialog(
  printers: List<BluetoothPrinterDeviceInfo>,
  onPrinterSelected: (BluetoothPrinterDeviceInfo) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedPrinterAddress by remember { mutableStateOf(printers.firstOrNull()?.address ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Bluetooth, contentDescription = null, tint = RoyalMagenta)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Select Bluetooth Thermal Printer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Paired ESC/POS Bluetooth Thermal Printers:", fontSize = 12.sp, color = TextMuted)

        printers.forEach { printer ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedPrinterAddress = printer.address },
            colors = CardDefaults.cardColors(
              containerColor = if (selectedPrinterAddress == printer.address) Color(0xFFFBE4EE) else Color(0xFFF8FAFC)
            ),
            border = if (selectedPrinterAddress == printer.address) androidx.compose.foundation.BorderStroke(1.5.dp, RoyalMagenta) else CardDefaults.outlinedCardBorder()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = selectedPrinterAddress == printer.address,
                onClick = { selectedPrinterAddress = printer.address }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(printer.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("MAC Address: ${printer.address}", fontSize = 11.sp, color = TextMuted)
              }
            }
          }
        }

        Text(
          "Ensure your 58mm / 80mm Bluetooth printer is turned on & paired in Android Bluetooth settings.",
          fontSize = 10.5.sp,
          color = TextMuted
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val printer = printers.find { it.address == selectedPrinterAddress } ?: printers.firstOrNull()
          printer?.let { onPrinterSelected(it) }
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
      ) {
        Text("Print ESC/POS Receipt")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
