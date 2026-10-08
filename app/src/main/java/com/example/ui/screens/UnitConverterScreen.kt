package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("INSULIN") } // "INSULIN", "POWDER", "VOLUME", "WEIGHT", "CONCENTRATION"

  // Standard input
  var inputValText by remember { mutableStateOf("40") }
  val inputVal = inputValText.toDoubleOrNull() ?: 40.0

  // --- INSULIN SPECIFIC STATE ---
  var insulinWeightKgText by remember { mutableStateOf("60") }
  val insulinWeightKg = insulinWeightKgText.toDoubleOrNull() ?: 60.0
  var selectedInsulinRegimen by remember { mutableStateOf("Type 1 DM (0.5 IU/kg)") } // 0.5 IU/kg, Basal T2DM (0.2 IU/kg), Intensive (0.4 IU/kg)
  var syringeInputUnits by remember { mutableStateOf("40") }

  // --- DRY RECONSTITUTION SPECIFIC STATE ---
  var powderUnitMode by remember { mutableStateOf("GRAMS") } // "GRAMS" or "MG"
  var powderVialValueText by remember { mutableStateOf("1") } // e.g. 1g or 1000mg
  val rawVialVal = powderVialValueText.toDoubleOrNull() ?: 1.0
  val effectiveVialMg = if (powderUnitMode == "GRAMS") rawVialVal * 1000.0 else rawVialVal

  var diluentAddedMlText by remember { mutableStateOf("10") }
  val diluentAddedMl = (diluentAddedMlText.toDoubleOrNull() ?: 10.0).coerceAtLeast(0.5)

  var powderDisplacementMlText by remember { mutableStateOf("0.6") }
  val powderDisplacementMl = (powderDisplacementMlText.toDoubleOrNull() ?: 0.6).coerceAtLeast(0.0)

  val totalReconstitutedMl = diluentAddedMl + powderDisplacementMl
  val resultingConcMgPerMl = if (totalReconstitutedMl > 0) effectiveVialMg / totalReconstitutedMl else 0.0

  var desiredPrescribedDoseMgText by remember { mutableStateOf("250") }
  val desiredPrescribedDoseMg = desiredPrescribedDoseMgText.toDoubleOrNull() ?: 250.0
  val mlToWithdraw = if (resultingConcMgPerMl > 0) desiredPrescribedDoseMg / resultingConcMgPerMl else 0.0

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
          IconButton(
            onClick = { viewModel.navigateTo(Screen.HOME) },
            modifier = Modifier.testTag("btn_back_unit_converter")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Column {
            Text(
              text = "Clinical Unit Converter",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Insulin (IU/mL) • Dry Reconstitution • Solutions • Drops",
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
          Icon(Icons.Default.Science, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
        }
      }

      // 2. Category Selector Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Triple("INSULIN", "💉 Insulin (IU / Syringes)", Icons.Default.Vaccines),
          Triple("POWDER", "🧪 Dry Reconstitution", Icons.Default.InvertColors),
          Triple("VOLUME", "💧 Liquid Volume", Icons.Default.Opacity),
          Triple("WEIGHT", "⚖️ Mass & mg/mcg", Icons.Default.Scale),
          Triple("CONCENTRATION", "🔬 % w/v Solutions", Icons.Default.Science)
        ).forEach { (catId, label, _) ->
          val isSel = selectedCategory == catId
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSel) RoyalNavy else GrayBackground)
              .border(1.dp, if (isSel) RoyalNavy else CardBorder, RoundedCornerShape(8.dp))
              .clickable { selectedCategory = catId }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text(
              text = label,
              fontSize = 11.5.sp,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
              color = if (isSel) Color.White else TextDark
            )
          }
        }
      }

      // 3. Converter Content Body
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {

        // ==========================================
        // MODE: INSULIN UNITS (IU) & SYRINGES
        // ==========================================
        if (selectedCategory == "INSULIN") {
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Vaccines, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("INSULIN DOSE & SYRINGE VOLUME CALCULATOR", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                }
                Text("Standard international units (IU), U-100 & U-40 calibration equivalents:", fontSize = 11.sp, color = TextMuted)

                // Prescribed Insulin Dose Input
                OutlinedTextField(
                  value = syringeInputUnits,
                  onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) syringeInputUnits = it },
                  label = { Text("Prescribed Insulin Dose (IU)") },
                  trailingIcon = { Text("IU", fontWeight = FontWeight.Bold, color = RoyalMagenta, modifier = Modifier.padding(end = 12.dp)) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth().testTag("input_insulin_units")
                )

                // Quick Dose Chips
                Row(
                  modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf("10", "20", "30", "40", "60", "80").forEach { d ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (syringeInputUnits == d) RoyalMagenta else GrayBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                        .clickable { syringeInputUnits = d }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text("$d IU", fontSize = 11.sp, color = if (syringeInputUnits == d) Color.White else TextDark, fontWeight = FontWeight.Bold)
                    }
                  }
                }

                val currentIU = syringeInputUnits.toDoubleOrNull() ?: 40.0
                val mlU100 = currentIU / 100.0 // 1 IU = 0.01 mL
                val mlU40 = currentIU / 40.0   // 1 IU = 0.025 mL
                val mlU500 = currentIU / 500.0 // 1 IU = 0.002 mL

                Spacer(modifier = Modifier.height(4.dp))

                // Syringe Results
                ConverterResultRow(
                  unitLabel = "U-100 Syringe (Standard Orange Cap)",
                  convertedValue = String.format(Locale.getDefault(), "%.2f mL", mlU100),
                  formulaNote = "100 IU/mL standard. Draw to ${currentIU.toInt()} markings on U-100 syringe (0.01 mL/unit)."
                )

                ConverterResultRow(
                  unitLabel = "U-40 Syringe (Red Cap - Retail / Hospital)",
                  convertedValue = String.format(Locale.getDefault(), "%.3f mL", mlU40),
                  formulaNote = "40 IU/mL formulation. Draw to ${currentIU.toInt()} markings on U-40 syringe (0.025 mL/unit)."
                )

                ConverterResultRow(
                  unitLabel = "U-500 High-Potency Syringe",
                  convertedValue = String.format(Locale.getDefault(), "%.3f mL", mlU500),
                  formulaNote = "500 IU/mL concentrated insulin for extreme insulin resistance (0.002 mL/unit)."
                )

                // Critical Syringe Safety Warning
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFEF2F2))
                    .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(8.dp))
                    .padding(10.dp)
                ) {
                  Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "SAFETY ALERT: Always match the syringe to insulin vial type! Injecting U-100 insulin with a U-40 syringe delivers 2.5x the intended dose, precipitating fatal hypoglycemia.",
                      fontSize = 10.5.sp,
                      color = Color(0xFF991B1B),
                      lineHeight = 14.sp
                    )
                  }
                }
              }
            }
          }

          // Weight-Based Daily Insulin Dose Estimator
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("WEIGHT-BASED DAILY INSULIN DOSE ESTIMATOR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                  OutlinedTextField(
                    value = insulinWeightKgText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 5) insulinWeightKgText = it },
                    label = { Text("Patient Weight (kg)") },
                    trailingIcon = { Text("kg", fontWeight = FontWeight.Bold, color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                  )
                }

                // Regimen Options
                Row(
                  modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf(
                    "T1DM Initial (0.5 IU/kg)" to 0.5,
                    "T2DM Basal Start (0.2 IU/kg)" to 0.2,
                    "T2DM Intensive (0.4 IU/kg)" to 0.4
                  ).forEach { (label, factor) ->
                    val isSel = selectedInsulinRegimen == label
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) RoyalNavy else GrayBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedInsulinRegimen = label }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                      Text(label, fontSize = 10.5.sp, color = if (isSel) Color.White else TextDark, fontWeight = FontWeight.Medium)
                    }
                  }
                }

                val factor = when {
                  selectedInsulinRegimen.contains("0.5") -> 0.5
                  selectedInsulinRegimen.contains("0.2") -> 0.2
                  else -> 0.4
                }
                val totalDailyDose = insulinWeightKg * factor
                val basalDose = totalDailyDose * 0.5
                val bolusDose = totalDailyDose * 0.5
                val perMealBolus = bolusDose / 3.0

                ConverterResultRow(
                  unitLabel = "Total Daily Dose (TDD)",
                  convertedValue = String.format(Locale.getDefault(), "%.1f IU / day", totalDailyDose),
                  formulaNote = "Based on $insulinWeightKg kg body weight @ $factor IU/kg"
                )

                ConverterResultRow(
                  unitLabel = "Basal Insulin (Glargine / Degludec)",
                  convertedValue = String.format(Locale.getDefault(), "%.0f IU @ Bedtime", basalDose),
                  formulaNote = "50% of TDD administered once daily at bedtime"
                )

                ConverterResultRow(
                  unitLabel = "Bolus Rapid Insulin (Lispro / Aspart)",
                  convertedValue = String.format(Locale.getDefault(), "%.0f IU per Meal", perMealBolus),
                  formulaNote = "50% of TDD split equally before Breakfast, Lunch, and Dinner"
                )
              }
            }
          }
        }

        // ==========================================
        // MODE: DRY POWDER RECONSTITUTION
        // ==========================================
        if (selectedCategory == "POWDER") {
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.InvertColors, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("DRY POWDER VIAL & SYRUP RECONSTITUTION", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                }
                Text("Calculate exact diluent, powder displacement & final concentration (mg/mL):", fontSize = 11.sp, color = TextMuted)

                // Quick Clinical Presets
                Text("Popular Clinical Powder Presets:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Row(
                  modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf(
                    Triple("Ceftriaxone 1g (Monocef)", "1", "GRAMS"),
                    Triple("Ceftriaxone 500mg", "500", "MG"),
                    Triple("Augmentin 1.2g IV", "1.2", "GRAMS"),
                    Triple("Meropenem 1g", "1", "GRAMS"),
                    Triple("Pip-Tazo 4.5g", "4.5", "GRAMS"),
                    Triple("Pantoprazole 40mg IV", "40", "MG"),
                    Triple("Hydrocortisone 100mg", "100", "MG")
                  ).forEach { (label, valStr, unit) ->
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                        .clickable {
                          powderVialValueText = valStr
                          powderUnitMode = unit
                          if (valStr == "1" && unit == "GRAMS") {
                            diluentAddedMlText = "9.6"
                            powderDisplacementMlText = "0.4"
                            desiredPrescribedDoseMgText = "500"
                          } else if (valStr == "500" && unit == "MG") {
                            diluentAddedMlText = "4.8"
                            powderDisplacementMlText = "0.2"
                            desiredPrescribedDoseMgText = "250"
                          } else if (valStr == "40" && unit == "MG") {
                            diluentAddedMlText = "10.0"
                            powderDisplacementMlText = "0.0"
                            desiredPrescribedDoseMgText = "40"
                          }
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                      Text(label, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = RoyalNavy)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Vial Strength Input with Unit Selector Toggle
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedTextField(
                    value = powderVialValueText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) powderVialValueText = it },
                    label = { Text("Vial Dry Active Strength*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1.3f).testTag("input_powder_vial_strength")
                  )

                  // Unit Toggle: Grams vs Milligrams
                  Row(modifier = Modifier.weight(1f)) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                        .background(if (powderUnitMode == "GRAMS") RoyalMagenta else GrayBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                        .clickable { powderUnitMode = "GRAMS" }
                        .padding(horizontal = 10.dp, vertical = 14.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text("Grams (g)", fontSize = 11.sp, color = if (powderUnitMode == "GRAMS") Color.White else TextDark, fontWeight = FontWeight.Bold)
                    }
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .background(if (powderUnitMode == "MG") RoyalMagenta else GrayBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .clickable { powderUnitMode = "MG" }
                        .padding(horizontal = 10.dp, vertical = 14.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text("mg", fontSize = 11.sp, color = if (powderUnitMode == "MG") Color.White else TextDark, fontWeight = FontWeight.Bold)
                    }
                  }
                }

                Text(
                  text = "Active Salt in Vial: ${String.format(Locale.getDefault(), "%.0f mg", effectiveVialMg)}",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMagenta
                )

                // Diluent & Displacement Inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = diluentAddedMlText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 5) diluentAddedMlText = it },
                    label = { Text("Diluent (SWFI) to Add (mL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                  )
                  OutlinedTextField(
                    value = powderDisplacementMlText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 4) powderDisplacementMlText = it },
                    label = { Text("Powder Volume (mL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                  )
                }

                // Desired Dose Input
                OutlinedTextField(
                  value = desiredPrescribedDoseMgText,
                  onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) desiredPrescribedDoseMgText = it },
                  label = { Text("Desired Prescribed Patient Dose (mg)") },
                  trailingIcon = { Text("mg", fontWeight = FontWeight.Bold, color = TextMuted) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                  modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Calculated Results
                ConverterResultRow(
                  unitLabel = "Total Reconstituted Volume",
                  convertedValue = String.format(Locale.getDefault(), "%.2f mL", totalReconstitutedMl),
                  formulaNote = "Diluent ($diluentAddedMl mL) + Powder Displacement ($powderDisplacementMl mL)"
                )

                ConverterResultRow(
                  unitLabel = "Reconstituted Concentration",
                  convertedValue = String.format(Locale.getDefault(), "%.1f mg/mL", resultingConcMgPerMl),
                  formulaNote = "${String.format(Locale.getDefault(), "%.0f", effectiveVialMg)} mg divided by ${String.format(Locale.getDefault(), "%.2f", totalReconstitutedMl)} mL total volume"
                )

                ConverterResultRow(
                  unitLabel = "Volume to Withdraw for Desired Dose (${desiredPrescribedDoseMg.toInt()} mg)",
                  convertedValue = String.format(Locale.getDefault(), "%.2f mL", mlToWithdraw),
                  formulaNote = "Withdraw exactly ${String.format(Locale.getDefault(), "%.2f", mlToWithdraw)} mL using calibrated syringe"
                )
              }
            }
          }
        }

        // ==========================================
        // MODE: LIQUID VOLUME
        // ==========================================
        if (selectedCategory == "VOLUME") {
          val ml = inputVal
          val dropsStandard = ml * 20.0 // 1 ml = 20 drops
          val dropsMicro = ml * 60.0    // 1 ml = 60 microdrops
          val teaspoons = ml / 5.0      // 1 tsp = 5 ml
          val tablespoons = ml / 15.0   // 1 tbsp = 15 ml
          val fluidOunce = ml / 29.5735 // 1 fl oz = ~30 ml

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("LIQUID PHARMACEUTICAL EQUIVALENTS (INPUT: $ml mL)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                ConverterResultRow("Standard Medicine Drops (gtt)", String.format(Locale.getDefault(), "%.0f drops", dropsStandard), "1 ml = 20 drops (Standard adult dropper)")
                ConverterResultRow("Pediatric Microdrops (μgtt)", String.format(Locale.getDefault(), "%.0f microdrops", dropsMicro), "1 ml = 60 microdrops (Pediatric IV set)")
                ConverterResultRow("5ml Teaspoon (tsp)", String.format(Locale.getDefault(), "%.2f tsp", teaspoons), "1 Teaspoon = 5.0 ml")
                ConverterResultRow("15ml Tablespoon (tbsp)", String.format(Locale.getDefault(), "%.2f tbsp", tablespoons), "1 Tablespoon = 15.0 ml")
                ConverterResultRow("Fluid Ounces (fl oz)", String.format(Locale.getDefault(), "%.2f fl oz", fluidOunce), "1 fl oz ≈ 29.57 ml")
              }
            }
          }
        }

        // ==========================================
        // MODE: WEIGHT & MASS (mg, mcg, g)
        // ==========================================
        if (selectedCategory == "WEIGHT") {
          val mg = inputVal
          val mcg = mg * 1000.0
          val grams = mg / 1000.0
          val kg = mg / 1000000.0
          val grains = mg / 64.79891
          val lbs = kg * 2.20462

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("MASS & PHARMA WEIGHT EQUIVALENTS (INPUT: $mg mg)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                ConverterResultRow("Micrograms (mcg / μg)", String.format(Locale.getDefault(), "%.0f mcg", mcg), "1 mg = 1000 mcg")
                ConverterResultRow("Grams (g)", String.format(Locale.getDefault(), "%.4f g", grams), "1000 mg = 1 g")
                ConverterResultRow("Kilograms (kg)", String.format(Locale.getDefault(), "%.6f kg", kg), "1 kg = 1,000,000 mg")
                ConverterResultRow("Apothecary Grains (gr)", String.format(Locale.getDefault(), "%.2f gr", grains), "1 gr ≈ 64.8 mg")
                ConverterResultRow("Pounds (lbs)", String.format(Locale.getDefault(), "%.4f lbs", lbs), "1 kg = 2.205 lbs")
              }
            }
          }
        }

        // ==========================================
        // MODE: % W/V SOLUTIONS
        // ==========================================
        if (selectedCategory == "CONCENTRATION") {
          val percent = inputVal // e.g. 5%
          val mgPerMl = percent * 10.0 // 1% = 10 mg/ml
          val mgPer5ml = mgPerMl * 5.0
          val ppm = percent * 10000.0 // 1% = 10,000 PPM

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("PHARMACEUTICAL CONCENTRATION ($percent% w/v)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                ConverterResultRow("Concentration (mg / mL)", String.format(Locale.getDefault(), "%.1f mg/mL", mgPerMl), "$percent% w/v = 10 mg/mL per 1%")
                ConverterResultRow("Strength per 5ml Teaspoon", String.format(Locale.getDefault(), "%.1f mg / 5mL", mgPer5ml), "Standard syrup spoon dose")
                ConverterResultRow("Parts Per Million (PPM)", String.format(Locale.getDefault(), "%.0f PPM", ppm), "1% = 10,000 PPM")
                ConverterResultRow("Gram per Liter (g/L)", String.format(Locale.getDefault(), "%.1f g/L", mgPerMl), "Industrial batch dilution")
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(40.dp))
        }
      }
    }
  }
}

@Composable
fun ConverterResultRow(
  unitLabel: String,
  convertedValue: String,
  formulaNote: String
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFFF8FAFC))
      .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(unitLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(formulaNote, fontSize = 10.sp, color = TextMuted)
      }

      Text(
        text = convertedValue,
        fontSize = 15.sp,
        fontWeight = FontWeight.ExtraBold,
        color = RoyalMagenta
      )
    }
  }
}
