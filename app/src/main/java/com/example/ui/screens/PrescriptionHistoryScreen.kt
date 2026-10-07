package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrescriptionRecord
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionHistoryScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val prescriptions by viewModel.allPrescriptions.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var filterStatus by remember { mutableStateOf("All") }
  var showAddManualDialog by remember { mutableStateOf(false) }

  val filteredPrescriptions = prescriptions.filter { rx ->
    val matchesSearch = rx.patientName.contains(searchQuery, ignoreCase = true) ||
      rx.doctorName.contains(searchQuery, ignoreCase = true) ||
      rx.medicinesSummary.contains(searchQuery, ignoreCase = true) ||
      rx.clinicName.contains(searchQuery, ignoreCase = true)

    val matchesFilter = when (filterStatus) {
      "Active" -> rx.status == "Active"
      "Dispensed" -> rx.status == "Dispensed"
      "Refill Due" -> rx.status == "Refill Due" || rx.refillDueDate.isNotBlank()
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
              text = "Prescription History",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "${prescriptions.size} Rx Logs • AI Scanned & Doctor Files",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.PRESCRIPTION_SCANNER) }) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Scan New Rx", tint = RoyalMagenta, modifier = Modifier.size(20.dp))
          }
          IconButton(onClick = { showAddManualDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Add Manual Rx", tint = RoyalNavy, modifier = Modifier.size(20.dp))
          }
        }
      }

      // 2. Search & Filter Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by patient, doctor, or drug name...", fontSize = 13.sp, color = TextMuted) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_search_prescription")
        )
      }

      // 3. Status Filters
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("All", "Active", "Dispensed", "Refill Due").forEach { st ->
          val isSel = filterStatus == st
          FilterChip(
            selected = isSel,
            onClick = { filterStatus = st },
            label = { Text(st, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalNavy,
              selectedLabelColor = Color.White,
              containerColor = GrayBackground,
              labelColor = TextDark
            )
          )
        }
      }

      // 4. Prescription History List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
      ) {
        items(filteredPrescriptions, key = { it.id }) { rx ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth().testTag("prescription_item_${rx.id}")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(RoyalMagentaLight),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(rx.patientName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${rx.patientAgeGender} • ${rx.prescriptionDate}", fontSize = 11.sp, color = TextMuted)
                  }
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (rx.status == "Active") StatusGreenLight else Color(0xFFF1F5F9))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = rx.status.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rx.status == "Active") StatusGreen else TextDark
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Prescribed by: ${rx.doctorName} (${rx.clinicName})",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalNavy
              )

              if (rx.diagnosisNotes.isNotBlank()) {
                Text(
                  text = "Diagnosis: ${rx.diagnosisNotes}",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              // Prescribed Medicines Highlight
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFF8FAFC))
                  .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text(
                  text = "Rx: ${rx.medicinesSummary}",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextDark
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Action Buttons: Dispense / WhatsApp Instructions
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    viewModel.startBillingForPatient(
                      com.example.data.model.Patient(
                        name = rx.patientName,
                        contactNumber = rx.patientPhone
                      )
                    )
                    Toast.makeText(context, "Started billing for ${rx.patientName}!", Toast.LENGTH_SHORT).show()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(38.dp).testTag("btn_dispense_prescription_${rx.id}")
                ) {
                  Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Dispense & Bill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                  onClick = {
                    val shareText = """
                      📋 *PRESCRIPTION INSTRUCTIONS*
                      👤 Patient: ${rx.patientName} (${rx.patientAgeGender})
                      👨‍⚕️ Prescribed by: ${rx.doctorName}
                      📅 Date: ${rx.prescriptionDate}
                      
                      💊 *Medicines & Dosage*:
                      ${rx.medicinesSummary}
                      
                      🏥 Dispensed with care by ROYAL PHARMACY.
                    """.trimIndent()
                    val intent = Intent(Intent.ACTION_SEND).apply {
                      type = "text/plain"
                      putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Dosage Instructions"))
                  },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(38.dp)
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Share Dosage", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
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

    // Modal Dialog: Add Manual Prescription
    if (showAddManualDialog) {
      var pName by remember { mutableStateOf("") }
      var pAgeGender by remember { mutableStateOf("30 / Male") }
      var pPhone by remember { mutableStateOf("") }
      var dName by remember { mutableStateOf("Dr. Sharma") }
      var clinic by remember { mutableStateOf("City Polyclinic") }
      var meds by remember { mutableStateOf("") }
      var diag by remember { mutableStateOf("Seasonal fever & cough") }

      AlertDialog(
        onDismissRequest = { showAddManualDialog = false },
        title = {
          Text("Archive Prescription", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = pName,
              onValueChange = { pName = it },
              label = { Text("Patient Name*") },
              placeholder = { Text("e.g. Ramesh Kumar") },
              modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = pAgeGender,
                onValueChange = { pAgeGender = it },
                label = { Text("Age / Gender") },
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = pPhone,
                onValueChange = { pPhone = it },
                label = { Text("Phone") },
                modifier = Modifier.weight(1f)
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = dName,
                onValueChange = { dName = it },
                label = { Text("Doctor Name") },
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = clinic,
                onValueChange = { clinic = it },
                label = { Text("Clinic") },
                modifier = Modifier.weight(1f)
              )
            }
            OutlinedTextField(
              value = meds,
              onValueChange = { meds = it },
              label = { Text("Medicines & Dosage*") },
              placeholder = { Text("e.g. Dolo 650 (1-0-1), Pan 40 (1-0-0)") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = diag,
              onValueChange = { diag = it },
              label = { Text("Diagnosis Notes") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (pName.isNotBlank() && meds.isNotBlank()) {
                viewModel.addPrescriptionRecord(
                  PrescriptionRecord(
                    patientName = pName,
                    patientAgeGender = pAgeGender,
                    patientPhone = pPhone,
                    doctorName = dName,
                    clinicName = clinic,
                    prescriptionDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
                    medicinesSummary = meds,
                    diagnosisNotes = diag,
                    status = "Active"
                  )
                )
                showAddManualDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
          ) {
            Text("Save Prescription")
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddManualDialog = false }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }
  }
}
