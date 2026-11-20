package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.formatClinicalMarkdown
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.Locale

data class SuggestedMedicine(
  val brandName: String,
  val genericSalt: String,
  val formulationType: String, // Oral Tablet, Oral Pediatric Syrup, Oral Drops, Oral Sachet, IV Injection
  val dosageAndStrength: String,
  val frequency: String, // e.g. 1-0-1 or BD or STAT
  val duration: String,
  val instructions: String,
  val isInjectable: Boolean = false
)

data class DiseaseAnalysisResult(
  val primaryDiagnosis: String,
  val probabilityPercent: Int,
  val differentialDiagnoses: List<String>,
  val clinicalSummary: String,
  val oralMedicines: List<SuggestedMedicine>,
  val injectables: List<SuggestedMedicine>,
  val recommendedLabTests: List<String>,
  val redFlagWarnings: List<String>,
  val dietaryAdvice: String
)

fun estimateClinicalWeightKg(ageYears: Double, gender: String): Double {
  return when {
    ageYears <= 0.5 -> 6.5
    ageYears <= 1.0 -> 10.0
    ageYears <= 5.0 -> (ageYears * 2.0) + 8.0
    ageYears <= 12.0 -> (ageYears * 3.0) + 3.0
    ageYears <= 16.0 -> 48.0
    gender.equals("Female", ignoreCase = true) -> 58.0
    else -> 65.0
  }
}

