package com.example.data.clinical

import com.example.BuildConfig
import com.example.data.model.MedicineItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class InteractionSeverity {
  SEVERE_CONTRAINDICATED, // Red 🔴
  MODERATE_MONITOR,       // Amber 🟠
  MINOR_CAUTION,          // Blue 🟡
  NO_KNOWN_INTERACTION    // Green 🟢
}

data class DrugPairInteraction(
  val drug1: String,
  val drug2: String,
  val severity: InteractionSeverity,
  val riskTitle: String,
  val mechanism: String,
  val clinicalAdvice: String,
  val foodPrecaution: String = "",
  val alternativeSuggestion: String = ""
)

data class InteractionCheckResult(
  val totalDrugsChecked: Int,
  val overallSeverity: InteractionSeverity,
  val interactions: List<DrugPairInteraction>,
  val clinicalSummary: String,
  val foodWarnings: List<String>,
  val timestamp: Long = System.currentTimeMillis()
)

class DrugInteractionService {
  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

  // Offline Curated Clinical Interaction Database (CDSCO & Standard Pharmacology Reference)
  private val offlineInteractions = listOf(
    // 1. Nitrates + PDE5 Inhibitors (Fatal Hypotension)
    DrugPairInteraction(
      drug1 = "Nitroglycerin / Isosorbide",
      drug2 = "Sildenafil / Tadalafil",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Fatal Refractory Hypotension & Cardiovascular Collapse",
      mechanism = "Synergistic cGMP-mediated vasodilation leading to severe precipitously low blood pressure and cardiac arrest.",
      clinicalAdvice = "ABSOLUTELY CONTRAINDICATED. Do not co-prescribe. Maintain at least a 24-48 hour separation interval if PDE5 inhibitor was ingested.",
      alternativeSuggestion = "Consult cardiologist for alternative anti-anginal therapy (e.g. Beta-blockers or Ranolazine)."
    ),
    // 2. Warfarin / Anticoagulants + NSAIDs
    DrugPairInteraction(
      drug1 = "Warfarin / Acenocoumarol",
      drug2 = "Aspirin / Ibuprofen / Diclofenac",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Severe GI Hemorrhage & Major Bleeding Risk",
      mechanism = "NSAIDs inhibit COX-1 platelet aggregation and cause gastric mucosal erosion, multiplying anticoagulant bleeding risk.",
      clinicalAdvice = "Avoid combination. If analgesia is required, use Paracetamol (up to 2g/day max) with regular INR monitoring.",
      alternativeSuggestion = "Paracetamol or topical analgesics (Volini / Diclofenac Gel)."
    ),
    // 3. Clopidogrel + Omeprazole
    DrugPairInteraction(
      drug1 = "Clopidogrel",
      drug2 = "Omeprazole / Esomeprazole",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Reduced Antiplatelet Efficacy (Thrombosis Risk)",
      mechanism = "Omeprazole competitively inhibits CYP2C19 enzyme, preventing bioactivation of Clopidogrel into its active metabolite.",
      clinicalAdvice = "Switch proton-pump inhibitor to Pantoprazole or Rabeprazole, which exhibit significantly less CYP2C19 inhibition.",
      alternativeSuggestion = "Substitute Omeprazole with Pantoprazole 40mg or Famotidine 20mg."
    ),
    // 4. Statins + Macrolide Antibiotics
    DrugPairInteraction(
      drug1 = "Atorvastatin / Simvastatin",
      drug2 = "Clarithromycin / Erythromycin",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Rhabdomyolysis & Acute Renal Failure",
      mechanism = "Macrolides are potent CYP3A4 inhibitors, increasing serum statin concentrations up to 5-10 fold, causing skeletal muscle breakdown.",
      clinicalAdvice = "Temporarily suspend statin therapy during course of Clarithromycin, or use Azithromycin (not a strong CYP3A4 inhibitor).",
      alternativeSuggestion = "Switch antibiotic to Azithromycin 500mg or Amoxicillin-Clavulanate."
    ),
    // 5. ACE Inhibitors / ARBs + Spironolactone / Potassium
    DrugPairInteraction(
      drug1 = "Telmisartan / Ramipril / Enalapril",
      drug2 = "Spironolactone / Potassium Supplements",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Severe Hyperkalemia & Cardiac Arrhythmia",
      mechanism = "Additive potassium-sparing effect in the distal nephron leading to toxic serum potassium levels (>5.5 mEq/L).",
      clinicalAdvice = "Monitor serum potassium and renal creatinine within 7-14 days of initiation. Advise low-potassium diet (limit bananas, coconut water).",
      foodPrecaution = "Avoid potassium-enriched salt substitutes and excessive coconut water."
    ),
    // 6. Fluoroquinolones + Antacids / Multivitamins
    DrugPairInteraction(
      drug1 = "Ciprofloxacin / Levofloxacin / Norfloxacin",
      drug2 = "Antacids (Aluminium/Magnesium) / Calcium / Iron",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Severely Impaired Antibiotic Absorption (Chelation)",
      mechanism = "Polyvalent cations (Al3+, Mg2+, Ca2+, Fe2+) form insoluble chelate complexes with quinolones, reducing absorption by up to 80%.",
      clinicalAdvice = "Separate administration times: Take Fluoroquinolones at least 2 hours before or 4-6 hours after antacids/minerals.",
      foodPrecaution = "Do not take with milk or calcium-fortified juices."
    ),
    // 7. Methotrexate + NSAIDs
    DrugPairInteraction(
      drug1 = "Methotrexate",
      drug2 = "Ibuprofen / Naproxen / Diclofenac",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Methotrexate Toxicity (Bone Marrow Suppression & Pancytopenia)",
      mechanism = "NSAIDs reduce renal blood flow and competitive renal tubular clearance of Methotrexate, causing fatal toxicity.",
      clinicalAdvice = "Strictly avoid high-dose NSAIDs. Monitor CBC and hepatic enzymes closely if low-dose co-administration is necessary.",
      alternativeSuggestion = "Use Paracetamol or low-dose Prednisolone under rheumatologist supervision."
    ),
    // 8. SSRIs + Tramadol
    DrugPairInteraction(
      drug1 = "Escitalopram / Sertraline / Fluoxetine",
      drug2 = "Tramadol / Tapentadol",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Serotonin Syndrome & Seizure Risk",
      mechanism = "Additive serotonergic enhancement causing hyperthermia, clonus, autonomic instability, and lowers seizure threshold.",
      clinicalAdvice = "Avoid combination. If pain relief needed, consider Paracetamol, topical agents, or low-dose Codeine.",
      alternativeSuggestion = "Paracetamol 650mg or Etoricoxib 90mg."
    ),
    // 9. Metformin + Alcohol
    DrugPairInteraction(
      drug1 = "Metformin",
      drug2 = "Alcohol / Ethanol",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Lactic Acidosis Risk & Hypoglycemia",
      mechanism = "Alcohol inhibits gluconeogenesis and potentiates Metformin's effect on lactate metabolism in the liver.",
      clinicalAdvice = "Warn patient against binge drinking or chronic excessive alcohol consumption while on Metformin.",
      foodPrecaution = "Strictly avoid alcoholic beverages while taking Metformin."
    ),
    // 10. Doxycycline + Dairy Products / Iron
    DrugPairInteraction(
      drug1 = "Doxycycline / Tetracycline",
      drug2 = "Milk / Calcium / Iron Supplements",
      severity = InteractionSeverity.MINOR_CAUTION,
      riskTitle = "Chelation & Reduced Bioavailability",
      mechanism = "Calcium and polyvalent cations bind Doxycycline in the digestive tract preventing therapeutic blood levels.",
      clinicalAdvice = "Take Doxycycline with a full glass of water, at least 2 hours apart from dairy products, milk, cheese, or antacids.",
      foodPrecaution = "Avoid consuming milk, curd, or dairy within 2 hours of taking medication."
    )
  )

