package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val sender: String, // "user" or "gemini"
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isError: Boolean = false,
  val actionSuggestion: String? = null
)

class GeminiPharmacistService {
  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private val systemPrompt = """
    You are the Senior Pharmacist AI and Clinical Support Agent for ROYAL PHARMACY.
    Your capabilities:
    1. Check drug-drug interactions, contraindications, and dosages.
    2. Provide generic medicine substitutes and cost-saving alternatives based on chemical salts.
    3. Help pharmacists manage suppliers, purchase orders, Udhar Khata credit ledgers, and inventory.
    4. Provide real-time info on pharmaceutical news, CDSCO regulatory guidelines, and clinical drug safety.
    5. Pull real-time directions, logistics routes, and nearby hospital/distributor locations using Google Maps data.
    Tone: Professional, clinical, reassuring, precise, and supportive of community pharmacy operations in India.
  """.trimIndent()

  private fun isApiKeyValid(key: String): Boolean {
    val k = key.trim()
    return k.isNotBlank() &&
      !k.equals("DEFAULT_GEMINI_API_KEY", ignoreCase = true) &&
      !k.equals("MY_GEMINI_API_KEY", ignoreCase = true) &&
      !k.equals("YOUR_API_KEY", ignoreCase = true) &&
      !k.contains("DEFAULT", ignoreCase = true) &&
      k.length >= 20
  }

  suspend fun sendMessage(
    userMessage: String,
    conversationHistory: List<ChatMessage>
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY.ifBlank { System.getenv("GEMINI_API_KEY") ?: "" }
    } catch (e: Exception) {
      System.getenv("GEMINI_API_KEY") ?: ""
    }

    if (!isApiKeyValid(apiKey)) {
      // Offline Intelligent Clinical Rule-Based Fallback
      return@withContext generateLocalClinicalResponse(userMessage)
    }

    try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

      val contentsArray = JSONArray()

      // Build valid alternating conversation turns starting with "user"
      val validHistory = conversationHistory
        .filter { it.text.isNotBlank() }
        .takeLast(10)

      val firstUserIndex = validHistory.indexOfFirst { it.sender == "user" }
      val trimmedHistory = if (firstUserIndex != -1) {
        validHistory.subList(firstUserIndex, validHistory.size)
      } else {
        emptyList()
      }

      var currentTurnRole = ""
      var currentTurnParts = JSONArray()

      trimmedHistory.forEach { msg ->
        val role = if (msg.sender == "user") "user" else "model"
        if (role == currentTurnRole) {
          currentTurnParts.put(JSONObject().apply { put("text", msg.text) })
        } else {
          if (currentTurnRole.isNotEmpty() && currentTurnParts.length() > 0) {
            contentsArray.put(JSONObject().apply {
              put("role", currentTurnRole)
              put("parts", currentTurnParts)
            })
          }
          currentTurnRole = role
          currentTurnParts = JSONArray().apply {
            put(JSONObject().apply { put("text", msg.text) })
          }
        }
      }

      if (currentTurnRole == "user") {
        currentTurnParts.put(JSONObject().apply { put("text", userMessage) })
        contentsArray.put(JSONObject().apply {
          put("role", "user")
          put("parts", currentTurnParts)
        })
      } else {
        if (currentTurnRole.isNotEmpty() && currentTurnParts.length() > 0) {
          contentsArray.put(JSONObject().apply {
            put("role", currentTurnRole)
            put("parts", currentTurnParts)
          })
        }
        contentsArray.put(JSONObject().apply {
          put("role", "user")
          put("parts", JSONArray().apply {
            put(JSONObject().apply { put("text", userMessage) })
          })
        })
      }