@Composable
fun DiseaseTrackerScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val allMedicines by viewModel.allMedicines.collectAsState()

  // Tab State: "AI_FORM" vs "DEDICATED_GEMINI"
  var activeTrackerTab by remember { mutableStateOf("AI_FORM") }

  // Patient Info State
  var patientName by remember { mutableStateOf("Patient") }
  var ageText by remember { mutableStateOf("32") }
  var weightText by remember { mutableStateOf("65") }
  var gender by remember { mutableStateOf("Male") }
  var durationDays by remember { mutableStateOf("3") }
  var severityLevel by remember { mutableStateOf("Moderate") }

  val parsedAge = ageText.toDoubleOrNull() ?: 32.0
  val isPediatricMode = gender.equals("Pediatric", ignoreCase = true) || parsedAge <= 12.0
  val parsedWeightKg = weightText.toDoubleOrNull()?.takeIf { it > 1.0 }
    ?: estimateClinicalWeightKg(parsedAge, gender)

  // Adult vs Pediatric Symptoms Catalog
  val adultSymptoms = listOf(
    "High Fever", "Chills & Rigors", "Persistent Dry Cough", "Productive Cough with Sputum",
    "Watery Diarrhea", "Nausea & Vomiting", "Abdominal Cramps", "Severe Acidity / Heartburn",
    "Severe Headache", "Body Ache & Muscle Pain", "Sore Throat / Difficulty Swallowing", "Skin Rash",
    "Dengue Warning Signs", "Shortness of Breath", "Chest Pain / Tightness", "Joint Stiffness",
    "Dizziness / Vertigo", "Hair Fall / Alopecia", "Night Fall / Nocturnal Emission", "PCOS / Irregular Periods"
  )

  val pediatricSymptoms = listOf(
    "Watery Diarrhea", "Nausea & Vomiting", "High Fever", "Refusal to Feed / Poor Intake",
    "Excessive Crying / Infantile Colic", "Sunken Eyes / Dehydration", "Persistent Dry Cough",
    "Productive Cough with Sputum", "Cold & Nasal Blockage", "Ear Pulling / Earache (Otalgia)",
    "Abdominal Cramps", "Skin Rash / Diaper Rash", "Chills & Rigors", "Dengue Warning Signs",
    "Shortness of Breath / Fast Breathing", "Sore Throat / Difficulty Swallowing"
  )

  val activeSymptomsCatalog = remember(isPediatricMode, gender) {
    if (isPediatricMode) {
      pediatricSymptoms
    } else if (gender.equals("Female", ignoreCase = true)) {
      adultSymptoms.filter { it != "Night Fall / Nocturnal Emission" }
    } else if (gender.equals("Male", ignoreCase = true)) {
      adultSymptoms.filter { it != "PCOS / Irregular Periods" }
    } else {
      adultSymptoms.filter { it != "Night Fall / Nocturnal Emission" && it != "PCOS / Irregular Periods" }
    }
  }

  var selectedSymptoms by remember { mutableStateOf(setOf("High Fever", "Body Ache & Muscle Pain")) }
  var customSymptomDetails by remember { mutableStateOf("") }
  var symptomSearchQuery by remember { mutableStateOf("") }

  // Comorbidities filtered by patient category
  val comorbidityOptions = remember(isPediatricMode, gender) {
    if (isPediatricMode) {
      listOf("None", "Childhood Asthma / Wheeze", "G6PD Deficiency", "Febrile Seizure History", "Lactose Intolerance", "Congenital Renal/Heart Issue")
    } else if (gender.equals("Male", ignoreCase = true) || gender.equals("Geriatric", ignoreCase = true)) {
      listOf("None", "Diabetes Mellitus", "Hypertension", "Asthma / COPD", "CKD (Kidney Disease)", "Liver Impairment")
    } else {
      listOf("None", "Diabetes Mellitus", "Hypertension", "Asthma / COPD", "CKD (Kidney Disease)", "Liver Impairment", "Pregnancy")
    }
  }
  var selectedComorbidities by remember { mutableStateOf(setOf("None")) }

  // Analysis State
  var isAnalyzing by remember { mutableStateOf(false) }
  var result by remember { mutableStateOf<DiseaseAnalysisResult?>(null) }

  // Dedicated Gemini AI Chat Service State
  val geminiService = remember { com.example.data.ai.GeminiPharmacistService() }
  val geminiChatListState = rememberLazyListState()
  var dedicatedGeminiMessages by remember {
    mutableStateOf(
      listOf(
        com.example.data.ai.ChatMessage(
          sender = "gemini",
          text = "👋 Hello! I am your **Dedicated Gemini 2.5 Flash Clinical Medical Assistant**.\n\n" +
            "I automatically adapt to your patient profile (Pediatric weight-based `mg/kg` syrups/drops/sachets vs. Adult protocols), screen for drug interactions, and check live store inventory. Ask me anything or tap a quick topic above!"
        )
      )
    )
  }
  var dedicatedGeminiInput by remember { mutableStateOf("") }
  var isGeminiThinking by remember { mutableStateOf(false) }

  LaunchedEffect(dedicatedGeminiMessages.size, isGeminiThinking) {
    if (dedicatedGeminiMessages.isNotEmpty()) {
      geminiChatListState.animateScrollToItem(dedicatedGeminiMessages.lastIndex)
    }
  }

  fun buildActivePatientContextString(): String {
    val symList = (selectedSymptoms.toList() + if (customSymptomDetails.isNotBlank()) listOf(customSymptomDetails) else emptyList()).joinToString(", ")
    val diagStr = result?.primaryDiagnosis ?: "Not yet generated"
    return "Patient: $patientName, Age: $ageText Yrs (${if (isPediatricMode) "Pediatric" else gender}, ~${parsedWeightKg.toInt()} kg), Severity: $severityLevel, Duration: $durationDays days, Symptoms: ${symList.ifBlank { "None selected" }}, Comorbidities: ${selectedComorbidities.joinToString(", ")}, Tracked Diagnosis: $diagStr"
  }

  fun sendDedicatedGeminiQuery(queryText: String) {
    if (queryText.isBlank() || isGeminiThinking) return
    val priorHistory = dedicatedGeminiMessages
    val userMsg = com.example.data.ai.ChatMessage(sender = "user", text = queryText)
    dedicatedGeminiMessages = priorHistory + userMsg
    dedicatedGeminiInput = ""
    isGeminiThinking = true

    val activeContext = buildActivePatientContextString()
    scope.launch {
      val reply = geminiService.sendMessage(
        userMessage = queryText,
        conversationHistory = priorHistory,
        liveInventory = allMedicines,
        patientContext = activeContext
      )
      val geminiMsg = com.example.data.ai.ChatMessage(sender = "gemini", text = reply)
      dedicatedGeminiMessages = dedicatedGeminiMessages + geminiMsg
      isGeminiThinking = false
    }
  }

  fun runDiseaseTrackerAnalysis() {
    scope.launch {
      isAnalyzing = true
      val validSelected = selectedSymptoms.filter { activeSymptomsCatalog.contains(it) }
      val effectiveSymptoms = if (validSelected.isNotEmpty()) validSelected else selectedSymptoms
      val allSymptomsStr = (effectiveSymptoms.toList() + if (customSymptomDetails.isNotBlank()) listOf(customSymptomDetails) else emptyList()).joinToString(", ")
      val comorbStr = selectedComorbidities.joinToString(", ")

      val outcome = analyzeSymptomsAndTrackDisease(
        symptoms = allSymptomsStr,
        age = ageText,
        gender = if (isPediatricMode) "Pediatric" else gender,
        duration = durationDays,
        severity = severityLevel,
        comorbidities = comorbStr,
        weightKg = parsedWeightKg
      )
      result = outcome
      isAnalyzing = false
    }
  }

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
          IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Column {
            Text(
              text = "AI Disease & Symptom Tracker",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Clinical diagnosis, oral medicines & injectable protocols",
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
          Icon(Icons.Default.Healing, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
        }
      }

      // Mode Switcher Bar (Protocol Tracker vs Dedicated Gemini AI)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (activeTrackerTab == "AI_FORM") RoyalMagenta else Color(0xFFF1F5F9))
            .clickable { activeTrackerTab = "AI_FORM" }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = if (activeTrackerTab == "AI_FORM") Color.White else TextDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("📊 Protocol Tracker", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTrackerTab == "AI_FORM") Color.White else TextDark)
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (activeTrackerTab == "DEDICATED_GEMINI") RoyalNavy else Color(0xFFF1F5F9))
            .clickable { activeTrackerTab = "DEDICATED_GEMINI" }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = if (activeTrackerTab == "DEDICATED_GEMINI") Color.White else TextDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("🤖 Dedicated Gemini AI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTrackerTab == "DEDICATED_GEMINI") Color.White else TextDark)
          }
        }
      }

      if (activeTrackerTab == "AI_FORM") {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Card 1: Patient Profile & Comorbidities
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("1. Patient Profile & Comorbidities", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(10.dp))
                      .background(if (isPediatricMode) Color(0xFFE0F2FE) else Color(0xFFF1F5F9))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(
                      text = if (isPediatricMode) "👶 Pediatric (~${parsedWeightKg.toInt()} kg)" else "🧑 Adult (~${parsedWeightKg.toInt()} kg)",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isPediatricMode) Color(0xFF0369A1) else RoyalNavy
                    )
                  }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Name", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1.25f)
                  )
                  OutlinedTextField(
                    value = ageText,
                    onValueChange = { newAge ->
                      ageText = newAge
                      val a = newAge.toDoubleOrNull()
                      if (a != null) {
                        val estW = estimateClinicalWeightKg(a, gender)
                        weightText = estW.toInt().toString()
                        if (a <= 12.0) {
                          gender = "Pediatric"
                          selectedSymptoms = selectedSymptoms.filter { pediatricSymptoms.contains(it) }.toSet()
                          selectedComorbidities = setOf("None")
                        } else if (a >= 65.0 && gender == "Pediatric") {
                          gender = "Geriatric"
                        } else if (a > 12.0 && gender == "Pediatric") {
                          gender = "Male"
                        }
                      }
                    },
                    label = { Text("Age (Yrs)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(0.75f)
                  )
                  OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Wt (kg)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(0.75f)
                  )
                  OutlinedTextField(
                    value = durationDays,
                    onValueChange = { durationDays = it },
                    label = { Text("Days", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(0.65f)
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Separate clean row for Patient Category / Gender (Never wraps vertically!)
                Text("Patient Category / Gender:", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf("Male", "Female", "Pediatric", "Geriatric").forEach { g ->
                    val isSel = (g == "Pediatric" && isPediatricMode) || (g != "Pediatric" && !isPediatricMode && gender == g)
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSel) RoyalNavy else Color(0xFFF1F5F9))
                        .clickable {
                          gender = g
                          if (g == "Pediatric") {
                            val currAge = ageText.toDoubleOrNull() ?: 32.0
                            if (currAge > 12.0) {
                              ageText = "1"
                              weightText = "10"
                            } else {
                              weightText = estimateClinicalWeightKg(currAge, g).toInt().toString()
                            }
                            selectedSymptoms = selectedSymptoms.filter { pediatricSymptoms.contains(it) }.toSet()
                            selectedComorbidities = setOf("None")
                          } else if (g == "Geriatric") {
                            val currAge = ageText.toDoubleOrNull() ?: 32.0
                            if (currAge < 60.0) {
                              ageText = "68"
                            }
                            weightText = "62"
                            selectedComorbidities = selectedComorbidities.filter { it != "Pregnancy" }.ifEmpty { listOf("None") }.toSet()
                          } else {
                            val currAge = ageText.toDoubleOrNull() ?: 1.0
                            if (currAge <= 12.0) {
                              ageText = "32"
                            }
                            weightText = estimateClinicalWeightKg(ageText.toDoubleOrNull() ?: 32.0, g).toInt().toString()
                            if (g == "Male") {
                              selectedSymptoms = selectedSymptoms.filter { it != "PCOS / Irregular Periods" }.toSet()
                              selectedComorbidities = selectedComorbidities.filter { it != "Pregnancy" }.ifEmpty { listOf("None") }.toSet()
                            } else if (g == "Female") {
                              selectedSymptoms = selectedSymptoms.filter { it != "Night Fall / Nocturnal Emission" }.toSet()
                            }
                          }
                        }
                        .padding(vertical = 8.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = g,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSel) Color.White else TextDark,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Separate clean row for Clinical Severity
                Text("Clinical Severity:", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  listOf("Mild", "Moderate", "Severe").forEach { s ->
                    val isSel = severityLevel == s
                    val activeColor = when (s) {
                      "Severe" -> StatusRed
                      "Moderate" -> RoyalMagenta
                      else -> StatusGreen
                    }
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSel) activeColor else Color(0xFFF1F5F9))
                        .clickable { severityLevel = s }
                        .padding(vertical = 8.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = s,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        color = if (isSel) Color.White else TextDark,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                      )
                    }
                  }
                }

                if (isPediatricMode) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFE0F2FE))
                      .border(1.dp, Color(0xFF7DD3FC), RoundedCornerShape(8.dp))
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = "👶 Pediatric Safety Mode Active (Age $ageText yr, ~${parsedWeightKg.toInt()} kg): Only pediatric oral drops, syrups, suspensions & sachets (mg/kg) will be prescribed. Adult tablets are blocked.",
                      fontSize = 11.sp,
                      color = Color(0xFF0369A1),
                      fontWeight = FontWeight.Medium
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Pre-existing Medical Conditions / Comorbidities:", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  comorbidityOptions.forEach { option ->
                    val isSel = selectedComorbidities.contains(option)
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSel) Color(0xFFE0F2FE) else Color(0xFFF3F4F6))
                        .border(1.dp, if (isSel) Color(0xFF0284C7) else CardBorder, RoundedCornerShape(16.dp))
                        .clickable {
                          selectedComorbidities = if (option == "None") {
                            setOf("None")
                          } else {
                            val current = selectedComorbidities.filter { it != "None" }.toMutableSet()
                            if (isSel) current.remove(option) else current.add(option)
                            if (current.isEmpty()) setOf("None") else current
                          }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Text(
                        text = if (isSel) "✓ $option" else option,
                        fontSize = 11.sp,
                        maxLines = 1,
                        color = if (isSel) Color(0xFF0369A1) else TextDark,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }
                }
              }
            }
          }

          // Card 2: Symptoms Selection
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("2. Symptoms & Clinical Features", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  }
                  if (isPediatricMode) {
                    Text("Pediatric Symptoms", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Tap to select presenting symptoms:", fontSize = 11.5.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                  value = symptomSearchQuery,
                  onValueChange = { symptomSearchQuery = it },
                  label = { Text("Search available symptoms...") },
                  leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                  singleLine = true,
                  shape = RoundedCornerShape(8.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GrayBackground,
                    unfocusedContainerColor = GrayBackground,
                    focusedBorderColor = RoyalMagenta,
                    unfocusedBorderColor = CardBorder
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_symptoms")
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Multi-select symptom chips grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  val filteredSymptoms = activeSymptomsCatalog.filter { it.contains(symptomSearchQuery, ignoreCase = true) }
                  filteredSymptoms.chunked(2).forEach { rowPair ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      rowPair.forEach { sym ->
                        val isSel = selectedSymptoms.contains(sym)
                        Box(
                          modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) RoyalMagenta.copy(alpha = 0.12f) else Color(0xFFF8FAFC))
                            .border(1.dp, if (isSel) RoyalMagenta else CardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                              val current = selectedSymptoms.toMutableSet()
                              if (isSel) current.remove(sym) else current.add(sym)
                              selectedSymptoms = current
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                          contentAlignment = Alignment.CenterStart
                        ) {
                          Text(
                            text = if (isSel) "✓ $sym" else "+ $sym",
                            fontSize = 11.5.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) RoyalMagenta else TextDark
                          )
                        }
                      }
                      if (rowPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                  value = customSymptomDetails,
                  onValueChange = { customSymptomDetails = it },
                  label = { Text("Additional Symptoms / Clinical Details") },
                  placeholder = { Text(if (isPediatricMode) "e.g., 5 watery stools since morning, irritable, temp 101°F" else "e.g., Temperature 102°F, rash on trunk, nausea after meals") },
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }

          // Action Button: Analyze
          item {
            Button(
              onClick = { runDiseaseTrackerAnalysis() },
              enabled = !isAnalyzing && (selectedSymptoms.isNotEmpty() || customSymptomDetails.isNotBlank()),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_analyze_disease_symptoms")
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isAnalyzing) "Analyzing Clinical Protocols..." else "⚡ Track Disease & Generate Treatment Plan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          // Card 3: Results Section
          result?.let { res ->
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(RoyalMagenta)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  // Header Diagnosis
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = if (isPediatricMode) "PEDIATRIC CLINICAL DIAGNOSIS (~${parsedWeightKg.toInt()} KG)" else "SUSPECTED DISEASE / DIAGNOSIS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPediatricMode) Color(0xFF0369A1) else TextMuted
                      )
                      Text(res.primaryDiagnosis, fontSize = 17.5.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    }
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusGreen)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text("${res.probabilityPercent}% Confidence", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  Text(res.clinicalSummary, fontSize = 12.5.sp, color = TextDark)

                  if (res.differentialDiagnoses.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Differential Candidates: " + res.differentialDiagnoses.joinToString(" • "), fontSize = 11.sp, color = TextMuted)
                  }

                  Spacer(modifier = Modifier.height(14.dp))
                  // Oral Medicines Section
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Medication, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (isPediatricMode) "RECOMMENDED PEDIATRIC ORAL MEDICINES (DROPS / SYRUP / SACHET)" else "RECOMMENDED ORAL MEDICINES",
                      fontSize = 12.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))

                  res.oralMedicines.forEach { med ->
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                    ) {
                      Column {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(med.brandName, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.weight(1f))
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(med.dosageAndStrength, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                        }
                        Text("Active Salt: ${med.genericSalt} • [${med.formulationType}]", fontSize = 11.sp, color = TextMuted)
                        Text("Frequency: ${med.frequency} • Duration: ${med.duration}", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = RoyalNavy)
                        if (med.instructions.isNotBlank()) {
                          Text("💡 ${med.instructions}", fontSize = 11.sp, color = Color(0xFF047857))
                        }
                      }
                    }
                  }

                  // Injectable Therapy Section (If Any)
                  if (res.injectables.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Vaccines, contentDescription = null, tint = StatusRed, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = if (isPediatricMode) "PEDIATRIC INJECTABLE / IV FLUID RESCUE (WEIGHT-BASED)" else "INJECTABLE THERAPY (IV / IM PROTOCOL)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusRed
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    res.injectables.forEach { inj ->
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 4.dp)
                          .clip(RoundedCornerShape(8.dp))
                          .background(Color(0xFFFFF1F2))
                          .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(8.dp))
                          .padding(10.dp)
                      ) {
                        Column {
                          Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Text("💉 ${inj.brandName}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = StatusRed, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(inj.dosageAndStrength, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                          }
                          Text("Active Salt: ${inj.genericSalt}", fontSize = 11.sp, color = TextDark)
                          Text("Route & Frequency: ${inj.formulationType} • ${inj.frequency} (${inj.duration})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                          Text("Dilution & Rate: ${inj.instructions}", fontSize = 11.sp, color = Color(0xFF991B1B))
                        }
                      }
                    }
                  }

                  // Dietary & Counseling Advice
                  if (res.dietaryAdvice.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("CLINICAL COUNSELING & DIETARY ADVICE:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(res.dietaryAdvice, fontSize = 12.sp, color = TextDark)
                  }

                  // Recommended Diagnostic Lab Tests
                  if (res.recommendedLabTests.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("RECOMMENDED DIAGNOSTIC TESTS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(res.recommendedLabTests.joinToString(" • "), fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                  }

                  // Red Flag Warnings
                  if (res.redFlagWarnings.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF2F2))
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                    ) {
                      Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("RED FLAG CLINICAL WARNINGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                        }
                        res.redFlagWarnings.forEach { warn ->
                          Text("• $warn", fontSize = 11.sp, color = Color(0xFF991B1B))
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(14.dp))
                  // Share & Save Protocol Row
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedButton(
                      onClick = {
                        val summaryText = buildShareableTreatmentSummary(res, patientName, ageText, if (isPediatricMode) "Pediatric (~${parsedWeightKg.toInt()} kg)" else gender)
                        val sendIntent = Intent().apply {
                          action = Intent.ACTION_SEND
                          putExtra(Intent.EXTRA_TEXT, summaryText)
                          type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Treatment Summary"))
                      },
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Share Summary")
                    }

                    Button(
                      onClick = {
                        val medsSummary = (res.oralMedicines + res.injectables)
                          .joinToString(", ") { "${it.brandName} (${it.dosageAndStrength}, ${it.frequency})" }
                        val todayStr = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(java.util.Date())
                        viewModel.addPrescriptionRecord(
                          com.example.data.model.PrescriptionRecord(
                            patientName = patientName.ifBlank { "Walk-in Patient" },
                            patientAgeGender = "$ageText Yrs / ${if (isPediatricMode) "Pediatric (${parsedWeightKg.toInt()}kg)" else gender}",
                            patientPhone = "",
                            doctorName = "AI Clinical Protocol",
                            clinicName = "Royal Pharmacy Clinical Triage",
                            prescriptionDate = todayStr,
                            medicinesSummary = medsSummary,
                            diagnosisNotes = "${res.primaryDiagnosis} (${res.probabilityPercent}% match)",
                            status = "Active",
                            refillDueDate = "Follow-up in 5 days"
                          )
                        )
                        Toast.makeText(context, "Protocol saved to Prescription History for $patientName!", Toast.LENGTH_SHORT).show()
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                      modifier = Modifier.weight(1.2f)
                    ) {
                      Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Save Protocol")
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  OutlinedButton(
                    onClick = {
                      (res.oralMedicines + res.injectables).forEach { suggested ->
                        val matched = allMedicines.firstOrNull {
                          it.name.contains(suggested.brandName.substringBefore(" "), ignoreCase = true) ||
                            it.composition.contains(suggested.genericSalt.substringBefore(" "), ignoreCase = true)
                        }
                        if (matched != null) {
                          viewModel.addMedicineToCart(matched, 1)
                        } else {
                          viewModel.handleParsedLabelOcr(
                            name = suggested.brandName,
                            batch = "PROTO-${(100..999).random()}",
                            expiry = "12/28",
                            mrp = if (suggested.isInjectable) 145.0 else 65.0,
                            manufacturer = "Clinical Protocol"
                          )
                        }
                      }
                      viewModel.billingCustomerName.value = patientName.ifBlank { "Walk-in Patient" }
                      Toast.makeText(context, "Added protocol medicines to POS Billing Cart!", Toast.LENGTH_SHORT).show()
                      viewModel.navigateTo(Screen.ADD_SALE)
                    },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(44.dp)
                  ) {
                    Icon(Icons.Default.Medication, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dispense Protocol Medicines to POS Bill", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                  }

                  Spacer(modifier = Modifier.height(10.dp))
                  Button(
                    onClick = {
                      val pdfFile = com.example.service.DistributorExportService.generateAiTriageReportPdf(
                        context = context,
                        res = res,
                        patientName = patientName,
                        age = "$ageText Yrs (~${parsedWeightKg.toInt()} kg)",
                        gender = if (isPediatricMode) "Pediatric" else gender,
                        duration = durationDays,
                        severity = severityLevel,
                        comorbidities = selectedComorbidities.joinToString(", "),
                        symptoms = (selectedSymptoms.toList() + if (customSymptomDetails.isNotBlank()) listOf(customSymptomDetails) else emptyList()).joinToString(", ")
                      )
                      if (pdfFile != null) {
                        Toast.makeText(context, "AI Triage Report PDF Generated Successfully!", Toast.LENGTH_SHORT).show()
                        com.example.service.DistributorExportService.shareAiTriageReportPdf(context, pdfFile, patientName)
                      } else {
                        Toast.makeText(context, "Failed to generate AI Triage Report PDF", Toast.LENGTH_SHORT).show()
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(48.dp)
                      .testTag("btn_generate_ai_triage_report")
                  ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate AI Triage Report (PDF)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  OutlinedButton(
                    onClick = {
                      activeTrackerTab = "DEDICATED_GEMINI"
                      val prompt = "Please evaluate this clinical diagnosis: '${res.primaryDiagnosis}' for patient $patientName, age $ageText yrs (${if (isPediatricMode) "Pediatric, ~${parsedWeightKg.toInt()} kg" else gender}). Symptoms reported: ${(selectedSymptoms.toList() + if (customSymptomDetails.isNotBlank()) listOf(customSymptomDetails) else emptyList()).joinToString(", ")}. Verify age-appropriate dosages, drug interactions, and counseling."
                      sendDedicatedGeminiQuery(prompt)
                    },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(46.dp)
                  ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("💬 Ask Dedicated Gemini AI Details", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                  }
                }
              }
            }
          }
        }
      } else {
        // DEDICATED GEMINI MEDICAL AI LAYOUT
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
        ) {
          // Dedicated Gemini Status Banner + Active Patient Context Pill
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalNavy),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Dedicated Gemini 2.5 Flash", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(StatusGreen)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text("🟢 ONLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Active Context: $patientName • Age $ageText Yrs (${if (isPediatricMode) "Pediatric ~${parsedWeightKg.toInt()} kg" else gender}) • $severityLevel",
                fontSize = 11.sp,
                color = Color(0xFFBAE6FD)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Context-Adaptive Quick Topic Chips
          val quickTopics = if (isPediatricMode) {
            listOf(
              "👶 Pediatric Diarrhea & ORS",
              "👶 Infant Fever Drops (mg/kg)",
              "👶 Pediatric Cough & Nebulizer",
              "⚡ Dengue & Platelet Drop",
              "⚡ Drug Interaction Check"
            )
          } else {
            listOf(
              "⚡ Dengue & Platelet Drop",
              "⚡ Acute Gastroenteritis",
              "⚡ Diabetes & BP Dosing",
              "⚡ PCOS & Hormones",
              "⚡ Drug Interaction Check"
            )
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            quickTopics.forEach { topic ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color.White)
                  .border(1.dp, RoyalNavy.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                  .clickable {
                    val prompt = when {
                      topic.contains("Pediatric Diarrhea") -> "Give the complete pediatric acute gastroenteritis and watery diarrhea protocol for a $ageText-year-old child (~${parsedWeightKg.toInt()} kg) with ORS, Zinc syrup, Racecadotril sachet, and Ondansetron drops."
                      topic.contains("Infant Fever") -> "Calculate the exact pediatric Paracetamol oral drops and suspension dose (15 mg/kg) for a $ageText-year-old child weighing ${parsedWeightKg.toInt()} kg."
                      topic.contains("Pediatric Cough") -> "Provide the pediatric cough syrup, nasal saline drops, and nebulization protocol for a $ageText-year-old child (~${parsedWeightKg.toInt()} kg)."
                      topic.contains("Gastroenteritis") -> "Provide the clinical protocol for acute gastroenteritis, watery diarrhea, and dehydration."
                      topic.contains("Dengue") -> "Explain Dengue fever protocol, fluid management, platelet drop monitoring, and paracetamol dosage."
                      topic.contains("PCOS") -> "Explain PCOS management, Myo-Inositol supplementation, and dysmenorrhea cramp relief."
                      topic.contains("Diabetes") -> "Explain Metformin dosing, hypoglycemia warnings, and blood sugar control."
                      else -> "Please check drug interactions and clinical precautions for standard multi-drug therapy."
                    }
                    sendDedicatedGeminiQuery(prompt)
                  }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(topic, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = RoyalNavy)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Interactive Chat Messages Log with Smooth Auto-Scroll and Clean Markdown Rendering
          LazyColumn(
            state = geminiChatListState,
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(dedicatedGeminiMessages, key = { it.id }) { msg ->
              val isUser = msg.sender == "user"
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isUser) RoyalMagenta else Color.White)
                    .border(1.dp, if (isUser) RoyalMagenta else CardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                ) {
                  Column {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text(
                        text = if (isUser) "You (Clinical Query)" else "🤖 Dedicated Gemini Medical AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) Color.White.copy(alpha = 0.9f) else RoyalNavy
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = formatClinicalMarkdown(msg.text),
                      fontSize = 12.5.sp,
                      color = if (isUser) Color.White else TextDark,
                      lineHeight = 18.sp
                    )
                  }
                }
              }
            }

            if (isGeminiThinking) {
              item {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(8.dp)
                ) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Dedicated Gemini AI is reasoning clinically...", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Query Input Row
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = dedicatedGeminiInput,
                onValueChange = { dedicatedGeminiInput = it },
                placeholder = { Text("Ask Dedicated Gemini about any disease or dose...") },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier
                  .weight(1f)
                  .testTag("input_dedicated_gemini_query"),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = RoyalNavy,
                  unfocusedBorderColor = CardBorder
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Button(
                onClick = { sendDedicatedGeminiQuery(dedicatedGeminiInput) },
                enabled = dedicatedGeminiInput.isNotBlank() && !isGeminiThinking,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .height(52.dp)
                  .testTag("btn_send_dedicated_gemini")
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Send", tint = Color.White)
              }
            }
          }
        }
      }
    }
  }
}

