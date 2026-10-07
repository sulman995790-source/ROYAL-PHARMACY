package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Doctor
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen

@Composable
fun DoctorManagementScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val doctors by viewModel.allDoctors.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedSpecialty by remember { mutableStateOf("All") }
  var showAddDialog by remember { mutableStateOf(false) }
  var doctorToEdit by remember { mutableStateOf<Doctor?>(null) }
  var doctorToDelete by remember { mutableStateOf<Doctor?>(null) }

  val specialties = listOf(
    "All", "General Physician", "Neurologist", "Pediatrician", "Cardiologist",
    "Endocrinologist", "Nephrologist", "Psychiatrist", "Pulmonologist",
    "Gastroenterologist", "Surgeon", "Orthopedic", "Dermatologist",
    "Gynecologist", "ENT Specialist", "Ophthalmologist", "Oncologist", "Urologist"
  )

  val filteredDoctors = doctors.filter { doc ->
    val matchesSearch = doc.name.contains(searchQuery, ignoreCase = true) ||
      doc.specialty.contains(searchQuery, ignoreCase = true) ||
      doc.clinicHospital.contains(searchQuery, ignoreCase = true) ||
      doc.registrationNo.contains(searchQuery, ignoreCase = true)
    val matchesSpecialty = selectedSpecialty == "All" || doc.specialty.contains(selectedSpecialty, ignoreCase = true)
    matchesSearch && matchesSpecialty
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
          IconButton(onClick = { viewModel.navigateTo(Screen.MORE) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Column {
            Text(
              text = "Doctor Directory & CRM",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "${doctors.size} Prescribing Doctors Registered",
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
          Icon(Icons.Default.LocalHospital, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
        }
      }

      // 2. Search Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search doctor, clinic, MCI registration...", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = RoyalMagenta,
            unfocusedBorderColor = CardBorder
          ),
          modifier = Modifier.fillMaxWidth().testTag("input_search_doctors")
        )
      }

      // 3. Specialty Filter Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        specialties.forEach { spec ->
          val isSelected = selectedSpecialty == spec
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) RoyalMagenta else Color.White)
              .border(1.dp, if (isSelected) RoyalMagenta else CardBorder, RoundedCornerShape(20.dp))
              .clickable { selectedSpecialty = spec }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = spec,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Color.White else TextDark
            )
          }
        }
      }

      // 4. Doctors List
      if (filteredDoctors.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No doctors matching criteria", fontSize = 14.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { showAddDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
            ) {
              Text("+ Add Doctor Manually")
            }
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredDoctors, key = { it.id }) { doc ->
            DoctorItemCard(
              doctor = doc,
              onEdit = { doctorToEdit = doc },
              onDelete = { doctorToDelete = doc },
              onCall = {
                if (doc.phone.isNotBlank()) {
                  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${doc.phone}"))
                  try {
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "Cannot open dialer: ${e.message}", Toast.LENGTH_SHORT).show()
                  }
                } else {
                  Toast.makeText(context, "No phone number configured", Toast.LENGTH_SHORT).show()
                }
              },
              onWhatsApp = {
                if (doc.phone.isNotBlank()) {
                  val cleanNum = doc.phone.replace(Regex("[^0-9]"), "")
                  val url = "https://api.whatsapp.com/send?phone=$cleanNum&text=Greetings%20${Uri.encode(doc.name)}%2C%20Royal%20Pharmacy%20Mangaldai."
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                  try {
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    Toast.makeText(context, "WhatsApp not installed: ${e.message}", Toast.LENGTH_SHORT).show()
                  }
                }
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      }
    }

    // Floating Button: "+ Add Doctor"
    Button(
      onClick = { showAddDialog = true },
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_add_doctor")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Add Doctor", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }

    // Add / Edit Dialog
    if (showAddDialog || doctorToEdit != null) {
      DoctorFormDialog(
        initialDoctor = doctorToEdit,
        onDismiss = {
          showAddDialog = false
          doctorToEdit = null
        },
        onSave = { savedDoc ->
          if (doctorToEdit != null) {
            viewModel.updateDoctor(savedDoc)
          } else {
            viewModel.addDoctor(savedDoc)
          }
          showAddDialog = false
          doctorToEdit = null
        }
      )
    }

    // Delete Confirmation Dialog
    doctorToDelete?.let { doc ->
      AlertDialog(
        onDismissRequest = { doctorToDelete = null },
        title = { Text("Remove Doctor", fontWeight = FontWeight.Bold) },
        text = { Text("Are you sure you want to remove ${doc.name} from your pharmacy directory?") },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deleteDoctor(doc)
              doctorToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
          ) {
            Text("Delete", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { doctorToDelete = null }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }
  }
}