  suspend fun checkDrugInteractions(medicines: List<String>): InteractionCheckResult = withContext(Dispatchers.IO) {
    if (medicines.size < 2) {
      return@withContext InteractionCheckResult(
        totalDrugsChecked = medicines.size,
        overallSeverity = InteractionSeverity.NO_KNOWN_INTERACTION,
        interactions = emptyList(),
        clinicalSummary = "Please select at least 2 medicines to evaluate drug-drug, drug-food, and pharmacokinetic interactions.",
        foodWarnings = emptyList()
      )
    }

    // First check local high-priority clinical pairings
    val localMatches = mutableListOf<DrugPairInteraction>()
    for (i in 0 until medicines.size) {
      for (j in (i + 1) until medicines.size) {
        val medA = medicines[i].lowercase()
        val medB = medicines[j].lowercase()

        val found = offlineInteractions.firstOrNull { rule ->
          val rule1Matches = rule.drug1.lowercase().split("/").any { part -> medA.contains(part.trim()) || medB.contains(part.trim()) }
          val rule2Matches = rule.drug2.lowercase().split("/").any { part -> medA.contains(part.trim()) || medB.contains(part.trim()) }
          rule1Matches && rule2Matches
        }

        if (found != null) {
          localMatches.add(found.copy(drug1 = medicines[i], drug2 = medicines[j]))
        }
      }
    }

    // Call Gemini for comprehensive real-time pharmacology cross-check
    val geminiResult = callGeminiInteractionApi(medicines)

    val finalInteractions = if (geminiResult != null && geminiResult.interactions.isNotEmpty()) {
      // Merge unique interactions
      (localMatches + geminiResult.interactions).distinctBy { "${it.drug1}-${it.drug2}" }
    } else if (localMatches.isNotEmpty()) {
      localMatches
    } else {
      emptyList()
    }

    val maxSeverity = when {
      finalInteractions.any { it.severity == InteractionSeverity.SEVERE_CONTRAINDICATED } -> InteractionSeverity.SEVERE_CONTRAINDICATED
      finalInteractions.any { it.severity == InteractionSeverity.MODERATE_MONITOR } -> InteractionSeverity.MODERATE_MONITOR
      finalInteractions.any { it.severity == InteractionSeverity.MINOR_CAUTION } -> InteractionSeverity.MINOR_CAUTION
      else -> InteractionSeverity.NO_KNOWN_INTERACTION
    }

    val summary = when (maxSeverity) {
      InteractionSeverity.SEVERE_CONTRAINDICATED -> "CRITICAL ALERT: Contraindicated drug combination detected. Potential for severe clinical toxicity or cardiovascular collapse. Do not dispense without consulting prescribing physician."
      InteractionSeverity.MODERATE_MONITOR -> "MODERATE INTERACTION: Interaction requires dose adjustment, time-spacing (2-4 hours), or close clinical/lab monitoring (INR, renal, electrolytes)."
      InteractionSeverity.MINOR_CAUTION -> "MINOR INTERACTION: Minimal clinical significance. Advise patient regarding food timing and mild symptoms."
      InteractionSeverity.NO_KNOWN_INTERACTION -> "SAFE: No major drug-drug or pharmacokinetic contraindications detected among the selected medicines. Follow standard dosage schedules."
    }

    val foodPrecautions = finalInteractions.mapNotNull { it.foodPrecaution.takeIf { p -> p.isNotBlank() } }.distinct()

    InteractionCheckResult(
      totalDrugsChecked = medicines.size,
      overallSeverity = maxSeverity,
      interactions = finalInteractions,
      clinicalSummary = summary,
      foodWarnings = foodPrecautions
    )
  }