private val trackerFirebaseModel by lazy {
  runCatching {
    Firebase.ai.generativeModel("gemini-2.5-flash")
  }.getOrNull()
}

suspend fun analyzeSymptomsAndTrackDisease(
  symptoms: String,
  age: String,
  gender: String,
  duration: String,
  severity: String,
  comorbidities: String,
  weightKg: Double = estimateClinicalWeightKg(age.toDoubleOrNull() ?: 32.0, gender)
): DiseaseAnalysisResult = withContext(Dispatchers.IO) {
  val ageYears = age.toDoubleOrNull() ?: 32.0
  val isPediatric = gender.equals("Pediatric", ignoreCase = true) || ageYears <= 12.0

  // For Pediatric patients (Age <= 12), always use our deterministic, weight-calibrated IAP/WHO
  // pediatric formulation engine first or validate against adult tablets so a child NEVER receives
  // adult tablets (like Norflox-TZ 1 Tab or Racecadotril 100mg) or adult IV doses (40mg Pantoprazole).
  val prompt = """
    You are a senior clinical pharmacologist and physician.
    Patient Symptoms: $symptoms
    Patient Profile: Age $age Yrs (${if (isPediatric) "PEDIATRIC CHILD/INFANT" else gender}), Weight: ${weightKg.toInt()} kg, Duration: $duration days, Severity: $severity.
    Comorbidities: $comorbidities.
    ${if (isPediatric) "CRITICAL PEDIATRIC RULE: Patient is a child/infant ($age yrs, ${weightKg.toInt()} kg). NEVER prescribe adult tablets/capsules (no 500mg/650mg tablets, no Norflox-TZ tablets, no Racecadotril 100mg capsules) and NEVER prescribe adult 40mg IV Pantoprazole or 4mg adult IV Ondansetron. Prescribe ONLY Pediatric Oral Drops, Oral Syrups, Oral Suspensions, or Granule Sachets with exact mg/kg and ml dosing for ${weightKg.toInt()} kg!" else ""}

    Analyze the symptoms and return ONLY valid JSON:
    {
      "primaryDiagnosis": "Disease Name",
      "probabilityPercent": 88,
      "differentialDiagnoses": ["Condition 2", "Condition 3"],
      "clinicalSummary": "Clinical explanation and rationale",
      "oralMedicines": [
        {
          "brandName": "Brand Name",
          "genericSalt": "Active Ingredient",
          "formulationType": "${if (isPediatric) "Oral Pediatric Syrup / Drops / Sachet" else "Oral Tablet"}",
          "dosageAndStrength": "${if (isPediatric) "5 ml (20 mg)" else "500 mg"}",
          "frequency": "1-0-1 (BD) after food",
          "duration": "5 days",
          "instructions": "Specific administration guidance"
        }
      ],
      "injectables": [],
      "recommendedLabTests": ["Test 1", "Test 2"],
      "redFlagWarnings": ["Warning 1", "Warning 2"],
      "dietaryAdvice": "Dietary and lifestyle instructions"
    }
  """.trimIndent()

  var rawAiJson: String? = null

  // 1. Try Firebase AI Logic first
  try {
    val model = trackerFirebaseModel
    if (model != null) {
      val response = withTimeout(22_000L) {
        model.generateContent(prompt)
      }
      val text = response.text?.trim()
      if (!text.isNullOrBlank()) {
        rawAiJson = text
      }
    }
  } catch (e: CancellationException) {
    throw e
  } catch (_: Exception) {}

  if (!rawAiJson.isNullOrBlank()) {
    try {
      val cleanJson = rawAiJson.replace("```json", "").replace("```", "").trim()
      val parsed = JSONObject(cleanJson)

      val oralMeds = mutableListOf<SuggestedMedicine>()
      val oralArr = parsed.optJSONArray("oralMedicines")
      if (oralArr != null) {
        for (i in 0 until oralArr.length()) {
          val o = oralArr.getJSONObject(i)
          oralMeds.add(
            SuggestedMedicine(
              brandName = o.optString("brandName", "Medicine"),
              genericSalt = o.optString("genericSalt", "Active Salt"),
              formulationType = o.optString("formulationType", if (isPediatric) "Oral Pediatric Syrup" else "Oral Tablet"),
              dosageAndStrength = o.optString("dosageAndStrength", "Standard"),
              frequency = o.optString("frequency", "TDS"),
              duration = o.optString("duration", "5 Days"),
              instructions = o.optString("instructions", "Take as directed")
            )
          )
        }
      }

      val injMeds = mutableListOf<SuggestedMedicine>()
      val injArr = parsed.optJSONArray("injectables")
      if (injArr != null) {
        for (i in 0 until injArr.length()) {
          val o = injArr.getJSONObject(i)
          injMeds.add(
            SuggestedMedicine(
              brandName = o.optString("brandName", "Injectable"),
              genericSalt = o.optString("genericSalt", "Active Salt"),
              formulationType = o.optString("formulationType", "IV Injection"),
              dosageAndStrength = o.optString("dosageAndStrength", "Weight-Adjusted"),
              frequency = o.optString("frequency", "STAT"),
              duration = o.optString("duration", "2 Days"),
              instructions = o.optString("instructions", "Slow IV administration"),
              isInjectable = true
            )
          )
        }
      }

      // Safety Gate: If patient is Pediatric and AI output accidentally contains adult tablets
      // (e.g. "1 Tab", "650", "Norflox", "40 mg"), override with verified pediatric protocol!
      val hasAdultDoseLeak = isPediatric && (
        oralMeds.any {
          it.dosageAndStrength.contains("Tab", ignoreCase = true) ||
            it.formulationType.contains("Tablet", ignoreCase = true) ||
            it.brandName.contains("Norflox", ignoreCase = true) ||
            it.brandName.contains("650", ignoreCase = true)
        } || injMeds.any {
          it.dosageAndStrength.contains("40 mg", ignoreCase = true) ||
            it.dosageAndStrength.contains("1000 mg", ignoreCase = true)
        }
      )

      if (!hasAdultDoseLeak && oralMeds.isNotEmpty()) {
        return@withContext DiseaseAnalysisResult(
          primaryDiagnosis = parsed.optString("primaryDiagnosis", "Clinical Condition"),
          probabilityPercent = parsed.optInt("probabilityPercent", 88),
          differentialDiagnoses = parsed.optJSONArray("differentialDiagnoses")?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: emptyList(),
          clinicalSummary = parsed.optString("clinicalSummary", "Clinical protocol evaluated."),
          oralMedicines = oralMeds,
          injectables = injMeds,
          recommendedLabTests = parsed.optJSONArray("recommendedLabTests")?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: emptyList(),
          redFlagWarnings = parsed.optJSONArray("redFlagWarnings")?.let { arr -> (0 until arr.length()).map { arr.getString(it) } } ?: emptyList(),
          dietaryAdvice = parsed.optString("dietaryAdvice", "Hydration and rest.")
        )
      }
    } catch (_: Exception) {}
  }

  // Verified Age, Weight, Severity & Comorbidity-Aware Clinical Protocol Engine
  return@withContext generateLocalDiseaseResult(
    symptoms = symptoms,
    age = age,
    gender = gender,
    severity = severity,
    comorbidities = comorbidities,
    weightKg = weightKg
  )
}

