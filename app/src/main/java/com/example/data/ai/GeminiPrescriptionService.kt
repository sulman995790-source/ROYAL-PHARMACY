package com.example.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.data.db.MedicineBatchEntity
import com.example.data.model.MedicineItem
import com.example.service.ImageOcrPreprocessor
import com.example.service.MedicineOcrParser
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class PrescribedDrug(
  val medicineName: String,
  val genericSalt: String,
  val dosage: String,
  val frequency: String,
  val duration: String,
  val packQty: Int = 1,
  val estPrice: Double = 0.0,
  val isAvailableInStock: Boolean = false,
  val matchedStockItem: MedicineItem? = null,
  val genericSubstituteSuggestion: String? = null,
  val substitutePriceSavings: String? = null,
  val googleSearchQuery: String = "",
  val googleSearchUrl: String = "",
  val safetyWarning: String? = null
)

data class PrescriptionScanResult(
  val doctorName: String,
  val doctorSpeciality: String,
  val clinicOrHospital: String,
  val patientName: String,
  val patientAgeGender: String,
  val diagnosis: String,
  val prescriptionDate: String,
  val medicines: List<PrescribedDrug>,
  val doctorAdvice: String,
  val drugInteractionAlert: String?,
  val googleSearchGroundingQuery: String,
  val rawGeminiOutput: String,
  val isAiPowered: Boolean = true,
  val errorMessage: String? = null
)

data class ExtractedRxItem(
  val prescribedName: String,
  val dosageSchedule: String,
  val matchedBatchId: Long?,
  val matchedBrandName: String?,
  val clinicalNote: String
)

object GeminiPrescriptionService {

  private const val TAG = "GeminiPrescriptionSvc"
  private const val VISION_MODEL = "gemini-3.8-flash"

  private val textRecognizer by lazy {
    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
  }

