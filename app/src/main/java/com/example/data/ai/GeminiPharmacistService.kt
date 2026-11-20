package com.example.data.ai

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.data.db.MedicineBatchEntity
import com.example.data.model.BrandMedicine
import com.example.data.model.MedicineItem
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val sender: String, // "user" or "gemini"
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isError: Boolean = false,
  val actionSuggestion: String? = null
)

fun formatClinicalMarkdown(rawText: String): AnnotatedString {
  val cleanedLines = rawText
    .replace("```json", "")
    .replace("```", "")
    .lines()
    .map { line ->
      val trimmed = line.trimStart()
      when {
        trimmed.startsWith("### ") -> "**" + trimmed.removePrefix("### ").trim() + "**"
        trimmed.startsWith("## ") -> "**" + trimmed.removePrefix("## ").trim() + "**"
        trimmed.startsWith("# ") -> "**" + trimmed.removePrefix("# ").trim() + "**"
        trimmed.startsWith("* ") -> "• " + trimmed.removePrefix("* ")
        trimmed.startsWith("- ") -> "• " + trimmed.removePrefix("- ")
        else -> line
      }
    }
  val normalized = cleanedLines.joinToString("\n")

  return buildAnnotatedString {
    var i = 0
    val n = normalized.length
    while (i < n) {
      if (i + 1 < n && normalized[i] == '*' && normalized[i + 1] == '*') {
        val endBold = normalized.indexOf("**", i + 2)
        if (endBold != -1) {
          val boldContent = normalized.substring(i + 2, endBold).replace("*", "")
          withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(boldContent)
          }
          i = endBold + 2
          continue
        }
      }
      if (normalized[i] == '*') {
        val endItalic = normalized.indexOf('*', i + 1)
        if (endItalic != -1 && endItalic > i + 1 && !normalized.substring(i + 1, endItalic).contains('\n')) {
          val italicContent = normalized.substring(i + 1, endItalic)
          withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
            append(italicContent)
          }
          i = endItalic + 1
          continue
        }
      }
      if (normalized[i] == '`') {
        val endCode = normalized.indexOf('`', i + 1)
        if (endCode != -1 && endCode > i + 1 && !normalized.substring(i + 1, endCode).contains('\n')) {
          val codeContent = normalized.substring(i + 1, endCode)
          withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(codeContent)
          }
          i = endCode + 1
          continue
        }
      }
      append(normalized[i])
      i++
    }
  }
}

fun mapGeminiFreeTierError(e: Throwable): String {
  val msg = buildString {
    append(e.message.orEmpty())
    append(' ')
    append(e.localizedMessage.orEmpty())
    append(' ')
    append(e.cause?.message.orEmpty())
  }
  val isRateLimit = msg.contains("429") ||
    msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
    msg.contains("quota", ignoreCase = true) ||
    msg.contains("rate limit", ignoreCase = true)
  return if (isRateLimit) {
    "Free Gemini API rate limit reached (RPM/RPD). Please wait 30–60 seconds and tap Retry."
  } else {
    e.localizedMessage ?: "Cloud AI request could not be completed. Please try again."
  }
}

class GeminiPharmacistService {

  companion object {
    const val PRIMARY_MODEL = "gemini-2.5-flash"
    const val FAST_MODEL = "gemini-2.5-flash-lite"
    private const val TAG = "GeminiPharmacistSvc"
  }

  private val systemPrompt = """
    You are the Senior Clinical Pharmacist & Indian Retail Pharmacy Architect for ROYAL PHARMACY.
    Clinical Rules:
    1. Provide accurate pharmacology, CDSCO Schedule (H, H1, X, OTC) awareness, and Salt-Level DDI guidance.
    2. For pediatric patients (Age <= 12 years), ALWAYS specify oral drops, syrups, suspensions, or sachets with weight-based (mg/kg and ml) dosing.
    3. Recommend bioequivalent generic salts and Indian retail alternatives accurately.
    Tone: Professional, clinical, concise, grounded in evidence.
  """.trimIndent()

  private val chatModel by lazy {
    Firebase.ai.generativeModel(
      modelName = PRIMARY_MODEL,
      generationConfig = generationConfig {
        temperature = 0.25f
        topP = 0.95f
      },
      systemInstruction = content { text(systemPrompt) }
    )
  }