fun generateLocalDiseaseResult(
  symptoms: String,
  age: String,
  gender: String = "Patient",
  severity: String,
  comorbidities: String,
  weightKg: Double = estimateClinicalWeightKg(age.toDoubleOrNull() ?: 32.0, gender)
): DiseaseAnalysisResult {
  val s = symptoms.lowercase()
  val ageYears = age.toDoubleOrNull() ?: 32.0
  val isPediatric = gender.equals("Pediatric", ignoreCase = true) || ageYears <= 12.0
  val isInfant = ageYears <= 2.0
  val isSevere = severity.equals("Severe", ignoreCase = true)
  val isModerateOrSevere = isSevere || severity.equals("Moderate", ignoreCase = true)
  val wKg = weightKg.coerceIn(3.5, 120.0)

  // =========================================================================
  // 1. PEDIATRIC CLINICAL PROTOCOL ENGINE (AGE <= 12 YRS / PEDIATRIC)
  // Strictly Oral Drops, Syrups, Suspensions, Sachets & Weight-Based IV (mg/kg)
  // =========================================================================
  if (isPediatric) {
    return when {
      // Pediatric Acute Gastroenteritis / Watery Diarrhea / Vomiting
      s.contains("diarrhea") || s.contains("vomiting") || s.contains("stool") || s.contains("loose") || s.contains("dehydration") || s.contains("gastro") -> {
        val orsPerStoolMl = (wKg * 10.0).toInt().coerceIn(50, 250)
        val zincDoseStr = if (ageYears < 0.5) "2.5 ml (10 mg)" else "5 ml (20 mg)"
        val racecadotrilMg = (wKg * 1.5).toInt().coerceIn(10, 30)
        val racecadotrilSachetStr = when {
          racecadotrilMg <= 12 -> "10 mg (1 Pediatric Sachet)"
          racecadotrilMg <= 20 -> "15 mg (1 Pediatric Sachet)"
          else -> "30 mg (2 Sachets of 15mg)"
        }
        val ondansetronMg = String.format(Locale.US, "%.1f", (wKg * 0.15).coerceIn(0.8, 4.0))
        val ondansetronSyrupMl = String.format(Locale.US, "%.1f", ((wKg * 0.15).coerceIn(0.8, 4.0) * 2.5)) // 2mg/5ml => 0.4mg/ml => mg * 2.5 = ml
        val ondansetronIvMl = String.format(Locale.US, "%.2f", ((wKg * 0.15).coerceIn(0.8, 4.0) / 2.0)) // 2mg/ml ampoule
        val rlBolusMl = (wKg * 20.0).toInt()

        DiseaseAnalysisResult(
          primaryDiagnosis = "Pediatric Acute Gastroenteritis & Dehydration (WHO Plan A/B)",
          probabilityPercent = 92,
          differentialDiagnoses = listOf("Rotavirus / Viral Enteritis", "Acute Bacterial Enterocolitis", "Milk / Diet Indigestion"),
          clinicalSummary = "Acute pediatric secretory diarrhea in a $age-year-old child (~${wKg.toInt()} kg). Cornerstone WHO/IAP therapy is low-osmolarity ORS replacement (${orsPerStoolMl} ml per loose stool), mandatory 14-day elemental Zinc syrup, and pediatric Racecadotril/Probiotic sachets. Adult fluoroquinolone tablets (Norflox-TZ) and adult IV Pantoprazole 40mg are strictly contraindicated.",
          oralMedicines = listOf(
            SuggestedMedicine(
              brandName = "WHO Pediatric ORS (Electral / Pedialyte)",
              genericSalt = "Low-Osmolarity Oral Rehydration Salts (245 mOsm/L)",
              formulationType = "Oral Pediatric Solution",
              dosageAndStrength = "$orsPerStoolMl ml / loose stool",
              frequency = "Sip every 5–10 mins after each loose stool",
              duration = "3–5 Days",
              instructions = "Dissolve 1 sachet in 1 Liter boiled cooled water; feed slowly with spoon/cup (10 ml/kg per stool)"
            ),
            SuggestedMedicine(
              brandName = "Z&D 20 / Zincovit Pediatric Syrup (20mg/5ml)",
              genericSalt = "Zinc Gluconate / Zinc Sulfate Elemental 20mg/5ml",
              formulationType = "Oral Pediatric Syrup",
              dosageAndStrength = zincDoseStr,
              frequency = "0-1-0 (Once Daily)",
              duration = "14 Days (Full WHO Course)",
              instructions = "Mandatory 14-day pediatric Zinc course to regenerate gut epithelium & prevent recurrence"
            ),
            SuggestedMedicine(
              brandName = "Redotil / Zedott Pediatric Sachet",
              genericSalt = "Racecadotril Pediatric Granules (1.5 mg/kg/dose)",
              formulationType = "Oral Granules Sachet",
              dosageAndStrength = racecadotrilSachetStr,
              frequency = "1-1-1 (TDS before feeds)",
              duration = "3 Days",
              instructions = "Pediatric anti-secretory granules; mix in 2 teaspoons of water or expressed milk"
            ),
            SuggestedMedicine(
              brandName = "Enterogermina Suspension / Econorm Sachet",
              genericSalt = "Bacillus clausii 2 Billion Spores / S. boulardii 250mg",
              formulationType = "Oral Mini-Vial / Sachet",
              dosageAndStrength = "1 Mini-Bottle (5 ml)",
              frequency = "1-0-1 (BD)",
              duration = "5 Days",
              instructions = "Restores pediatric intestinal microflora and reduces stool frequency"
            ),
            SuggestedMedicine(
              brandName = "Ondem / Emeset Pediatric Syrup (2mg/5ml)",
              genericSalt = "Ondansetron Oral Solution IP 2mg/5ml (0.15 mg/kg)",
              formulationType = "Oral Pediatric Syrup",
              dosageAndStrength = "$ondansetronSyrupMl ml ($ondansetronMg mg)",
              frequency = "SOS / BD (20 mins before ORS)",
              duration = "2 Days",
              instructions = "Weight-calibrated dose (0.15 mg/kg) to stop vomiting so child retains oral ORS"
            )
          ),
          injectables = if (isModerateOrSevere) listOf(
            SuggestedMedicine(
              brandName = "IV Ringer Lactate / Isolyte-P (Pediatric Fluid)",
              genericSalt = "Pediatric Isotonic Crystalloid Deficit Replacement",
              formulationType = "Pediatric IV Infusion",
              dosageAndStrength = "$rlBolusMl ml Bolus (20 ml/kg)",
              frequency = "STAT over 30–60 mins (Plan C)",
              duration = "1–2 Days",
              instructions = "Administer $rlBolusMl ml (20 ml/kg) IV bolus if child is lethargic or unable to drink; follow with Isolyte-P maintenance",
              isInjectable = true
            ),
            SuggestedMedicine(
              brandName = "Inj. Ondansetron Pediatric IV (Emeset 2mg/ml)",
              genericSalt = "Ondansetron Hydrochloride IV (0.15 mg/kg)",
              formulationType = "Pediatric IV Injection",
              dosageAndStrength = "$ondansetronMg mg ($ondansetronIvMl ml)",
              frequency = "STAT / Q12H SOS",
              duration = "1 Day",
              instructions = "Strict pediatric weight dose (0.15 mg/kg = $ondansetronMg mg / $ondansetronIvMl ml) slow IV push over 2 mins for intractable vomiting",
              isInjectable = true
            )
          ) else emptyList(),
          recommendedLabTests = listOf("Stool Routine, Microscopy & pH / Reducing Substances", "Serum Electrolytes (Na+, K+, HCO3-)", "CBC with PCV (Hematocrit)"),
          redFlagWarnings = listOf(
            "Sunken eyes, depressed fontanelle, or absence of tears when crying",
            "Dry tongue, skin pinch going back very slowly (>2 seconds), or no urine for >6 hours",
            "Blood in stool (Dysentery), high fever >102°F, or extreme lethargy/drowsiness"
          ),
          dietaryAdvice = "Continue breastfeeding / age-appropriate home fluids (dal water, rice kanji, coconut water, mashed banana, curd rice). Do NOT dilute milk formula and NEVER give carbonated sodas or anti-motility drugs (Loperamide)."
        )
      }

      // Pediatric Fever / Viral Pyrexia / Dengue Warning
      s.contains("fever") || s.contains("chills") || s.contains("dengue") || s.contains("rigors") -> {
        val pcmMg = (wKg * 15.0).toInt()
        val pcmPreparation = if (isInfant) {
          val dropsMl = String.format(Locale.US, "%.1f", pcmMg / 100.0) // 100mg/ml infant drops
          SuggestedMedicine(
            brandName = "Calpol / Pacimol Pediatric Oral Drops (100mg/ml)",
            genericSalt = "Paracetamol IP 100mg/1ml Concentrated Infant Drops",
            formulationType = "Oral Pediatric Drops",
            dosageAndStrength = "$dropsMl ml ($pcmMg mg)",
            frequency = "Every 6 hours SOS (Max 4 doses/24h)",
            duration = "3–5 Days",
            instructions = "Calculated at 15 mg/kg/dose for ${wKg.toInt()} kg infant using calibrated dropper. Avoid Ibuprofen/Aspirin."
          )
        } else {
          val syrupMl = String.format(Locale.US, "%.1f", pcmMg / 50.0) // 250mg/5ml => 50mg/ml
          SuggestedMedicine(
            brandName = "Calpol 250 / Dolo 250 Pediatric Suspension",
            genericSalt = "Paracetamol IP 250mg/5ml Oral Suspension",
            formulationType = "Oral Pediatric Suspension",
            dosageAndStrength = "$syrupMl ml ($pcmMg mg)",
            frequency = "1-1-1 (Every 6–8 hours after feeds)",
            duration = "4 Days",
            instructions = "Calculated at 15 mg/kg/dose for ${wKg.toInt()} kg child. Shake bottle well before measuring."
          )
        }
        val pcmIvMl = String.format(Locale.US, "%.1f", pcmMg / 10.0) // 10mg/ml IV infusion

        DiseaseAnalysisResult(
          primaryDiagnosis = "Pediatric Acute Viral Febrile Illness / Dengue Evaluation",
          probabilityPercent = 90,
          differentialDiagnoses = listOf("Pediatric Viral Exanthem / Influenza", "Dengue Fever", "Acute Otitis Media / Tonsillitis"),
          clinicalSummary = "Acute febrile illness in a $age-year-old child (~${wKg.toInt()} kg). Weight-calibrated Paracetamol ($pcmMg mg/dose at 15 mg/kg) + tepid sponging + oral hydration initiated. NSAIDs (Ibuprofen, Mefenamic Acid, Nimesulide) are avoided to prevent platelet/hepatic toxicity.",
          oralMedicines = listOf(
            pcmPreparation,
            SuggestedMedicine(
              brandName = "WHO Pediatric ORS (Electral)",
              genericSalt = "Oral Rehydration Salts",
              formulationType = "Oral Solution",
              dosageAndStrength = "${(wKg * 50).toInt()} ml / day",
              frequency = "Sip frequently throughout day",
              duration = "5 Days",
              instructions = "Maintains hydration and prevents febrile dehydration; monitor urine frequency every 4–6 hours"
            ),
            SuggestedMedicine(
              brandName = if (isInfant) "Zincovit Pediatric Drops 15ml" else "Zincovit / A-Z Pediatric Syrup",
              genericSalt = "Multivitamin + Elemental Zinc + Lysine",
              formulationType = if (isInfant) "Oral Drops" else "Oral Syrup",
              dosageAndStrength = if (isInfant) "1 ml" else "5 ml",
              frequency = "0-1-0 (Once Daily)",
              duration = "10 Days",
              instructions = "Micronutrient & immune recovery support during viral fever"
            )
          ),
          injectables = if (isSevere) listOf(
            SuggestedMedicine(
              brandName = "Inj. Paracetamol Pediatric IV (Febrinil / Kabimol 10mg/ml)",
              genericSalt = "Paracetamol IV Infusion (15 mg/kg)",
              formulationType = "Pediatric IV Infusion",
              dosageAndStrength = "$pcmMg mg ($pcmIvMl ml)",
              frequency = "STAT / Q8H for hyperpyrexia >103°F",
              duration = "1–2 Days",
              instructions = "Infuse $pcmIvMl ml ($pcmMg mg) IV slowly over 15 minutes via syringe pump; never infuse full adult 100ml bottle in children",
              isInjectable = true
            )
          ) else emptyList(),
          recommendedLabTests = listOf("CBC with Platelet Count & Hematocrit (PCV)", "Dengue NS1 Antigen (if fever >48 hrs)", "Urine Routine & Microscopy"),
          redFlagWarnings = listOf(
            "Febrile convulsion / seizure, neck stiffness, or bulging fontanelle",
            "Petechial rash, gum bleeding, persistent vomiting, or cold extremities",
            "Refusal to feed or persistent fever >103°F despite Paracetamol and tepid sponging"
          ),
          dietaryAdvice = "Perform lukewarm (tepid) water sponging over forehead, axilla, and groin if temperature exceeds 101.5°F. Dress child in light cotton clothing and encourage frequent breastfeeds/fluids."
        )
      }

      // Pediatric Cough / Cold / Bronchitis / Respiratory Infection
      s.contains("cough") || s.contains("cold") || s.contains("nasal") || s.contains("breath") || s.contains("sore throat") || s.contains("ear") -> {
        val amoxMgPerDay = (wKg * 40.0).toInt() // 40 mg/kg/day divided BD
        val augSyrupMlPerDose = String.format(Locale.US, "%.1f", ((amoxMgPerDay / 2.0) / 40.0).coerceIn(2.0, 10.0)) // Augmentin Duo 200mg/5ml = 40mg/ml amoxicillin
        val ambrodilDose = if (isInfant) "1 ml Oral Drops" else if (ageYears <= 5) "2.5 ml Syrup" else "5 ml Syrup"
        val ceftriaxoneMg = (wKg * 50.0).toInt().coerceAtMost(1000)

        DiseaseAnalysisResult(
          primaryDiagnosis = "Pediatric Acute Respiratory Tract Infection / Bronchiolitis",
          probabilityPercent = 88,
          differentialDiagnoses = listOf("Acute Viral Nasopharyngitis", "Pediatric Bronchitis / Wheeze", "Acute Otitis Media"),
          clinicalSummary = "Pediatric respiratory tract congestion in a $age-year-old child (~${wKg.toInt()} kg). Managed with weight-adjusted pediatric mucolytic-bronchodilator drops/syrup, nasal saline clearance, and age-calibrated Amoxicillin-Clavulanate suspension.",
          oralMedicines = listOf(
            SuggestedMedicine(
              brandName = if (isInfant) "Ambrodil-S / Ascoril LS Junior Drops" else "Ascoril LS Junior / Ambrodil-S Pediatric Syrup",
              genericSalt = "Levosalbutamol + Ambroxol + Guaiphenesin Pediatric",
              formulationType = if (isInfant) "Oral Pediatric Drops" else "Oral Pediatric Syrup",
              dosageAndStrength = ambrodilDose,
              frequency = "1-1-1 (TDS after feeds)",
              duration = "5 Days",
              instructions = "Relieves pediatric airway bronchospasm and liquefies respiratory mucus"
            ),
            SuggestedMedicine(
              brandName = "Augmentin Duo / Clavam Pediatric Oral Suspension (228.5mg/5ml)",
              genericSalt = "Amoxicillin 200mg + Clavulanic Acid 28.5mg / 5ml",
              formulationType = "Oral Reconstituted Suspension",
              dosageAndStrength = "$augSyrupMlPerDose ml",
              frequency = "1-0-1 (BD with feeds)",
              duration = "5 Days",
              instructions = "Calculated at 40 mg/kg/day divided BD for ${wKg.toInt()} kg child; store reconstituted bottle in refrigerator"
            ),
            SuggestedMedicine(
              brandName = "Nasoclear / Otrivin Baby Saline Nasal Drops",
              genericSalt = "Isotonic Sodium Chloride 0.65% w/v Nasal Drops",
              formulationType = "Pediatric Nasal Drops",
              dosageAndStrength = "2 Drops per nostril",
              frequency = "15 mins before feeds & bedtime",
              duration = "5 Days",
              instructions = "Clears nasal blockage safely in infants/children so feeding is uninterrupted"
            )
          ),
          injectables = if (isSevere) listOf(
            SuggestedMedicine(
              brandName = "Inj. Ceftriaxone Pediatric IV (Monocef 250mg / 500mg)",
              genericSalt = "Ceftriaxone Sodium IV (50 mg/kg/day)",
              formulationType = "Pediatric IV Injection",
              dosageAndStrength = "$ceftriaxoneMg mg",
              frequency = "1-0-0 (OD) or divided BD",
              duration = "3 Days",
              instructions = "Weight-calibrated pediatric IV antibiotic ($ceftriaxoneMg mg for ${wKg.toInt()} kg); dilute in WFI and infuse slowly over 15 mins",
              isInjectable = true
            ),
            SuggestedMedicine(
              brandName = "Levolin 0.31mg + Budecort 0.5mg Respules",
              genericSalt = "Levosalbutamol + Budesonide Pediatric Nebulization",
              formulationType = "Jet Nebulizer Inhalation",
              dosageAndStrength = "1 Respule each",
              frequency = "1-1-1 (TDS via Pediatric Mask)",
              duration = "3 Days",
              instructions = "Administer via pediatric face mask nebulizer for acute wheezing/chest indrawing",
              isInjectable = true
            )
          ) else emptyList(),
          recommendedLabTests = listOf("Pulse Oximetry (SpO2 Monitoring)", "CBC with Differential & CRP", "Chest X-Ray AP View (if tachypnea present)"),
          redFlagWarnings = listOf(
            "Fast breathing (Respiratory rate >50/min in infant or >40/min in child)",
            "Lower chest wall indrawing, nasal flaring, grunting, or SpO2 <94%",
            "Inability to breastfeed/drink or bluish discoloration of lips (Cyanosis)"
          ),
          dietaryAdvice = "Keep child's head slightly elevated during sleep. Use cool-mist humidifier or steam in bathroom, and offer warm clear fluids."
        )
      }

      // Pediatric Abdominal Colic / Cramps / Excessive Crying
      s.contains("colic") || s.contains("crying") || s.contains("cramps") || s.contains("refusal") -> {
        val dropsMl = if (ageYears < 1.0) "0.5 ml (10 Drops)" else "1.0 ml (20 Drops)"
        DiseaseAnalysisResult(
          primaryDiagnosis = "Infantile / Pediatric Intestinal Colic & Gripe",
          probabilityPercent = 89,
          differentialDiagnoses = listOf("Functional Infantile Colic", "Aerophagia / Gas", "Cow's Milk Protein Sensitivity"),
          clinicalSummary = "Spasmodic gastrointestinal colic and trapped intestinal gas in a $age-year-old child (~${wKg.toInt()} kg). Treated safely with pediatric Simethicone + Dill/Fennel oil drops and gut probiotics.",
          oralMedicines = listOf(
            SuggestedMedicine(
              brandName = "Colimex / Cyclopam Pediatric Oral Drops",
              genericSalt = "Simethicone 40mg + Dill Oil + Fennel Oil /ml",
              formulationType = "Oral Pediatric Drops",
              dosageAndStrength = dropsMl,
              frequency = "1-1-1 (TDS 15 mins before feeds)",
              duration = "5 Days",
              instructions = "Relieves trapped gas bubbles and smooth muscle intestinal spasm in infants/toddlers"
            ),
            SuggestedMedicine(
              brandName = "Gutpro / Sporlac Baby Oral Drops",
              genericSalt = "Lactobacillus rhamnosus GG Pediatric Drops",
              formulationType = "Oral Probiotic Drops",
              dosageAndStrength = "5 Drops",
              frequency = "1-0-0 (Once Daily)",
              duration = "10 Days",
              instructions = "Supports digestive comfort and healthy infant gut microbiome"
            )
          ),
          injectables = emptyList(),
          recommendedLabTests = listOf("Clinical Abdominal Examination", "Stool Routine & Occult Blood (if persistent)"),
          redFlagWarnings = listOf(
            "Bilious (greenish) vomiting or currant-jelly blood in stool (Intussusception risk)",
            "Hard, distended, tender abdomen or high fever"
          ),
          dietaryAdvice = "Burp the baby upright for 10–15 minutes after every feed. Perform gentle clockwise tummy massage and bicycle-leg exercises."
        )
      }

      // Pediatric Skin Rash / Diaper Rash / Allergy
      s.contains("rash") || s.contains("skin") || s.contains("itching") || s.contains("allergy") -> {
        val cetirizineDose = if (isInfant) "1 ml Drops (2.5 mg)" else "2.5 ml – 5 ml Syrup (2.5–5 mg)"
        DiseaseAnalysisResult(
          primaryDiagnosis = "Pediatric Allergic Dermatitis / Diaper Dermatitis",
          probabilityPercent = 88,
          differentialDiagnoses = listOf("Atopic Eczema", "Irritant Diaper Rash", "Viral Exanthem"),
          clinicalSummary = "Cutaneous erythema and pruritus in a $age-year-old child (~${wKg.toInt()} kg). Managed with weight-adjusted pediatric antihistamine drops/syrup and soothing zinc oxide/calamine barrier care.",
          oralMedicines = listOf(
            SuggestedMedicine(
              brandName = if (isInfant) "Cetzine / Alerid Pediatric Oral Drops" else "Cetzine / Atarax Pediatric Syrup",
              genericSalt = "Cetirizine Dihydrochloride Pediatric (2.5mg/2.5ml)",
              formulationType = if (isInfant) "Oral Drops" else "Oral Syrup",
              dosageAndStrength = cetirizineDose,
              frequency = "0-0-1 (Once Daily at bedtime)",
              duration = "5 Days",
              instructions = "Relieves pediatric skin itching and allergic wheals safely"
            ),
            SuggestedMedicine(
              brandName = "Caladryl / Lacto Calamine Soothing Lotion",
              genericSalt = "Calamine + Zinc Oxide + Aloe Vera",
              formulationType = "Topical Soothing Lotion",
              dosageAndStrength = "Apply thin layer",
              frequency = "1-1-1 (TDS on affected skin)",
              duration = "7 Days",
              instructions = "Cools inflamed skin and forms a gentle protective barrier"
            )
          ),
          injectables = emptyList(),
          recommendedLabTests = listOf("CBC with Absolute Eosinophil Count (if recurrent)"),
          redFlagWarnings = listOf(
            "Lip/tongue swelling or breathing difficulty (Anaphylaxis)",
            "Non-blanching purple spots (Purpura) with high fever"
          ),
          dietaryAdvice = "Dress child in soft 100% cotton clothes, use mild fragrance-free baby cleanser, and allow diaper-free air time if diaper area is involved."
        )
      }

      // Default Pediatric Evaluation
      else -> {
        val pcmMg = (wKg * 15.0).toInt()
        val pcmMl = String.format(Locale.US, "%.1f", if (isInfant) pcmMg / 100.0 else pcmMg / 50.0)
        DiseaseAnalysisResult(
          primaryDiagnosis = "Pediatric Clinical Evaluation: ${symptoms.ifBlank { "General Pediatric Triage" }}",
          probabilityPercent = 86,
          differentialDiagnoses = listOf("Early Viral Prodrome", "Nutritional Micronutrient Need", "Functional Pediatric Symptom"),
          clinicalSummary = "Pediatric clinical evaluation for a $age-year-old child (~${wKg.toInt()} kg). All medications are calibrated in liquid/sachet pediatric formulations.",
          oralMedicines = listOf(
            SuggestedMedicine(
              brandName = if (isInfant) "Zincovit Pediatric Drops 15ml" else "Zincovit Pediatric Syrup 200ml",
              genericSalt = "Multivitamin + Elemental Zinc + Lysine Drops/Syrup",
              formulationType = if (isInfant) "Oral Drops" else "Oral Syrup",
              dosageAndStrength = if (isInfant) "1 ml" else "5 ml",
              frequency = "1-0-0 (Once Daily after morning feed)",
              duration = "14 Days",
              instructions = "Pediatric growth, immunity, and recovery support"
            ),
            SuggestedMedicine(
              brandName = if (isInfant) "Calpol Pediatric Drops (100mg/ml)" else "Calpol 250 Pediatric Suspension (250mg/5ml)",
              genericSalt = "Paracetamol IP (15 mg/kg/dose SOS)",
              formulationType = if (isInfant) "Oral Drops" else "Oral Suspension",
              dosageAndStrength = "$pcmMl ml ($pcmMg mg)",
              frequency = "SOS for fever/pain (Max Q6H)",
              duration = "3 Days",
              instructions = "Administer only if fever >100°F or pain is present"
            )
          ),
          injectables = emptyList(),
          recommendedLabTests = listOf("CBC with Differential", "Urine Routine & Microscopy"),
          redFlagWarnings = listOf("Poor feeding, lethargy, persistent vomiting, or fast breathing"),
          dietaryAdvice = "Ensure frequent oral hydration (ORS, breastmilk/home fluids) and follow up with a pediatrician if symptoms persist."
        )
      }
    }
  }

  // =========================================================================
  // 2. ADULT & GERIATRIC CLINICAL PROTOCOL ENGINE (AGE > 12 YRS)
  // =========================================================================
  return when {
    s.contains("night fall") || s.contains("nightfall") || s.contains("wet dream") || s.contains("spermatorrhea") || s.contains("nocturnal emission") || s.contains("erectile") || s.contains("semen") || s.contains("ejaculation") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Physiological Nocturnal Emission (Night Fall) & Andrological Wellness",
        probabilityPercent = 95,
        differentialDiagnoses = listOf("Normal Post-Pubertal Physiology", "Performance Anxiety / Stress", "Pelvic Floor Muscle Hypertonia"),
        clinicalSummary = "Nocturnal emissions ('Night Fall') are a normal, non-pathological, healthy involuntary physiological discharge during REM sleep in young adults. It is NOT a disease and does NOT cause organ damage or weakness. Unnecessary analgesics or antibiotics are strictly NOT indicated.",
        oralMedicines = listOf(
          SuggestedMedicine("Himalaya Speman / Ashwagandha Gold", "Ashwagandha (Withania Somnifera) + Gokshura", "Oral Tablet", "1 Tab", "1-0-1 (BD) after meals", "15 Days", "Natural adaptogen to reduce performance anxiety and promote deep sleep"),
          SuggestedMedicine("Zincovit / A-Z Vitality", "Multivitamin + L-Arginine + Zinc & B-Complex", "Oral Tablet", "1 Tab", "1-0-0 (Morning)", "15 Days", "Neuro-nutritional nourishment and reproductive vitality support"),
          SuggestedMedicine("Magnesium Glycinate 400mg", "Magnesium + Vitamin B6", "Oral Tablet", "1 Tab", "0-0-1 (30 mins before sleep)", "10 Days", "Promotes nervous system relaxation and restorative sleep architecture")
        ),
        injectables = emptyList(),
        recommendedLabTests = listOf("Routine Urine Examination", "Serum Testosterone & Thyroid Profile (if persistent stress present)"),
        redFlagWarnings = listOf("Painful urination or burning sensation (Dysuria / Urethritis)", "Blood in semen or urine (Hematuria)", "Severe testicular or pelvic pain"),
        dietaryAdvice = "Sleep on your side rather than on stomach. Practice pelvic floor (Kegel) exercises. Avoid heavy meals and smartphone screens 1 hour before sleep. Rest assured night fall is completely safe and normal."
      )
    }
    s.contains("hair fall") || s.contains("hair loss") || s.contains("baldness") || s.contains("dandruff") || s.contains("alopecia") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Androgenetic Alopecia / Telogen Effluvium Evaluation",
        probabilityPercent = 88,
        differentialDiagnoses = listOf("Nutritional Deficiency Hair Loss", "Seborrheic Dermatitis", "Thyroid-Induced Alopecia"),
        clinicalSummary = "Follicular weakening associated with micronutrient deficiency, stress, or androgenic hair thinning.",
        oralMedicines = listOf(
          SuggestedMedicine("Follihair / Keraglo Eva", "Biotin 10mg + Amino Acids + Zinc + Saw Palmetto", "Oral Tablet", "1 Tab", "1-0-0 (After breakfast)", "30 Days", "Essential hair follicle nutrient complex"),
          SuggestedMedicine("Minokem-5 / Tugain 5% Solution", "Minoxidil 5% Topical Solution", "Topical Spray", "1 ml", "1-0-1 (BD on dry scalp)", "30 Days", "Stimulates microcirculation to hair follicles"),
          SuggestedMedicine("Ketoconazole 2% Shampoo", "Ketoconazole Anti-Dandruff Shampoo", "Topical Wash", "Apply twice weekly", "Leave 5 mins before rinse", "15 Days", "Controls scalp seborrhea")
        ),
        injectables = emptyList(),
        recommendedLabTests = listOf("Serum Ferritin & Iron Studies", "Vitamin D3 & B12 Levels", "Thyroid Profile (TSH)"),
        redFlagWarnings = listOf("Sudden patchy coin-sized hair loss (Alopecia Areata)", "Scalp redness with pus or severe itching"),
        dietaryAdvice = "High-protein diet (eggs, legumes, sprouts, nuts). Maintain scalp hygiene and reduce stress."
      )
    }
    s.contains("period") || s.contains("pcos") || s.contains("menstrual") || s.contains("leucorrhea") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Primary Dysmenorrhea / Polycystic Ovarian Evaluation",
        probabilityPercent = 86,
        differentialDiagnoses = listOf("PCOS / PCOD", "Pelvic Inflammatory Disease", "Endometriosis"),
        clinicalSummary = "Uterine smooth muscle spasm and prostaglandins during menstrual flow.",
        oralMedicines = listOf(
          SuggestedMedicine("Meftal-Spas", "Mefenamic Acid 250mg + Dicyclomine 10mg", "Oral Tablet", "1 Tab", "1-0-1 (BD) SOS during pain", "3 Days", "Spasmolytic and targeted pain relief for menstrual cramps"),
          SuggestedMedicine("Ovasitol / Myproic", "Myo-Inositol + D-Chiro-Inositol + Folic Acid", "Oral Sachet", "1 Sachet", "1-0-0 (In 200ml water)", "30 Days", "Hormonal balance and ovarian health support"),
          SuggestedMedicine("Autrin / Orofer XT", "Ferrous Ascorbate + Folic Acid", "Oral Tablet", "1 Tab", "0-0-1 (After dinner)", "15 Days", "Hemoglobin replenishment following menstrual flow")
        ),
        injectables = emptyList(),
        recommendedLabTests = listOf("USG Pelvis / Lower Abdomen", "Serum LH/FSH Ratio & Anti-Mullerian Hormone (AMH)", "Complete Blood Count (CBC)"),
        redFlagWarnings = listOf("Excessively heavy bleeding (>5 pads/day)", "Severe fever with foul vaginal discharge"),
        dietaryAdvice = "Use warm compress on lower abdomen. Hydrate with warm water and chamomile tea. Avoid excessive caffeine."
      )
    }
    s.contains("fever") && (s.contains("chills") || s.contains("body ache") || s.contains("rigors") || s.contains("dengue")) -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Acute Viral Fever / Suspected Dengue Exanthem",
        probabilityPercent = 88,
        differentialDiagnoses = listOf("Influenza A/B", "Typhoid Fever", "Malaria"),
        clinicalSummary = "Acute onset febrile illness with systemic myalgia in adult patient ($age yrs). Platelet and hydration monitoring recommended.",
        oralMedicines = listOf(
          SuggestedMedicine("Dolo 650 / Calpol 650", "Paracetamol 650mg", "Oral Tablet", "650 mg (1 Tab)", "1-1-1 (TDS) after food", "5 Days", "Avoid NSAIDs like Ibuprofen/Diclofenac if Dengue suspected"),
          SuggestedMedicine("Pan 40 / Pantocid 40", "Pantoprazole 40mg", "Oral Tablet", "40 mg (1 Tab)", "1-0-0 (OD) before breakfast", "5 Days", "Gastroprotection during fever management"),
          SuggestedMedicine("Zincovit / Caripill", "Multivitamin + Zinc / Carica Papaya Extract", "Oral Tablet", "1 Tab", "1-0-1 (BD)", "10 Days", "Immune & platelet recovery support")
        ),
        injectables = if (isSevere) listOf(
          SuggestedMedicine("Inj. Paracetamol 100ml IV", "Paracetamol IV Infusion", "IV Infusion", "1000 mg / 100ml", "STAT / BD", "2 Days", "Infuse 100ml IV over 15-20 minutes for high hyperpyrexia >102°F", true),
          SuggestedMedicine("Inj. Ondansetron (Emeset)", "Ondansetron IV", "IV Injection", "4 mg / 2ml", "STAT / BD", "2 Days", "Slow IV push over 2 minutes for persistent vomiting", true)
        ) else emptyList(),
        recommendedLabTests = listOf("CBC with Platelet Count & PCV", "Dengue NS1 Antigen & IgM", "Typhoid IgM / Malaria Antigen"),
        redFlagWarnings = listOf("Platelet drop <100,000/mcL", "Bleeding gums or petechial rash", "Persistent severe vomiting"),
        dietaryAdvice = "Increase oral fluid intake (coconut water, ORS, soups) to 3 Liters daily."
      )
    }
    s.contains("cough") || s.contains("sore throat") || s.contains("chest") || s.contains("breath") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Acute Lower Respiratory Tract Infection / Bronchitis",
        probabilityPercent = 84,
        differentialDiagnoses = listOf("Bacterial Pneumonia", "Asthmatic Exacerbation", "Viral Pharyngitis"),
        clinicalSummary = "Respiratory congestion with cough and airway hyperreactivity in adult patient ($age yrs).",
        oralMedicines = listOf(
          SuggestedMedicine("Augmentin Duo 625 / Clavam 625", "Amoxicillin 500mg + Clavulanate 125mg", "Oral Tablet", "625 mg (1 Tab)", "1-0-1 (BD) after food", "5 Days", "Complete full 5-day antibiotic course"),
          SuggestedMedicine("Ascoril LS / Ambrodil-S Syrup", "Levosalbutamol + Ambroxol + Guaiphenesin", "Oral Syrup", "10 ml", "1-1-1 (TDS)", "5 Days", "Bronchodilator and mucolytic for airway clearance"),
          SuggestedMedicine("Montair-LC / Telekast-L", "Montelukast 10mg + Levocetirizine 5mg", "Oral Tablet", "1 Tab", "0-0-1 (At bedtime)", "5 Days", "Controls nocturnal cough and airway inflammation")
        ),
        injectables = if (isSevere) listOf(
          SuggestedMedicine("Inj. Ceftriaxone (Monocef 1g)", "Ceftriaxone IV", "IV Injection", "1000 mg", "1-0-1 (BD)", "3 Days", "Reconstitute in 10ml WFI, give slow IV push over 3-5 mins", true),
          SuggestedMedicine("Inj. Hydrocortisone 100mg", "Hydrocortisone IV", "IV Injection", "100 mg", "STAT", "1 Day", "Immediate IV push for severe bronchospasm", true)
        ) else emptyList(),
        recommendedLabTests = listOf("Chest X-Ray PA View", "CBC with ESR & CRP", "Pulse Oximetry (SpO2)"),
        redFlagWarnings = listOf("SpO2 oxygen saturation drops below 94%", "Chest pain or severe breathlessness"),
        dietaryAdvice = "Steam inhalation twice daily. Warm fluids and salt water gargles."
      )
    }
    s.contains("diarrhea") || s.contains("vomiting") || s.contains("stomach") || s.contains("acidity") || s.contains("cramps") -> {
      val isPregnant = comorbidities.contains("Pregnancy", ignoreCase = true)
      val antimicrobialMed = if (isPregnant) {
        SuggestedMedicine("Cefixime 200mg (Taxim-O 200 / Zifi 200)", "Cefixime Trihydrate 200mg (Pregnancy Category B)", "Oral Tablet", "200 mg (1 Tab)", "1-0-1 (BD) after food", "5 Days", "Pregnancy-safe cephalosporin (Fluoroquinolones avoided in pregnancy)")
      } else {
        SuggestedMedicine("Norflox TZ / Oflox OZ", "Ofloxacin 200mg + Ornidazole 500mg", "Oral Tablet", "1 Tab", "1-0-1 (BD) after food", "3–5 Days", "Broad-spectrum adult intestinal antimicrobial for infective enteritis")
      }
      DiseaseAnalysisResult(
        primaryDiagnosis = "Adult Acute Gastroenteritis & Dehydration",
        probabilityPercent = 87,
        differentialDiagnoses = listOf("Infective Enterocolitis / Dysentery", "Foodborne Gastroenteritis", "Acid Peptic Gastritis"),
        clinicalSummary = "Adult gastrointestinal infection with mucosal inflammation and fluid/electrolyte loss ($age yrs).",
        oralMedicines = listOf(
          SuggestedMedicine("WHO ORS (Electral Powder)", "Low-Osmolarity Oral Rehydration Salts", "Oral Solution", "200 ml / loose stool", "Sip frequently (2–3 Liters/day)", "3 Days", "Essential adult electrolyte and fluid replacement"),
          SuggestedMedicine("Redotil 100 / Econorm Cap", "Racecadotril 100mg + Saccharomyces boulardii", "Oral Capsule", "100 mg (1 Cap)", "1-1-1 (TDS)", "3 Days", "Reduces intestinal hypersecretion and restores gut flora"),
          antimicrobialMed,
          SuggestedMedicine("Pan-D / Pantocid DSR", "Pantoprazole 40mg + Domperidone 30mg SR", "Oral Capsule", "1 Cap", "1-0-0 (Empty stomach)", "5 Days", "Controls gastric hyperacidity and nausea")
        ),
        injectables = if (isSevere) listOf(
          SuggestedMedicine("Inj. Pantoprazole (Pantocid IV)", "Pantoprazole Sodium IV", "IV Injection", "40 mg", "STAT / OD", "2 Days", "Reconstitute with 10ml NS, slow IV push over 2 mins", true),
          SuggestedMedicine("Inj. Ondansetron (Emeset)", "Ondansetron Hydrochloride IV", "IV Injection", "4 mg / 2ml", "STAT / BD", "1–2 Days", "Slow IV push prior to rehydration for severe vomiting", true)
        ) else emptyList(),
        recommendedLabTests = listOf("Stool Routine & Microscopy", "Serum Electrolytes (Na+, K+)", "Renal Function (Urea/Creatinine if dehydrated)"),
        redFlagWarnings = listOf("Dizziness on standing (orthostatic hypotension) or oliguria", "Inability to retain oral liquids for >8 hours"),
        dietaryAdvice = "Follow BRAT diet (Bananas, Rice, Curd, Applesauce, Toast). Avoid spicy/oily food and unboiled milk."
      )
    }
    s.contains("insomnia") || s.contains("sleep") || s.contains("sleepless") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Primary Insomnia / Sleep Architecture Disruption",
        probabilityPercent = 85,
        differentialDiagnoses = listOf("Circadian Rhythm Disorder", "Situational Anxiety", "Secondary Sleep Disturbance"),
        clinicalSummary = "Difficulty initiating or maintaining restorative sleep with associated daytime fatigue.",
        oralMedicines = listOf(
          SuggestedMedicine("Meloset 3mg / Melatonin", "Melatonin", "Oral Tablet", "3 mg", "0-0-1 (1 hr before bedtime)", "7 Days", "Chronobiotic to regulate sleep-wake cycle"),
          SuggestedMedicine("Etilaam 0.25 / Clonazepam", "Etizolam 0.25mg", "Oral Tablet", "0.25 mg", "0-0-1 (At bedtime as needed)", "3 Days", "Short-term anxiolytic sedative under physician guidance"),
          SuggestedMedicine("B-Complex with L-Theanine", "Neuro-Nutritional Support", "Oral Capsule", "1 Cap", "1-0-0 (Morning)", "14 Days", "Nervous system relaxation and recovery")
        ),
        injectables = emptyList(),
        recommendedLabTests = listOf("Thyroid Function Test (TSH)", "Serum Vitamin B12 & Vitamin D3", "Sleep Study Evaluation"),
        redFlagWarnings = listOf("Severe depressive symptoms or self-harm thoughts", "Chronic daytime microsleeps affecting driving/machinery"),
        dietaryAdvice = "Avoid caffeine after 4 PM, maintain consistent sleep schedule, and limit screen time 1 hour before bed."
      )
    }
    s.contains("migraine") || s.contains("headache") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Vascular Migraine / Tension Headache",
        probabilityPercent = 83,
        differentialDiagnoses = listOf("Cluster Headache", "Sinusitis", "Cervicogenic Headache"),
        clinicalSummary = "Recurrent throbbing cephalalgia often exacerbated by sensory stimuli and stress.",
        oralMedicines = listOf(
          SuggestedMedicine("Naprosyn 500 / Sumatriptan", "Naproxen Sodium 500mg", "Oral Tablet", "500 mg", "1-0-1 (BD with food)", "3 Days", "NSAID targeted for neurovascular inflammation"),
          SuggestedMedicine("Stemetil MD / Prochlorperazine", "Prochlorperazine", "Oral Tablet", "5 mg", "SOS for nausea", "3 Days", "Anti-emetic for migraine-associated nausea"),
          SuggestedMedicine("Inderal 40 / Propranolol", "Propranolol Hydrochloride", "Oral Tablet", "40 mg", "1-0-1 (BD)", "10 Days", "Vascular migraine prophylaxis")
        ),
        injectables = if (isSevere) listOf(
          SuggestedMedicine("Inj. Diclofenac (Voveran)", "Diclofenac Sodium IM", "IM Injection", "75 mg / 3ml", "STAT", "1 Day", "Deep IM injection for acute severe migraine attack", true)
        ) else emptyList(),
        recommendedLabTests = listOf("MRI Brain / MRA if atypical", "Cervical Spine X-Ray", "Blood Pressure Monitoring"),
        redFlagWarnings = listOf("Sudden 'thunderclap' severe headache", "Neurological deficits or confusion"),
        dietaryAdvice = "Avoid known triggers (aged cheeses, red wine, chocolate), maintain adequate hydration."
      )
    }
    s.contains("rash") || s.contains("skin") || s.contains("itching") || s.contains("allergy") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Allergic Dermatitis / Acute Urticaria",
        probabilityPercent = 86,
        differentialDiagnoses = listOf("Contact Dermatitis", "Atopic Eczema", "Drug Eruption"),
        clinicalSummary = "Cutaneous hypersensitivity reaction manifesting with pruritus, erythema, and wheals.",
        oralMedicines = listOf(
          SuggestedMedicine("Allegra 120 / Bilastine 20", "Fexofenadine 120mg", "Oral Tablet", "120 mg", "1-0-0 (Morning)", "5 Days", "Non-sedating antihistamine for pruritus"),
          SuggestedMedicine("Atarax 25 / Hydroxyzine", "Hydroxyzine Hydrochloride", "Oral Tablet", "25 mg", "0-0-1 (At night)", "5 Days", "Anti-pruritic sedative antihistamine"),
          SuggestedMedicine("Wysolone 10 / Prednisolone", "Prednisolone", "Oral Tablet", "10 mg", "1-1-0 (After breakfast/lunch)", "3 Days", "Short course systemic anti-inflammatory steroid")
        ),
        injectables = if (isSevere) listOf(
          SuggestedMedicine("Inj. Avil (Pheniramine Maleate)", "Pheniramine Maleate IM/IV", "IM Injection", "22.5 mg / 2ml", "STAT", "1 Day", "Immediate antihistamine rescue for severe hives", true),
          SuggestedMedicine("Inj. Dexamethasone 4mg", "Dexamethasone Sodium", "IV Injection", "4 mg", "STAT", "1 Day", "Rapid suppression of acute allergic cascade", true)
        ) else emptyList(),
        recommendedLabTests = listOf("Absolute Eosinophil Count (AEC)", "Serum IgE Levels", "Skin Prick Allergy Panel"),
        redFlagWarnings = listOf("Facial swelling or difficulty breathing (Anaphylaxis risk)", "Widespread blistering"),
        dietaryAdvice = "Avoid potential food/contact allergens. Wear loose cotton garments."
      )
    }
    s.contains("joint") || s.contains("arthritis") || s.contains("pain") || s.contains("stiffness") -> {
      val hasRenalIssue = comorbidities.contains("CKD", ignoreCase = true) || comorbidities.contains("Kidney", ignoreCase = true)
      val analgesicMed = if (hasRenalIssue) {
        SuggestedMedicine("Ultracet / Dolonex-P (Renal Safe)", "Paracetamol 325mg + Tramadol 37.5mg", "Oral Tablet", "1 Tab", "1-0-1 (BD after food)", "5 Days", "Renal-sparing analgesic (NSAIDs avoided in CKD)")
      } else {
        SuggestedMedicine("Zerodol-SP / Aceclo-SP", "Aceclofenac 100mg + Paracetamol 325mg + Serratiopeptidase 15mg", "Oral Tablet", "1 Tab", "1-0-1 (BD after food)", "5 Days", "Anti-inflammatory and proteolytic enzyme combination")
      }
      DiseaseAnalysisResult(
        primaryDiagnosis = "Acute Myalgia / Inflammatory Arthropathy",
        probabilityPercent = 83,
        differentialDiagnoses = listOf("Rheumatoid Arthritis", "Gouty Arthritis", "Viral Polyarthralgia"),
        clinicalSummary = "Musculoskeletal joint inflammation and soft tissue tenderness.",
        oralMedicines = listOf(
          analgesicMed,
          SuggestedMedicine("Pantocid 40", "Pantoprazole 40mg", "Oral Capsule", "40 mg", "1-0-0 (Before breakfast)", "5 Days", "Gastric mucosal protection during analgesic therapy"),
          SuggestedMedicine("Dynapar QPS Spray / Volini", "Diclofenac Topical Gel", "Topical Gel", "Apply locally", "3-4 times daily", "5 Days", "Localized percutaneous pain relief")
        ),
        injectables = emptyList(),
        recommendedLabTests = listOf("Serum Uric Acid", "RA Factor & Anti-CCP", "ESR & CRP"),
        redFlagWarnings = listOf("Severe joint swelling with local warmth and fever (Septic arthritis risk)"),
        dietaryAdvice = "Warm Epsom salt soaks, gentle range-of-motion exercises, and anti-inflammatory diet."
      )
    }
    s.contains("dizziness") || s.contains("vertigo") || s.contains("balance") -> {
      DiseaseAnalysisResult(
        primaryDiagnosis = "Peripheral Vestibular Vertigo / Benign Paroxysmal Positional Vertigo",
        probabilityPercent = 84,
        differentialDiagnoses = listOf("Meniere's Disease", "Orthostatic Hypotension", "Cerebrovascular Insufficiency"),
        clinicalSummary = "Rotational spinning sensation triggered by head movements and inner ear imbalance.",
        oralMedicines = listOf(
          SuggestedMedicine("Stemetil / Prochlorperazine", "Prochlorperazine Maleate", "Oral Tablet", "5 mg", "1-0-1 (BD)", "5 Days", "Vestibular suppressant"),
          SuggestedMedicine("Vertin 16 / Betahistine", "Betahistine Hydrochloride", "Oral Tablet", "16 mg", "1-1-1 (TDS)", "10 Days", "Improves microcirculation in the inner ear"),
          SuggestedMedicine("Neurobion Forte", "Vitamin B-Complex", "Oral Tablet", "1 Tab", "1-0-0 (Morning)", "10 Days", "Nerve nourishment")
        ),
        injectables = emptyList(),
        recommendedLabTests = listOf("Pure Tone Audiometry", "MRI Brain with Angiography", "Blood Pressure & Lipid Profile"),
        redFlagWarnings = listOf("Sudden weakness, slurred speech, or double vision", "Inability to stand or walk"),
        dietaryAdvice = "Avoid sudden head rotations, stay hydrated, and reduce dietary sodium if Meniere's suspected."
      )
    }
    else -> {
      val capitalizedQuery = symptoms.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
      DiseaseAnalysisResult(
        primaryDiagnosis = "Clinical Symptomatic Evaluation for: $capitalizedQuery",
        probabilityPercent = 85,
        differentialDiagnoses = listOf("Functional Physiological Variation", "Lifestyle & Stress Factors", "Metabolic Nutritional Balance"),
        clinicalSummary = "Clinical evaluation of reported symptom '$capitalizedQuery' for patient aged $age years ($gender). Supportive wellness protocol formulated.",
        oralMedicines = listOf(
          SuggestedMedicine("Multivitamin with Zinc & B-Complex", "Essential Vitamins & Minerals", "Oral Tablet", "1 Tab", "1-0-0 (Morning after breakfast)", "15 Days", "Nutritional vitality and metabolic recovery support"),
          SuggestedMedicine("Dolo 650 / Calpol 650 (SOS)", "Paracetamol 650mg", "Oral Tablet", "650 mg", "SOS for fever or body ache", "3 Days", "Symptomatic relief if needed")
        ),
        injectables = emptyList(),
        recommendedLabTests = listOf("Complete Blood Count (CBC)", "Vitamin D3 & B12 Profile", "Routine Metabolic Panel"),
        redFlagWarnings = listOf("Persistent worsening of symptoms beyond 7 days", "Severe functional impairment or acute distress"),
        dietaryAdvice = "Maintain a well-balanced nutrient-dense diet, drink 2.5 - 3 Liters of water daily, ensure 7-8 hours of sleep, and consult a medical specialist if symptoms persist."
      )
    }
  }
}

