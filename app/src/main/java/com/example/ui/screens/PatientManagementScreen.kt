package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Patient
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PatientManagementScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val patients by viewModel.allPatients.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedCrmFilter by remember { mutableStateOf("All") }
  var showAddPatientDialog by remember { mutableStateOf(false) }
  var selectedPatientForDetail by remember { mutableStateOf<Patient?>(null) }
  var patientToAdjustLoyalty by remember { mutableStateOf<Patient?>(null) }

  // CRM Analytics computations
  val totalPatients = patients.size
  val chronicPatientsCount = patients.count { it.chronicConditions.isNotBlank() || it.crmTag == "Chronic Patient" }
  val refillDueCount = patients.count { it.nextRefillDueDate.isNotBlank() }
  val totalLoyaltyPool = patients.sumOf { it.loyaltyPoints }

  val filteredPatients = patients.filter { patient ->
    val matchesSearch = patient.name.contains(searchQuery, ignoreCase = true) ||
      patient.contactNumber.contains(searchQuery, ignoreCase = true) ||
      patient.chronicConditions.contains(searchQuery, ignoreCase = true) ||
      patient.knownAllergies.contains(searchQuery, ignoreCase = true)

    val matchesFilter = when (selectedCrmFilter) {
      "Refill Due" -> patient.nextRefillDueDate.isNotBlank()
      "Chronic Care" -> patient.chronicConditions.isNotBlank() || patient.crmTag == "Chronic Patient"
      "VIP Loyalty" -> patient.loyaltyPoints >= 100 || patient.crmTag == "VIP"
      "Allergies" -> patient.knownAllergies.isNotBlank()
      else -> true
    }
    matchesSearch && matchesFilter
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Header Bar
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
              text = "Patient CRM & Clinical Care",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Refill Tracking, Allergies & Loyalty Ledger",
              fontSize = 11.5.sp,
              color = TextMuted
            )
          }
        }

        Button(
          onClick = { showAddPatientDialog = true },
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("btn_new_patient_header")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("New Patient", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      // 2. CRM Metric KPI Cards
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Total Patients Card
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier.weight(1f).testTag("kpi_total_patients")
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Registered", fontSize = 10.sp, color = TextMuted)
            Text("$totalPatients", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
          }
        }

        // Refill Due Card
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A))
          ),
          modifier = Modifier.weight(1f).testTag("kpi_refill_due")
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Refills Due", fontSize = 10.sp, color = Color(0xFF92400E))
            Text("$refillDueCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
          }
        }

        // Chronic Patients Card
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier.weight(1f).testTag("kpi_chronic_patients")
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Chronic Care", fontSize = 10.sp, color = TextMuted)
            Text("$chronicPatientsCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
          }
        }

        // Loyalty Pool Card
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier.weight(1f).testTag("kpi_loyalty_pool")
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("Loyalty Pts", fontSize = 10.sp, color = TextMuted)
            Text("$totalLoyaltyPool", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
          }
        }
      }

      // 3. Search Bar
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search patient by name, mobile, chronic condition...", fontSize = 12.sp, color = TextMuted) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = RoyalMagenta
          ),
          modifier = Modifier.fillMaxWidth().testTag("patient_search_input")
        )
      }

      // 4. CRM Filter Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("All", "Refill Due", "Chronic Care", "VIP Loyalty", "Allergies").forEach { filter ->
          val isSelected = selectedCrmFilter == filter
          FilterChip(
            selected = isSelected,
            onClick = { selectedCrmFilter = filter },
            label = {
              Text(
                text = filter,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalMagentaLight,
              selectedLabelColor = RoyalMagenta,
              containerColor = Color.White,
              labelColor = TextMuted
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) RoyalMagenta else CardBorder
            ),
            modifier = Modifier.testTag("filter_${filter.lowercase().replace(" ", "_")}")
          )
        }
      }

      // 5. Patient Cards List
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (filteredPatients.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("No patients matching criteria.", fontSize = 13.sp, color = TextMuted)
            }
          }
        }

        items(filteredPatients, key = { it.id }) { patient ->
          Card(
            onClick = { selectedPatientForDetail = patient },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("patient_card_${patient.id}")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              // Top row: Avatar, Name, Loyalty & Tag
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(44.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(24.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(patient.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Spacer(modifier = Modifier.width(6.dp))
                      // Loyalty badge
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(0xFFFEF3C7))
                          .clickable { patientToAdjustLoyalty = patient }
                          .padding(horizontal = 5.dp, vertical = 2.dp)
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(10.dp))
                          Spacer(modifier = Modifier.width(2.dp))
                          Text("${patient.loyaltyPoints} pts", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        }
                      }
                    }
                    Text("DOB: ${patient.dob} (${patient.age} yrs, ${patient.gender}) • Mob: ${patient.contactNumber}", fontSize = 11.sp, color = TextMuted)
                  }
                }

                IconButton(
                  onClick = { viewModel.deletePatient(patient) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(16.dp))
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Chronic Conditions tag
              if (patient.chronicConditions.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.MedicalServices, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Chronic Care: ${patient.chronicConditions}",
                    fontSize = 11.5.sp,
                    color = RoyalNavy,
                    fontWeight = FontWeight.SemiBold
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
              }

              // Known Drug Allergies Warning Alert
              if (patient.knownAllergies.isNotBlank()) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFEE2E2))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Allergies Alert: ${patient.knownAllergies}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = StatusRed
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
              }

              // Refill Due Indicator Banner & Fast Actions
              if (patient.nextRefillDueDate.isNotBlank()) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFFFBEB))
                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Refill Due: ${patient.nextRefillDueDate} (${patient.refillCycleDays}d cycle)",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF92400E)
                    )
                  }

                  Text(
                    text = "Mark Refilled",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavy,
                    modifier = Modifier.clickable {
                      viewModel.markPatientRefillCompleted(patient)
                      Toast.makeText(context, "Refill logged for ${patient.name} (+25 pts rewarded)", Toast.LENGTH_SHORT).show()
                    }
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
              }

              // Fast Action Buttons: [WhatsApp Reminder] [Call] [New Bill]
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // WhatsApp Reminder
                OutlinedButton(
                  onClick = {
                    sendPatientWhatsAppReminder(context, patient)
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f).height(36.dp).testTag("btn_whatsapp_${patient.id}")
                ) {
                  Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("WhatsApp", fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                }

                // Call Patient
                OutlinedButton(
                  onClick = {
                    try {
                      val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${patient.contactNumber}"))
                      context.startActivity(intent)
                    } catch (e: Exception) {
                      Toast.makeText(context, "Dialer unavailable", Toast.LENGTH_SHORT).show()
                    }
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Icon(Icons.Default.Call, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Call", fontSize = 11.sp, color = RoyalNavy)
                }

                // Start Billing for this patient
                Button(
                  onClick = {
                    viewModel.startBillingForPatient(patient)
                  },
                  shape = RoundedCornerShape(6.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f).height(36.dp).testTag("btn_bill_patient_${patient.id}")
                ) {
                  Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Create Bill", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }

    // Add Patient Dialog
    if (showAddPatientDialog) {
      AddPatientCrmDialog(
        onDismiss = { showAddPatientDialog = false },
        onSave = { pat ->
          viewModel.addPatient(pat)
          showAddPatientDialog = false
          Toast.makeText(context, "Patient clinical profile created!", Toast.LENGTH_SHORT).show()
        }
      )
    }

    // Patient Deep-Dive Detail Dialog
    selectedPatientForDetail?.let { pat ->
      PatientDetailCrmDialog(
        patient = pat,
        onDismiss = { selectedPatientForDetail = null },
        onStartBill = {
          selectedPatientForDetail = null
          viewModel.startBillingForPatient(pat)
        },
        onMarkRefilled = {
          viewModel.markPatientRefillCompleted(pat)
          selectedPatientForDetail = null
        }
      )
    }

    // Quick Loyalty Points Adjustment Dialog
    patientToAdjustLoyalty?.let { pat ->
      PatientLoyaltyAdjustDialog(
        patient = pat,
        onDismiss = { patientToAdjustLoyalty = null },
        onAdjust = { delta ->
          viewModel.adjustPatientLoyalty(pat, delta)
          patientToAdjustLoyalty = null
        }
      )
    }
  }
}

private fun sendPatientWhatsAppReminder(context: Context, patient: Patient) {
  try {
    val message = "Namaste ${patient.name}! This is a friendly reminder from ROYAL PHARMACY. Your regular medicines${if (patient.chronicConditions.isNotBlank()) " for ${patient.chronicConditions}" else ""} are due for monthly refill${if (patient.nextRefillDueDate.isNotBlank()) " on ${patient.nextRefillDueDate}" else ""}. Please let us know if you would like your order prepared or delivered. Thank you!"
    val encoded = Uri.encode(message)
    val uri = Uri.parse("https://api.whatsapp.com/send?phone=+91${patient.contactNumber.replace("[^0-9]".toRegex(), "")}&text=$encoded")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
  } catch (e: Exception) {
    Toast.makeText(context, "Unable to launch WhatsApp", Toast.LENGTH_SHORT).show()
  }
}

@Composable
fun AddPatientCrmDialog(
  onDismiss: () -> Unit,
  onSave: (Patient) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var dob by remember { mutableStateOf("") }
  var ageText by remember { mutableStateOf("") }
  var gender by remember { mutableStateOf("Male") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var chronic by remember { mutableStateOf("") }
  var allergies by remember { mutableStateOf("") }
  var refillDue by remember { mutableStateOf("") }
  var refillCycle by remember { mutableStateOf("30") }
  var doctor by remember { mutableStateOf("Dr. Suleman Hoque") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("New Patient Clinical CRM Profile", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = androidx.compose.foundation.rememberScrollState().let { Modifier },
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Patient Name*") }, modifier = Modifier.fillMaxWidth().testTag("input_patient_name"))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = ageText, onValueChange = { ageText = it }, label = { Text("Age") }, placeholder = { Text("45") }, modifier = Modifier.weight(1f).testTag("input_patient_age"))
          OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact No*") }, modifier = Modifier.weight(2f).testTag("input_patient_phone"))
        }
        OutlinedTextField(value = chronic, onValueChange = { chronic = it }, label = { Text("Chronic Conditions (CRM)") }, placeholder = { Text("e.g. Type 2 Diabetes, Hypertension") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = allergies, onValueChange = { allergies = it }, label = { Text("Known Allergies*") }, placeholder = { Text("e.g. Penicillin, Sulfa, None") }, modifier = Modifier.fillMaxWidth().testTag("input_patient_allergies"))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = refillDue, onValueChange = { refillDue = it }, label = { Text("Next Refill Due") }, placeholder = { Text("15-10-2026") }, modifier = Modifier.weight(1f))
          OutlinedTextField(value = refillCycle, onValueChange = { refillCycle = it }, label = { Text("Cycle Days") }, modifier = Modifier.weight(1f))
        }
        OutlinedTextField(value = doctor, onValueChange = { doctor = it }, label = { Text("Preferred Doctor") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Residential Address") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val age = ageText.toIntOrNull() ?: 35
            val cycle = refillCycle.toIntOrNull() ?: 30
            onSave(
              Patient(
                name = name,
                dob = dob.ifBlank { "01-01-1990" },
                age = age,
                gender = gender,
                contactNumber = phone,
                address = address,
                chronicConditions = chronic,
                knownAllergies = allergies,
                nextRefillDueDate = refillDue,
                refillCycleDays = cycle,
                preferredDoctor = doctor,
                loyaltyPoints = 50 // 50 welcome bonus loyalty points!
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
      ) {
        Text("Save & Enroll (+50 Pts)")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
    }
  )
}

@Composable
fun PatientDetailCrmDialog(
  patient: Patient,
  onDismiss: () -> Unit,
  onStartBill: () -> Unit,
  onMarkRefilled: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Person, contentDescription = null, tint = RoyalNavy)
        Spacer(modifier = Modifier.width(8.dp))
        Text(patient.name, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Age & Gender: ${patient.age} yrs, ${patient.gender}", fontSize = 12.sp, color = TextDark)
        Text("Contact: ${patient.contactNumber}", fontSize = 12.sp, color = TextDark)
        Text("Address: ${patient.address}", fontSize = 12.sp, color = TextMuted)

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))

        Text("⭐ Loyalty Points: ${patient.loyaltyPoints} points", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
        Text("Chronic Care: ${if (patient.chronicConditions.isNotBlank()) patient.chronicConditions else "None listed"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = RoyalNavy)

        if (patient.knownAllergies.isNotBlank()) {
          Text("⚠️ Known Allergies: ${patient.knownAllergies}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusRed)
        }

        if (patient.nextRefillDueDate.isNotBlank()) {
          Text("📅 Next Refill Due: ${patient.nextRefillDueDate} (${patient.refillCycleDays}-day cycle)", fontSize = 12.sp, color = Color(0xFF92400E))
        }

        if (patient.preferredDoctor.isNotBlank()) {
          Text("Preferred Doctor: ${patient.preferredDoctor}", fontSize = 12.sp, color = TextMuted)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onStartBill,
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
      ) {
        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Create Bill")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Close", color = TextMuted) }
    }
  )
}

@Composable
fun PatientLoyaltyAdjustDialog(
  patient: Patient,
  onDismiss: () -> Unit,
  onAdjust: (Int) -> Unit
) {
  var deltaInput by remember { mutableStateOf("50") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Adjust Loyalty Points", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Patient: ${patient.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Current Balance: ${patient.loyaltyPoints} points", fontSize = 12.sp, color = Color(0xFFD97706))
        OutlinedTextField(
          value = deltaInput,
          onValueChange = { deltaInput = it },
          label = { Text("Points (+/-)") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val delta = deltaInput.toIntOrNull() ?: 0
          onAdjust(delta)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
      ) {
        Text("Update Balance")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
    }
  )
}