  private val prescriptionSchema = Schema.obj(
    properties = mapOf(
      "doctorName" to Schema.string(),
      "doctorSpeciality" to Schema.string(),
      "clinicOrHospital" to Schema.string(),
      "patientName" to Schema.string(),
      "patientAgeGender" to Schema.string(),
      "diagnosis" to Schema.string(),
      "prescriptionDate" to Schema.string(),
      "medicines" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "medicineName" to Schema.string(),
            "genericSalt" to Schema.string(),
            "dosage" to Schema.string(),
            "frequency" to Schema.string(),
            "duration" to Schema.string(),
            "packQty" to Schema.integer(),
            "estPrice" to Schema.double(),
            "genericSubstituteSuggestion" to Schema.string(),
            "substitutePriceSavings" to Schema.string(),
            "googleSearchQuery" to Schema.string(),
            "safetyWarning" to Schema.string()
          )
        )
      ),
      "doctorAdvice" to Schema.string(),
      "drugInteractionAlert" to Schema.string(),
      "googleSearchGroundingQuery" to Schema.string()
    )
  )

  private val extractedRxSchema = Schema.obj(
    properties = mapOf(
      "items" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "prescribedName" to Schema.string(),
            "dosageSchedule" to Schema.string(),
            "matchedBatchId" to Schema.integer(),
            "matchedBrandName" to Schema.string(),
            "clinicalNote" to Schema.string()
          ),
          optionalProperties = listOf("matchedBatchId", "matchedBrandName")
        )
      )
    )
  )

  private val visionModel by lazy {
    Firebase.ai.generativeModel(
      modelName = VISION_MODEL,
      generationConfig = generationConfig {
        temperature = 0.15f
        responseMimeType = "application/json"
        responseSchema = prescriptionSchema
      }
    )
  }

  private val quickExtractModel by lazy {
    Firebase.ai.generativeModel(
      modelName = VISION_MODEL,
      generationConfig = generationConfig {
        temperature = 0.1f
        responseMimeType = "application/json"
        responseSchema = extractedRxSchema
      }
    )
  }

  private fun Bitmap.scaleForAiVision(): Bitmap {
    val maxDim = 1200
    if (width <= maxDim && height <= maxDim) return this
    val ratio = width.toFloat() / height.toFloat()
    val newWidth = if (width > height) maxDim else (maxDim * ratio).toInt()
    val newHeight = if (height > width) maxDim else (maxDim / ratio).toInt()
    return Bitmap.createScaledBitmap(this, newWidth.coerceAtLeast(1), newHeight.coerceAtLeast(1), true)
  }

  suspend fun scanPrescriptionWithGemini4(
    imageBytes: ByteArray,
    availableStock: List<MedicineBatchEntity>
  ): Result<List<ExtractedRxItem>> = withContext(Dispatchers.IO) {
    try {
      val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        ?: return@withContext Result.failure(IllegalArgumentException("Unable to decode prescription image"))

      val scaled = bitmap.scaleForAiVision()
      val stockLines = availableStock.take(80).joinToString("\n") {
        "ID:${it.id}|Brand:${it.brandName}|Salt:${it.genericSalt}|Qty:${it.stockQuantity}|MRP:${it.sellingMrp}"
      }

      val prompt = """
        Analyze this Indian medical prescription image carefully.
        Match prescribed medicines against our live inventory if available:
        $stockLines

        Extract every prescribed medicine, dosage schedule (e.g. "1-0-1 after meals x 5 days"),
        matchedBatchId (or omit if unstocked), matchedBrandName, and concise clinicalNote (including active salt composition).
      """.trimIndent()

      val response = withTimeout(60_000L) {
        quickExtractModel.generateContent(
          content {
            image(scaled)
            text(prompt)
          }
        )
      }

      val rawText = response.text?.trim().orEmpty()
      if (rawText.isBlank()) {
        return@withContext Result.failure(IllegalStateException("Gemini Vision returned empty response"))
      }

      val arr = if (rawText.startsWith("[")) {
        JSONArray(rawText)
      } else {
        JSONObject(rawText).optJSONArray("items") ?: JSONArray()
      }

      val items = List(arr.length()) { i ->
        val obj = arr.getJSONObject(i)
        ExtractedRxItem(
          prescribedName = obj.optString("prescribedName", "Prescribed Drug"),
          dosageSchedule = obj.optString("dosageSchedule", "As Directed"),
          matchedBatchId = if (obj.isNull("matchedBatchId") || obj.optLong("matchedBatchId", -1L) <= 0L) null else obj.optLong("matchedBatchId"),
          matchedBrandName = obj.optString("matchedBrandName").takeIf { it.isNotBlank() && it != "null" },
          clinicalNote = obj.optString("clinicalNote", "Verified via Cloud AI Vision")
        )
      }
      Result.success(items)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "scanPrescriptionWithGemini4 failed", e)
      Result.failure(e)
    }
  }

  suspend fun extractTextFromBitmap(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
    try {
      val enhancedBitmap = ImageOcrPreprocessor.preprocessForOcr(bitmap)
      val image = InputImage.fromBitmap(enhancedBitmap, 0)
      val visionText = Tasks.await(textRecognizer.process(image), 6000, TimeUnit.MILLISECONDS)
      visionText.text
    } catch (e: Exception) {
      Log.w(TAG, "ML Kit OCR fallback warning: ${e.message}")
      ""
    }
  }

  suspend fun analyzePrescription(
    context: Context,
    imageUri: Uri?,
    bitmap: Bitmap?,
    samplePreset: String?,
    availableInventory: List<MedicineItem>
  ): PrescriptionScanResult = withContext(Dispatchers.IO) {
    if (bitmap == null && imageUri == null && !samplePreset.isNullOrBlank()) {
      return@withContext generatePresetResult(samplePreset, availableInventory)
    }

    val resolvedBitmap = bitmap ?: imageUri?.let { uri ->
      try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
          BitmapFactory.decodeStream(stream)
        }
      } catch (_: Exception) {
        null
      }
    }

    if (resolvedBitmap == null) {
      return@withContext PrescriptionScanResult(
        doctorName = "",
        doctorSpeciality = "",
        clinicOrHospital = "",
        patientName = "",
        patientAgeGender = "",
        diagnosis = "",
        prescriptionDate = "Today",
        medicines = emptyList(),
        doctorAdvice = "",
        drugInteractionAlert = null,
        googleSearchGroundingQuery = "",
        rawGeminiOutput = "",
        isAiPowered = false,
        errorMessage = "No prescription image selected. Capture a photo, pick from gallery, or select a clinical sample above."
      )
    }

    val extractedOcrText = extractTextFromBitmap(resolvedBitmap)
    val scaledBitmap = resolvedBitmap.scaleForAiVision()

    val aiErrorReason: String? = try {
      val prompt = """
        You are an expert AI clinical pharmacist assistant for ROYAL PHARMACY in India.
        Analyze this medical prescription image thoroughly.
        On-device OCR text hints extracted from the image:
        $extractedOcrText

        Extract doctor details, patient details, provisional diagnosis, and ALL prescribed medicines
        with exact Indian brand names, active generic chemical salts + strengths, dosage, frequency, duration,
        packQty, estimated Indian MRP in INR, bioequivalent generic substitute suggestion, and any safety/DDI warning.
      """.trimIndent()

      val response = withTimeout(60_000L) {
        visionModel.generateContent(
          content {
            image(scaledBitmap)
            text(prompt)
          }
        )
      }

      val text = response.text?.trim().orEmpty()
      if (text.isNotBlank()) {
        val parsed = parseJsonResponse(text, availableInventory)
        if (parsed != null && parsed.medicines.isNotEmpty()) {
          return@withContext parsed
        }
      }
      "Cloud Vision returned no recognized prescription medicines"
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Firebase AI Logic Vision analysis failed", e)
      e.localizedMessage ?: "Cloud Vision service unavailable"
    }

    return@withContext buildResultFromOcrText(
      ocrText = extractedOcrText,
      inventory = availableInventory,
      errorMessage = "Cloud AI Vision unavailable ($aiErrorReason). Showing on-device OCR extraction."
    )
  }

  private fun buildResultFromOcrText(
    ocrText: String,
    inventory: List<MedicineItem>,
    errorMessage: String? = null
  ): PrescriptionScanResult {
    if (ocrText.isBlank()) {
      return PrescriptionScanResult(
        doctorName = "Prescription OCR",
        doctorSpeciality = "General Practice",
        clinicOrHospital = "Medical Clinic",
        patientName = "Patient Details",
        patientAgeGender = "Adult",
        diagnosis = "Prescription Scan",
        prescriptionDate = "Today",
        medicines = emptyList(),
        doctorAdvice = "No text detected from image. Please ensure good lighting and clear camera focus.",
        drugInteractionAlert = null,
        googleSearchGroundingQuery = "",
        rawGeminiOutput = "No text detected on image frame",
        isAiPowered = false,
        errorMessage = errorMessage ?: "No readable text found in the image."
      )
    }

    val parsed = MedicineOcrParser.parseDoctorPrescriptionText(ocrText, inventory)
    val prescribedDrugs = parsed.prescribedItems.map { item ->
      val matched = item.matchedInventoryItem ?: inventory.firstOrNull {
        it.name.contains(item.name, ignoreCase = true) || item.name.contains(it.name, ignoreCase = true)
      }
      val price = matched?.let { if (it.saleRate > 0) it.saleRate else it.mrp } ?: 100.0
      val searchQuery = "${item.name} uses dosage indications"

      PrescribedDrug(
        medicineName = item.name,
        genericSalt = matched?.composition?.ifBlank { matched.saltMolecule } ?: "Verify generic salt",
        dosage = item.dosage,
        frequency = item.dosage,
        duration = item.duration,
        packQty = 1,
        estPrice = price,
        isAvailableInStock = matched != null && matched.stockPacks > 0,
        matchedStockItem = matched,
        genericSubstituteSuggestion = null,
        substitutePriceSavings = null,
        googleSearchQuery = searchQuery,
        googleSearchUrl = "https://www.google.com/search?q=" + URLEncoder.encode(searchQuery, "UTF-8"),
        safetyWarning = if (item.name.contains("Augmentin", ignoreCase = true)) "Check for Penicillin allergy" else null
      )
    }

    return PrescriptionScanResult(
      doctorName = parsed.doctorName.ifBlank { "Prescribing Doctor" },
      doctorSpeciality = "Physician",
      clinicOrHospital = "Clinic / Hospital",
      patientName = parsed.patientName.ifBlank { "Patient Walk-in" },
      patientAgeGender = "Walk-in",
      diagnosis = "Prescription Analysis (On-device OCR)",
      prescriptionDate = "Today",
      medicines = prescribedDrugs,
      doctorAdvice = "Take medicines as instructed by physician.",
      drugInteractionAlert = if (prescribedDrugs.size > 1) "Review salt-level interactions before dispensing." else null,
      googleSearchGroundingQuery = prescribedDrugs.firstOrNull()?.googleSearchQuery ?: "",
      rawGeminiOutput = ocrText,
      isAiPowered = false,
      errorMessage = errorMessage
    )
  }

  private fun parseJsonResponse(jsonStr: String, availableInventory: List<MedicineItem>): PrescriptionScanResult? {
    return try {
      val cleanJson = jsonStr.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
      val obj = JSONObject(cleanJson)

      val doctorName = obj.optString("doctorName", "Prescribing Physician")
      val doctorSpeciality = obj.optString("doctorSpeciality", "Physician")
      val clinicOrHospital = obj.optString("clinicOrHospital", "Clinic / Hospital")
      val patientName = obj.optString("patientName", "Walk-in Patient")
      val patientAgeGender = obj.optString("patientAgeGender", "")
      val diagnosis = obj.optString("diagnosis", "")
      val prescriptionDate = obj.optString("prescriptionDate", "Today")
      val doctorAdvice = obj.optString("doctorAdvice", "")
      val drugInteractionAlert = obj.optString("drugInteractionAlert").takeIf { it.isNotBlank() && it != "null" }
      val googleSearchGroundingQuery = obj.optString("googleSearchGroundingQuery", "")

      val medsArray = obj.optJSONArray("medicines") ?: JSONArray()
      val medicinesList = mutableListOf<PrescribedDrug>()

      for (i in 0 until medsArray.length()) {
        val m = medsArray.optJSONObject(i) ?: continue
        val medName = m.optString("medicineName").trim()
        if (medName.isBlank()) continue
        val genericSalt = m.optString("genericSalt")
        val dosage = m.optString("dosage")
        val freq = m.optString("frequency")
        val dur = m.optString("duration")
        val qty = m.optInt("packQty", 1).coerceAtLeast(1)
        val estPrice = m.optDouble("estPrice", 85.0)
        val sub = m.optString("genericSubstituteSuggestion").takeIf { it.isNotBlank() }
        val savings = m.optString("substitutePriceSavings").takeIf { it.isNotBlank() }
        val warning = m.optString("safetyWarning").takeIf { it.isNotBlank() }

        val searchQuery = m.optString("googleSearchQuery", "$medName $genericSalt uses dosage side effects")
        val searchUrl = "https://www.google.com/search?q=" + URLEncoder.encode(searchQuery, "UTF-8")

        val matched = availableInventory.firstOrNull {
          it.name.contains(medName, ignoreCase = true) ||
            medName.contains(it.name, ignoreCase = true) ||
            (genericSalt.isNotBlank() && (
              it.composition.contains(genericSalt, ignoreCase = true) ||
                it.saltMolecule.contains(genericSalt, ignoreCase = true)
              ))
        }

        val itemPrice = matched?.let { if (it.saleRate > 0) it.saleRate else it.mrp } ?: estPrice

        medicinesList.add(
          PrescribedDrug(
            medicineName = medName,
            genericSalt = genericSalt,
            dosage = dosage,
            frequency = freq,
            duration = dur,
            packQty = qty,
            estPrice = itemPrice,
            isAvailableInStock = (matched?.stockPacks ?: 0) > 0,
            matchedStockItem = matched,
            genericSubstituteSuggestion = sub,
            substitutePriceSavings = savings,
            googleSearchQuery = searchQuery,
            googleSearchUrl = searchUrl,
            safetyWarning = warning
          )
        )
      }

      PrescriptionScanResult(
        doctorName = doctorName,
        doctorSpeciality = doctorSpeciality,
        clinicOrHospital = clinicOrHospital,
        patientName = patientName,
        patientAgeGender = patientAgeGender,
        diagnosis = diagnosis,
        prescriptionDate = prescriptionDate,
        medicines = medicinesList,
        doctorAdvice = doctorAdvice,
        drugInteractionAlert = drugInteractionAlert,
        googleSearchGroundingQuery = googleSearchGroundingQuery,
        rawGeminiOutput = jsonStr,
        isAiPowered = true,
        errorMessage = null
      )
    } catch (e: Exception) {
      Log.w(TAG, "parseJsonResponse failed: ${e.message}")
      null
    }
  }

  fun generatePresetResult(presetName: String, availableInventory: List<MedicineItem>): PrescriptionScanResult {
    return when (presetName) {
      "Pediatric Care Rx" -> {
        val meds = listOf(
          createPrescribedDrug("Calpol 250mg Suspension", "Paracetamol 250mg/5ml", "5 ml", "1-1-1 (TDS) when fever > 100°F", "3 Days", 1, 45.0, "Pacimol 250 Oral Drop", "Save 24%", "Calpol 250 pediatric dosage side effects", availableInventory),
          createPrescribedDrug("Augmentin Duo Oral Suspension", "Amoxicillin 200mg + Clavulanic Acid 28.5mg", "3.5 ml", "1-0-1 (BD) with meals", "5 Days", 1, 168.0, "Moxclav DS Syrup", "Save 30%", "Augmentin Duo syrup indications CDSCO", availableInventory),
          createPrescribedDrug("Ascoril D Junior Syrup", "Dextromethorphan + Phenylephrine + CPM", "2.5 ml", "0-0-1 (Night) before sleep", "4 Days", 1, 98.0, "Alex Junior Cough Syrup", "Save 18%", "Ascoril D junior pediatric safety", availableInventory)
        )
        PrescriptionScanResult(
          doctorName = "Sample Prescription: Pediatric Care",
          doctorSpeciality = "Pediatrics",
          clinicOrHospital = "Demo Sample Rx",
          patientName = "Sample Child (14.5 kg)",
          patientAgeGender = "4 Yrs",
          diagnosis = "Acute Bronchitis with Pyrexia",
          prescriptionDate = "Sample Template",
          medicines = meds,
          doctorAdvice = "Sponge with lukewarm water if fever > 101°F. Maintain oral hydration.",
          drugInteractionAlert = "Sample prescription loaded — upload a photo for real AI Vision extraction.",
          googleSearchGroundingQuery = "Pediatric Amoxyclav 200 suspension dosage",
          rawGeminiOutput = "Sample Preset: Pediatric Care Rx",
          isAiPowered = false
        )
      }
      "Cardiology & Diabetic Rx" -> {
        val meds = listOf(
          createPrescribedDrug("Telma 40 Tablet", "Telmisartan 40mg", "40 mg", "1-0-0 (Morning) after breakfast", "30 Days", 3, 142.0, "Telmikind 40", "Save 35%", "Telmisartan 40mg hypertension", availableInventory),
          createPrescribedDrug("Glycomet GP 1 Tablet", "Metformin 500mg + Glimepiride 1mg", "1 Tab", "1-0-1 (BD) before meals", "30 Days", 2, 118.0, "Glyciphage-G 1", "Save 28%", "Metformin Glimepiride CDSCO", availableInventory),
          createPrescribedDrug("Rosuvas 10 Tablet", "Rosuvastatin 10mg", "10 mg", "0-0-1 (Night) after dinner", "30 Days", 2, 210.0, "Roseday 10", "Save 40%", "Rosuvastatin 10mg", availableInventory),
          createPrescribedDrug("Ecosprin 75 Tablet", "Aspirin 75mg (Enteric Coated)", "75 mg", "0-1-0 (Afternoon) after lunch", "30 Days", 2, 12.0, "Delisprin 75", "Standard", "Ecosprin 75 cardiovascular prevention", availableInventory)
        )
        PrescriptionScanResult(
          doctorName = "Sample Prescription: Cardiology & Diabetes",
          doctorSpeciality = "Cardiology",
          clinicOrHospital = "Demo Sample Rx",
          patientName = "Sample Adult Patient",
          patientAgeGender = "58 Yrs",
          diagnosis = "Type-2 Diabetes & Hypertension",
          prescriptionDate = "Sample Template",
          medicines = meds,
          doctorAdvice = "Low-sodium diet and morning glucose monitoring.",
          drugInteractionAlert = "Monitor blood glucose closely when combining Metformin + Glimepiride + Telmisartan.",
          googleSearchGroundingQuery = "Telmisartan Metformin co-administration",
          rawGeminiOutput = "Sample Preset: Cardiology & Diabetic Rx",
          isAiPowered = false
        )
      }
      else -> {
        val meds = listOf(
          createPrescribedDrug("Augmentin 625 Duo", "Amoxicillin 500mg + Clavulanic Acid 125mg", "625 mg", "1-0-1 (BD) after food", "5 Days", 1, 204.0, "Moxikind-CV 625", "Save 38%", "Augmentin 625 Duo CDSCO", availableInventory),
          createPrescribedDrug("Dolo 650 Tablet", "Paracetamol 650mg", "650 mg", "1-0-1 (BD) as needed", "3 Days", 1, 30.6, "Pacimol 650", "Save 28%", "Dolo 650 dosage", availableInventory),
          createPrescribedDrug("Pan 40 Tablet", "Pantoprazole 40mg", "40 mg", "1-0-0 (Morning) before food", "5 Days", 1, 155.0, "Pantocid 40", "Save 32%", "Pantoprazole 40mg", availableInventory),
          createPrescribedDrug("Montair LC Tablet", "Montelukast 10mg + Levocetirizine 5mg", "1 Tab", "0-0-1 (Night)", "7 Days", 1, 185.0, "Telekast-L", "Save 30%", "Montair LC allergic rhinitis", availableInventory)
        )
        PrescriptionScanResult(
          doctorName = "Sample Prescription: General Physician",
          doctorSpeciality = "General Medicine",
          clinicOrHospital = "Demo Sample Rx",
          patientName = "Sample Adult Patient",
          patientAgeGender = "36 Yrs",
          diagnosis = "Acute Upper Respiratory Infection",
          prescriptionDate = "Sample Template",
          medicines = meds,
          doctorAdvice = "Complete full 5-day antibiotic course.",
          drugInteractionAlert = "No major drug-drug interactions detected.",
          googleSearchGroundingQuery = "Amoxicillin Clavulanate 625 guidelines",
          rawGeminiOutput = "Sample Preset: General Physician Rx",
          isAiPowered = false
        )
      }
    }
  }

  private fun createPrescribedDrug(
    name: String,
    salt: String,
    dosage: String,
    freq: String,
    dur: String,
    qty: Int,
    price: Double,
    sub: String,
    savings: String,
    searchQuery: String,
    availableInventory: List<MedicineItem>
  ): PrescribedDrug {
    val matched = availableInventory.firstOrNull {
      it.name.contains(name.take(6), ignoreCase = true) ||
        name.contains(it.name.take(6), ignoreCase = true) ||
        it.composition.contains(salt.take(6), ignoreCase = true) ||
        it.saltMolecule.contains(salt.take(6), ignoreCase = true)
    }

    val itemPrice = matched?.let { if (it.saleRate > 0) it.saleRate else it.mrp } ?: price
    val searchUrl = "https://www.google.com/search?q=" + URLEncoder.encode(searchQuery, "UTF-8")

    return PrescribedDrug(
      medicineName = name,
      genericSalt = salt,
      dosage = dosage,
      frequency = freq,
      duration = dur,
      packQty = qty,
      estPrice = itemPrice,
      isAvailableInStock = (matched?.stockPacks ?: 0) > 0,
      matchedStockItem = matched,
      genericSubstituteSuggestion = sub,
      substitutePriceSavings = savings,
      googleSearchQuery = searchQuery,
      googleSearchUrl = searchUrl,
      safetyWarning = if (name.contains("Augmentin", ignoreCase = true)) "Check for Penicillin allergy" else null
    )
  }
}