  private val medicineEnrichmentSchema = Schema.obj(
    properties = mapOf(
      "name" to Schema.string(description = "Canonical Indian retail medicine brand name with strength, e.g. Augmentin 625 Duo Tablet"),
      "manufacturer" to Schema.string(description = "Indian pharma company name, e.g. GSK, Micro Labs, Sun Pharma, Cipla"),
      "composition" to Schema.string(description = "Full active generic salts with strengths, e.g. Amoxicillin 500mg + Clavulanic Acid 125mg"),
      "saltMolecule" to Schema.string(description = "Primary generic active salt molecule(s), e.g. Amoxicillin + Clavulanic Acid"),
      "therapeuticClass" to Schema.string(description = "Pharmacological class, e.g. Penicillin Antibiotic, PPI + Prokinetic"),
      "category" to Schema.enumeration(
        values = listOf("Tablet", "Capsule", "Syrup", "Injection", "Drops", "Ointment", "Inhaler", "Powder", "OTC"),
        description = "Dosage form category"
      ),
      "hsnCode" to Schema.string(description = "Indian GST HSN code, typically 3004"),
      "gstPercent" to Schema.double(description = "Indian GST slab rate: 0.0, 5.0, 12.0, or 18.0"),
      "scheduleDrug" to Schema.enumeration(
        values = listOf("OTC", "Schedule H", "Schedule H1", "Schedule X"),
        description = "CDSCO Drug Schedule"
      ),
      "estimatedMrp" to Schema.double(description = "Typical Indian retail strip/bottle MRP in INR"),
      "estimatedPurchaseRate" to Schema.double(description = "Typical Indian PTR wholesale price in INR"),
      "isEssential" to Schema.boolean(description = "True if listed in NLEM Essential Medicines List"),
      "isLifeSaving" to Schema.boolean(description = "True if critical care / life-saving emergency drug")
    )
  )

  private val medicineEnrichmentModel by lazy {
    Firebase.ai.generativeModel(
      modelName = FAST_MODEL,
      generationConfig = generationConfig {
        temperature = 0.1f
        responseMimeType = "application/json"
        responseSchema = medicineEnrichmentSchema
      }
    )
  }

