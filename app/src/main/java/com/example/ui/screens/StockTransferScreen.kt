package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineItem
import com.example.ui.theme.*
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun StockTransferScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val medicines by viewModel.allMedicines.collectAsState()
  val activityLogs by viewModel.staffActivityLogs.collectAsState()

  var selectedMedicine by remember { mutableStateOf<MedicineItem?>(null) }
  var medExpanded by remember { mutableStateOf(false) }

  var sourceLocation by remember { mutableStateOf("Main Warehouse Store") }
  var sourceExpanded by remember { mutableStateOf(false) }

  var destLocation by remember { mutableStateOf("Display Rack A1 (POS Front)") }
  var destExpanded by remember { mutableStateOf(false) }

  var transferQty by remember { mutableStateOf("25") }
  var transferNotes by remember { mutableStateOf("Routine shelf replenishment") }

  val locations = listOf(
    "Main Warehouse Store",
    "Cold Storage Room B",
    "Display Rack A1 (POS Front)",
    "Display Rack B2",
    "Emergency Dispensing Shelf",
    "Store Room #1"
  )

  val transferLogs = activityLogs.filter { it.actionType == "STOCK_TRANSFER" }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(Screen.STOCK) }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
              Text("Stock Transfer & Relocation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("Shift inventory between internal storage & display racks", fontSize = 11.sp, color = TextMuted)
            }
          }
        }
      }

      // Transfer Form Card
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("New Location Transfer", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            // 1. Source Location Dropdown
            ExposedDropdownMenuBox(expanded = sourceExpanded, onExpandedChange = { sourceExpanded = !sourceExpanded }) {
              OutlinedTextField(
                value = sourceLocation,
                onValueChange = {},
                readOnly = true,
                label = { Text("Source Storage Location*") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
              )
              ExposedDropdownMenu(expanded = sourceExpanded, onDismissRequest = { sourceExpanded = false }) {
                locations.filter { it != destLocation }.forEach { loc ->
                  DropdownMenuItem(text = { Text(loc) }, onClick = { sourceLocation = loc; sourceExpanded = false })
                }
              }
            }

            // 2. Destination Location Dropdown
            ExposedDropdownMenuBox(expanded = destExpanded, onExpandedChange = { destExpanded = !destExpanded }) {
              OutlinedTextField(
                value = destLocation,
                onValueChange = {},
                readOnly = true,
                label = { Text("Destination Rack / Shelf*") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
              )
              ExposedDropdownMenu(expanded = destExpanded, onDismissRequest = { destExpanded = false }) {
                locations.filter { it != sourceLocation }.forEach { loc ->
                  DropdownMenuItem(text = { Text(loc) }, onClick = { destLocation = loc; destExpanded = false })
                }
              }
            }

            // 3. Medicine Dropdown
            ExposedDropdownMenuBox(expanded = medExpanded, onExpandedChange = { medExpanded = !medExpanded }) {
              OutlinedTextField(
                value = selectedMedicine?.let { "${it.name} (Stock: ${it.stockPacks})" } ?: "Select Medicine Item*",
                onValueChange = {},
                readOnly = true,
                label = { Text("Medicine Item to Transfer*") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = medExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("dropdown_transfer_medicine")
              )
              ExposedDropdownMenu(expanded = medExpanded, onDismissRequest = { medExpanded = false }) {
                medicines.forEach { med ->
                  DropdownMenuItem(
                    text = { Text("${med.name} (Batch: ${med.batchNumber} • Stock: ${med.stockPacks})") },
                    onClick = { selectedMedicine = med; medExpanded = false }
                  )
                }
              }
            }

            // 4. Quantity & Notes
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedTextField(
                value = transferQty,
                onValueChange = { transferQty = it },
                label = { Text("Transfer Qty (Packs)*") },
                modifier = Modifier.weight(1f).testTag("input_transfer_qty")
              )
              OutlinedTextField(
                value = transferNotes,
                onValueChange = { transferNotes = it },
                label = { Text("Transfer Notes") },
                modifier = Modifier.weight(1.5f)
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
              onClick = {
                val med = selectedMedicine
                val qty = transferQty.toIntOrNull() ?: 0
                if (med != null && qty > 0) {
                  viewModel.transferStock(med.id, med.name, qty, sourceLocation, destLocation)
                  android.widget.Toast.makeText(context, "Successfully relocated $qty packs of ${med.name} to $destLocation", android.widget.Toast.LENGTH_SHORT).show()
                  selectedMedicine = null
                } else {
                  android.widget.Toast.makeText(context, "Please select medicine and valid quantity", android.widget.Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_confirm_transfer")
            ) {
              Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Execute Stock Relocation", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }

      // Recent Transfers Section header
      item {
        Text("Recent Transfer Audit Logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
      }

      if (transferLogs.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
              Text("No internal stock transfers recorded yet. Use the form above to shift inventory.", fontSize = 12.sp, color = TextMuted)
            }
          }
        }
      }

      items(transferLogs, key = { it.id }) { log ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(log.actionType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
              Text(log.timestamp, fontSize = 10.sp, color = TextMuted)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(log.description, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Logged by: ${log.staffName} (${log.staffRole})", fontSize = 10.5.sp, color = TextMuted)
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(70.dp))
      }
    }
  }
}
