package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineItem
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Individual medicine edit/view modal in the Drug Database (Stock Screen).
 * Features a dedicated "Batch Details" section allowing editing and displaying:
 * - Expiry date (with presets and validation)
 * - Manufacturer (with prominent pharma laboratory chips)
 * - Storage rack location (with rack/fridge presets)
 * - Specific batch number and allocated stock
 */
@Composable
fun MedicineDetailEditDialog(
  item: MedicineItem,
  onDismiss: () -> Unit,
  onSave: (MedicineItem) -> Unit
) {
  var name by remember { mutableStateOf(item.name) }
  var composition by remember { mutableStateOf(item.composition.ifBlank { item.saltMolecule }) }
  var hsnCode by remember { mutableStateOf(item.hsnCode.ifBlank { "3004" }) }
  var category by remember { mutableStateOf(item.category) }
  var mrpText by remember { mutableStateOf(if (item.mrp > 0) item.mrp.toString() else "35.0") }
  var reorderLevelText by remember { mutableStateOf(item.minStockAlert.toString()) }

  // Batch Details Section State
  var batchNumber by remember {
    mutableStateOf(item.batchNumber.ifBlank { "BX-" + (1000..9999).random() })
  }
  var expiryDate by remember {
    mutableStateOf(item.expiryDate.ifBlank { "2027-12-31" })
  }
  var manufacturer by remember {
    mutableStateOf(item.manufacturer.ifBlank { "Cipla Healthcare Ltd" })
  }
  var rackLocation by remember {
    mutableStateOf(item.rackLocation.ifBlank { "Rack A-1" })
  }
  var stockPacksText by remember {
    mutableStateOf(item.stockPacks.toString())
  }

  val mfgPresets = listOf("Cipla Healthcare", "Sun Pharma", "Abbott", "Mankind Pharma", "Dr. Reddy's", "Micro Labs")
  val rackPresets = listOf("Rack A-1", "Rack A-2", "Rack B-1", "Rack B-3", "Cold Storage Shelf 1")
  val categoryPresets = listOf("Tablet", "Syrup", "Injection", "Ointment", "Antibiotic", "Analgesic", "OTC")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(RoyalMagenta.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Medication, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Edit Medicine & Batch", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(item.name, fontSize = 11.sp, color = TextMuted)
          }
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(18.dp))
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 520.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Top Banner Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFDF2F8))
            .border(1.dp, Color(0xFFFBCFE8), RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(name.ifBlank { "Medicine Name" }, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = RoyalMagenta)
              Text(composition.ifBlank { "Generic Salt / Formula" }, fontSize = 11.sp, color = Color(0xFF831843))
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Current MRP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              Text("₹$mrpText", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RoyalMagenta)
            }
          }
        }

        // Section 1: General Medicine Information
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "GENERAL MEDICINE INFORMATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
          )

          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Brand Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_edit_medicine_name")
          )

          OutlinedTextField(
            value = composition,
            onValueChange = { composition = it },
            label = { Text("Generic Composition / Salt") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = mrpText,
              onValueChange = { mrpText = it },
              label = { Text("MRP (₹)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
              value = reorderLevelText,
              onValueChange = { reorderLevelText = it.filter { ch -> ch.isDigit() } },
              label = { Text("Reorder Min") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
              value = hsnCode,
              onValueChange = { hsnCode = it },
              label = { Text("HSN Code") },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
          }

          // Category Chips
          Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            categoryPresets.forEach { cat ->
              FilterChip(
                selected = category.equals(cat, ignoreCase = true),
                onClick = { category = cat },
                label = { Text(cat, fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = RoyalNavy,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }

        // =====================================================================
        // SECTION 2: BATCH DETAILS (CORE REQUIREMENT)
        // =====================================================================
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEEF2FF))
            .border(1.5.dp, Color(0xFFC7D2FE), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("section_batch_details")
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header with Layers icon
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF4F46E5)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Layers, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text("Batch Details", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E1B4B))
                  Text("Expiry date, manufacturer & storage rack location", fontSize = 10.sp, color = Color(0xFF4338CA))
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFF4338CA))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("SPECIFIC BATCH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }

            // Batch Number Field
            OutlinedTextField(
              value = batchNumber,
              onValueChange = { batchNumber = it.uppercase(Locale.getDefault()) },
              label = { Text("Batch Number *") },
              singleLine = true,
              textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1B4B)
              ),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4F46E5),
                unfocusedBorderColor = Color(0xFFC7D2FE),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
              ),
              modifier = Modifier.fillMaxWidth().testTag("input_batch_number")
            )

            // Expiry Date Field & Presets
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = { Text("Expiry Date (YYYY-MM-DD or MM/YY) *") },
                leadingIcon = {
                  Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF991B1B)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color(0xFF4F46E5),
                  unfocusedBorderColor = Color(0xFFC7D2FE),
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().testTag("input_batch_expiry_date")
              )

              // Expiry Quick Presets
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text("Presets:", fontSize = 10.sp, color = TextMuted)
                listOf(6 to "+6 Mo", 12 to "+1 Yr", 24 to "+2 Yrs").forEach { (months, label) ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color.White)
                      .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(4.dp))
                      .clickable {
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.MONTH, months)
                        expiryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                      }
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
                  }
                }
              }
            }

            // Manufacturer Field & Suggestion Chips
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              OutlinedTextField(
                value = manufacturer,
                onValueChange = { manufacturer = it },
                label = { Text("Manufacturer *") },
                leadingIcon = {
                  Icon(Icons.Default.Domain, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color(0xFF4F46E5),
                  unfocusedBorderColor = Color(0xFFC7D2FE),
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().testTag("input_batch_manufacturer")
              )

              // Mfg Suggestion Chips
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                mfgPresets.forEach { mfg ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color.White)
                      .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(4.dp))
                      .clickable { manufacturer = mfg }
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(mfg, fontSize = 10.sp, color = Color(0xFF334155))
                  }
                }
              }
            }

            // Storage Rack Location Field & Presets
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              OutlinedTextField(
                value = rackLocation,
                onValueChange = { rackLocation = it },
                label = { Text("Storage Rack Location *") },
                leadingIcon = {
                  Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(16.dp))
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color(0xFF4F46E5),
                  unfocusedBorderColor = Color(0xFFC7D2FE),
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().testTag("input_batch_rack_location")
              )

              // Rack Location Suggestion Chips
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                rackPresets.forEach { rack ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color.White)
                      .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(4.dp))
                      .clickable { rackLocation = rack }
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(rack, fontSize = 10.sp, color = Color(0xFF334155))
                  }
                }
              }
            }

            // Allocated Stock for this Batch
            OutlinedTextField(
              value = stockPacksText,
              onValueChange = { stockPacksText = it.filter { ch -> ch.isDigit() } },
              label = { Text("Allocated Stock for this Batch (units/packs)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4F46E5),
                unfocusedBorderColor = Color(0xFFC7D2FE),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
              ),
              modifier = Modifier.fillMaxWidth().testTag("input_batch_stock")
            )

            // Recorded Batch Summary Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("RECORDED BATCH SUMMARY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Batch #$batchNumber", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF4338CA))
                  Text("Exp: $expiryDate", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFB91C1C))
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Mfg: $manufacturer", fontSize = 10.5.sp, color = TextDark)
                  Text("Rack: $rackLocation", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color(0xFF0F766E))
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val updated = item.copy(
            name = name.trim().ifBlank { item.name },
            composition = composition.trim(),
            saltMolecule = composition.trim(),
            hsnCode = hsnCode.trim().ifBlank { "3004" },
            category = category,
            mrp = mrpText.toDoubleOrNull() ?: item.mrp,
            minStockAlert = reorderLevelText.toIntOrNull() ?: item.minStockAlert,
            batchNumber = batchNumber.trim().uppercase(Locale.getDefault()),
            expiryDate = expiryDate.trim(),
            manufacturer = manufacturer.trim(),
            rackLocation = rackLocation.trim(),
            stockPacks = stockPacksText.toIntOrNull() ?: item.stockPacks
          )
          onSave(updated)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        modifier = Modifier.testTag("btn_save_medicine_and_batch")
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Medicine & Batch")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}
