package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiPrescriptionService
import com.example.data.ai.PrescribedDrug
import com.example.data.ai.PrescriptionScanResult
import com.example.ui.components.InAppSearchDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PrescriptionScannerScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val inventory by viewModel.allMedicines.collectAsState()

  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isAnalyzing by remember { mutableStateOf(false) }
  var scanResult by remember { mutableStateOf<PrescriptionScanResult?>(null) }
  var selectedSamplePreset by remember { mutableStateOf("General Physician Rx") }
  var inAppSearchQuery by remember { mutableStateOf<String?>(null) }
  var inAppSearchTitle by remember { mutableStateOf("Google Search") }
  var isTorchOn by remember { mutableStateOf(false) }

  // Photo Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      selectedImageUri = uri
      capturedBitmap = null
      scope.launch {
        isAnalyzing = true
        scanResult = GeminiPrescriptionService.analyzePrescription(
          context = context,
          imageUri = uri,
          bitmap = null,
          samplePreset = null,
          availableInventory = inventory
        )
        isAnalyzing = false
      }
    }
  }

  // Camera Capture Launcher (returns thumbnail bitmap)
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      capturedBitmap = bitmap
      selectedImageUri = null
      scope.launch {
        isAnalyzing = true
        scanResult = GeminiPrescriptionService.analyzePrescription(
          context = context,
          imageUri = null,
          bitmap = bitmap,
          samplePreset = null,
          availableInventory = inventory
        )
        isAnalyzing = false
      }
    }
  }

  // Initial load with default sample preset if not yet analyzed
  LaunchedEffect(Unit) {
    if (scanResult == null) {
      scanResult = GeminiPrescriptionService.generatePresetResult(selectedSamplePreset, inventory)
    }
  }

  // Automatically register scanned doctor in Doctor Directory
  LaunchedEffect(scanResult) {
    scanResult?.let { res ->
      if (res.doctorName.isNotBlank() && !res.doctorName.equals("Unknown", ignoreCase = true) && res.doctorName.length > 3) {
        viewModel.autoRegisterDoctorFromPrescription(
          name = res.doctorName,
          specialty = res.doctorSpeciality,
          clinic = res.clinicOrHospital
        )
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // 1. Top Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(RoyalNavy, Color(0xFF581C87))
          )
        )
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { viewModel.navigateTo(Screen.HOME) },
          modifier = Modifier.size(36.dp).testTag("btn_back_prescription_scanner")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("AI Prescription Scanner", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF59E0B))
                .padding(horizontal = 5.dp, vertical = 1.5.dp)
            ) {
              Text("Gemini 3.5", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }
          Text("Multimodal Vision OCR & Google Search Grounding", color = Color(0xFFE2E8F0), fontSize = 11.sp)
        }
      }

      IconButton(
        onClick = { viewModel.navigateTo(Screen.AI_CHATBOT) },
        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).testTag("btn_header_gemini_ask")
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = "Ask Gemini", tint = Color(0xFFFFE082), modifier = Modifier.size(20.dp))
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 2. Action Bar: Camera Scan, Photo Gallery & Sample Prescriptions
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text("Scan Prescription Doctor Note", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text("Take a live photo, choose from gallery, or select doctor Rx sample:", fontSize = 11.5.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Button(
                onClick = { cameraLauncher.launch(null) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f).testTag("btn_camera_prescription_scan")
              ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f).testTag("btn_gallery_prescription_scan")
              ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pick Image", fontSize = 12.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
              }

              IconButton(
                onClick = {
                  val nextState = !isTorchOn
                  isTorchOn = nextState
                  try {
                    val camManager = context.getSystemService(android.content.Context.CAMERA_SERVICE) as? android.hardware.camera2.CameraManager
                    val cameraId = camManager?.cameraIdList?.firstOrNull()
                    if (cameraId != null) {
                      camManager.setTorchMode(cameraId, nextState)
                    }
                  } catch (_: Exception) {}
                  Toast.makeText(
                    context,
                    if (nextState) "Flashlight Turned ON ⚡" else "Flashlight Turned OFF",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isTorchOn) Color(0xFFFFD54F).copy(alpha = 0.3f) else Color(0xFFF1F5F9))
                  .testTag("btn_flash_light_toggle")
              ) {
                Icon(
                  imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                  contentDescription = "Flashlight",
                  tint = if (isTorchOn) Color(0xFFD97706) else RoyalNavy
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Text("Quick Rx Presets (Instant Simulation):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              val presets = listOf("General Physician Rx", "Pediatric Care Rx", "Cardiology & Diabetic Rx")
              presets.forEach { preset ->
                val isSelected = selectedSamplePreset == preset
                FilterChip(
                  selected = isSelected,
                  onClick = {
                    selectedSamplePreset = preset
                    selectedImageUri = null
                    capturedBitmap = null
                    scanResult = GeminiPrescriptionService.generatePresetResult(preset, inventory)
                    Toast.makeText(context, "Loaded $preset", Toast.LENGTH_SHORT).show()
                  },
                  label = { Text(preset, fontSize = 11.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

      // Loading indicator
      if (isAnalyzing) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9FE)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(color = Color(0xFF6B21A8), modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text("Gemini 3.5 Flash Vision Analyzing Rx...", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF4C1D95))
                Text("Reading doctor handwriting, recognizing salts & matching inventory...", fontSize = 11.sp, color = Color(0xFF6D28D9))
              }
            }
          }
        }
      }

      val result = scanResult
      if (result != null && !isAnalyzing) {
        // 3. Doctor & Patient Summary Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(result.doctorName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                  Text(result.doctorSpeciality, fontSize = 11.5.sp, color = RoyalMagenta, fontWeight = FontWeight.Medium)
                  Text(result.clinicOrHospital, fontSize = 11.sp, color = TextMuted)
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verified Rx", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = Color(0xFFF1F5F9))
              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("PATIENT", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text(result.patientName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text(result.patientAgeGender, fontSize = 11.sp, color = TextMuted)
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text("DATE / TIMING", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text(result.prescriptionDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFF8FAFC))
                  .padding(8.dp)
              ) {
                Text(
                  text = "Diagnosis: ${result.diagnosis}",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF334155)
                )
              }
            }
          }
        }

        // 4. Drug-Drug Interaction / Clinical Warning Card
        if (!result.drugInteractionAlert.isNullOrBlank()) {
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (result.drugInteractionAlert.contains("⚠️")) Color(0xFFFEF3C7) else Color(0xFFF0FDF4)
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (result.drugInteractionAlert.contains("⚠️")) Color(0xFFF59E0B) else Color(0xFF86EFAC)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
              ) {
                Icon(
                  imageVector = if (result.drugInteractionAlert.contains("⚠️")) Icons.Default.Warning else Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = if (result.drugInteractionAlert.contains("⚠️")) Color(0xFFD97706) else Color(0xFF16A34A),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = result.drugInteractionAlert,
                  fontSize = 12.sp,
                  color = if (result.drugInteractionAlert.contains("⚠️")) Color(0xFF92400E) else Color(0xFF166534),
                  fontWeight = FontWeight.Medium,
                  lineHeight = 16.sp
                )
              }
            }
          }
        }

        // 5. Section Header & "Add All to Sale Bill" CTA
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Prescribed Medicines (${result.medicines.size})",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )

            Button(
              onClick = {
                // Add all available medicines to billing cart
                result.medicines.forEach { drug ->
                  val med = drug.matchedStockItem ?: inventory.firstOrNull {
                    it.name.contains(drug.medicineName.take(6), ignoreCase = true)
                  }
                  if (med != null) {
                    viewModel.addMedicineToCart(med, drug.packQty)
                  }
                }
                viewModel.billingCustomerName.value = result.patientName
                viewModel.billingDoctorName.value = result.doctorName
                viewModel.billingTo.value = result.patientName
                Toast.makeText(context, "Added prescribed items to Bill Cart!", Toast.LENGTH_LONG).show()
                viewModel.navigateTo(Screen.ADD_SALE)
              },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
              shape = RoundedCornerShape(16.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.testTag("btn_add_all_rx_to_bill")
            ) {
              Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add All to Bill", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }

        // 6. Individual Prescribed Medicine Cards
        items(result.medicines) { drug ->
          PrescribedDrugCard(
            drug = drug,
            onAddToBill = {
              val med = drug.matchedStockItem ?: inventory.firstOrNull {
                it.name.contains(drug.medicineName.take(6), ignoreCase = true)
              }
              if (med != null) {
                viewModel.addMedicineToCart(med, drug.packQty)
                Toast.makeText(context, "Added ${drug.medicineName} to bill", Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(context, "${drug.medicineName} not in inventory. Check substitutes.", Toast.LENGTH_SHORT).show()
              }
            },
            onGoogleSearch = {
              inAppSearchTitle = "${drug.medicineName} • Monograph"
              inAppSearchQuery = drug.googleSearchUrl
            },
            onViewSubstitute = {
              viewModel.substituteQuery.value = drug.genericSalt
              viewModel.searchSubstitutesWithGemini(drug.genericSalt)
              viewModel.navigateTo(Screen.SUBSTITUTES)
            }
          )
        }

        // 7. Doctor's Clinical Advice Card
        item {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Doctor's Special Instructions & Lifestyle Advice", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextDark)
              Spacer(modifier = Modifier.height(4.dp))
              Text(result.doctorAdvice, fontSize = 11.5.sp, color = TextMuted, lineHeight = 16.sp)
            }
          }
        }

        // 8. Global Google Search Grounding Card
        item {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Google Search Grounding & Drug Verification", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = RoyalNavy)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text("Query: ${result.googleSearchGroundingQuery}", fontSize = 11.sp, color = Color(0xFF1E40AF))
              Spacer(modifier = Modifier.height(8.dp))

              Button(
                onClick = {
                  inAppSearchTitle = "Prescription Verification"
                  inAppSearchQuery = "https://www.google.com/search?q=" + java.net.URLEncoder.encode(result.googleSearchGroundingQuery, "UTF-8")
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Verify Drug on Google Search (In-App)", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    if (inAppSearchQuery != null) {
      InAppSearchDialog(
        initialQuery = inAppSearchQuery!!,
        title = inAppSearchTitle,
        onDismiss = { inAppSearchQuery = null }
      )
    }
  }
}

@Composable
fun PrescribedDrugCard(
  drug: PrescribedDrug,
  onAddToBill: () -> Unit,
  onGoogleSearch: () -> Unit,
  onViewSubstitute: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(drug.medicineName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
          Text(drug.genericSalt, fontSize = 11.5.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Medium)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (drug.isAvailableInStock) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (drug.isAvailableInStock) "In Stock (${drug.matchedStockItem?.stockPacks ?: 10} pk)" else "Out of Stock",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (drug.isAvailableInStock) Color(0xFF15803D) else Color(0xFFB91C1C)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Dosage, Frequency, Duration tags
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text("Dosage: ${drug.dosage}", fontSize = 10.5.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFEDE9FE))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text("Freq: ${drug.frequency}", fontSize = 10.5.sp, color = Color(0xFF6B21A8), fontWeight = FontWeight.Bold)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFEF3C7))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text("Duration: ${drug.duration}", fontSize = 10.5.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Medium)
        }
      }

      // Generic substitute badge
      if (!drug.genericSubstituteSuggestion.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF0FDF4))
            .clickable(onClick = onViewSubstitute)
            .padding(horizontal = 6.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FindReplace, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Generic Alt: ${drug.genericSubstituteSuggestion}",
              fontSize = 11.sp,
              color = Color(0xFF166534),
              fontWeight = FontWeight.Medium
            )
          }
          if (!drug.substitutePriceSavings.isNullOrBlank()) {
            Text(
              text = drug.substitutePriceSavings,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = Color(0xFFF8FAFC))
      Spacer(modifier = Modifier.height(6.dp))

      // Bottom buttons: Google Search & Add to Bill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onGoogleSearch,
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Search, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Google Search Drug", fontSize = 10.5.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onAddToBill,
          colors = ButtonDefaults.buttonColors(containerColor = if (drug.isAvailableInStock) RoyalNavy else Color(0xFF64748B)),
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add to Bill", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
