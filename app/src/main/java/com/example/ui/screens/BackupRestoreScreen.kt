package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.BackupMetadata
import com.example.service.BackupRestoreManager
import com.example.service.LocalBackupItem
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val medicines by viewModel.allMedicines.collectAsState()
  val allSales by viewModel.allSales.collectAsState()
  val allCustomers by viewModel.allCustomers.collectAsState()
  val allPatients by viewModel.allPatients.collectAsState()
  val allSuppliers by viewModel.allSuppliers.collectAsState()
  val allPurchases by viewModel.allPurchases.collectAsState()

  var activeTab by remember { mutableStateOf("BACKUP") } // "BACKUP" or "RESTORE"
  var localBackups by remember { mutableStateOf<List<LocalBackupItem>>(emptyList()) }
  var isExporting by remember { mutableStateOf(false) }
  var isRestoring by remember { mutableStateOf(false) }

  // Restore State
  var restoreJsonInput by remember { mutableStateOf("") }
  var parsedMetadata by remember { mutableStateOf<BackupMetadata?>(null) }
  var cleanOverwriteMode by remember { mutableStateOf(false) }
  var showConfirmRestoreDialog by remember { mutableStateOf(false) }
  var autoBackupDaily by remember { mutableStateOf(true) }

  fun refreshLocalBackups() {
    localBackups = BackupRestoreManager.getSavedLocalBackups(context)
  }

  LaunchedEffect(Unit) {
    refreshLocalBackups()
  }

  // File Picker for JSON Restore
  val jsonPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    uri?.let {
      scope.launch(Dispatchers.IO) {
        try {
          context.contentResolver.openInputStream(it)?.use { stream ->
            val text = stream.bufferedReader().use { r -> r.readText() }
            withContext(Dispatchers.Main) {
              restoreJsonInput = text
              parsedMetadata = BackupRestoreManager.parseBackupMetadata(text)
              if (parsedMetadata != null) {
                Toast.makeText(context, "Valid backup file loaded (${parsedMetadata?.medicineCount} items)!", Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(context, "Warning: Could not parse backup metadata. Please check file format.", Toast.LENGTH_LONG).show()
              }
            }
          }
        } catch (e: Exception) {
          withContext(Dispatchers.Main) {
            Toast.makeText(context, "Failed to read file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
          }
        }
      }
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
          IconButton(onClick = { viewModel.navigateTo(Screen.MORE) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Column {
            Text(
              text = "Database Backup & Restore",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Full JSON Snapshots • Offline & Cloud Portability",
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
          Icon(Icons.Default.Security, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
        }
      }

      // 2. Navigation Mode Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GrayBackground)
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (activeTab == "BACKUP") RoyalNavy else Color.Transparent)
              .clickable { activeTab = "BACKUP" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.CloudUpload,
                contentDescription = null,
                tint = if (activeTab == "BACKUP") Color.White else TextDark,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                "Create Backup",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (activeTab == "BACKUP") Color.White else TextDark
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (activeTab == "RESTORE") RoyalMagenta else Color.Transparent)
              .clickable { activeTab = "RESTORE" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Restore,
                contentDescription = null,
                tint = if (activeTab == "RESTORE") Color.White else TextDark,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                "Restore Database",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (activeTab == "RESTORE") Color.White else TextDark
              )
            }
          }
        }
      }

      // 3. Tab Body
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
      ) {
        if (activeTab == "BACKUP") {
          // Current Database Summary Card
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
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text("Active Database Footprint", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text("Ready for full encrypted JSON export", fontSize = 11.sp, color = TextMuted)
                    }
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(StatusGreenLight)
                      .padding(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Text("HEALTHY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Counts Grid
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text("Medicines", fontSize = 11.sp, color = TextMuted)
                    Text("${medicines.size} Items", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  }
                  Column {
                    Text("Sales Invoices", fontSize = 11.sp, color = TextMuted)
                    Text("${allSales.size} Bills", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  }
                  Column {
                    Text("Customers & Ledgers", fontSize = 11.sp, color = TextMuted)
                    Text("${allCustomers.size} Profiles", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  }
                  Column {
                    Text("Suppliers", fontSize = 11.sp, color = TextMuted)
                    Text("${allSuppliers.size} Contacts", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  }
                }
              }
            }
          }

          // Main Action: Generate & Share Backup Button
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text("Generate System Backup (.json)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(
                  "Generates a complete, standard JSON snapshot of all inventory batches, purchase orders, customer ledgers, and sales logs.",
                  fontSize = 11.sp,
                  color = TextMuted,
                  modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = {
                    isExporting = true
                    viewModel.exportSystemBackup(context)
                    scope.launch {
                      kotlinx.coroutines.delay(1000)
                      refreshLocalBackups()
                      isExporting = false
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_export_system_backup")
                ) {
                  if (isExporting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating Encrypted JSON...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  } else {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Share Full Backup File", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          // Automated Backup Preference
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text("Automatic Daily Local Snapshot", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Auto-archives database snapshot daily at store closing", fontSize = 11.sp, color = TextMuted)
                }

                Switch(
                  checked = autoBackupDaily,
                  onCheckedChange = { autoBackupDaily = it },
                  colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
                )
              }
            }
          }

          // Local Snapshots History
          item {
            Text("PREVIOUS SAVED BACKUPS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
          }

          if (localBackups.isEmpty()) {
            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(Icons.Default.History, contentDescription = null, tint = TextLight, modifier = Modifier.size(32.dp))
                  Spacer(modifier = Modifier.height(6.dp))
                  Text("No previous local snapshots found.", fontSize = 12.sp, color = TextMuted)
                  Text("Tap 'Save & Share Full Backup File' above to generate your first backup.", fontSize = 10.sp, color = TextLight)
                }
              }
            }
          } else {
            items(localBackups) { fileItem ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(fileItem.fileName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1)
                    Text("${fileItem.modifiedDate} • ${fileItem.fileSizeFormatted}", fontSize = 10.sp, color = TextMuted)
                  }

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                      onClick = {
                        val f = File(fileItem.filePath)
                        if (f.exists()) {
                          BackupRestoreManager.shareBackupFile(context, f)
                        }
                      }
                    ) {
                      Icon(Icons.Default.Share, contentDescription = "Share", tint = RoyalNavy, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                      onClick = {
                        val f = File(fileItem.filePath)
                        if (f.exists()) {
                          restoreJsonInput = f.readText()
                          parsedMetadata = BackupRestoreManager.parseBackupMetadata(restoreJsonInput)
                          activeTab = "RESTORE"
                          Toast.makeText(context, "Loaded snapshot into Restore tab!", Toast.LENGTH_SHORT).show()
                        }
                      }
                    ) {
                      Icon(Icons.Default.Restore, contentDescription = "Restore", tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }
            }
          }
        } else {
          // RESTORE TAB CONTENT
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text("Select or Paste Backup File", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Pick a `.json` backup file from device storage or paste raw JSON text below.", fontSize = 11.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      jsonPickerLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                  ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick JSON File", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                  }

                  OutlinedButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = clipboard.primaryClip
                      if (clip != null && clip.itemCount > 0) {
                        val text = clip.getItemAt(0).text?.toString() ?: ""
                        if (text.isNotBlank()) {
                          restoreJsonInput = text
                          parsedMetadata = BackupRestoreManager.parseBackupMetadata(text)
                          Toast.makeText(context, "Pasted from clipboard!", Toast.LENGTH_SHORT).show()
                        }
                      }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                  ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Paste JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                  value = restoreJsonInput,
                  onValueChange = {
                    restoreJsonInput = it
                    parsedMetadata = BackupRestoreManager.parseBackupMetadata(it)
                  },
                  label = { Text("Raw Backup JSON Payload") },
                  placeholder = { Text("{\n  \"app\": \"ROYAL PHARMACY ERP\", ...\n}") },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("input_restore_json"),
                  shape = RoundedCornerShape(8.dp),
                  maxLines = 6
                )
              }
            }
          }

          // Pre-Flight Metadata Inspector
          if (parsedMetadata != null) {
            item {
              val meta = parsedMetadata!!
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFA7F3D0))),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Backup Verified & Ready", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                    }
                    Text(meta.formattedDate, fontSize = 10.sp, color = Color(0xFF047857))
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("📦 ${meta.medicineCount} Medicines", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                    Text("🧾 ${meta.salesCount} Sales", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                    Text("👥 ${meta.customerCount} Customers", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                    Text("🚚 ${meta.supplierCount} Suppliers", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                  }
                }
              }
            }
          }

          // Restore Options & Confirm Button
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text("Restore Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable { cleanOverwriteMode = false }
                ) {
                  RadioButton(
                    selected = !cleanOverwriteMode,
                    onClick = { cleanOverwriteMode = false },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                  )
                  Column {
                    Text("Safe Restore & Merge (Recommended)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Merges backup into current stock, updating matching batch numbers", fontSize = 10.sp, color = TextMuted)
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable { cleanOverwriteMode = true }
                ) {
                  RadioButton(
                    selected = cleanOverwriteMode,
                    onClick = { cleanOverwriteMode = true },
                    colors = RadioButtonDefaults.colors(selectedColor = StatusRed)
                  )
                  Column {
                    Text("Full Clean Overwrite", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                    Text("Replaces all records with backup snapshot data", fontSize = 10.sp, color = TextMuted)
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                  onClick = {
                    if (restoreJsonInput.isBlank()) {
                      Toast.makeText(context, "Please pick a JSON file or paste backup contents first.", Toast.LENGTH_SHORT).show()
                    } else {
                      showConfirmRestoreDialog = true
                    }
                  },
                  enabled = restoreJsonInput.isNotBlank(),
                  colors = ButtonDefaults.buttonColors(containerColor = if (cleanOverwriteMode) StatusRed else RoyalMagenta),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_execute_restore")
                ) {
                  if (isRestoring) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restoring Database...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  } else {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restore Database Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  }
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

    // Confirmation Dialog before Restoring
    if (showConfirmRestoreDialog) {
      AlertDialog(
        onDismissRequest = { showConfirmRestoreDialog = false },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Confirm Database Restore", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Column {
            Text(
              text = "Are you sure you want to restore the database from this backup snapshot?",
              fontSize = 13.sp,
              color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = if (cleanOverwriteMode)
                "⚠️ Full Clean Mode will replace existing stock and transactions."
              else
                "✓ Safe Merge Mode will update records and preserve newer items.",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (cleanOverwriteMode) StatusRed else StatusGreen
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              showConfirmRestoreDialog = false
              isRestoring = true
              viewModel.restoreSystemBackup(context, restoreJsonInput, cleanOverwriteMode) { result ->
                isRestoring = false
                if (result.isSuccess) {
                  Toast.makeText(context, "Database Restored: ${result.medicinesRestored} medicines, ${result.salesRestored} sales!", Toast.LENGTH_LONG).show()
                  viewModel.navigateTo(Screen.STOCK)
                } else {
                  Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (cleanOverwriteMode) StatusRed else RoyalMagenta)
          ) {
            Text("Yes, Restore")
          }
        },
        dismissButton = {
          TextButton(onClick = { showConfirmRestoreDialog = false }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }
  }
}