      val rootJson = JSONObject().apply {
        put("systemInstruction", JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply { put("text", systemPrompt) })
          })
        })
        put("contents", contentsArray)
        put("generationConfig", JSONObject().apply {
          put("temperature", 0.3)
          put("topP", 0.95)
        })
      }

      val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
      val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

      val response = client.newCall(request).execute()
      val responseBody = response.body?.string() ?: ""

      if (response.isSuccessful) {
        val json = JSONObject(responseBody)
        val candidates = json.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")

        if (!text.isNullOrBlank()) {
          return@withContext text
        }
      }
      return@withContext generateLocalClinicalResponse(userMessage)
    } catch (e: Exception) {
      return@withContext generateLocalClinicalResponse(userMessage)
    }
  }

  private fun generateLocalClinicalResponse(query: String): String {
    val q = query.lowercase()
    return when {
      q.contains("interaction") || q.contains("warfarin") || q.contains("aspirin") -> {
        "⚠️ Drug Interaction Alert: Aspirin (antiplatelet) + Warfarin (anticoagulant) substantially elevates gastrointestinal bleeding risk. If co-prescribed, monitor INR closely and consider a PPI like Pantoprazole 40mg for mucosal gastroprotection."
      }
      q.contains("dolo") || q.contains("paracetamol") || q.contains("substitute") -> {
        "💡 Generic Substitute Analysis for Paracetamol 650mg:\n• Dolo 650 (Micro Labs): ₹30.6/strip\n• Calpol 650 (GSK): ₹29.5/strip\n• Pacimol / Generic Paracetamol 650 (Ipca): ₹22.0/strip (Best Value, saves 28% for the patient!). Both provide bioequivalent antipyretic/analgesic efficacy."
      }
      q.contains("augmentin") || q.contains("amoxyclav") || q.contains("antibiotic") -> {
        "💊 Amoxicillin + Clavulanate (625mg) formulation:\nStandard dosage is 1 tablet BID after meals to minimize GI distress. Check patient allergy records for penicillin hypersensitivity before dispensing."
      }
      q.contains("route") || q.contains("direction") || q.contains("distributor") -> {
        "📍 Distributor Logistics Hubs:\n• Sun Pharma Distribution Hub (Guwahati): 42 km via NH-15 (~55 mins drive)\n• Cipla Regional Logistics: 48 km via NH-27\n• Civil Hospital Blood Bank: 1.2 km via Hospital Road (3 mins)."
      }
      q.contains("search") || q.contains("news") || q.contains("cdsco") || q.contains("recall") -> {
        "🔍 Regulatory & Formulation Update:\nCDSCO Gazette Notice: Fixed-Dose Combinations (FDCs) review mandates stringent Schedule H1 labeling and QR codes on top 300 medicine brands. All Royal Pharmacy inventory adheres to current GS1 barcode standards."
      }
      q.contains("udhar") || q.contains("credit") || q.contains("khata") -> {
        "📒 Udhar Khata Intelligence: Current total outstanding credit is ₹6,860.00 across 4 registered accounts. Highest pending balance is Dr. Amit Patel Clinic (₹3,850.00). You can send automatic WhatsApp payment reminders directly from the Udhar Khata tab."
      }
      else -> {
        "Namaste! I am your ROYAL PHARMACY AI Clinical & Operations Assistant. I can assist you with:\n1. Checking drug-drug interactions & dosage guidelines\n2. Recommending lower-cost generic substitutes by chemical salt\n3. Checking supplier purchase order status\n4. Monitoring low stock and expiring batches\nHow can I help you today?"
      }
    }
  }

  suspend fun searchBrandProductsOnline(brandName: String): List<com.example.data.model.BrandMedicine> = withContext(Dispatchers.IO) {
    val localList = BrandCatalogProvider.getBrandMedicines(brandName)
    val apiKey = try { BuildConfig.GEMINI_API_KEY.ifBlank { System.getenv("GEMINI_API_KEY") ?: "" } } catch (e: Exception) { System.getenv("GEMINI_API_KEY") ?: "" }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext localList
    }

    try {
      val prompt = """
        Return a JSON array of the top 8 popular prescription & hospital medicines and injectables manufactured by $brandName in India.
        Include injectables (IV/IM), tablets, and syrups.
        Each JSON object must have keys:
        - "name": string
        - "saltComposition": string
        - "category": string (e.g. "Tablet", "Injectable", "Syrup", "Capsule")
        - "mrp": number
        - "isInjectable": boolean
        - "packaging": string (e.g. "10 Tablets", "1 Vial", "2ml Ampoule")
        - "description": string
        Return ONLY valid JSON array without markdown formatting.
      """.trimIndent()

      val responseText = queryGeminiRaw(prompt, apiKey)
      val parsed = parseBrandMedicinesJson(responseText, brandName)
      if (parsed.isNotEmpty()) {
        (parsed + localList).distinctBy { it.name }
      } else {
        localList
      }
    } catch (e: Exception) {
      localList
    }
  }

  suspend fun searchSubstitutesOnline(queryOrSalt: String): List<com.example.data.model.BrandMedicine> = withContext(Dispatchers.IO) {
    val apiKey = try { BuildConfig.GEMINI_API_KEY.ifBlank { System.getenv("GEMINI_API_KEY") ?: "" } } catch (e: Exception) { System.getenv("GEMINI_API_KEY") ?: "" }
    val q = queryOrSalt.trim()

    val fallbackSubstitutes = generateRichSubstitutesFallback(q)
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext fallbackSubstitutes
    }

    try {
      val prompt = """
        For the medicine or chemical salt "$q", return a JSON array of 8 alternative brand substitutes and generic options available in Indian pharmacies across popular brands (GSK, IPCA, Cipla, Alkem, Sun Pharma, Mankind, Abbott, Jan Aushadhi).
        Include oral tablets, syrups, and injectables (IV/IM or infusion) where available.
        Each JSON object must have keys:
        - "name": string
        - "brandName": string (e.g. "GSK", "IPCA", "Cipla", "ALKEM", "SUN PHARMA", "Generic Jan Aushadhi")
        - "saltComposition": string
        - "category": string ("Tablet", "Injectable", "Syrup", "Capsule")
        - "mrp": number
        - "isInjectable": boolean
        - "packaging": string
        - "description": string
        Return ONLY valid JSON array without markdown formatting.
      """.trimIndent()

      val responseText = queryGeminiRaw(prompt, apiKey)
      val parsed = parseSubstitutesJson(responseText, q)
      if (parsed.isNotEmpty()) {
        (parsed + fallbackSubstitutes).distinctBy { it.name }
      } else {
        fallbackSubstitutes
      }
    } catch (e: Exception) {
      fallbackSubstitutes
    }
  }

  private suspend fun queryGeminiRaw(prompt: String, apiKey: String): String = withContext(Dispatchers.IO) {
    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
    val rootJson = JSONObject().apply {
      put("contents", JSONArray().apply {
        put(JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().apply { put("text", prompt) })
          })
        })
      })
      put("generationConfig", JSONObject().apply {
        put("responseMimeType", "application/json")
        put("temperature", 0.2)
      })
    }

    val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
    val request = Request.Builder().url(url).post(requestBody).build()
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: ""

    if (response.isSuccessful) {
      val json = JSONObject(body)
      val candidates = json.optJSONArray("candidates")
      val content = candidates?.optJSONObject(0)?.optJSONObject("content")
      val parts = content?.optJSONArray("parts")
      parts?.optJSONObject(0)?.optString("text") ?: ""
    } else {
      ""
    }
  }

  private fun parseBrandMedicinesJson(rawText: String, brandName: String): List<com.example.data.model.BrandMedicine> {
    val list = mutableListOf<com.example.data.model.BrandMedicine>()
    try {
      val cleanJson = rawText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
      val arr = JSONArray(cleanJson)
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          com.example.data.model.BrandMedicine(
            name = obj.optString("name"),
            brandName = brandName,
            saltComposition = obj.optString("saltComposition"),
            category = obj.optString("category", "Tablet"),
            mrp = obj.optDouble("mrp", 50.0),
            isInjectable = obj.optBoolean("isInjectable", false),
            packaging = obj.optString("packaging", "Standard Pack"),
            description = obj.optString("description", "")
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }

  private fun parseSubstitutesJson(rawText: String, querySalt: String): List<com.example.data.model.BrandMedicine> {
    val list = mutableListOf<com.example.data.model.BrandMedicine>()
    try {
      val cleanJson = rawText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
      val arr = JSONArray(cleanJson)
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          com.example.data.model.BrandMedicine(
            name = obj.optString("name"),
            brandName = obj.optString("brandName", "Indian Pharma"),
            saltComposition = obj.optString("saltComposition", querySalt),
            category = obj.optString("category", "Tablet"),
            mrp = obj.optDouble("mrp", 40.0),
            isInjectable = obj.optBoolean("isInjectable", false),
            packaging = obj.optString("packaging", "10 Tablets"),
            description = obj.optString("description", "")
          )
        )
      }
    } catch (_: Exception) {}
    return list
  }

  fun generateRichSubstitutesFallback(queryOrSalt: String): List<com.example.data.model.BrandMedicine> {
    val q = queryOrSalt.lowercase().trim()

    // 1. PEDIATRIC ORAL DROPS (e.g. "pacimol pediatric drops 15 ml", "calpol drops", "paracetamol drops")
    if (q.contains("drop") || q.contains("pediatric") || q.contains("pead") || q.contains("15 ml") || q.contains("15ml") || q.contains("infant") || q.contains("baby")) {
      return listOf(
        com.example.data.model.BrandMedicine(
          name = "Pacimol Pediatric Drops 15ml",
          brandName = "IPCA",
          saltComposition = "Paracetamol 100mg/ml",
          category = "Syrup",
          mrp = 32.0,
          packaging = "15ml Dropper Bottle",
          description = "Concentrated infant paracetamol antipyretic drops with calibrated dropper"
        ),
        com.example.data.model.BrandMedicine(
          name = "Calpol Pediatric Oral Drops 15ml",
          brandName = "GSK",
          saltComposition = "Paracetamol 100mg/ml",
          category = "Syrup",
          mrp = 34.5,
          packaging = "15ml Dropper Bottle",
          description = "GSK's trusted infant paracetamol drops for pyrexia & post-immunization fever"
        ),
        com.example.data.model.BrandMedicine(
          name = "Crocin 100mg Baby Drops 15ml",
          brandName = "GSK",
          saltComposition = "Paracetamol 100mg/ml",
          category = "Syrup",
          mrp = 35.0,
          packaging = "15ml Dropper Bottle",
          description = "Rapid relief fever & teething pain drops with graduated pipette"
        ),
        com.example.data.model.BrandMedicine(
          name = "T-98 Oral Pediatric Drops 15ml",
          brandName = "Mankind",
          saltComposition = "Paracetamol 100mg/ml",
          category = "Syrup",
          mrp = 28.0,
          packaging = "15ml Dropper Bottle",
          description = "Affordable antipyretic drops for neonates and infants"
        ),
        com.example.data.model.BrandMedicine(
          name = "Febrex Plus Oral Drops 15ml",
          brandName = "Indoco",
          saltComposition = "Paracetamol 100mg + Chlorpheniramine 1mg/ml",
          category = "Syrup",
          mrp = 42.0,
          packaging = "15ml Dropper Bottle",
          description = "Pediatric cold and fever symptom relief drops"
        ),
        com.example.data.model.BrandMedicine(
          name = "Jan Aushadhi Paracetamol Drops 15ml",
          brandName = "Jan Aushadhi (Generic)",
          saltComposition = "Paracetamol 100mg/ml",
          category = "Syrup",
          mrp = 11.5,
          packaging = "15ml Dropper Bottle",
          description = "Govt. subsidized Jan Aushadhi generic pediatric drops (65% savings)"
        )
      )
    }

    // 2. PEDIATRIC SYRUPS & SUSPENSIONS (e.g. "syrup", "suspension", "60ml", "120", "250")
    if (q.contains("syrup") || q.contains("suspension") || q.contains("60ml") || q.contains("120") || q.contains("250")) {
      return listOf(
        com.example.data.model.BrandMedicine(
          name = "Calpol 250mg Pead Suspension 60ml",
          brandName = "GSK",
          saltComposition = "Paracetamol 250mg/5ml",
          category = "Syrup",
          mrp = 54.0,
          packaging = "60ml Bottle",
          description = "Pleasant flavored fever syrup for children above 1 year"
        ),
        com.example.data.model.BrandMedicine(
          name = "Pacimol 250mg Oral Suspension 60ml",
          brandName = "IPCA",
          saltComposition = "Paracetamol 250mg/5ml",
          category = "Syrup",
          mrp = 46.0,
          packaging = "60ml Bottle",
          description = "High quality cost-effective children's antipyretic suspension"
        ),
        com.example.data.model.BrandMedicine(
          name = "Dolo 250 Oral Suspension 60ml",
          brandName = "Micro Labs",
          saltComposition = "Paracetamol 250mg/5ml",
          category = "Syrup",
          mrp = 52.0,
          packaging = "60ml Bottle",
          description = "Popular strawberry flavored pediatric suspension"
        ),
        com.example.data.model.BrandMedicine(
          name = "Crocin 120 Suspension 60ml",
          brandName = "GSK",
          saltComposition = "Paracetamol 120mg/5ml",
          category = "Syrup",
          mrp = 40.0,
          packaging = "60ml Bottle",
          description = "Low-dose paracetamol syrup for younger children"
        ),
        com.example.data.model.BrandMedicine(
          name = "Jan Aushadhi Paracetamol Syrup 60ml",
          brandName = "Jan Aushadhi (Generic)",
          saltComposition = "Paracetamol 120mg/5ml",
          category = "Syrup",
          mrp = 15.0,
          packaging = "60ml Bottle",
          description = "Govt. generic syrup equivalent (70% cost savings)"
        )
      )
    }

    // 3. ADULT PARACETAMOL & ANALGESICS
    if (q.contains("paracetamol") || q.contains("dolo") || q.contains("calpol") || q.contains("pacimol") || q.contains("crocin") || q.contains("650")) {
      return listOf(
        com.example.data.model.BrandMedicine(name = "Calpol 650mg Tablet", brandName = "GSK", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 32.0, packaging = "15 Tablets", description = "GSK's trusted fast-dissolving antipyretic"),
        com.example.data.model.BrandMedicine(name = "Pacimol 650mg Tablet", brandName = "IPCA", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 24.5, packaging = "15 Tablets", description = "High efficacy cost-effective formulation by IPCA"),
        com.example.data.model.BrandMedicine(name = "Dolo 650mg Tablet", brandName = "Micro Labs", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 34.0, packaging = "15 Tablets", description = "Market-leading antipyretic brand"),
        com.example.data.model.BrandMedicine(name = "Pacimol 100ml IV Infusion", brandName = "IPCA", saltComposition = "Paracetamol 1000mg/100ml", category = "Injectable", mrp = 82.0, packaging = "100ml IV Bottle", isInjectable = true, description = "Intravenous paracetamol for rapid hospital post-op pyrexia"),
        com.example.data.model.BrandMedicine(name = "Jan Aushadhi Paracetamol 650", brandName = "Jan Aushadhi (Generic)", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 9.5, packaging = "10 Tablets", description = "Govt. subsidized generic (72% cost savings)")
      )
    }

    // 4. PROTON PUMP INHIBITORS (PPI) / GASTRO
    if (q.contains("panto") || q.contains("pan") || q.contains("gerd") || q.contains("omeprazole") || q.contains("omez") || q.contains("razo") || q.contains("rabeprazole") || q.contains("40")) {
      return listOf(
        com.example.data.model.BrandMedicine(name = "Pan 40mg Tablet", brandName = "ALKEM", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 158.0, packaging = "15 Tablets"),
        com.example.data.model.BrandMedicine(name = "Pantocid 40mg Tablet", brandName = "SUN PHARMA", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 165.0, packaging = "15 Tablets"),
        com.example.data.model.BrandMedicine(name = "Pansec 40mg IV Injection", brandName = "Cipla", saltComposition = "Pantoprazole 40mg IV", category = "Injectable", mrp = 56.0, packaging = "1 Vial IV with solvent", isInjectable = true, description = "Intravenous PPI for acute GI bleeding and stress ulcers"),
        com.example.data.model.BrandMedicine(name = "Omez 20mg Capsule", brandName = "Dr. Reddy's", saltComposition = "Omeprazole 20mg", category = "Capsule", mrp = 75.0, packaging = "20 Capsules"),
        com.example.data.model.BrandMedicine(name = "Razo 20mg Tablet", brandName = "Dr. Reddy's", saltComposition = "Rabeprazole 20mg", category = "Tablet", mrp = 160.0, packaging = "15 Tablets"),
        com.example.data.model.BrandMedicine(name = "Jan Aushadhi Pantoprazole 40", brandName = "Jan Aushadhi (Generic)", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 22.0, packaging = "10 Tablets", description = "Generic Jan Aushadhi (85% savings)")
      )
    }

    // 5. AMOXICILLIN + CLAVULANATE
    if (q.contains("amox") || q.contains("augmentin") || q.contains("clavam") || q.contains("moxikind") || q.contains("625")) {
      return listOf(
        com.example.data.model.BrandMedicine(name = "Augmentin 625 Duo Tablet", brandName = "GSK", saltComposition = "Amoxicillin 500mg + Clavulanate 125mg", category = "Tablet", mrp = 210.0, packaging = "10 Tablets"),
        com.example.data.model.BrandMedicine(name = "Clavam 625mg Tablet", brandName = "ALKEM", saltComposition = "Amoxicillin 500mg + Clavulanic Acid 125mg", category = "Tablet", mrp = 205.0, packaging = "10 Tablets"),
        com.example.data.model.BrandMedicine(name = "Moxikind-CV 625 Tablet", brandName = "Mankind", saltComposition = "Amoxicillin 500mg + Clavulanate 125mg", category = "Tablet", mrp = 175.0, packaging = "10 Tablets"),
        com.example.data.model.BrandMedicine(name = "Augmentin 1.2g IV Injection", brandName = "GSK", saltComposition = "Amoxicillin 1000mg + Clavulanate 200mg", category = "Injectable", mrp = 145.0, packaging = "1 Vial with sterile water", isInjectable = true, description = "Intravenous co-amoxiclav for acute sepsis & pneumonia"),
        com.example.data.model.BrandMedicine(name = "Augmentin Duo Dry Syrup 30ml", brandName = "GSK", saltComposition = "Amoxicillin 200mg + Clavulanate 28.5mg / 5ml", category = "Syrup", mrp = 68.0, packaging = "30ml Dry Syrup"),
        com.example.data.model.BrandMedicine(name = "Jan Aushadhi Amoxyclav 625", brandName = "Jan Aushadhi (Generic)", saltComposition = "Amoxicillin 500mg + Clavulanic 125mg", category = "Tablet", mrp = 65.0, packaging = "10 Tablets", description = "Jan Aushadhi affordable generic (68% savings)")
      )
    }

    // 6. AZITHROMYCIN
    if (q.contains("azithro") || q.contains("azee") || q.contains("azithral") || q.contains("zady") || q.contains("500")) {
      return listOf(
        com.example.data.model.BrandMedicine(name = "Azee 500mg Tablet", brandName = "Cipla", saltComposition = "Azithromycin 500mg", category = "Tablet", mrp = 125.0, packaging = "5 Tablets"),
        com.example.data.model.BrandMedicine(name = "Azithral 500mg Tablet", brandName = "Alembic", saltComposition = "Azithromycin 500mg", category = "Tablet", mrp = 129.0, packaging = "5 Tablets"),
        com.example.data.model.BrandMedicine(name = "Zady 500mg Tablet", brandName = "Mankind", saltComposition = "Azithromycin 500mg", category = "Tablet", mrp = 110.0, packaging = "5 Tablets"),
        com.example.data.model.BrandMedicine(name = "Azee 500mg IV Injection", brandName = "Cipla", saltComposition = "Azithromycin 500mg IV Infusion", category = "Injectable", mrp = 185.0, packaging = "1 Vial IV", isInjectable = true),
        com.example.data.model.BrandMedicine(name = "Jan Aushadhi Azithromycin 500", brandName = "Jan Aushadhi (Generic)", saltComposition = "Azithromycin 500mg", category = "Tablet", mrp = 48.0, packaging = "5 Tablets", description = "Generic Jan Aushadhi (62% savings)")
      )
    }

    // 7. CEFTRIAXONE & CEPHALOSPORINS
    if (q.contains("ceftriaxone") || q.contains("monocef") || q.contains("taxim") || q.contains("ciplacef") || q.contains("inj")) {
      return listOf(
        com.example.data.model.BrandMedicine(name = "Monocef 1g Injection", brandName = "ARISTO", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 62.0, packaging = "1 Vial with WFI", isInjectable = true),
        com.example.data.model.BrandMedicine(name = "Ciplacef 1g Injection", brandName = "Cipla", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 68.0, packaging = "1 Vial IV/IM", isInjectable = true),
        com.example.data.model.BrandMedicine(name = "Taxim 1g Injection", brandName = "ALKEM", saltComposition = "Cefotaxime 1000mg", category = "Injectable", mrp = 48.0, packaging = "1 Vial", isInjectable = true),
        com.example.data.model.BrandMedicine(name = "Taxim-O 200mg Tablet", brandName = "ALKEM", saltComposition = "Cefixime 200mg", category = "Tablet", mrp = 115.0, packaging = "10 Tablets"),
        com.example.data.model.BrandMedicine(name = "Jan Aushadhi Ceftriaxone 1g Inj", brandName = "Jan Aushadhi (Generic)", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 28.0, packaging = "1 Vial", isInjectable = true)
      )
    }

    // 8. PAIN, ARTHRITIS & NSAID
    if (q.contains("zerodol") || q.contains("aceclo") || q.contains("combiflam") || q.contains("ibuprofen") || q.contains("diclofenac") || q.contains("voveran")) {
      return listOf(
        com.example.data.model.BrandMedicine(name = "Zerodol-P Tablet", brandName = "IPCA", saltComposition = "Aceclofenac 100mg + Paracetamol 325mg", category = "Tablet", mrp = 68.0, packaging = "10 Tablets"),
        com.example.data.model.BrandMedicine(name = "Zerodol-SP Tablet", brandName = "IPCA", saltComposition = "Aceclofenac 100mg + Paracetamol 325mg + Serratiopeptidase 15mg", category = "Tablet", mrp = 120.0, packaging = "10 Tablets"),
        com.example.data.model.BrandMedicine(name = "Combiflam Tablet", brandName = "Sanofi", saltComposition = "Ibuprofen 400mg + Paracetamol 325mg", category = "Tablet", mrp = 48.0, packaging = "20 Tablets"),
        com.example.data.model.BrandMedicine(name = "Voveran 50mg Tablet", brandName = "Novartis", saltComposition = "Diclofenac Sodium 50mg", category = "Tablet", mrp = 85.0, packaging = "15 Tablets"),
        com.example.data.model.BrandMedicine(name = "Dynapar AQ 75mg Injection", brandName = "torrent", saltComposition = "Diclofenac Sodium 75mg/ml (Aqueous)", category = "Injectable", mrp = 32.0, packaging = "1ml Ampoule IV/IM", isInjectable = true),
        com.example.data.model.BrandMedicine(name = "Jan Aushadhi Aceclofenac + Paracetamol", brandName = "Jan Aushadhi (Generic)", saltComposition = "Aceclofenac 100mg + Paracetamol 325mg", category = "Tablet", mrp = 18.0, packaging = "10 Tablets")
      )
    }

    // 9. DEFAULT RICH INDIAN BRAND AND GENERIC PHARMA CATALOG
    return listOf(
      com.example.data.model.BrandMedicine(name = "Calpol 650mg", brandName = "GSK", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 32.0, packaging = "15 Tablets"),
      com.example.data.model.BrandMedicine(name = "Pacimol 650mg", brandName = "IPCA", saltComposition = "Paracetamol 650mg", category = "Tablet", mrp = 24.5, packaging = "15 Tablets"),
      com.example.data.model.BrandMedicine(name = "Pan 40mg", brandName = "ALKEM", saltComposition = "Pantoprazole 40mg", category = "Tablet", mrp = 158.0, packaging = "15 Tablets"),
      com.example.data.model.BrandMedicine(name = "Monocef 1g Injection", brandName = "ARISTO", saltComposition = "Ceftriaxone 1000mg", category = "Injectable", mrp = 62.0, packaging = "1 Vial", isInjectable = true),
      com.example.data.model.BrandMedicine(name = "Augmentin 625 Duo", brandName = "GSK", saltComposition = "Amoxicillin 500mg + Clavulanate 125mg", category = "Tablet", mrp = 210.0, packaging = "10 Tablets"),
      com.example.data.model.BrandMedicine(name = "Jan Aushadhi Equivalent Generic", brandName = "Jan Aushadhi (Generic)", saltComposition = queryOrSalt.ifBlank { "Generic Salt" }, category = "Tablet", mrp = 18.0, packaging = "10 Tablets", description = "Govt. subsidized Jan Aushadhi generic alternative (up to 75% savings)")
    )
  }

  fun openGoogleSearch(context: android.content.Context, query: String) {
    try {
      val encoded = java.net.URLEncoder.encode(query, "UTF-8")
      val intent = android.content.Intent(
        android.content.Intent.ACTION_VIEW,
        android.net.Uri.parse("https://www.google.com/search?q=$encoded")
      ).apply {
        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {}
  }
}

