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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
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
  var selectedCategory by remember { mutableStateOf("VOLUME") } // "VOLUME", "WEIGHT", "CONCENTRATION", "INSULIN", "POWDER"
  var inputValText by remember { mutableStateOf("10") }
  val inputVal = inputValText.toDoubleOrNull() ?: 10.0
  var targetMgPerMl by remember { mutableStateOf(100.0) }

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
              text = "Clinical Unit Converter",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Volume • Mass • Solutions • Insulin • Reconstitution",
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

      // 2. Converter Mode Selector Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Triple("VOLUME", "Liquid Volume", Icons.Default.Opacity),
          Triple("WEIGHT", "Weight & Mass", Icons.Default.Scale),
          Triple("CONCENTRATION", "% w/v & mg/ml", Icons.Default.Science),
          Triple("INSULIN", "Insulin Units (IU)", Icons.Default.Vaccines),
          Triple("POWDER", "Dry Reconstitution", Icons.Default.InvertColors)
        ).forEach { (catId, label, icon) ->
          val isSel = selectedCategory == catId
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSel) RoyalNavy else GrayBackground)
              .border(1.dp, if (isSel) RoyalNavy else CardBorder, RoundedCornerShape(8.dp))
              .clickable { selectedCategory = catId }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(icon, contentDescription = null, tint = if (isSel) Color.White else TextDark, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                color = if (isSel) Color.White else TextDark
              )
            }
          }
        }
      }

      // 3. Main Body
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
      ) {
        // Universal Input Value Box
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = when (selectedCategory) {
                  "VOLUME" -> "ENTER VOLUME IN MILLILITERS (ml)"
                  "WEIGHT" -> "ENTER MASS IN MILLIGRAMS (mg)"
                  "CONCENTRATION" -> "ENTER PERCENTAGE STRENGTH (% w/v)"
                  "INSULIN" -> "ENTER INSULIN UNITS (IU)"
                  else -> "ENTER TARGET VIAL DOSE (mg)"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
              )

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = inputValText,
                onValueChange = { inputValText = it },
                label = { Text("Base Input Value") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_converter_base_val")
              )
            }
          }
        }

        // MODE 1: LIQUID VOLUME
        if (selectedCategory == "VOLUME") {
          val ml = inputVal
          val drops = ml * 20.0 // standard 20 drops per ml
          val tsp = ml / 5.0 // 1 teaspoon = 5 ml
          val tbsp = ml / 15.0 // 1 tablespoon = 15 ml
          val flOz = ml / 29.5735 // 1 fluid ounce ≈ 30 ml
          val liters = ml / 1000.0

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("LIQUID EQUIVALENTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                ConverterResultRow("Drops / Minims (gtt)", String.format(Locale.getDefault(), "%.1f drops", drops), "1 ml ≈ 20 drops")
                ConverterResultRow("Teaspoons (tsp)", String.format(Locale.getDefault(), "%.2f tsp", tsp), "1 tsp = 5 ml")
                ConverterResultRow("Tablespoons (tbsp)", String.format(Locale.getDefault(), "%.2f tbsp", tbsp), "1 tbsp = 15 ml")
                ConverterResultRow("Fluid Ounces (fl oz)", String.format(Locale.getDefault(), "%.2f fl oz", flOz), "1 fl oz ≈ 30 ml")
                ConverterResultRow("Liters (L)", String.format(Locale.getDefault(), "%.4f L", liters), "1000 ml = 1 L")
              }
            }
          }
        }

        // MODE 2: WEIGHT & MASS
        if (selectedCategory == "WEIGHT") {
          val mg = inputVal
          val mcg = mg * 1000.0
          val grams = mg / 1000.0
          val kg = mg / 1000000.0
          val grains = mg / 64.79891 // 1 grain ≈ 65 mg (Aspirin/Codeine)
          val lbs = kg * 2.20462

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("MASS & PHARMA WEIGHT EQUIVALENTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                ConverterResultRow("Micrograms (mcg / μg)", String.format(Locale.getDefault(), "%.0f mcg", mcg), "1 mg = 1000 mcg")
                ConverterResultRow("Grams (g)", String.format(Locale.getDefault(), "%.4f g", grams), "1000 mg = 1 g")
                ConverterResultRow("Kilograms (kg)", String.format(Locale.getDefault(), "%.6f kg", kg), "1 kg = 1,000,000 mg")
                ConverterResultRow("Apothecary Grains (gr)", String.format(Locale.getDefault(), "%.2f gr", grains), "1 gr ≈ 64.8 mg")
                ConverterResultRow("Pounds (lbs)", String.format(Locale.getDefault(), "%.4f lbs", lbs), "1 kg = 2.205 lbs")
              }
            }
          }
        }

        // MODE 3: % W/V & MG/ML
        if (selectedCategory == "CONCENTRATION") {
          val percent = inputVal // e.g. 2%
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
                Text("PHARMACEUTICAL CONCENTRATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                ConverterResultRow("Concentration (mg / ml)", String.format(Locale.getDefault(), "%.1f mg/ml", mgPerMl), "$percent% w/v = 10 mg/ml per 1%")
                ConverterResultRow("Strength per 5ml Teaspoon", String.format(Locale.getDefault(), "%.1f mg / 5ml", mgPer5ml), "Standard syrup spoon")
                ConverterResultRow("Parts Per Million (PPM)", String.format(Locale.getDefault(), "%.0f PPM", ppm), "1% = 10,000 PPM")
                ConverterResultRow("Gram per Liter (g/L)", String.format(Locale.getDefault(), "%.1f g/L", mgPerMl), "Industrial batch dilution")
              }
            }
          }
        }

        // MODE 4: INSULIN UNITS (IU)
        if (selectedCategory == "INSULIN") {
          val units = inputVal // e.g. 40 IU
          val mlU100 = units / 100.0 // U-100 syringe: 1 unit = 0.01 ml
          val mlU40 = units / 40.0 // U-40 syringe: 1 unit = 0.025 ml

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("INSULIN SYRINGE & BIOLOGICAL DOSE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                ConverterResultRow("U-100 Syringe (Standard Orange)", String.format(Locale.getDefault(), "%.2f ml", mlU100), "100 Units = 1 ml (0.01 ml/unit)")
                ConverterResultRow("U-40 Syringe (Red Cap)", String.format(Locale.getDefault(), "%.2f ml", mlU40), "40 Units = 1 ml (0.025 ml/unit)")
                ConverterResultRow("Marking on U-100 Syringe", "${units.toInt()} Markings / Units", "Use calibrated syringe")
              }
            }
          }
        }

        // MODE 5: DRY POWDER RECONSTITUTION
        if (selectedCategory == "POWDER") {
          val vialMg = inputVal // e.g. 1000 mg vial
          val waterNeededMl = (vialMg / targetMgPerMl).coerceAtLeast(1.0)

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("DRY POWDER SYRUP & VIAL RECONSTITUTION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)

                Text("Target Strength: $targetMgPerMl mg/ml", fontSize = 12.sp, color = TextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  listOf(50.0, 100.0, 125.0, 200.0, 250.0).forEach { t ->
                    val isSel = targetMgPerMl == t
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) RoyalMagenta else GrayBackground)
                        .border(1.dp, if (isSel) RoyalMagenta else CardBorder, RoundedCornerShape(6.dp))
                        .clickable { targetMgPerMl = t }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text("${t.toInt()} mg/ml", fontSize = 10.sp, color = if (isSel) Color.White else TextDark, fontWeight = FontWeight.Bold)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                ConverterResultRow(
                  "Sterile Water for Injection (SWFI) to Add",
                  String.format(Locale.getDefault(), "%.1f ml", waterNeededMl),
                  "Add water up to ring line or exact calculated volume"
                )
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
