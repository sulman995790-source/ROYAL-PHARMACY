package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.StatusGreenLight
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

data class DrugDosePreset(
  val drugName: String,
  val formulation: String,
  val concentrationMgPerMl: Double, // e.g., 100 mg/ml for infant drops, 24 mg/ml for syrup (120mg/5ml)
  val standardDoseMgPerKg: Double, // mg per kg per dose
  val frequencyDescription: String,
  val minAgeMonths: Int = 0,
  val maxDailyDoseMgPerKg: Double = 60.0,
  val defaultInstructions: String = "Take with water after food"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartDosageCalculatorScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Patient inputs
  var selectedAgeTier by remember { mutableStateOf("Neonatal / Infant (0-1 Yrs)") }
  var patientWeightKg by remember { mutableStateOf("6.5") }
  var patientAgeText by remember { mutableStateOf("6 Months") }
  var patientName by remember { mutableStateOf("Baby") }

  // Presets
  val drugPresets = listOf(
    DrugDosePreset(
      drugName = "Paracetamol (Infant Drops)",
      formulation = "100 mg / 1 ml (with calibrated dropper)",
      concentrationMgPerMl = 100.0,
      standardDoseMgPerKg = 15.0,
      frequencyDescription = "Every 4-6 hours as needed (Max 4 times/day)",
      minAgeMonths = 1,
      defaultInstructions = "Give using dropper. Max 4 doses in 24 hours."
    ),
    DrugDosePreset(
      drugName = "Paracetamol (Syrup)",
      formulation = "120 mg / 5 ml (24 mg/ml) or 250 mg / 5 ml",
      concentrationMgPerMl = 24.0,
      standardDoseMgPerKg = 15.0,
      frequencyDescription = "Every 4-6 hours (TDS / QDS)",
      minAgeMonths = 3,
      defaultInstructions = "Shake well before dispensing. Keep hydrated."
    ),
    DrugDosePreset(
      drugName = "Amoxicillin + Clavulanate (Augmentin Duo)",
      formulation = "228.5 mg / 5 ml (45.7 mg/ml) Dry Syrup",
      concentrationMgPerMl = 45.7,
      standardDoseMgPerKg = 20.0,
      frequencyDescription = "Twice daily (BD) for 5-7 days",
      minAgeMonths = 2,
      defaultInstructions = "Reconstitute with sterile water up to ring mark. Complete 5-day course."
    ),
    DrugDosePreset(
      drugName = "Ibuprofen (Pediatric Suspension)",
      formulation = "100 mg / 5 ml (20 mg/ml)",
      concentrationMgPerMl = 20.0,
      standardDoseMgPerKg = 10.0,
      frequencyDescription = "Every 6-8 hours with food (Max 3 times/day)",
      minAgeMonths = 6,
      defaultInstructions = "Always administer after feeding to avoid gastric irritation."
    ),
    DrugDosePreset(
      drugName = "Cetirizine (Syrup)",
      formulation = "5 mg / 5 ml (1 mg/ml)",
      concentrationMgPerMl = 1.0,
      standardDoseMgPerKg = 0.25,
      frequencyDescription = "Once daily at bedtime (OD) or BD",
      minAgeMonths = 6,
      defaultInstructions = "Give at night time. May cause mild drowsiness."
    ),
    DrugDosePreset(
      drugName = "Azithromycin (Suspension)",
      formulation = "100 mg / 5 ml (20 mg/ml) or 200 mg / 5 ml",
      concentrationMgPerMl = 20.0,
      standardDoseMgPerKg = 10.0,
      frequencyDescription = "Once daily (OD) for 3-5 days",
      minAgeMonths = 6,
      defaultInstructions = "Give 1 hour before food or 2 hours after food."
    ),
    DrugDosePreset(
      drugName = "Ondansetron (Emeset Drops/Syrup)",
      formulation = "2 mg / 5 ml (0.4 mg/ml) or 2 mg / 1 ml",
      concentrationMgPerMl = 0.4,
      standardDoseMgPerKg = 0.15,
      frequencyDescription = "Before feeding / oral fluids (Max TDS)",
      minAgeMonths = 1,
      defaultInstructions = "Give 30 minutes before oral rehydration solution (ORS)."
    ),
    DrugDosePreset(
      drugName = "Zinc Gluconate (Zincovit Drops)",
      formulation = "10 mg / 1 ml drops",
      concentrationMgPerMl = 10.0,
      standardDoseMgPerKg = 1.0,
      frequencyDescription = "Once daily for 14 days during diarrhea",
      minAgeMonths = 1,
      defaultInstructions = "Recommended for 14 days in acute diarrhea management."
    )
  )

  var selectedDrug by remember { mutableStateOf(drugPresets.first()) }

  // Calculation logic
  val weight = patientWeightKg.toDoubleOrNull() ?: 6.5
  val doseMg = (weight * selectedDrug.standardDoseMgPerKg).coerceAtLeast(0.0)
  val doseMl = (doseMg / selectedDrug.concentrationMgPerMl).coerceAtLeast(0.0)
  val dropsCount = (doseMl * 20).toInt() // standard dropper: ~20 drops per 1ml

  fun generateWhatsAppDosageSchedule(): String {
    return """
      👶 *SMART MEDICATION & DOSAGE SCHEDULE*
      👤 Patient: $patientName (Age: $patientAgeText, Weight: ${String.format(Locale.getDefault(), "%.1f", weight)} kg)
      💊 Medicine: *${selectedDrug.drugName}*
      🧪 Strength: ${selectedDrug.formulation}
      
      📏 *EXACT CALCULATED DOSAGE*:
      • Dose per Administration: *${String.format(Locale.getDefault(), "%.1f", doseMl)} ml* (${String.format(Locale.getDefault(), "%.0f", doseMg)} mg)
      • Dropper Count: *~$dropsCount drops*
      • Frequency: ${selectedDrug.frequencyDescription}
      
      🕒 *Administration Timing*:
      • Morning: ${String.format(Locale.getDefault(), "%.1f", doseMl)} ml
      • Afternoon (if 3x/day): ${String.format(Locale.getDefault(), "%.1f", doseMl)} ml
      • Night: ${String.format(Locale.getDefault(), "%.1f", doseMl)} ml
      
      💡 *Instructions*: ${selectedDrug.defaultInstructions}
      
      🏥 Calculated with care by ROYAL PHARMACY.
    """.trimIndent()
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
              text = "Smart Dosage & Reminders",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Neonatal (0-1y) • Child (1-12y) • Weight mg/kg & ml",
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
          Icon(Icons.Default.Calculate, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
        }
      }

      // 2. Scrollable Body
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
      ) {
        // Patient Age Category Selector
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("SELECT AGE GROUP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  "Neonatal / Infant (0-1 Yrs)",
                  "Young Child (1-5 Yrs)",
                  "Older Child (6-12 Yrs)",
                  "Adult / Senior (12+ Yrs)"
                ).forEach { tier ->
                  val isSel = selectedAgeTier == tier
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSel) RoyalNavy else GrayBackground)
                      .border(1.dp, if (isSel) RoyalNavy else CardBorder, RoundedCornerShape(8.dp))
                      .clickable {
                        selectedAgeTier = tier
                        if (tier.contains("0-1")) {
                          patientWeightKg = "6.5"
                          patientAgeText = "6 Months"
                        } else if (tier.contains("1-5")) {
                          patientWeightKg = "14.0"
                          patientAgeText = "3 Years"
                        } else if (tier.contains("6-12")) {
                          patientWeightKg = "28.0"
                          patientAgeText = "8 Years"
                        } else {
                          patientWeightKg = "60.0"
                          patientAgeText = "35 Years"
                        }
                      }
                      .padding(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Text(
                      text = tier,
                      fontSize = 11.sp,
                      fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSel) Color.White else TextDark
                    )
                  }
                }
              }
            }
          }
        }

        // Patient Weight & Name Input
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedTextField(
                  value = patientName,
                  onValueChange = { patientName = it },
                  label = { Text("Patient Name") },
                  modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                  value = patientAgeText,
                  onValueChange = { patientAgeText = it },
                  label = { Text("Age") },
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = patientWeightKg,
                onValueChange = { patientWeightKg = it },
                label = { Text("Patient Weight in Kilograms (kg)*") },
                placeholder = { Text("e.g. 7.5") },
                leadingIcon = {
                  Icon(Icons.Default.Scale, contentDescription = null, tint = RoyalMagenta)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_patient_weight")
              )
            }
          }
        }

        // Medicine Selector
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text("SELECT PEDIATRIC / ESSENTIAL DRUG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                drugPresets.forEach { drug ->
                  val isSel = selectedDrug.drugName == drug.drugName
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSel) RoyalMagenta else GrayBackground)
                      .border(1.dp, if (isSel) RoyalMagenta else CardBorder, RoundedCornerShape(8.dp))
                      .clickable { selectedDrug = drug }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = drug.drugName,
                      fontSize = 11.sp,
                      fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSel) Color.White else TextDark
                    )
                  }
                }
              }
            }
          }
        }

        // Live Calculated Dosage Result Card
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE))),
            modifier = Modifier.fillMaxWidth().testTag("dosage_result_card")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(selectedDrug.drugName, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RoyalNavy)
                  Text("Strength: ${selectedDrug.formulation}", fontSize = 11.sp, color = Color(0xFF1E40AF))
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFDBEAFE))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "${selectedDrug.standardDoseMgPerKg} mg/kg/dose",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // High-visibility Dosage Metric Badges
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                // Exact Volume (ml)
                Column(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("SINGLE DOSE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text(
                    text = String.format(Locale.getDefault(), "%.1f ml", doseMl),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalNavy
                  )
                  Text("(${String.format(Locale.getDefault(), "%.0f", doseMg)} mg)", fontSize = 10.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Dropper Count (Drops)
                Column(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("DROPPER COUNT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Text(
                    text = "~$dropsCount drops",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalMagenta
                  )
                  Text("(Standard 20 gtt/ml)", fontSize = 10.sp, color = TextMuted)
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "Frequency: ${selectedDrug.frequencyDescription}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
              )
              Text(
                text = "Clinical Instruction: ${selectedDrug.defaultInstructions}",
                fontSize = 11.sp,
                color = Color(0xFF3B82F6)
              )
            }
          }
        }

        // WhatsApp Dosage Reminder Card & Share Button
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Smart Dosage Schedule Reminder", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Formatted preview box
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF8FAFC))
                  .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Text(
                  text = "🌅 Morning: ${String.format(Locale.getDefault(), "%.1f", doseMl)} ml\n" +
                    "☀️ Afternoon: ${String.format(Locale.getDefault(), "%.1f", doseMl)} ml (if 3x)\n" +
                    "🌙 Night: ${String.format(Locale.getDefault(), "%.1f", doseMl)} ml\n" +
                    "Notes: ${selectedDrug.defaultInstructions}",
                  fontSize = 11.5.sp,
                  lineHeight = 17.sp,
                  color = TextDark
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Dosage Card", generateWhatsAppDosageSchedule()))
                    Toast.makeText(context, "Dosage card copied to clipboard!", Toast.LENGTH_SHORT).show()
                  },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(44.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Copy Schedule", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                }

                Button(
                  onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                      type = "text/plain"
                      putExtra(Intent.EXTRA_TEXT, generateWhatsAppDosageSchedule())
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Send Dosage Schedule on WhatsApp"))
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(44.dp).testTag("btn_share_dosage_whatsapp")
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Send WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
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