  private val brandProductSchema = Schema.obj(
    properties = mapOf(
      "items" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "name" to Schema.string(),
            "brandName" to Schema.string(),
            "saltComposition" to Schema.string(),
            "category" to Schema.string(),
            "mrp" to Schema.double(),
            "isInjectable" to Schema.boolean(),
            "packaging" to Schema.string(),
            "description" to Schema.string()
          )
        )
      )
    )
  )

  private val structuredCatalogModel by lazy {
    Firebase.ai.generativeModel(
      modelName = FAST_MODEL,
      generationConfig = generationConfig {
        temperature = 0.15f
        responseMimeType = "application/json"
        responseSchema = brandProductSchema
      }
    )
  }

  /**
   * Cache-Through AI Enrichment: Resolves any unknown Indian brand or molecule via Firebase AI Logic
   * into a complete MedicineItem ready to save into Room + Firestore.
   */
  suspend fun resolveMedicineProfileWithAi(brandQuery: String): Result<MedicineItem> = withContext(Dispatchers.IO) {
    val cleaned = brandQuery.trim()
    if (cleaned.isBlank()) {
      return@withContext Result.failure(IllegalArgumentException("Enter a medicine brand or generic salt name"))
    }
    try {
      val prompt = """
        Resolve this Indian pharmacy medicine query into an accurate clinical & GST drug profile: "$cleaned".
        Provide exact active generic salts and strengths (e.g. "Telmisartan 40mg + Hydrochlorothiazide 12.5mg"),
        manufacturer in India, CDSCO Schedule (OTC, Schedule H, Schedule H1, Schedule X), HSN code ("3004" for most medicaments),
        Indian GST slab (0.0, 5.0, 12.0, or 18.0), realistic retail MRP in INR, and PTR (~75% of MRP).
      """.trimIndent()

      val response = withTimeout(60_000L) {
        medicineEnrichmentModel.generateContent(prompt)
      }
      val rawJson = response.text?.trim().orEmpty()
      if (rawJson.isBlank()) {
        return@withContext Result.failure(IllegalStateException("Gemini returned an empty medicine profile"))
      }

      val obj = JSONObject(rawJson)
      val mrp = obj.optDouble("estimatedMrp", 50.0).coerceAtLeast(1.0)
      val ptr = obj.optDouble("estimatedPurchaseRate", (mrp * 0.75)).coerceIn(0.5, mrp)
      val gst = obj.optDouble("gstPercent", 12.0).let { g ->
        if (g in listOf(0.0, 5.0, 12.0, 18.0)) g else 12.0
      }

      val resolvedItem = MedicineItem(
        name = obj.optString("name", cleaned).ifBlank { cleaned },
        manufacturer = obj.optString("manufacturer", "Indian Pharma").ifBlank { "Indian Pharma" },
        composition = obj.optString("composition", cleaned).ifBlank { cleaned },
        saltMolecule = obj.optString("saltMolecule", obj.optString("composition", cleaned)).ifBlank { cleaned },
        category = obj.optString("category", "Tablet").ifBlank { "Tablet" },
        hsnCode = obj.optString("hsnCode", "3004").ifBlank { "3004" },
        batchNumber = "NEW-AI",
        expiryDate = "12/27",
        stockPacks = 1,
        mrp = mrp,
        purchaseRate = ptr,
        saleRate = mrp,
        gstPercent = gst,
        rackLocation = "Rack A-1",
        scheduleDrug = obj.optString("scheduleDrug", "Schedule H").ifBlank { "Schedule H" },
        isGeneric = obj.optString("manufacturer").contains("Jan Aushadhi", ignoreCase = true) ||
          obj.optString("manufacturer").contains("Generic", ignoreCase = true),
        minStockAlert = 5,
        isEssential = obj.optBoolean("isEssential", false),
        isLifeSaving = obj.optBoolean("isLifeSaving", false)
      )
      Result.success(resolvedItem)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "resolveMedicineProfileWithAi failed for '$cleaned'", e)
      Result.failure(Exception(mapGeminiFreeTierError(e), e))
    }
  }

  suspend fun askClinicalPharmacist(
    userQuery: String,
    inventory: List<MedicineBatchEntity>
  ): Result<String> = withContext(Dispatchers.IO) {
    val stockDigest = inventory.take(25).joinToString("; ") {
      "${it.brandName} (Salt: ${it.genericSalt}, Batch: ${it.batchNumber}, Exp: ${it.expiryYearMonth}, Qty: ${it.stockQuantity}, MRP: ₹${it.sellingMrp})"
    }

    val prompt = """
      Live FEFO Inventory: ${stockDigest.ifBlank { "No local batches loaded" }}

      User Query: $userQuery

      Provide:
      1. Direct clinical answer (dosage, pediatric weight-based adjustment if applicable, contraindications, renal/hepatic warnings).
      2. Drug-Drug Interaction severity if multiple drugs are mentioned.
      3. Matching brand or identical generic salt substitute from Live FEFO Inventory (or standard Indian market formulations).
    """.trimIndent()

    try {
      val response = withTimeout(60_000L) {
        chatModel.generateContent(prompt)
      }
      val text = response.text?.trim().orEmpty()
      if (text.isBlank()) {
        Result.failure(IllegalStateException("Empty response from Firebase AI Logic"))
      } else {
        Result.success(text)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "askClinicalPharmacist failed", e)
      Result.failure(Exception(mapGeminiFreeTierError(e), e))
    }
  }

  suspend fun sendMessageResult(
    userMessage: String,
    conversationHistory: List<ChatMessage>,
    liveInventory: List<MedicineItem> = emptyList(),
    patientContext: String = ""
  ): Result<String> = withContext(Dispatchers.IO) {
    val recentContext = conversationHistory
      .filter { !it.isError && it.text.isNotBlank() }
      .takeLast(6)
      .joinToString("\n") { "${if (it.sender == "user") "User" else "Pharmacist AI"}: ${it.text}" }

    val queryTokens = userMessage.lowercase()
      .split(Regex("[^a-z0-9]+"))
      .filter { it.length >= 3 }
      .toSet()

    val relevantInventory = if (queryTokens.isEmpty()) {
      liveInventory.take(20)
    } else {
      liveInventory.sortedByDescending { med ->
        val haystack = "${med.name} ${med.composition} ${med.saltMolecule} ${med.category}".lowercase()
        queryTokens.count { token -> haystack.contains(token) }
      }.take(20)
    }

    val inventoryDigest = if (relevantInventory.isNotEmpty()) {
      "Live Store Inventory (Top Relevant): " + relevantInventory.joinToString("; ") {
        "${it.name} (${it.composition.ifBlank { it.saltMolecule }}, Stock: ${it.stockPacks}, MRP: ₹${it.mrp})"
      }
    } else "Live Store Inventory: Empty / not yet stocked"

    val fullPrompt = buildString {
      if (patientContext.isNotBlank()) {
        append("Active Patient Profile:\n$patientContext\n\n")
      }
      append("$inventoryDigest\n\n")
      if (recentContext.isNotBlank()) {
        append("Recent Conversation:\n$recentContext\n\n")
      }
      append("Current Pharmacist Query: $userMessage")
    }

    try {
      val response = withTimeout(60_000L) {
        chatModel.generateContent(fullPrompt)
      }
      val text = response.text?.trim().orEmpty()
      if (text.isBlank()) {
        Result.failure(IllegalStateException("Firebase AI returned an empty response. Please retry."))
      } else {
        Result.success(text)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "sendMessageResult failed", e)
      Result.failure(Exception(mapGeminiFreeTierError(e), e))
    }
  }

  suspend fun sendMessage(
    userMessage: String,
    conversationHistory: List<ChatMessage>,
    liveInventory: List<MedicineItem> = emptyList(),
    patientContext: String = ""
  ): String {
    return sendMessageResult(userMessage, conversationHistory, liveInventory, patientContext)
      .getOrElse { err ->
        "⚠️ ${mapGeminiFreeTierError(err)}"
      }
  }

  suspend fun searchBrandProductsOnlineResult(brandName: String): Result<List<BrandMedicine>> = withContext(Dispatchers.IO) {
    val localList = BrandCatalogProvider.getBrandMedicines(brandName)
    val prompt = """
      Return top 8 popular Indian retail prescription & hospital medicines (including tablets, syrups, and IV/IM injectables)
      manufactured by "$brandName" in India with accurate generic salt compositions, category, packaging, and realistic INR MRP.
    """.trimIndent()

    try {
      val response = withTimeout(60_000L) {
        structuredCatalogModel.generateContent(prompt)
      }
      val raw = response.text?.trim().orEmpty()
      val parsed = parseStructuredItems(raw, defaultBrand = brandName)
      if (parsed.isEmpty() && localList.isEmpty()) {
        Result.failure(IllegalStateException("No products returned for $brandName"))
      } else {
        Result.success((parsed + localList).distinctBy { it.name.lowercase() })
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "searchBrandProductsOnlineResult failed for $brandName", e)
      Result.failure(Exception(mapGeminiFreeTierError(e), e))
    }
  }

  suspend fun searchBrandProductsOnline(brandName: String): List<BrandMedicine> {
    val localList = BrandCatalogProvider.getBrandMedicines(brandName)
    return searchBrandProductsOnlineResult(brandName).getOrDefault(localList)
  }

  suspend fun searchSubstitutesOnlineResult(queryOrSalt: String): Result<List<BrandMedicine>> = withContext(Dispatchers.IO) {
    val q = queryOrSalt.trim()
    val prompt = """
      For Indian medicine or active chemical salt "$q", return 8 bioequivalent alternative brand substitutes and Jan Aushadhi generics
      available in India (across Cipla, GSK, IPCA, Alkem, Sun Pharma, Mankind, Abbott, Jan Aushadhi).
      Include exact active saltComposition, dosage form category, INR MRP, and packaging.
    """.trimIndent()

    try {
      val response = withTimeout(60_000L) {
        structuredCatalogModel.generateContent(prompt)
      }
      val raw = response.text?.trim().orEmpty()
      val parsed = parseStructuredItems(raw, defaultBrand = "Indian Pharma")
      if (parsed.isEmpty()) {
        Result.failure(IllegalStateException("No bioequivalent substitutes found for $q"))
      } else {
        Result.success(parsed.distinctBy { it.name.lowercase() })
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "searchSubstitutesOnlineResult failed for $q", e)
      Result.failure(Exception(mapGeminiFreeTierError(e), e))
    }
  }

  suspend fun searchSubstitutesOnline(queryOrSalt: String): List<BrandMedicine> {
    return searchSubstitutesOnlineResult(queryOrSalt).getOrElse {
      generateRichSubstitutesFallback(queryOrSalt)
    }
  }

  private fun parseStructuredItems(rawText: String, defaultBrand: String): List<BrandMedicine> {
    val list = mutableListOf<BrandMedicine>()
    try {
      val cleanJson = rawText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
      val arr: JSONArray = if (cleanJson.startsWith("[")) {
        JSONArray(cleanJson)
      } else {
        JSONObject(cleanJson).optJSONArray("items") ?: JSONArray()
      }
      for (i in 0 until arr.length()) {
        val obj = arr.optJSONObject(i) ?: continue
        val name = obj.optString("name").trim()
        if (name.isBlank()) continue
        list.add(
          BrandMedicine(
            name = name,
            brandName = obj.optString("brandName", defaultBrand).ifBlank { defaultBrand },
            saltComposition = obj.optString("saltComposition", ""),
            category = obj.optString("category", "Tablet").ifBlank { "Tablet" },
            mrp = obj.optDouble("mrp", 50.0).coerceAtLeast(1.0),
            isInjectable = obj.optBoolean("isInjectable", false),
            packaging = obj.optString("packaging", "Standard Pack").ifBlank { "Standard Pack" },
            description = obj.optString("description", "")
          )
        )
      }
    } catch (e: Exception) {
      Log.w(TAG, "parseStructuredItems parse error: ${e.message}")
    }
    return list
  }

  fun generateRichSubstitutesFallback(queryOrSalt: String): List<BrandMedicine> {
    val q = queryOrSalt.lowercase().trim()
    if (q.contains("paracetamol") || q.contains("dolo") || q.contains("calpol") || q.contains("pacimol") || q.contains("crocin")) {
      return listOf(
        BrandMedicine(name = "Calpol 650mg Tablet", brandName = "GSK", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 32.0, packaging = "15 Tablets"),
        BrandMedicine(name = "Pacimol 650mg Tablet", brandName = "IPCA", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 24.5, packaging = "15 Tablets"),
        BrandMedicine(name = "Dolo 650mg Tablet", brandName = "Micro Labs", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 34.0, packaging = "15 Tablets"),
        BrandMedicine(name = "Jan Aushadhi Paracetamol 650", brandName = "Jan Aushadhi (Generic)", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 9.5, packaging = "10 Tablets")
      )
    }
    if (q.contains("panto") || q.contains("pan 40") || q.contains("pan-d") || q.contains("omeprazole")) {
      return listOf(
        BrandMedicine(name = "Pan 40mg Tablet", brandName = "ALKEM", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 158.0, packaging = "15 Tablets"),
        BrandMedicine(name = "Pantocid 40mg Tablet", brandName = "SUN PHARMA", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 165.0, packaging = "15 Tablets"),
        BrandMedicine(name = "Jan Aushadhi Pantoprazole 40", brandName = "Jan Aushadhi (Generic)", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 22.0, packaging = "10 Tablets")
      )
    }
    if (q.contains("amox") || q.contains("augmentin") || q.contains("clavam") || q.contains("625")) {
      return listOf(
        BrandMedicine(name = "Augmentin 625 Duo Tablet", brandName = "GSK", saltComposition = "Amoxicillin 500mg + Clavulanic Acid 125mg", category = "Tablet", mrp = 210.0, packaging = "10 Tablets"),
        BrandMedicine(name = "Clavam 625mg Tablet", brandName = "ALKEM", saltComposition = "Amoxicillin 500mg + Clavulanic Acid 125mg", category = "Tablet", mrp = 205.0, packaging = "10 Tablets"),
        BrandMedicine(name = "Moxikind-CV 625 Tablet", brandName = "Mankind", saltComposition = "Amoxicillin 500mg + Clavulanic Acid 125mg", category = "Tablet", mrp = 175.0, packaging = "10 Tablets")
      )
    }
    if (q.contains("azithro") || q.contains("azee") || q.contains("azithral")) {
      return listOf(
        BrandMedicine(name = "Azee 500mg Tablet", brandName = "Cipla", saltComposition = "Azithromycin 500mg", category = "Tablet", mrp = 125.0, packaging = "5 Tablets"),
        BrandMedicine(name = "Azithral 500mg Tablet", brandName = "Alembic", saltComposition = "Azithromycin 500mg", category = "Tablet", mrp = 129.0, packaging = "5 Tablets")
      )
    }
    return emptyList()
  }

  fun openGoogleSearch(context: Context, query: String) {
    try {
      val encoded = URLEncoder.encode(query, "UTF-8")
      val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://www.google.com/search?q=$encoded")
      ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {}
  }
}