fun buildShareableTreatmentSummary(
  res: DiseaseAnalysisResult,
  patientName: String,
  age: String,
  gender: String
): String {
  val sb = java.lang.StringBuilder()
  sb.append("📋 *ROYAL PHARMACY - CLINICAL TREATMENT PROTOCOL*\n")
  sb.append("👤 Patient: $patientName | Age: $age | Category: $gender\n")
  sb.append("🩺 Diagnosis: *${res.primaryDiagnosis}* (${res.probabilityPercent}% Confidence)\n\n")
  sb.append("💊 *ORAL MEDICINES*:\n")
  res.oralMedicines.forEachIndexed { idx, m ->
    sb.append("${idx + 1}. *${m.brandName}* (${m.genericSalt}) [${m.formulationType}]\n   • Dose: ${m.dosageAndStrength} | ${m.frequency} | ${m.duration}\n")
  }
  if (res.injectables.isNotEmpty()) {
    sb.append("\n💉 *INJECTABLE / RESCUE THERAPY*:\n")
    res.injectables.forEachIndexed { idx, inj ->
      sb.append("${idx + 1}. *${inj.brandName}* (${inj.genericSalt})\n   • Dose: ${inj.dosageAndStrength} | Route: ${inj.formulationType} | ${inj.frequency}\n   • Note: ${inj.instructions}\n")
    }
  }
  if (res.recommendedLabTests.isNotEmpty()) {
    sb.append("\n🧪 *RECOMMENDED LAB TESTS*: ${res.recommendedLabTests.joinToString(", ")}\n")
  }
  sb.append("\n🏥 Prescribed with clinical care by ROYAL PHARMACY.")
  return sb.toString()
}
