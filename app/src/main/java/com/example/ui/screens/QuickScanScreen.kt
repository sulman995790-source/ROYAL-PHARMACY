@file:Suppress("DEPRECATION")
package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.MedicineItem
import com.example.service.ImageOcrPreprocessor
import com.example.service.MedicineOcrParser
import com.example.service.ParsedMedicineOcrResult
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.concurrent.Executors

data class CapturedPhoto(
  val id: Long = System.currentTimeMillis(),
  val bitmap: Bitmap,
  val label: String,
  var recognizedText: String = ""
)

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
  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

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

  var scanMode by remember { mutableIntStateOf(1) } // 0: Barcode, 1: Blister/Strip OCR (Multi-photo), 2: Doctor Prescription Rx, 3: Quick Return
  var lastScannedBarcode by remember { mutableStateOf("") }
  var scanCount by remember { mutableIntStateOf(0) }
  
  val feedbackMessage by viewModel.scanFeedbackMessage.collectAsState()
  val cartItems by viewModel.billingCartItems.collectAsState()
  val allMedicines by viewModel.allMedicines.collectAsState()

  // Multi-Photo Capture Queue
  val capturedPhotos = remember { mutableStateListOf<CapturedPhoto>() }

  // Camera & Flashlight Torch state
  var cameraInstance by remember { mutableStateOf<Camera?>(null) }
  var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
  var isTorchOn by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    onDispose {
      try {
        cameraInstance?.cameraControl?.enableTorch(false)
      } catch (_: Exception) {}
      try {
        val camManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        camManager?.cameraIdList?.firstOrNull()?.let { camManager.setTorchMode(it, false) }
      } catch (_: Exception) {}
    }
  }

  LaunchedEffect(isTorchOn, cameraInstance) {
    try {
      if (cameraInstance != null && cameraInstance?.cameraInfo?.hasFlashUnit() == true) {
        cameraInstance?.cameraControl?.enableTorch(isTorchOn)
      }
    } catch (_: Exception) {}
  }

  // OCR Verification Dialog State
  var activeParsedOcrResult by remember { mutableStateOf<ParsedMedicineOcrResult?>(null) }
  var isAnalyzingFrame by remember { mutableStateOf(false) }

  fun triggerVibration() {
    val vibrator = androidx.core.content.ContextCompat.getSystemService(context, Vibrator::class.java)
    vibrator?.let {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        it.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        it.vibrate(80)
      }
    }
  }

  // ML Kit Instances
  val barcodeScanner = remember {
    BarcodeScanning.getClient(
      BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()
    )
  }
  val textRecognizer = remember {
    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
  }
  val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

  // Adds a photo to the multi-photo staging queue and immediately triggers on-device OCR with local pre-processing
  fun addPhotoToQueue(bitmap: Bitmap, customLabel: String? = null) {
    triggerVibration()
    val nextIndex = capturedPhotos.size + 1
    val label = customLabel ?: when (nextIndex) {
      1 -> "Photo 1 (Front / Brand)"
      2 -> "Photo 2 (Batch & Expiry)"
      3 -> "Photo 3 (MRP & Details)"
      else -> "Photo $nextIndex (Side / Flap)"
    }
    val newPhoto = CapturedPhoto(
      id = System.currentTimeMillis() + nextIndex,
      bitmap = bitmap,
      label = label
    )
    capturedPhotos.add(newPhoto)

    // Local Pre-processing: Convert to high-contrast grayscale + 3x3 sharpening for dot-matrix batch/expiry
    val enhanced = ImageOcrPreprocessor.preprocessForOcr(bitmap, contrastGain = 1.5f, brightnessOffset = 5f, applySharpening = true)
    val image = InputImage.fromBitmap(enhanced, 0)
    textRecognizer.process(image)
      .addOnSuccessListener { visionText ->
        val idx = capturedPhotos.indexOfFirst { it.id == newPhoto.id }
        if (idx != -1) {
          capturedPhotos[idx] = capturedPhotos[idx].copy(recognizedText = visionText.text)
        }
      }

    Toast.makeText(context, "$label added! (Local contrast & sharpness enhanced)", Toast.LENGTH_SHORT).show()
  }

  fun completeMultiPhotoMerge(texts: List<String>) {
    isAnalyzingFrame = false
    val parsed = if (scanMode == 2) {
      val merged = texts.joinToString("\n")
      MedicineOcrParser.parseDoctorPrescriptionText(merged, allMedicines)
    } else {
      MedicineOcrParser.parseMultipleMedicinePackageTexts(texts, allMedicines)
    }
    activeParsedOcrResult = parsed
  }

  fun processAllCapturedPhotos() {
    if (capturedPhotos.isEmpty()) {
      val currentPreviewBitmap = previewViewRef?.bitmap
      if (currentPreviewBitmap != null) {
        addPhotoToQueue(currentPreviewBitmap)
      } else {
        Toast.makeText(context, "Please take or pick at least one photo", Toast.LENGTH_SHORT).show()
        return
      }
    }

    triggerVibration()
    isAnalyzingFrame = true

    val photosSnapshot = capturedPhotos.toList()
    val texts = mutableListOf<String>()
    var pendingCount = photosSnapshot.size

    photosSnapshot.forEach { photo ->
      if (photo.recognizedText.isNotBlank()) {
        texts.add(photo.recognizedText)
        pendingCount--
        if (pendingCount == 0) {
          completeMultiPhotoMerge(texts)
        }
      } else {
        val enhanced = ImageOcrPreprocessor.preprocessForOcr(photo.bitmap, contrastGain = 1.55f, applySharpening = true)
        val image = InputImage.fromBitmap(enhanced, 0)
        textRecognizer.process(image)
          .addOnSuccessListener { visionText ->
            texts.add(visionText.text)
          }
          .addOnFailureListener {
            texts.add("")
          }
          .addOnCompleteListener {
            pendingCount--
            if (pendingCount == 0) {
              completeMultiPhotoMerge(texts)
            }
          }
      }
    }
  }

  // Camera Capture Launcher (Direct photo snapping)
  val cameraCaptureLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      addPhotoToQueue(bitmap)
    }
  }

  // Multi Photo Picker Launcher (Gallery multiple selection)
  val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
  ) { uris: List<Uri> ->
    if (uris.isNotEmpty()) {
      uris.forEach { uri ->
        try {
          val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
          }
          if (bitmap != null) {
            addPhotoToQueue(bitmap)
          }
        } catch (e: Exception) {
          Toast.makeText(context, "Image error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  // Single Photo Picker Launcher
  val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
          BitmapFactory.decodeStream(stream)
        }
        if (bitmap != null) {
          addPhotoToQueue(bitmap)
        }
      } catch (e: Exception) {
        Toast.makeText(context, "Image error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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

  // --- Real Camera Frame Live Processing Logic ---
  fun processImageProxy(imageProxy: ImageProxy) {
    if (isAnalyzingFrame || activeParsedOcrResult != null) {
      imageProxy.close()
      return
    }

    val mediaImage = imageProxy.image
    if (mediaImage != null) {
      val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
      
      if (scanMode == 0 || scanMode == 3) {
        // Barcode Mode / Quick Return
        barcodeScanner.process(image)
          .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
              val barcode = barcodes.first().rawValue ?: ""
              if (barcode.isNotBlank() && barcode != lastScannedBarcode) {
                lastScannedBarcode = barcode
                scanCount++
                triggerVibration()
                if (scanMode == 0) {
                    viewModel.handleScannedBarcode(barcode)
                } else {
                    viewModel.scannedBarcodeForReturn.value = barcode
                    viewModel.navigateTo(Screen.BATCH_TRACKING)
                }
              }
            }
          }
          .addOnCompleteListener { imageProxy.close() }
      } else {
        imageProxy.close()
      }
    } else {
      imageProxy.close()
    }
  }

  // Auto-register parsed doctor in Doctor directory
  LaunchedEffect(activeParsedOcrResult) {
    activeParsedOcrResult?.let { ocr ->
      if (ocr.doctorName.isNotBlank() && !ocr.doctorName.equals("Unknown Doctor", ignoreCase = true) && ocr.doctorName.length > 3) {
        viewModel.autoRegisterDoctorFromPrescription(ocr.doctorName)
      }
    }
  }

  // Sample Presets for Quick Testing
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

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    // 1. Camera Viewfinder
    if (hasCameraPermission) {
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx)
          previewViewRef = previewView
          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
          cameraProviderFuture.addListener({
            try {
              val cameraProvider = cameraProviderFuture.get()
              val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
              }
              val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

              val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                  it.setAnalyzer(analysisExecutor) { imageProxy ->
                    processImageProxy(imageProxy)
                  }
                }

              val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
              cameraProvider.unbindAll()
              val cam = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalysis)
              cameraInstance = cam
            } catch (_: Exception) {}
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
      val reticleWidth = canvasWidth * 0.84f
      val reticleHeight = if (scanMode == 0) reticleWidth * 0.52f else reticleWidth * 0.72f
      val left = (canvasWidth - reticleWidth) / 2
      val top = (canvasHeight - reticleHeight) / 2 - 40.dp.toPx()

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
          .background(Color.Black.copy(alpha = 0.85f))
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f, fill = false),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }, modifier = Modifier.size(40.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(4.dp))
          Column {
            Text(
              text = when (scanMode) {
                0 -> "QuikScan Barcode"
                1 -> "Multi-Photo Strip OCR"
                else -> "Doctor Prescription Rx"
              },
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .clip(CircleShape)
                  .background(if (scanMode == 0) Color(0xFFE91E63) else Color(0xFF10B981))
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (scanMode == 0) "Barcode ($scanCount items)"
                       else "Photos (${capturedPhotos.size} ready)",
                fontSize = 10.sp,
                color = Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Camera Photo Capture Launcher (for OCR modes)
          if (scanMode != 0) {
            IconButton(
              onClick = { cameraCaptureLauncher.launch(null) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.Default.AddAPhoto, contentDescription = "Take Photo", tint = Color.White, modifier = Modifier.size(18.dp))
            }

            // Multi Photo Gallery Picker
            IconButton(
              onClick = { multiPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.Default.PhotoLibrary, contentDescription = "Pick Photos", tint = Color.White, modifier = Modifier.size(18.dp))
            }
          }

          // Dedicated Flashlight Torch Toggle (Accessible & prominent in ALL modes)
          IconButton(
            onClick = {
              val nextState = !isTorchOn
              isTorchOn = nextState
              triggerVibration()

              var torchApplied = false
              if (cameraInstance != null) {
                try {
                  cameraInstance?.cameraControl?.enableTorch(nextState)
                  torchApplied = true
                } catch (_: Exception) {}
              }
              if (!torchApplied) {
                try {
                  val camManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                  val cameraId = camManager?.cameraIdList?.firstOrNull()
                  if (cameraId != null) {
                    camManager.setTorchMode(cameraId, nextState)
                  }
                } catch (_: Exception) {}
              }

              Toast.makeText(
                context,
                if (nextState) "Flashlight Turned ON ⚡" else "Flashlight Turned OFF",
                Toast.LENGTH_SHORT
              ).show()
            },
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (isTorchOn) Color(0xFFFFD54F) else Color(0xFF334155))
              .testTag("btn_toggle_flashlight")
          ) {
            Icon(
              imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
              contentDescription = "Flashlight Toggle",
              tint = if (isTorchOn) Color.Black else Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Mode Selector Tab Row
      TabRow(
        selectedTabIndex = scanMode,
        containerColor = Color.Black.copy(alpha = 0.85f),
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
          text = { Text("Multi-Photo OCR", fontSize = 11.sp, color = if (scanMode == 1) Color.White else Color.Gray) },
          modifier = Modifier.testTag("tab_scan_blister_ocr")
        )
        Tab(
          selected = scanMode == 2,
          onClick = { scanMode = 2 },
          text = { Text("Prescription Rx", fontSize = 11.sp, color = if (scanMode == 2) Color.White else Color.Gray) },
          modifier = Modifier.testTag("tab_scan_rx_ocr")
        )
        Tab(
          selected = scanMode == 3,
          onClick = { scanMode = 3 },
          text = { Text("Quick Return", fontSize = 11.sp, color = if (scanMode == 3) Color.White else Color.Gray) },
          modifier = Modifier.testTag("tab_quick_return")
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

      // In-flight analyzing indicator
      if (isAnalyzingFrame) {
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF6D28D9)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Extracting & Merging text from ${capturedPhotos.size} photos...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }

    // 3. Multi-Photo Thumbnail Bar (Appears when photos have been captured)
    if (capturedPhotos.isNotEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(bottom = 270.dp)
          .background(Color.Black.copy(alpha = 0.85f))
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Captured Angles (${capturedPhotos.size})",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          Row {
            Text(
              text = "Clear All",
              fontSize = 11.sp,
              color = Color(0xFFEF4444),
              modifier = Modifier
                .clickable {
                  capturedPhotos.clear()
                  triggerVibration()
                }
                .padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
          itemsIndexed(capturedPhotos) { index, photo ->
            Box(
              modifier = Modifier
                .width(100.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
            ) {
              Image(
                bitmap = photo.bitmap.asImageBitmap(),
                contentDescription = photo.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )

              // Label badge
              Box(
                modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .fillMaxWidth()
                  .background(Color.Black.copy(alpha = 0.75f))
                  .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (index) {
                    0 -> "#1 Front/Brand"
                    1 -> "#2 Batch/Exp"
                    2 -> "#3 MRP/Flap"
                    else -> "#${index + 1} Side"
                  },
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }

              // Delete button
              Box(
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(3.dp)
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(Color.Black.copy(alpha = 0.7f))
                  .clickable {
                    capturedPhotos.removeAt(index)
                    triggerVibration()
                  },
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Close, contentDescription = "Delete Photo", tint = Color.White, modifier = Modifier.size(12.dp))
              }
            }
          }

          // + Add Photo Card
          item {
            Box(
              modifier = Modifier
                .width(90.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                .clickable {
                  val currentPreviewBitmap = previewViewRef?.bitmap
                  if (currentPreviewBitmap != null) {
                    addPhotoToQueue(currentPreviewBitmap)
                  } else {
                    cameraCaptureLauncher.launch(null)
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Angle", tint = Color.LightGray, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("+ Angle", fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 4. Shutter Capture & Action Buttons Area
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 190.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (capturedPhotos.isNotEmpty()) {
        Button(
          onClick = { processAllCapturedPhotos() },
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
          modifier = Modifier.padding(bottom = 10.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Merge & Analyze (${capturedPhotos.size} Photos)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }

      // Shutter Circle Button
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.25f))
          .clickable {
            val currentPreviewBitmap = previewViewRef?.bitmap
            if (currentPreviewBitmap != null) {
              if (scanMode == 0) {
                // In barcode mode, direct process
                val image = InputImage.fromBitmap(currentPreviewBitmap, 0)
                barcodeScanner.process(image).addOnSuccessListener { barcodes ->
                  if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first().rawValue ?: ""
                    if (barcode.isNotBlank()) {
                      lastScannedBarcode = barcode
                      scanCount++
                      viewModel.handleScannedBarcode(barcode)
                    }
                  } else {
                    addPhotoToQueue(currentPreviewBitmap)
                  }
                }
              } else {
                addPhotoToQueue(currentPreviewBitmap)
              }
            } else {
              cameraCaptureLauncher.launch(null)
            }
          },
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color.White),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Capture Photo for OCR",
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
          )
        }
      }
      
      Text(
        text = if (capturedPhotos.isEmpty()) "Tap shutter to capture Photo 1 (Front)" else "Tap shutter for next photo or tap Merge",
        fontSize = 10.5.sp,
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    // 5. Bottom Controls & Presets Strip
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .background(Color.Black.copy(alpha = 0.92f))
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (scanMode == 0) "Point at barcode or snap photo"
                 else "Take multiple photos (Front + Batch/Exp flap)",
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

      Spacer(modifier = Modifier.height(6.dp))

      // Presets Horizontal Carousel for instant demo testing
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
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
            Column(modifier = Modifier.padding(8.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = preset.title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
              Text(text = preset.subtitle, fontSize = 9.5.sp, color = Color(0xFF64D2B4))
              Text(text = "MRP: ₹${preset.mrp} • ${preset.mfg}", fontSize = 9.sp, color = Color.LightGray)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Quick Action Navigation Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          modifier = Modifier.weight(1f).height(42.dp)
        ) {
          Text("Go to Billing", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Button(
          onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
          modifier = Modifier.weight(1f).height(42.dp)
        ) {
          Text("Expiry Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // 6. Interactive OCR Verification & Review Dialog with Full Editing & Multi-Photo Support
    activeParsedOcrResult?.let { ocr ->
      var editName by remember { mutableStateOf(ocr.medicineName) }
      var editSalt by remember { mutableStateOf(ocr.saltComposition) }
      var editBatch by remember { mutableStateOf(ocr.batchNumber) }
      var editExpiry by remember { mutableStateOf(ocr.expiryDate) }
      var editMfgDate by remember { mutableStateOf(ocr.mfgDate) }
      var editMrp by remember { mutableStateOf(if (ocr.mrp > 0) ocr.mrp.toString() else "100.0") }
      var editMfgCompany by remember { mutableStateOf(ocr.manufacturer) }
      var editCategory by remember { mutableStateOf(ocr.category) }
      var editStockPacks by remember { mutableStateOf("10") }

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
              Text("OCR Extracted (${ocr.confidenceScore}%)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { activeParsedOcrResult = null }) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }
        },
        text = {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .verticalScroll(rememberScrollState())
          ) {
            if (capturedPhotos.size > 1) {
              Card(
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE9FE)),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 8.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF6D28D9), modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Fused from ${capturedPhotos.size} photos (Front + Flap/Details)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5B21B6)
                  )
                }
              }
            }

            Text(
              text = if (ocr.prescribedItems.size > 1) "Doctor: ${ocr.doctorName} • Patient: ${ocr.patientName}"
                     else "Verify & edit medicine specifications before saving:",
              fontSize = 11.sp,
              color = TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                  Column(modifier = Modifier.weight(1f)) {
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
              // Single / Multi-photo Medicine fields
              OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text("Brand / Medicine Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(54.dp)
              )

              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = editSalt,
                onValueChange = { editSalt = it },
                label = { Text("Salt / Composition") },
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

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = editMrp,
                  onValueChange = { editMrp = it },
                  label = { Text("MRP (₹)") },
                  singleLine = true,
                  modifier = Modifier.weight(1f).height(54.dp)
                )

                OutlinedTextField(
                  value = editStockPacks,
                  onValueChange = { editStockPacks = it },
                  label = { Text("Stock Qty (Packs)") },
                  singleLine = true,
                  modifier = Modifier.weight(1f).height(54.dp)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = editMfgCompany,
                onValueChange = { editMfgCompany = it },
                label = { Text("Manufacturer / Marketer") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(54.dp)
              )
            }
          }
        },
        confirmButton = {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              // Option 1: Save directly to Inventory
              Button(
                onClick = {
                  val price = editMrp.toDoubleOrNull() ?: ocr.mrp
                  val qty = editStockPacks.toIntOrNull() ?: 10
                  val item = MedicineItem(
                    name = editName.ifBlank { "Scanned Medicine" },
                    manufacturer = editMfgCompany.ifBlank { "Standard Pharmaceutical Ltd" },
                    composition = editSalt.ifBlank { "Active Formula" },
                    saltMolecule = editSalt.ifBlank { "Active Molecule" },
                    category = editCategory,
                    barcode = "890" + (1000000000..9999999999).random(),
                    batchNumber = editBatch.ifBlank { "BAT" + (1000..9999).random() },
                    expiryDate = editExpiry.ifBlank { "12/27" },
                    stockPacks = qty,
                    mrp = if (price > 0) price else 120.0,
                    purchaseRate = if (price > 0) price * 0.75 else 90.0,
                    saleRate = if (price > 0) price else 120.0
                  )
                  viewModel.addNewMedicine(item)
                  Toast.makeText(context, "Saved to Inventory Stock (${qty} packs)!", Toast.LENGTH_SHORT).show()
                  activeParsedOcrResult = null
                  capturedPhotos.clear()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add to Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              // Option 2: Add to Sale Bill Cart
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
                      manufacturer = editMfgCompany.ifBlank { "Standard Pharmaceutical Ltd" }
                    )
                  }
                  Toast.makeText(context, "Added to Sale Bill Cart!", Toast.LENGTH_SHORT).show()
                  activeParsedOcrResult = null
                  capturedPhotos.clear()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add to Bill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        },
        dismissButton = {
          OutlinedButton(
            onClick = { activeParsedOcrResult = null },
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Cancel", fontSize = 11.sp)
          }
        }
      )
    }
  }
}
