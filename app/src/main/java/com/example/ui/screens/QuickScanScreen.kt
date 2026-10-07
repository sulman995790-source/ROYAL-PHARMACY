package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.MedicineItem
import com.example.service.MedicineOcrParser
import com.example.service.ParsedMedicineOcrResult
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

data class ScanSamplePreset(
  val title: String,
  val subtitle: String,
  val barcode: String,
  val rawOcrText: String,
  val medicineName: String,
  val salt: String,
  val batch: String,
  val expiry: String,
  val mrp: Double,
  val mfg: String,
  val category: String = "Tablet"
)

@Composable
fun QuickScanScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasCameraPermission = granted
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  var scanMode by remember { mutableIntStateOf(0) } // 0: Barcode Scanner, 1: Blister/Strip OCR, 2: Doctor Prescription (Rx) OCR
  val feedbackMessage by viewModel.scanFeedbackMessage.collectAsState()
  val cartItems by viewModel.billingCartItems.collectAsState()
  val allMedicines by viewModel.allMedicines.collectAsState()

  // OCR Verification Dialog State
  var activeParsedOcrResult by remember { mutableStateOf<ParsedMedicineOcrResult?>(null) }
  var isAnalyzingFrame by remember { mutableStateOf(false) }

  // Photo Picker Launcher
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      isAnalyzingFrame = true
      // Simulate on-device OCR inference from captured/selected image
      val sampleText = if (scanMode == 2) {
        """
        CIVIL HOSPITAL CLINIC
        Dr. A. K. Sharma, MD
        Pt: Rahim Ali (38M)
        Rx:
        Tab Augmentin 625 Duo 1-0-1 x 5 days
        Tab Dolo 650 1-0-1 x 3 days
        Tab Pan 40 1-0-0 x 5 days
        """.trimIndent()
      } else {
        """
        GLAXOSMITHKLINE PHARMACEUTICALS
        AUGMENTIN 625 DUO
        Amoxicillin & Potassium Clavulanate Tablets IP
        B.No. AG2410
        EXP: 05/27
        MFG: 06/25
        M.R.P. Rs. 201.70
        SCHEDULE H1 PRESCRIPTION DRUG
        """.trimIndent()
      }

      val parsed = if (scanMode == 2) {
        MedicineOcrParser.parseDoctorPrescriptionText(sampleText, allMedicines)
      } else {
        MedicineOcrParser.parseMedicinePackageText(sampleText, allMedicines)
      }
      activeParsedOcrResult = parsed
      isAnalyzingFrame = false
    }
  }

  // Sample Presets for Quick Testing & Realistic Simulation
  val samplePresets = listOf(
    ScanSamplePreset(
      title = "Augmentin 625",
      subtitle = "Strip: AG2410 • Exp 05/27",
      barcode = "8903334445556",
      rawOcrText = "AUGMENTIN 625 DUO\nAmoxicillin & Potassium Clavulanate IP\nB.No: AG2410\nEXP: 05/27\nMRP Rs. 201.70\nGlaxoSmithKline",
      medicineName = "Augmentin 625 Duo",
      salt = "Amoxicillin 500mg + Clavulanic Acid 125mg",
      batch = "AG2410",
      expiry = "05/27",
      mrp = 201.70,
      mfg = "GlaxoSmithKline",
      category = "Tablet"
    ),
    ScanSamplePreset(
      title = "Dolo 650mg",
      subtitle = "Blister: DL6501 • Exp 08/27",
      barcode = "8901112223334",
      rawOcrText = "Dolo 650 Tablet\nParacetamol Tablets IP 650mg\nBatch: DL6501\nEXP: 08/27\nMRP Rs. 30.90\nMicro Labs Ltd",
      medicineName = "Dolo 650",
      salt = "Paracetamol 650mg",
      batch = "DL6501",
      expiry = "08/27",
      mrp = 30.90,
      mfg = "Micro Labs",
      category = "Tablet"
    ),
    ScanSamplePreset(
      title = "Pan 40 Tablet",
      subtitle = "Strip: PN4012 • Exp 11/26",
      barcode = "8902223334445",
      rawOcrText = "Pan 40 Tablet\nPantoprazole Gastro-resistant\nB.No. PN4012\nEXP. 11/26\nM.R.P. Rs. 155.00\nAlkem Laboratories",
      medicineName = "Pan 40 Tablet",
      salt = "Pantoprazole 40mg",
      batch = "PN4012",
      expiry = "11/26",
      mrp = 155.00,
      mfg = "Alkem Laboratories",
      category = "Tablet"
    ),
    ScanSamplePreset(
      title = "Monocef 1g Inj",
      subtitle = "Vial: MN1088 • Exp 09/27",
      barcode = "8904445556667",
      rawOcrText = "Monocef 1g Injection Vial\nCeftriaxone Sodium IP 1000mg\nLot: MN1088\nEXP: 09/27\nMRP: ₹68.50\nAristo Pharma",
      medicineName = "Monocef 1g Injection",
      salt = "Ceftriaxone 1g",
      batch = "MN1088",
      expiry = "09/27",
      mrp = 68.50,
      mfg = "Aristo",
      category = "Injection"
    ),
    ScanSamplePreset(
      title = "Rx: Dr. Sharma Slip",
      subtitle = "Prescription: 3 Medicines",
      barcode = "8907778889990",
      rawOcrText = "Dr. A. K. Sharma, MD\nPt: Rahim Ali (38M)\nTab Azithral 500 OD x 3d\nTab Montair LC HS x 5d\nSyp Ascoril-LS TDS x 5d",
      medicineName = "Azithral 500 Tablet",
      salt = "Azithromycin 500mg",
      batch = "AZ9901",
      expiry = "04/27",
      mrp = 119.50,
      mfg = "Alembic Pharma",
      category = "Tablet"
    ),
    ScanSamplePreset(
      title = "Betadine 10%",
      subtitle = "Bottle: BT8901 • Exp 03/27",
      barcode = "8909991112223",
      rawOcrText = "Betadine 10% Solution\nPovidone Iodine 10% w/v\nB/No: BT8901\nEXP: 03/27\nMRP Rs. 115.00\nWin-Medicare",
      medicineName = "Betadine 10% Solution",
      salt = "Povidone Iodine 10%",
      batch = "BT8901",
      expiry = "03/27",
      mrp = 115.00,
      mfg = "Win-Medicare",
      category = "Ointment"
    )
  )

  fun triggerVibration() {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        it.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        it.vibrate(80)
      }
    }
  }

  fun runOcrOnText(text: String) {
    triggerVibration()
    val parsed = if (scanMode == 2) {
      MedicineOcrParser.parseDoctorPrescriptionText(text, allMedicines)
    } else {
      MedicineOcrParser.parseMedicinePackageText(text, allMedicines)
    }
    activeParsedOcrResult = parsed
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    // 1. Camera Viewfinder or Simulated Visual Scanner
    if (hasCameraPermission) {
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx)
          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
          cameraProviderFuture.addListener({
            try {
              val cameraProvider = cameraProviderFuture.get()
              val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
              }
              val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
              cameraProvider.unbindAll()
              cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            } catch (e: Exception) {
              // Gracefully handle emulator camera setup
            }
          }, ContextCompat.getMainExecutor(ctx))
          previewView
        },
        modifier = Modifier.fillMaxSize()
      )
    }

    // Semi-transparent darkened overlay with clear scanning target reticle
    Canvas(modifier = Modifier.fillMaxSize()) {
      val canvasWidth = size.width
      val canvasHeight = size.height
      val reticleWidth = canvasWidth * 0.82f
      val reticleHeight = if (scanMode == 0) reticleWidth * 0.52f else reticleWidth * 0.80f
      val left = (canvasWidth - reticleWidth) / 2
      val top = (canvasHeight - reticleHeight) / 2 - 30.dp.toPx()

      // Target reticle box border with rounded corners
      val reticleColor = when (scanMode) {
        0 -> Color(0xFFE91E63)
        1 -> Color(0xFF10B981)
        else -> Color(0xFF8B5CF6)
      }

      drawRoundRect(
        color = reticleColor,
        topLeft = Offset(left, top),
        size = Size(reticleWidth, reticleHeight),
        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
        style = Stroke(width = 3.dp.toPx())
      )

      // Corner accent brackets
      val cornerLength = 28.dp.toPx()
      val strokeW = 5.dp.toPx()
      drawLine(Color.White, Offset(left - 2, top), Offset(left + cornerLength, top), strokeW)
      drawLine(Color.White, Offset(left, top - 2), Offset(left, top + cornerLength), strokeW)
      drawLine(Color.White, Offset(left + reticleWidth - cornerLength, top), Offset(left + reticleWidth + 2, top), strokeW)
      drawLine(Color.White, Offset(left + reticleWidth, top - 2), Offset(left + reticleWidth, top + cornerLength), strokeW)
      drawLine(Color.White, Offset(left - 2, top + reticleHeight), Offset(left + cornerLength, top + reticleHeight), strokeW)
      drawLine(Color.White, Offset(left, top + reticleHeight - cornerLength), Offset(left, top + reticleHeight + 2), strokeW)
      drawLine(Color.White, Offset(left + reticleWidth - cornerLength, top + reticleHeight), Offset(left + reticleWidth + 2, top + reticleHeight), strokeW)
      drawLine(Color.White, Offset(left + reticleWidth, top + reticleHeight - cornerLength), Offset(left + reticleWidth, top + reticleHeight + 2), strokeW)
    }

    // 2. Top Header & Mode Tabs
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.Black.copy(alpha = 0.65f))
          .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Column {
            Text(
              text = when (scanMode) {
                0 -> "QuikScan Barcode"
                1 -> "Blister & Packaging OCR"
                else -> "Doctor Prescription (Rx) OCR"
              },
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Auto-extracts Name, Batch, Expiry & MRP",
              fontSize = 10.sp,
              color = Color.LightGray
            )
          }
        }

        Row {
          IconButton(onClick = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          }) {
            Icon(Icons.Default.Image, contentDescription = "Pick Image from Gallery", tint = Color.White)
          }

          IconButton(onClick = { triggerVibration() }) {
            Icon(Icons.Default.FlashOn, contentDescription = "Torch", tint = Color(0xFFFFD54F))
          }
        }
      }

      // Mode Selector Tab Row
      TabRow(
        selectedTabIndex = scanMode,
        containerColor = Color.Black.copy(alpha = 0.75f),
        contentColor = RoyalMagenta,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[scanMode]),
            color = when (scanMode) {
              0 -> Color(0xFFE91E63)
              1 -> Color(0xFF10B981)
              else -> Color(0xFF8B5CF6)
            }
          )
        }
      ) {
        Tab(
          selected = scanMode == 0,
          onClick = { scanMode = 0 },
          text = { Text("Barcode", fontSize = 11.sp, color = if (scanMode == 0) Color.White else Color.Gray) },
          modifier = Modifier.testTag("tab_scan_barcode")
        )
        Tab(
          selected = scanMode == 1,
          onClick = { scanMode = 1 },
          text = { Text("Strip / Box OCR", fontSize = 11.sp, color = if (scanMode == 1) Color.White else Color.Gray) },
          modifier = Modifier.testTag("tab_scan_blister_ocr")
        )
        Tab(
          selected = scanMode == 2,
          onClick = { scanMode = 2 },
          text = { Text("Prescription Rx", fontSize = 11.sp, color = if (scanMode == 2) Color.White else Color.Gray) },
          modifier = Modifier.testTag("tab_scan_rx_ocr")
        )
      }

      // Scan feedback alert message
      AnimatedVisibility(visible = feedbackMessage != null) {
        feedbackMessage?.let { msg ->
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = StatusGreen),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = msg, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }
    }

    // 3. Shutter Capture Button in center bottom
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 220.dp)
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.25f))
          .clickable {
            triggerVibration()
            val preset = samplePresets.firstOrNull { it.title.contains("Augmentin") } ?: samplePresets.first()
            runOcrOnText(preset.rawOcrText)
          },
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.White),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Capture Frame for OCR",
            tint = Color.Black,
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }

    // 4. Bottom Controls & Presets Strip
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .background(Color.Black.copy(alpha = 0.90f))
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (scanMode == 0) "Tap preset barcode or align package inside reticle"
                 else "Tap sample foil/slip or shutter to analyze via AI OCR",
          fontSize = 11.sp,
          color = Color.LightGray
        )

        // Cart items badge button
        if (cartItems.isNotEmpty()) {
          Button(
            onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.testTag("btn_view_cart_from_scanner")
          ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Bill (${cartItems.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Presets Horizontal Carousel
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
      ) {
        items(samplePresets) { preset ->
          Card(
            onClick = {
              if (scanMode == 0) {
                triggerVibration()
                viewModel.handleScannedBarcode(preset.barcode)
              } else {
                runOcrOnText(preset.rawOcrText)
              }
            },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF334155))
            ),
            modifier = Modifier.testTag("preset_${preset.title.replace(" ", "_").lowercase()}")
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = preset.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF0F766E))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(preset.category, fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
              }
              Text(text = preset.subtitle, fontSize = 10.sp, color = Color(0xFF64D2B4))
              Text(text = "MRP: ₹${preset.mrp} • ${preset.mfg}", fontSize = 9.5.sp, color = Color.LightGray)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Quick Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          modifier = Modifier.weight(1f).height(44.dp)
        ) {
          Text("Go to Billing", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Button(
          onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
          modifier = Modifier.weight(1f).height(44.dp)
        ) {
          Text("Expiry Dashboard", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // 5. Interactive OCR Verification & Review Dialog
    activeParsedOcrResult?.let { ocr ->
      var editName by remember { mutableStateOf(ocr.medicineName) }
      var editBatch by remember { mutableStateOf(ocr.batchNumber) }
      var editExpiry by remember { mutableStateOf(ocr.expiryDate) }
      var editMrp by remember { mutableStateOf(ocr.mrp.toString()) }

      AlertDialog(
        onDismissRequest = { activeParsedOcrResult = null },
        title = {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("OCR Parsed (${ocr.confidenceScore}%)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { activeParsedOcrResult = null }) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }
        },
        text = {
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = if (ocr.prescribedItems.size > 1) "Doctor: ${ocr.doctorName} • Patient: ${ocr.patientName}"
                     else "Extracted from packaging. Review and confirm below:",
              fontSize = 11.5.sp,
              color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // If Multi-item Prescription
            if (ocr.prescribedItems.size > 1) {
              Text("Prescribed Medicines:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Spacer(modifier = Modifier.height(4.dp))
              ocr.prescribedItems.forEach { rxItem ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(rxItem.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${rxItem.dosage} • ${rxItem.duration}", fontSize = 10.sp, color = TextMuted)
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (rxItem.inStock) StatusGreen.copy(alpha = 0.15f) else Color(0xFFFEE2E2))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = if (rxItem.inStock) "In Stock" else "Low Stock",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (rxItem.inStock) StatusGreen else Color(0xFFDC2626)
                    )
                  }
                }
              }
            } else {
              // Single Blister/Strip item fields
              OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text("Medicine Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(54.dp)
              )

              Spacer(modifier = Modifier.height(6.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = editBatch,
                  onValueChange = { editBatch = it },
                  label = { Text("Batch No") },
                  singleLine = true,
                  modifier = Modifier.weight(1f).height(54.dp)
                )

                OutlinedTextField(
                  value = editExpiry,
                  onValueChange = { editExpiry = it },
                  label = { Text("Expiry (MM/YY)") },
                  singleLine = true,
                  modifier = Modifier.weight(1f).height(54.dp)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = editMrp,
                onValueChange = { editMrp = it },
                label = { Text("MRP (₹)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(54.dp)
              )
            }
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (ocr.prescribedItems.size > 1) {
                ocr.prescribedItems.forEach { rxItem ->
                  rxItem.matchedInventoryItem?.let { med ->
                    viewModel.addMedicineToCart(med, 1)
                  } ?: run {
                    viewModel.handleParsedLabelOcr(
                      name = rxItem.name,
                      batch = "RX-DR",
                      expiry = "12/27",
                      mrp = 110.0,
                      manufacturer = "Standard Generic"
                    )
                  }
                }
              } else {
                viewModel.handleParsedLabelOcr(
                  name = editName,
                  batch = editBatch,
                  expiry = editExpiry,
                  mrp = editMrp.toDoubleOrNull() ?: ocr.mrp,
                  manufacturer = ocr.manufacturer
                )
              }
              Toast.makeText(context, "Added to Bill Cart!", Toast.LENGTH_SHORT).show()
              activeParsedOcrResult = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
          ) {
            Text("Add to Sale Bill")
          }
        },
        dismissButton = {
          OutlinedButton(onClick = { activeParsedOcrResult = null }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}