@Composable
fun DoctorItemCard(
  doctor: Doctor,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onCall: () -> Unit,
  onWhatsApp: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
    modifier = Modifier.fillMaxWidth().testTag("doctor_card_${doctor.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFFEDE9FE)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = doctor.name.take(2).uppercase(),
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF6D28D9)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = doctor.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                Icons.Default.Verified,
                contentDescription = "Verified Doctor",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(15.dp)
              )
            }
            Text(
              text = "${doctor.degree} • ${doctor.specialty}",
              fontSize = 11.5.sp,
              color = Color(0xFF4B5563)
            )
            if (doctor.clinicHospital.isNotBlank()) {
              Text(
                text = doctor.clinicHospital,
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }
        }

        // Action Icons (Edit / Delete)
        Row {
          IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF4B5563), modifier = Modifier.size(16.dp))
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Badges Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Prescription Count Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFEFF6FF))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "${doctor.prescriptionCount} Prescriptions",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D4ED8)
              )
            }
          }

          // Auto-added badge
          if (doctor.autoAddedFromRx) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF3E8FF))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Rx Scanned", fontSize = 10.5.sp, color = Color(0xFF7E22CE), fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }

        // Quick Call & WhatsApp buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFE0F2FE))
              .clickable { onCall() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0369A1), modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFDCFCE7))
              .clickable { onWhatsApp() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color(0xFF15803D), modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
            }
          }
        }
      }
    }
  }
}

@Composable
fun DoctorFormDialog(
  initialDoctor: Doctor?,
  onDismiss: () -> Unit,
  onSave: (Doctor) -> Unit
) {
  var name by remember { mutableStateOf(initialDoctor?.name ?: "Dr. ") }
  var degree by remember { mutableStateOf(initialDoctor?.degree ?: "MBBS, MD") }
  var specialty by remember { mutableStateOf(initialDoctor?.specialty ?: "General Medicine") }
  var clinicHospital by remember { mutableStateOf(initialDoctor?.clinicHospital ?: "") }
  var phone by remember { mutableStateOf(initialDoctor?.phone ?: "+91 ") }
  var email by remember { mutableStateOf(initialDoctor?.email ?: "") }
  var registrationNo by remember { mutableStateOf(initialDoctor?.registrationNo ?: "") }
  var notes by remember { mutableStateOf(initialDoctor?.notes ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (initialDoctor != null) "Edit Doctor Profile" else "Add Prescribing Doctor",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Doctor Name*") },
          modifier = Modifier.fillMaxWidth().testTag("input_doc_name")
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = degree,
            onValueChange = { degree = it },
            label = { Text("Degree (e.g. MBBS, MD)") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = specialty,
            onValueChange = { specialty = it },
            label = { Text("Specialty*") },
            modifier = Modifier.weight(1f)
          )
        }
        OutlinedTextField(
          value = clinicHospital,
          onValueChange = { clinicHospital = it },
          label = { Text("Hospital / Clinic Name") },
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number*") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = registrationNo,
            onValueChange = { registrationNo = it },
            label = { Text("MCI / Registration No") },
            modifier = Modifier.weight(1f)
          )
        }
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Clinical Notes / Preferred Brands") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val cleanName = if (name.startsWith("Dr", ignoreCase = true)) name.trim() else "Dr. ${name.trim()}"
          val doc = (initialDoctor ?: Doctor(name = cleanName)).copy(
            name = cleanName,
            degree = degree.trim(),
            specialty = specialty.trim(),
            clinicHospital = clinicHospital.trim(),
            phone = phone.trim(),
            email = email.trim(),
            registrationNo = registrationNo.trim(),
            notes = notes.trim()
          )
          onSave(doc)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        modifier = Modifier.testTag("btn_save_doctor")
      ) {
        Text("Save Doctor")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}
