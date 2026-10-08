package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.clinical.DrugInteractionService
import com.example.data.clinical.DrugPairInteraction
import com.example.data.clinical.InteractionCheckResult
import com.example.data.clinical.InteractionSeverity
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenLight
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugInteractionCheckerScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val medicines by viewModel.allMedicines.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()

  val interactionService = remember { DrugInteractionService() }

  val selectedDrugs = remember { mutableStateListOf<String>() }
  var drugSearchQuery by remember { mutableStateOf("") }
  var isChecking by remember { mutableStateOf(false) }
  var checkResult by remember { mutableStateOf<InteractionCheckResult?>(null) }

  // Filtered inventory suggestions
  val searchSuggestions = remember(drugSearchQuery, medicines, selectedDrugs) {
    if (drugSearchQuery.isBlank()) emptyList()
    else {
      medicines.filter {
        (it.name.contains(drugSearchQuery, ignoreCase = true) ||
          it.composition.contains(drugSearchQuery, ignoreCase = true)) &&
          !selectedDrugs.contains(it.name)
      }.take(5)
    }
  }

  fun runCheck(overrideList: List<String>? = null) {
    val listToCheck = (overrideList ?: selectedDrugs.toList()).toMutableList()
    if (drugSearchQuery.isNotBlank() && !listToCheck.contains(drugSearchQuery.trim())) {
      val clean = drugSearchQuery.trim()
      listToCheck.add(clean)
      if (!selectedDrugs.contains(clean)) {
        selectedDrugs.add(clean)
      }
      drugSearchQuery = ""
    }
    if (listToCheck.isEmpty()) {
      Toast.makeText(context, "Please enter or select medicines to evaluate", Toast.LENGTH_SHORT).show()
      return
    }
    scope.launch {
      isChecking = true
      try {
        val res = interactionService.checkDrugInteractions(listToCheck)
        checkResult = res
      } catch (e: Exception) {
        Toast.makeText(context, "Error scanning interactions: ${e.message}", Toast.LENGTH_SHORT).show()
      } finally {
        isChecking = false
      }
    }
  }

  fun shareInteractionReport() {
    val res = checkResult ?: return
    val reportText = buildString {
      append("🏥 *${profile.businessName} — DRUG INTERACTION AUDIT*\n")
      append("═══════════════════════════════\n")
      append("💊 *Medicines Evaluated:* ${selectedDrugs.joinToString(", ")}\n")
      append("⚡ *Overall Status:* ${res.overallSeverity.name.replace("_", " ")}\n\n")
      if (res.interactions.isEmpty()) {
        append("✅ *Verdict:* No significant adverse drug interactions or contraindications found.\n")
      } else {
        append("🚨 *Identified Interactions (${res.interactions.size}):*\n")
        res.interactions.forEachIndexed { idx, item ->
          append("\n${idx + 1}. *${item.drug1}* ⚡ *${item.drug2}*\n")
          append("   • Severity: ${item.severity.name}\n")
          append("   • Risk: ${item.riskTitle}\n")
          append("   • Mechanism: ${item.mechanism}\n")
          append("   • Pharmacist Guidance: ${item.clinicalAdvice}\n")
          if (item.alternativeSuggestion.isNotBlank()) {
            append("   • Safer Substitute: ${item.alternativeSuggestion}\n")
          }
          if (item.foodPrecaution.isNotBlank()) {
            append("   • Food Precaution: ${item.foodPrecaution}\n")
          }
        }
      }
      append("\n═══════════════════════════════\n")
      append("Generated via Royal Pharmacy Clinical Intelligence")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Drug Interaction Report")
      putExtra(Intent.EXTRA_TEXT, reportText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Clinical Interaction Report"))
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
              text = "Drug Interaction Checker",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Gemini Clinical AI & CDSCO Safety Database",
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
          Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
        }
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // 2. Preset High-Risk Clinical Test Scenarios
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Quick Preset Clinical Scenarios", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
              }
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  "Dolo 650 + Calpol (Overdose Risk)" to listOf("Dolo 650mg", "Calpol 650mg"),
                  "Combiflam + Brufen (NSAID Duplicate)" to listOf("Combiflam", "Brufen 400mg"),
                  "Clopidogrel + Omeprazole" to listOf("Clopidogrel 75mg", "Omeprazole 20mg"),
                  "Nitroglycerin + Sildenafil" to listOf("Nitroglycerin 2.6mg", "Sildenafil 50mg"),
                  "Warfarin + Aspirin" to listOf("Warfarin 5mg", "Aspirin 75mg"),
                  "Telmisartan + Spironolactone" to listOf("Telmisartan 40mg", "Spironolactone 25mg"),
                  "Ciprofloxacin + Antacids" to listOf("Ciprofloxacin 500mg", "Digene / Antacid Gel"),
                  "Digoxin + Amiodarone" to listOf("Digoxin 0.25mg", "Amiodarone 200mg"),
                  "Metronidazole + Alcohol" to listOf("Metronidazole 400mg", "Alcohol")
                ).forEach { (label, presetList) ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFF1F5F9))
                      .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                      .clickable {
                        selectedDrugs.clear()
                        selectedDrugs.addAll(presetList)
                        drugSearchQuery = ""
                        runCheck(presetList)
                      }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextDark)
                  }
                }
              }
            }
          }
        }

        // 3. Drug Search & Multi-Selection Box
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "SELECT MEDICINES TO EVALUATE (${selectedDrugs.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Spacer(modifier = Modifier.height(6.dp))

              // Quick Add Common Brand / Molecule Chips
              Text("Quick Add Popular Drugs:", fontSize = 10.5.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                listOf(
                  "Dolo 650", "Pan 40", "Augmentin 625", "Telma 40", "Clopidogrel 75mg",
                  "Omeprazole 20mg", "Sorbitrate 5mg", "Sildenafil 50mg", "Azithral 500",
                  "Combiflam", "Shelcal 500", "Ciplox 500"
                ).forEach { medName ->
                  val isAdded = selectedDrugs.contains(medName)
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(16.dp))
                      .background(if (isAdded) RoyalMagenta.copy(alpha = 0.15f) else Color(0xFFF3F4F6))
                      .border(1.dp, if (isAdded) RoyalMagenta else Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                      .clickable {
                        if (!isAdded) {
                          selectedDrugs.add(medName)
                          checkResult = null
                        } else {
                          selectedDrugs.remove(medName)
                          checkResult = null
                        }
                      }
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = if (isAdded) "✓ $medName" else "+ $medName",
                      fontSize = 10.5.sp,
                      fontWeight = if (isAdded) FontWeight.Bold else FontWeight.Normal,
                      color = if (isAdded) RoyalMagenta else TextDark
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Search or Add custom input
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedTextField(
                  value = drugSearchQuery,
                  onValueChange = { drugSearchQuery = it },
                  placeholder = { Text("Search stock or type molecule name...", fontSize = 13.sp) },
                  leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("input_drug_search")
                )

                if (drugSearchQuery.isNotBlank()) {
                  Spacer(modifier = Modifier.width(8.dp))
                  Button(
                    onClick = {
                      val trimmed = drugSearchQuery.trim()
                      if (trimmed.isNotBlank() && !selectedDrugs.contains(trimmed)) {
                        selectedDrugs.add(trimmed)
                        drugSearchQuery = ""
                        checkResult = null
                      }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              // Search Suggestions Dropdown
              if (searchSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                ) {
                  searchSuggestions.forEach { med ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                          if (!selectedDrugs.contains(med.name)) {
                            selectedDrugs.add(med.name)
                            drugSearchQuery = ""
                            checkResult = null
                          }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(med.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                        Text("${med.composition} • ${med.manufacturer}", fontSize = 10.5.sp, color = TextMuted)
                      }
                      Icon(Icons.Default.Add, contentDescription = "Add", tint = RoyalMagenta, modifier = Modifier.size(16.dp))
                    }
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                  }
                }
              }

              // Selected Medicine Chips
              if (selectedDrugs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Selected Prescription List:", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  selectedDrugs.forEach { drug ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEDE9FE))
                        .border(1.dp, Color(0xFFC4B5FD), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(drug, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4C1D95))
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                          Icons.Default.Close,
                          contentDescription = "Remove",
                          tint = Color(0xFF6D28D9),
                          modifier = Modifier
                            .size(14.dp)
                            .clickable {
                              selectedDrugs.remove(drug)
                              checkResult = null
                            }
                        )
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Action Buttons Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    selectedDrugs.clear()
                    checkResult = null
                  },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.weight(0.8f)
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Clear", fontSize = 12.sp)
                }

                Button(
                  onClick = { runCheck() },
                  enabled = (selectedDrugs.isNotEmpty() || drugSearchQuery.isNotBlank()) && !isChecking,
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .weight(1.5f)
                    .testTag("btn_run_interaction_check")
                ) {
                  if (isChecking) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Analyzing...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  } else {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan Interactions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // 4. Interaction Results Banner & Details
        if (checkResult != null) {
          val res = checkResult!!
          item {
            val (bannerBg, bannerBorder, bannerIcon, bannerTitle, bannerColor) = when (res.overallSeverity) {
              InteractionSeverity.SEVERE_CONTRAINDICATED -> listOf(
                Color(0xFFFEF2F2),
                Color(0xFFF87171),
                Icons.Default.Dangerous,
                "HIGH RISK: Contraindicated Combination",
                StatusRed
              )
              InteractionSeverity.MODERATE_MONITOR -> listOf(
                Color(0xFFFFFBEB),
                Color(0xFFFBBF24),
                Icons.Default.Warning,
                "MODERATE: Clinical Monitoring Required",
                Color(0xFFD97706)
              )
              InteractionSeverity.MINOR_CAUTION -> listOf(
                Color(0xFFEFF6FF),
                Color(0xFF93C5FD),
                Icons.Default.Info,
                "MINOR: Low Clinical Significance",
                Color(0xFF2563EB)
              )
              InteractionSeverity.NO_KNOWN_INTERACTION -> listOf(
                Color(0xFFF0FDF4),
                Color(0xFF86EFAC),
                Icons.Default.CheckCircle,
                "SAFE: No Known Significant Interactions",
                StatusGreen
              )
            }

            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = bannerBg as Color),
              border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder as Color),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(bannerIcon as androidx.compose.ui.graphics.vector.ImageVector, contentDescription = null, tint = bannerColor as Color, modifier = Modifier.size(24.dp))
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(bannerTitle as String, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = bannerColor)
                    Text(
                      text = "${res.interactions.size} interaction pair(s) detected across ${res.totalDrugsChecked} drugs",
                      fontSize = 11.sp,
                      color = TextDark
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(res.clinicalSummary, fontSize = 12.sp, color = TextDark, lineHeight = 17.sp)

                // Food warnings tag
                if (res.foodWarnings.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Food Precautions: ${res.foodWarnings.joinToString("; ")}", fontSize = 11.sp, color = Color(0xFF92400E))
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  OutlinedButton(
                    onClick = { shareInteractionReport() },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                  ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Share WhatsApp Report", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                  }
                }
              }
            }
          }

          // Individual Interacting Drug Pair Cards
          items(res.interactions) { interaction ->
            val (badgeBg, badgeColor, sevLabel) = when (interaction.severity) {
              InteractionSeverity.SEVERE_CONTRAINDICATED -> Triple(Color(0xFFFEF2F2), StatusRed, "SEVERE / CONTRAINDICATED")
              InteractionSeverity.MODERATE_MONITOR -> Triple(Color(0xFFFFFBEB), Color(0xFFD97706), "MODERATE MONITORING")
              InteractionSeverity.MINOR_CAUTION -> Triple(Color(0xFFEFF6FF), Color(0xFF2563EB), "MINOR CAUTION")
              InteractionSeverity.NO_KNOWN_INTERACTION -> Triple(Color(0xFFF0FDF4), StatusGreen, "SAFE")
            }

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
                    Icon(Icons.Default.WarningAmber, contentDescription = null, tint = badgeColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "${interaction.drug1} ⚡ ${interaction.drug2}",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(badgeBg)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(sevLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = interaction.riskTitle,
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextDark
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Pharmacological Mechanism: ${interaction.mechanism}",
                  fontSize = 11.5.sp,
                  color = TextMuted,
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(8.dp)
                ) {
                  Column {
                    Text(
                      text = "Pharmacist Clinical Advice:",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalNavy
                    )
                    Text(
                      text = interaction.clinicalAdvice,
                      fontSize = 11.5.sp,
                      color = TextDark
                    )
                  }
                }

                if (interaction.alternativeSuggestion.isNotBlank()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "💡 Safer Alternative: ${interaction.alternativeSuggestion}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF047857)
                  )
                }

                if (interaction.foodPrecaution.isNotBlank()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "🍽 Food Warning: ${interaction.foodPrecaution}",
                    fontSize = 11.sp,
                    color = Color(0xFFB45309)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
