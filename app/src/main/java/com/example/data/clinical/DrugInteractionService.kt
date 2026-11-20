package com.example.data.clinical

import android.util.Log
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

enum class InteractionSeverity {
  SEVERE_CONTRAINDICATED, // Red
  MODERATE_MONITOR,       // Amber
  MINOR_CAUTION,          // Blue
  NO_KNOWN_INTERACTION    // Green
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
  val resolvedSalts: List<String> = emptyList(),
  val saltMappingByBrand: Map<String, List<String>> = emptyMap(),
  val aiError: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

class DrugInteractionService {

  companion object {
    private const val TAG = "DrugInteractionSvc"
    private const val DDI_MODEL = "gemini-2.5-flash"
  }

  private val ddiSchema = Schema.obj(
    properties = mapOf(
      "interactions" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "drug1" to Schema.string(description = "Brand or salt name 1"),
            "drug2" to Schema.string(description = "Brand or salt name 2"),
            "severity" to Schema.enumeration(
              values = listOf("SEVERE_CONTRAINDICATED", "MODERATE_MONITOR", "MINOR_CAUTION"),
              description = "Severity classification"
            ),
            "riskTitle" to Schema.string(description = "Short clinical risk headline"),
            "mechanism" to Schema.string(description = "Pharmacokinetic or pharmacodynamic mechanism"),
            "clinicalAdvice" to Schema.string(description = "Actionable guidance for pharmacist"),
            "foodPrecaution" to Schema.string(description = "Food or timing precaution if any"),
            "alternativeSuggestion" to Schema.string(description = "Safer therapeutic alternative")
          )
        )
      ),
      "clinicalSummary" to Schema.string(description = "Concise clinical verdict"),
      "foodWarnings" to Schema.array(items = Schema.string())
    )
  )

  private val ddiModel by lazy {
    Firebase.ai.generativeModel(
      modelName = DDI_MODEL,
      generationConfig = generationConfig {
        temperature = 0.1f
        responseMimeType = "application/json"
        responseSchema = ddiSchema
      },
      systemInstruction = content {
        text("You are an expert clinical pharmacologist evaluating salt-level drug-drug and drug-food interactions in India.")
      }
    )
  }

  private val offlineInteractions = listOf(
    DrugPairInteraction(
      drug1 = "Nitroglycerin / Isosorbide",
      drug2 = "Sildenafil / Tadalafil",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Fatal Refractory Hypotension & Cardiovascular Collapse",
      mechanism = "Synergistic cGMP-mediated vasodilation leading to severe precipitously low blood pressure.",
      clinicalAdvice = "CONTRAINDICATED. Maintain at least a 24-48 hour separation interval.",
      alternativeSuggestion = "Consult cardiologist for beta-blockers or ranolazine."
    ),
    DrugPairInteraction(
      drug1 = "Warfarin / Acenocoumarol",
      drug2 = "Aspirin / Ibuprofen / Diclofenac",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Severe GI Hemorrhage & Major Bleeding Risk",
      mechanism = "NSAIDs inhibit COX-1 platelet aggregation and erode gastric mucosa, multiplying anticoagulant bleeding risk.",
      clinicalAdvice = "Avoid combination. Use Paracetamol (max 2g/day) with INR monitoring.",
      alternativeSuggestion = "Paracetamol or topical Diclofenac gel."
    ),
    DrugPairInteraction(
      drug1 = "Clopidogrel",
      drug2 = "Omeprazole / Esomeprazole",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Reduced Antiplatelet Efficacy (CYP2C19 Inhibition)",
      mechanism = "Omeprazole inhibits CYP2C19 bioactivation of Clopidogrel into its active antiplatelet metabolite.",
      clinicalAdvice = "Switch PPI to Pantoprazole 40mg or Rabeprazole 20mg.",
      alternativeSuggestion = "Pantoprazole 40mg (Pan 40) or Famotidine 20mg."
    ),
    DrugPairInteraction(
      drug1 = "Atorvastatin / Simvastatin",
      drug2 = "Clarithromycin / Erythromycin",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Rhabdomyolysis & Acute Renal Failure",
      mechanism = "Potent CYP3A4 inhibition raises serum statin concentrations 5-10 fold.",
      clinicalAdvice = "Hold statin during antibiotic therapy or switch antibiotic to Azithromycin.",
      alternativeSuggestion = "Azithromycin 500mg."
    ),
    DrugPairInteraction(
      drug1 = "Telmisartan / Ramipril / Enalapril",
      drug2 = "Spironolactone / Potassium",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Severe Hyperkalemia & Cardiac Arrhythmia",
      mechanism = "Additive potassium retention in the distal nephron leading to hyperkalemia (>5.5 mEq/L).",
      clinicalAdvice = "Monitor serum potassium and renal creatinine within 7-14 days.",
      foodPrecaution = "Avoid potassium salt substitutes and excess coconut water."
    ),
    DrugPairInteraction(
      drug1 = "Ciprofloxacin / Levofloxacin / Norfloxacin",
      drug2 = "Antacids (Aluminium/Magnesium) / Calcium / Iron",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Impaired Antibiotic Absorption (Chelation)",
      mechanism = "Polyvalent cations form insoluble chelates in the gut, cutting fluoroquinolone absorption up to 80%.",
      clinicalAdvice = "Separate administration by 2 hours before or 4-6 hours after minerals/antacids.",
      foodPrecaution = "Do not take with milk or calcium-fortified beverages."
    ),
    DrugPairInteraction(
      drug1 = "Methotrexate",
      drug2 = "Ibuprofen / Naproxen / Diclofenac",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Methotrexate Toxicity & Bone Marrow Suppression",
      mechanism = "NSAIDs decrease renal tubular clearance of Methotrexate.",
      clinicalAdvice = "Avoid high-dose NSAIDs; monitor CBC and liver enzymes.",
      alternativeSuggestion = "Paracetamol under rheumatologist supervision."
    ),
    DrugPairInteraction(
      drug1 = "Escitalopram / Sertraline / Fluoxetine",
      drug2 = "Tramadol / Tapentadol",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Serotonin Syndrome & Seizure Risk",
      mechanism = "Additive serotonergic excess and lowered seizure threshold.",
      clinicalAdvice = "Avoid co-administration.",
      alternativeSuggestion = "Paracetamol 650mg or Etoricoxib."
    ),
    DrugPairInteraction(
      drug1 = "Metformin",
      drug2 = "Alcohol / Ethanol",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Lactic Acidosis & Hypoglycemia Risk",
      mechanism = "Alcohol inhibits gluconeogenesis and impairs lactate clearance.",
      clinicalAdvice = "Advise patient to avoid heavy or binge alcohol use.",
      foodPrecaution = "Strictly avoid alcoholic beverages."
    ),
    DrugPairInteraction(
      drug1 = "Doxycycline / Tetracycline",
      drug2 = "Calcium / Iron / Milk",
      severity = InteractionSeverity.MINOR_CAUTION,
      riskTitle = "Chelation & Reduced Bioavailability",
      mechanism = "Divalent/trivalent cations bind tetracycline antibiotics in the GI tract.",
      clinicalAdvice = "Separate Doxycycline and calcium/iron by at least 2 hours.",
      foodPrecaution = "Avoid dairy products within 2 hours of dosing."
    ),
    DrugPairInteraction(
      drug1 = "Digoxin",
      drug2 = "Amiodarone",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Digoxin Toxicity & Heart Block",
      mechanism = "Amiodarone inhibits P-glycoprotein renal clearance of Digoxin, doubling serum levels.",
      clinicalAdvice = "Reduce Digoxin dose by 50% and monitor ECG/serum levels.",
      alternativeSuggestion = "Beta-blocker rate control under cardiologist guidance."
    ),
    DrugPairInteraction(
      drug1 = "Lithium",
      drug2 = "Ibuprofen / Diclofenac / Naproxen",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Lithium Toxicity & Renal Injury",
      mechanism = "NSAIDs reduce renal lithium excretion causing toxic accumulation.",
      clinicalAdvice = "Avoid NSAIDs in patients taking Lithium.",
      alternativeSuggestion = "Paracetamol 650mg."
    ),
    DrugPairInteraction(
      drug1 = "Metronidazole / Tinidazole",
      drug2 = "Alcohol / Ethanol",
      severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
      riskTitle = "Disulfiram-like Reaction",
      mechanism = "Inhibits aldehyde dehydrogenase causing acute acetaldehyde accumulation.",
      clinicalAdvice = "Abstain from alcohol during and for 48 hours after therapy.",
      foodPrecaution = "Avoid alcohol and alcohol-containing syrups."
    ),
    DrugPairInteraction(
      drug1 = "Levothyroxine",
      drug2 = "Calcium / Iron",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Reduced Thyroid Hormone Absorption",
      mechanism = "Calcium and iron salts form insoluble complexes with levothyroxine.",
      clinicalAdvice = "Take Levothyroxine on an empty stomach at least 4 hours before calcium/iron.",
      foodPrecaution = "Take with plain water 30-60 mins before breakfast."
    ),
    DrugPairInteraction(
      drug1 = "Theophylline / Etofylline",
      drug2 = "Ciprofloxacin",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Theophylline Toxicity (Tachycardia, Seizures)",
      mechanism = "Ciprofloxacin inhibits CYP1A2 clearance of theophylline.",
      clinicalAdvice = "Reduce theophylline dose 30-50% or switch antibiotic.",
      alternativeSuggestion = "Azithromycin or Amoxicillin-Clavulanate."
    ),
    DrugPairInteraction(
      drug1 = "Azithromycin",
      drug2 = "Domperidone",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Additive QT Prolongation & Arrhythmia Risk",
      mechanism = "Both agents prolong cardiac ventricular repolarization (QTc interval).",
      clinicalAdvice = "Use caution in elderly or cardiac patients; avoid in baseline QT prolongation.",
      alternativeSuggestion = "Substitute Domperidone with Ondansetron cautiously or Metoclopramide after review."
    ),
    DrugPairInteraction(
      drug1 = "Paracetamol",
      drug2 = "Alcohol / Ethanol",
      severity = InteractionSeverity.MODERATE_MONITOR,
      riskTitle = "Hepatotoxicity Risk",
      mechanism = "Chronic ethanol induces CYP2E1 increasing toxic NAPQI formation.",
      clinicalAdvice = "Limit Paracetamol to <= 2g/day in chronic alcohol users.",
      foodPrecaution = "Avoid alcohol during high-dose Paracetamol therapy."
    )
  )

  private val brandToMoleculesMap = mapOf(
    "dolo" to listOf("paracetamol"),
    "calpol" to listOf("paracetamol"),
    "pacimol" to listOf("paracetamol"),
    "crocin" to listOf("paracetamol"),
    "t-98" to listOf("paracetamol"),
    "paracip" to listOf("paracetamol"),
    "febrex" to listOf("paracetamol", "chlorpheniramine"),
    "combiflam" to listOf("ibuprofen", "paracetamol"),
    "brufen" to listOf("ibuprofen"),
    "zerodol-p" to listOf("aceclofenac", "paracetamol"),
    "zerodol-sp" to listOf("aceclofenac", "paracetamol", "serratiopeptidase"),
    "zerodol" to listOf("aceclofenac"),
    "voveran" to listOf("diclofenac"),
    "volini" to listOf("diclofenac"),
    "augmentin" to listOf("amoxicillin", "clavulanic acid"),
    "clavam" to listOf("amoxicillin", "clavulanic acid"),
    "moxikind" to listOf("amoxicillin", "clavulanic acid"),
    "pan 40" to listOf("pantoprazole"),
    "pan-d" to listOf("pantoprazole", "domperidone"),
    "pan d" to listOf("pantoprazole", "domperidone"),
    "pantocid" to listOf("pantoprazole"),
    "pantosec" to listOf("pantoprazole"),
    "omez" to listOf("omeprazole"),
    "razo" to listOf("rabeprazole"),
    "clopilet" to listOf("clopidogrel"),
    "ecosprin" to listOf("aspirin"),
    "warf" to listOf("warfarin"),
    "coumadin" to listOf("warfarin"),
    "sorbitrate" to listOf("isosorbide", "nitroglycerin"),
    "manforce" to listOf("sildenafil"),
    "azithral" to listOf("azithromycin"),
    "azee" to listOf("azithromycin"),
    "ciplox" to listOf("ciprofloxacin"),
    "cifran" to listOf("ciprofloxacin"),
    "monocef" to listOf("ceftriaxone"),
    "taxim-o" to listOf("cefixime"),
    "telma-h" to listOf("telmisartan", "hydrochlorothiazide"),
    "telma" to listOf("telmisartan"),
    "aldactone" to listOf("spironolactone"),
    "lanoxin" to listOf("digoxin"),
    "cordarone" to listOf("amiodarone"),
    "flagyl" to listOf("metronidazole"),
    "metrogyl" to listOf("metronidazole"),
    "shelcal" to listOf("calcium", "vitamin d3"),
    "asthalin" to listOf("salbutamol"),
    "deriphyllin" to listOf("theophylline", "etofylline"),
    "thyronorm" to listOf("levothyroxine"),
    "eltroxin" to listOf("levothyroxine"),
    "glycomet-gp" to listOf("metformin", "glimepiride"),
    "glycomet" to listOf("metformin"),
    "montair-lc" to listOf("montelukast", "levocetirizine"),
    "rosuvas" to listOf("rosuvastatin")
  )

  private fun matchesSaltToken(salt: String, rulePart: String): Boolean {
    val s = salt.trim().lowercase()
    val r = rulePart.trim().lowercase()
    if (s.isBlank() || r.isBlank()) return false
    if (s == r) return true
    val rBoundary = Regex("\\b${Regex.escape(r)}\\b")
    val sBoundary = Regex("\\b${Regex.escape(s)}\\b")
    return rBoundary.containsMatchIn(s) || (s.length >= 4 && sBoundary.containsMatchIn(r))
  }

  private fun parseSaltString(rawSalt: String): List<String> {
    return rawSalt
      .split("+", ",", "/", "&", " and ")
      .map { token ->
        token.lowercase()
          .replace(Regex("\\d+(\\.\\d+)?\\s*(mg|mcg|gm|g|ml|iu|%|w/v|w/w)"), "")
          .replace(Regex("\\b(ip|bp|usp|sr|er|xr|dt|md|ds|duo|plus|forte)\\b"), "")
          .replace(Regex("[^a-z\\s-]"), " ")
          .trim()
      }
      .filter { it.length >= 3 }
      .distinct()
  }

  private fun resolveLocalSalts(drugInput: String, cachedInventory: List<MedicineItem>): List<String> {
    val lower = drugInput.lowercase().trim()
    val found = mutableListOf<String>()

    // 1. Check cached Room inventory first (Cache-Through persisted salts)
    val cachedMatch = cachedInventory.firstOrNull { med ->
      med.name.equals(drugInput, ignoreCase = true)
    } ?: cachedInventory.firstOrNull { med ->
      drugInput.length >= 3 && (
        med.name.contains(drugInput, ignoreCase = true) ||
          drugInput.contains(med.name, ignoreCase = true)
        )
    }
    if (cachedMatch != null) {
      val saltSource = cachedMatch.saltMolecule.ifBlank { cachedMatch.composition }
      if (saltSource.isNotBlank()) {
        found.addAll(parseSaltString(saltSource))
      }
    }

    // 2. Check static Indian brand -> salt map
    brandToMoleculesMap.forEach { (brand, molecules) ->
      if (lower.contains(brand)) {
        found.addAll(molecules)
      }
    }

    // 3. Fallback if user typed raw generic molecule directly
    if (found.isEmpty()) {
      found.addAll(parseSaltString(lower))
    }

    return found.distinct()
  }

  suspend fun checkDrugInteractions(
    medicines: List<String>,
    cachedInventory: List<MedicineItem> = emptyList(),
    resolveMissingMedicine: (suspend (String) -> MedicineItem?)? = null
  ): InteractionCheckResult = withContext(Dispatchers.IO) {
    val cleanDrugs = medicines.map { it.trim() }.filter { it.isNotBlank() }.distinct()
    if (cleanDrugs.isEmpty()) {
      return@withContext InteractionCheckResult(
        totalDrugsChecked = 0,
        overallSeverity = InteractionSeverity.NO_KNOWN_INTERACTION,
        interactions = emptyList(),
        clinicalSummary = "Add at least 2 medicines to run Salt-Level Drug-Drug Interaction screening.",
        foodWarnings = emptyList()
      )
    }

    // Step 1: Resolve active generic salts per brand (Local DB -> Static Map -> Cache-Through AI)
    val saltMapping = linkedMapOf<String, List<String>>()
    for (drug in cleanDrugs) {
      var salts = resolveLocalSalts(drug, cachedInventory)
      val matchedCached = cachedInventory.any {
        it.name.equals(drug, ignoreCase = true) ||
          (drug.length >= 3 && it.name.contains(drug, ignoreCase = true))
      }
      val matchedKnownMap = brandToMoleculesMap.keys.any { drug.lowercase().contains(it) }

      if (!matchedCached && !matchedKnownMap && resolveMissingMedicine != null) {
        val aiResolvedMed = resolveMissingMedicine(drug)
        if (aiResolvedMed != null) {
          val saltSource = aiResolvedMed.saltMolecule.ifBlank { aiResolvedMed.composition }
          val aiSalts = parseSaltString(saltSource)
          if (aiSalts.isNotEmpty()) {
            salts = aiSalts
          }
        }
      }
      saltMapping[drug] = salts.ifEmpty { listOf(drug.lowercase()) }
    }

    val deduplicatedSalts = saltMapping.values.flatten().distinct()

    if (cleanDrugs.size < 2 && deduplicatedSalts.size < 2) {
      val singleDrug = cleanDrugs.first()
      val singleSalts = saltMapping[singleDrug].orEmpty()
      val singleWarnings = offlineInteractions.filter { rule ->
        val r1 = rule.drug1.lowercase().split("/").map { it.trim() }
        val r2 = rule.drug2.lowercase().split("/").map { it.trim() }
        r1.any { p -> singleSalts.any { matchesSaltToken(it, p) } } ||
          r2.any { p -> singleSalts.any { matchesSaltToken(it, p) } }
      }.take(3)
      val foodList = singleWarnings.mapNotNull { it.foodPrecaution.takeIf { p -> p.isNotBlank() } }.distinct()

      return@withContext InteractionCheckResult(
        totalDrugsChecked = cleanDrugs.size,
        overallSeverity = if (singleWarnings.isNotEmpty()) InteractionSeverity.MINOR_CAUTION else InteractionSeverity.NO_KNOWN_INTERACTION,
        interactions = singleWarnings,
        clinicalSummary = if (singleWarnings.isNotEmpty()) {
          "Resolved active salts for $singleDrug (${singleSalts.joinToString(", ")}). Add a second medicine to evaluate co-administration safety."
        } else {
          "Resolved active salts for $singleDrug (${singleSalts.joinToString(", ")}). Add another medicine to screen pairs."
        },
        foodWarnings = foodList,
        resolvedSalts = deduplicatedSalts,
        saltMappingByBrand = saltMapping
      )
    }

    // Step 2: Deterministic Salt-Level Duplicate Overdose + Curated CDSCO Rule Screen
    val localMatches = mutableListOf<DrugPairInteraction>()
    for (i in cleanDrugs.indices) {
      for (j in (i + 1) until cleanDrugs.size) {
        val molsA = saltMapping[cleanDrugs[i]].orEmpty()
        val molsB = saltMapping[cleanDrugs[j]].orEmpty()

        val commonActive = molsA.firstOrNull { a ->
          a.length > 3 && molsB.any { b -> b == a || (b.contains(a) && a.length > 4) || (a.contains(b) && b.length > 4) }
        }

        if (commonActive != null) {
          val saltCap = commonActive.replaceFirstChar { it.uppercase() }
          localMatches.add(
            DrugPairInteraction(
              drug1 = cleanDrugs[i],
              drug2 = cleanDrugs[j],
              severity = InteractionSeverity.SEVERE_CONTRAINDICATED,
              riskTitle = "DUPLICATE ACTIVE SALT ($saltCap) — TOXIC OVERDOSE RISK",
              mechanism = "Both '${cleanDrugs[i]}' and '${cleanDrugs[j]}' contain active salt $saltCap, risking cumulative overdose toxicity.",
              clinicalAdvice = "CONTRAINDICATED TO DISPENSE TOGETHER. Dispense only one brand containing $saltCap.",
              alternativeSuggestion = "Retain either ${cleanDrugs[i]} or ${cleanDrugs[j]}."
            )
          )
        }

        val found = offlineInteractions.firstOrNull { rule ->
          val r1Parts = rule.drug1.lowercase().split("/").map { it.trim() }
          val r2Parts = rule.drug2.lowercase().split("/").map { it.trim() }
          val aR1 = r1Parts.any { p -> molsA.any { matchesSaltToken(it, p) } }
          val bR2 = r2Parts.any { p -> molsB.any { matchesSaltToken(it, p) } }
          val aR2 = r2Parts.any { p -> molsA.any { matchesSaltToken(it, p) } }
          val bR1 = r1Parts.any { p -> molsB.any { matchesSaltToken(it, p) } }
          (aR1 && bR2) || (aR2 && bR1)
        }

        if (found != null) {
          localMatches.add(found.copy(drug1 = cleanDrugs[i], drug2 = cleanDrugs[j]))
        }
      }
    }

    // Step 3: Cloud AI Salt-Level Pharmacology Verification via Firebase AI Logic
    val (geminiResult, aiErr) = callFirebaseDdiModel(cleanDrugs, saltMapping, deduplicatedSalts)

    val finalInteractions = if (geminiResult != null && geminiResult.interactions.isNotEmpty()) {
      (localMatches + geminiResult.interactions).distinctBy {
        "${it.drug1.lowercase()}|${it.drug2.lowercase()}|${it.riskTitle.take(20)}"
      }
    } else {
      localMatches
    }

    val maxSeverity = when {
      finalInteractions.any { it.severity == InteractionSeverity.SEVERE_CONTRAINDICATED } -> InteractionSeverity.SEVERE_CONTRAINDICATED
      finalInteractions.any { it.severity == InteractionSeverity.MODERATE_MONITOR } -> InteractionSeverity.MODERATE_MONITOR
      finalInteractions.any { it.severity == InteractionSeverity.MINOR_CAUTION } -> InteractionSeverity.MINOR_CAUTION
      else -> InteractionSeverity.NO_KNOWN_INTERACTION
    }

    val summary = geminiResult?.clinicalSummary?.takeIf { it.isNotBlank() } ?: when (maxSeverity) {
      InteractionSeverity.SEVERE_CONTRAINDICATED ->
        "CONTRAINDICATED COMBINATION DETECTED across active salts (${deduplicatedSalts.joinToString(", ")}). Do not dispense without prescriber consultation."
      InteractionSeverity.MODERATE_MONITOR ->
        "MODERATE INTERACTION: Adjust dose spacing (2-4 hrs) or monitor clinical labs."
      InteractionSeverity.MINOR_CAUTION ->
        "MINOR CAUTION: Counsel patient regarding food/timing instructions."
      InteractionSeverity.NO_KNOWN_INTERACTION ->
        "SAFE COMBINATION: No major pharmacokinetic or pharmacodynamic contraindications detected across salts (${deduplicatedSalts.joinToString(", ")})."
    }

    val foodPrecautions = (finalInteractions.mapNotNull { it.foodPrecaution.takeIf { p -> p.isNotBlank() } } +
      (geminiResult?.foodWarnings ?: emptyList())).distinct()

    InteractionCheckResult(
      totalDrugsChecked = cleanDrugs.size,
      overallSeverity = maxSeverity,
      interactions = finalInteractions,
      clinicalSummary = summary,
      foodWarnings = foodPrecautions,
      resolvedSalts = deduplicatedSalts,
      saltMappingByBrand = saltMapping,
      aiError = aiErr
    )
  }

  private suspend fun callFirebaseDdiModel(
    brands: List<String>,
    saltMapping: Map<String, List<String>>,
    deduplicatedSalts: List<String>
  ): Pair<InteractionCheckResult?, String?> = withContext(Dispatchers.IO) {
    try {
      val breakdown = saltMapping.entries.joinToString("\n") { (brand, salts) ->
        "- $brand => Active Salts: ${salts.joinToString(" + ")}"
      }
      val prompt = """
        Evaluate Salt-Level Drug-Drug Interactions for the following Indian pharmacy cart/prescription:
        $breakdown

        Deduplicated Active Generic Salts List: ${deduplicatedSalts.joinToString(", ")}

        Check pharmacokinetic (CYP450, P-gp, renal/hepatic) and pharmacodynamic (QT prolongation, bleeding, hyperkalemia, duplicate-ingredient overdose) interactions.
        Return every clinically relevant pair and actionable pharmacist guidance.
      """.trimIndent()

      val response = withTimeout(60_000L) {
        ddiModel.generateContent(prompt)
      }
      val textContent = response.text?.trim().orEmpty()
      if (textContent.isBlank()) {
        return@withContext null to "Firebase AI returned empty DDI response"
      }

      val cleanJson = textContent.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
      val parsed = JSONObject(cleanJson)
      val interactionsArray = parsed.optJSONArray("interactions") ?: JSONArray()
      val interactionsList = mutableListOf<DrugPairInteraction>()

      for (k in 0 until interactionsArray.length()) {
        val obj = interactionsArray.optJSONObject(k) ?: continue
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
          val w = foodArr.optString(f).trim()
          if (w.isNotBlank()) foodList.add(w)
        }
      }

      val result = InteractionCheckResult(
        totalDrugsChecked = brands.size,
        overallSeverity = when {
          interactionsList.any { it.severity == InteractionSeverity.SEVERE_CONTRAINDICATED } -> InteractionSeverity.SEVERE_CONTRAINDICATED
          interactionsList.any { it.severity == InteractionSeverity.MODERATE_MONITOR } -> InteractionSeverity.MODERATE_MONITOR
          interactionsList.any { it.severity == InteractionSeverity.MINOR_CAUTION } -> InteractionSeverity.MINOR_CAUTION
          else -> InteractionSeverity.NO_KNOWN_INTERACTION
        },
        interactions = interactionsList,
        clinicalSummary = parsed.optString("clinicalSummary", ""),
        foodWarnings = foodList
      )
      result to null
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Cloud AI DDI assessment failed", e)
      null to com.example.data.ai.mapGeminiFreeTierError(e)
    }
  }
}