  private suspend fun callGeminiInteractionApi(medicines: List<String>): InteractionCheckResult? = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") return@withContext null

    try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
      val prompt = """
        You are a clinical pharmacologist. Check drug-drug interactions between these medicines: ${medicines.joinToString(", ")}.
        Return ONLY valid JSON in this exact structure:
        {
          "interactions": [
            {
              "drug1": "Name",
              "drug2": "Name",
              "severity": "SEVERE_CONTRAINDICATED" | "MODERATE_MONITOR" | "MINOR_CAUTION",
              "riskTitle": "Short Risk Title",
              "mechanism": "Pharmacological mechanism in 1-2 lines",
              "clinicalAdvice": "Actionable pharmacist guidance",
              "foodPrecaution": "Specific food/drink warning if any",
              "alternativeSuggestion": "Safer alternative drug"
            }
          ],
          "clinicalSummary": "Overall clinical verdict",
          "foodWarnings": ["Food warning 1", "Food warning 2"]
        }
        If no interaction exists, return "interactions": [] and "clinicalSummary": "No known interactions".
      """.trimIndent()

      val bodyJson = JSONObject().apply {
        put("contents", JSONArray().apply {
          put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
              put(JSONObject().apply { put("text", prompt) })
            })
          })
        })
        put("generationConfig", JSONObject().apply {
          put("responseMimeType", "application/json")
          put("temperature", 0.1)
        })
      }

      val request = Request.Builder()
        .url(url)
        .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = client.newCall(request).execute()
      val responseString = response.body?.string() ?: return@withContext null

      val root = JSONObject(responseString)
      val candidates = root.optJSONArray("candidates") ?: return@withContext null
      val firstCandidate = candidates.optJSONObject(0) ?: return@withContext null
      val textContent = firstCandidate.optJSONObject("content")
        ?.optJSONArray("parts")
        ?.optJSONObject(0)
        ?.optString("text") ?: return@withContext null

      val parsed = JSONObject(textContent)
      val interactionsArray = parsed.optJSONArray("interactions") ?: JSONArray()
      val interactionsList = mutableListOf<DrugPairInteraction>()

      for (k in 0 until interactionsArray.length()) {
        val obj = interactionsArray.getJSONObject(k)
        val sevStr = obj.optString("severity", "MODERATE_MONITOR")
        val sevEnum = when (sevStr) {
          "SEVERE_CONTRAINDICATED" -> InteractionSeverity.SEVERE_CONTRAINDICATED
          "MINOR_CAUTION" -> InteractionSeverity.MINOR_CAUTION
          else -> InteractionSeverity.MODERATE_MONITOR
        }

        interactionsList.add(
          DrugPairInteraction(
            drug1 = obj.optString("drug1"),
            drug2 = obj.optString("drug2"),
            severity = sevEnum,
            riskTitle = obj.optString("riskTitle", "Drug Interaction Alert"),
            mechanism = obj.optString("mechanism", ""),
            clinicalAdvice = obj.optString("clinicalAdvice", "Monitor patient closely."),
            foodPrecaution = obj.optString("foodPrecaution", ""),
            alternativeSuggestion = obj.optString("alternativeSuggestion", "")
          )
        )
      }

      val foodList = mutableListOf<String>()
      val foodArr = parsed.optJSONArray("foodWarnings")
      if (foodArr != null) {
        for (f in 0 until foodArr.length()) {
          foodList.add(foodArr.getString(f))
        }
      }

      InteractionCheckResult(
        totalDrugsChecked = medicines.size,
        overallSeverity = if (interactionsList.any { it.severity == InteractionSeverity.SEVERE_CONTRAINDICATED }) InteractionSeverity.SEVERE_CONTRAINDICATED else InteractionSeverity.MODERATE_MONITOR,
        interactions = interactionsList,
        clinicalSummary = parsed.optString("clinicalSummary", "Interaction review complete."),
        foodWarnings = foodList
      )
    } catch (e: Exception) {
      null
    }
  }
}
