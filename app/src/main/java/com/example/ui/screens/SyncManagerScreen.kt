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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.service.BackupFrequency
import com.example.service.DriveBackupSnapshot
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncManagerScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val userEmail by viewModel.driveUserEmail.collectAsState()
  val userName by viewModel.driveUserName.collectAsState()
  val isDriveConnected by viewModel.isDriveConnected.collectAsState()
  val isDriveBackingUp by viewModel.isDriveBackingUp.collectAsState()
  val driveLastBackupTime by viewModel.driveLastBackupTime.collectAsState()
  val autoDriveSync by viewModel.autoDriveSync.collectAsState()
  val syncOnMobileData by viewModel.driveSyncOnMobileData.collectAsState()
  val driveSavedAccounts by viewModel.driveSavedAccounts.collectAsState()
  val driveSnapshots by viewModel.driveSnapshots.collectAsState()
  val backupFrequency by viewModel.driveBackupFrequency.collectAsState()
  val storageUsage by viewModel.driveStorageUsage.collectAsState()
  val syncStatusInfo by viewModel.syncStatusInfo.collectAsState()
  val pendingSyncCount by viewModel.pendingSyncCount.collectAsState(initial = 0)
  val syncHistory by viewModel.syncHistory.collectAsState()

  val medicines by viewModel.allMedicines.collectAsState()
  val sales by viewModel.allSales.collectAsState()
  val purchases by viewModel.allPurchases.collectAsState()
  val customers by viewModel.allCustomers.collectAsState()
  val patients by viewModel.allPatients.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Queue & Operations, 1: Google Drive Snapshots, 2: Automation & Rules
  var showAccountSwitcher by remember { mutableStateOf(false) }
  var conflictPolicy by remember { mutableStateOf("LOCAL_WINS") }

  val totalLocalRecords = medicines.size + sales.size + purchases.size + customers.size + patients.size

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // 1. Top Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalNavy)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { viewModel.navigateTo(Screen.HOME) },
          modifier = Modifier.size(36.dp).testTag("btn_back_sync_manager")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Sync Manager & Cloud Health", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("Room Database, Google Drive & Queue Engine", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Button(
        onClick = {
          viewModel.triggerManualSync(context)
          Toast.makeText(context, "Syncing database with Google Drive...", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = if (isDriveBackingUp) Color(0xFF475569) else Color(0xFF10B981)),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        enabled = !isDriveBackingUp,
        modifier = Modifier.testTag("btn_sync_all_now")
      ) {
        if (isDriveBackingUp) {
          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(13.dp), strokeWidth = 2.dp)
        } else {
          Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(if (isDriveBackingUp) "Syncing..." else "Sync Now", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
      }
    }

    // 2. Health & Connected Account Overview Banner
    Card(
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Slate 800
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
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
                .background(Color(0xFF334155)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = syncStatusInfo.overallHealthLabel,
                color = if (syncStatusInfo.isFullySynced) Color(0xFF34D399) else Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "Drive: $userEmail",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
            }
          }

          OutlinedButton(
            onClick = { showAccountSwitcher = true },
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569))
          ) {
            Text("Switch Acc", fontSize = 10.5.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3 mini badges for sync status
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF0F172A))
              .padding(8.dp)
          ) {
            Column {
              Text("SYNC QUEUE", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
              Text(
                if (pendingSyncCount == 0) "0 Pending (Clean)" else "$pendingSyncCount Unsynced",
                fontSize = 11.5.sp,
                color = if (pendingSyncCount == 0) Color(0xFF34D399) else Color(0xFFF87171),
                fontWeight = FontWeight.Bold
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF0F172A))
              .padding(8.dp)
          ) {
            Column {
              Text("LAST BACKUP", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
              Text(driveLastBackupTime.take(16), fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF0F172A))
              .padding(8.dp)
          ) {
            Column {
              Text("LOCAL DB SIZE", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
              Text("$totalLocalRecords rec (${storageUsage.formattedLocalDbSize})", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 3. Tab Bar
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = RoyalNavy,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = RoyalNavy,
          height = 3.dp
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Live Queue", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Drive Snapshots", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Automation & Rules", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
      )
    }

    // Tab Contents
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      when (selectedTab) {
        0 -> {
          // Live Sync Queue & Local Storage breakdown
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sync Queue Pipeline", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (pendingSyncCount == 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                      .padding(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = if (pendingSyncCount == 0) "All Changes Flushed" else "$pendingSyncCount In Queue",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (pendingSyncCount == 0) Color(0xFF15803D) else Color(0xFFB91C1C)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "Transactions are saved locally in SQLite with millisecond latency. A background worker streams modified records to Google Drive snapshots and Firestore.",
                  fontSize = 11.5.sp,
                  color = TextMuted,
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      viewModel.triggerSyncNow()
                      Toast.makeText(context, "Local sync queue flushed to cloud.", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Flush Queue", fontSize = 11.sp, color = RoyalNavy)
                  }

                  Button(
                    onClick = {
                      viewModel.backupToGoogleDrive(context)
                      Toast.makeText(context, "Backup saved to Google Drive!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Backup Now", fontSize = 11.sp, color = Color.White)
                  }
                }
              }
            }
          }

          // Database Statistics Cards
          item {
            Text("Local Database Entities (Room SQLite)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              SyncEntityCard("Medicines", medicines.size, Color(0xFF0369A1), Color(0xFFE0F2FE), Modifier.weight(1f))
              SyncEntityCard("Sales", sales.size, Color(0xFF15803D), Color(0xFFDCFCE7), Modifier.weight(1f))
              SyncEntityCard("Purchases", purchases.size, Color(0xFF7C3AED), Color(0xFFEDE9FE), Modifier.weight(1f))
            }
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              SyncEntityCard("Customers", customers.size, Color(0xFFD97706), Color(0xFFFEF3C7), Modifier.weight(1f))
              SyncEntityCard("Patients", patients.size, Color(0xFFBE123C), Color(0xFFFFE4E6), Modifier.weight(1f))
              SyncEntityCard("Snapshots", driveSnapshots.size, Color(0xFF334155), Color(0xFFF1F5F9), Modifier.weight(1f))
            }
          }
        }
        1 -> {
          // Google Drive Snapshots
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("Cloud Backup Snapshots (Google Drive)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Text("Encrypted JSON database snapshots stored in your personal Google Drive ($userEmail).", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                  onClick = {
                    viewModel.backupToGoogleDrive(context)
                    Toast.makeText(context, "Creating new Google Drive snapshot...", Toast.LENGTH_SHORT).show()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Create New Cloud Snapshot Now")
                }
              }
            }
          }

          items(driveSnapshots) { snap ->
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(snap.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Text(snap.timestamp, fontSize = 11.sp, color = TextMuted)
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0xFFDCFCE7))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text("${snap.totalRecords} records • ${snap.formattedSize}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  OutlinedButton(
                    onClick = {
                      viewModel.restoreFromGoogleDrive(context, snap)
                      Toast.makeText(context, "Restoring database from ${snap.fileName}...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                  ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restore from this Snapshot", fontSize = 11.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
                  }

                  IconButton(
                    onClick = {
                      val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(android.content.Intent.EXTRA_TEXT, snap.rawJsonContent)
                        putExtra(android.content.Intent.EXTRA_SUBJECT, snap.fileName)
                      }
                      context.startActivity(android.content.Intent.createChooser(shareIntent, "Export Backup JSON"))
                    },
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(GrayBackground)
                  ) {
                    Icon(Icons.Default.Download, contentDescription = "Export", tint = RoyalNavy, modifier = Modifier.size(16.dp))
                  }
                }
              }
            }
          }
        }
        2 -> {
          // Automation & Conflict Resolution
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("Automated Synchronization Controls", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))

                // Auto-Sync Toggle
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text("Auto-Sync Engine", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Text("Automatically sync newly created bills and stock adjustments in background", fontSize = 11.sp, color = TextMuted)
                  }
                  Switch(
                    checked = autoDriveSync,
                    onCheckedChange = { viewModel.toggleAutoDriveSync(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(10.dp))

                // Sync on Mobile Data
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text("Sync on Cellular Mobile Data", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Text("Allow sync operations when Wi-Fi is disconnected (4G/5G data usage)", fontSize = 11.sp, color = TextMuted)
                  }
                  Switch(
                    checked = syncOnMobileData,
                    onCheckedChange = { viewModel.toggleDriveSyncOnMobileData(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalNavy)
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(10.dp))

                // Auto-Sync Frequency
                Text("Auto-Backup Frequency", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                Spacer(modifier = Modifier.height(6.dp))

                BackupFrequency.entries.forEach { freq ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { viewModel.setBackupFrequency(freq) }
                      .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    RadioButton(
                      selected = backupFrequency == freq,
                      onClick = { viewModel.setBackupFrequency(freq) },
                      colors = RadioButtonDefaults.colors(selectedColor = RoyalNavy)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text(freq.title, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TextDark)
                      Text(freq.subtitle, fontSize = 10.5.sp, color = TextMuted)
                    }
                  }
                }
              }
            }
          }

          // Conflict Resolution Policy
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Security, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Conflict Resolution Policy", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDark)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = conflictPolicy == "LOCAL_WINS",
                    onClick = { conflictPolicy = "LOCAL_WINS" },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalNavy)
                  )
                  Column {
                    Text("Local Terminal Wins (Recommended)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Offline sales generated at counter always preserve invoice number integrity", fontSize = 10.5.sp, color = TextMuted)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Google Account Switcher Modal
  if (showAccountSwitcher) {
    AlertDialog(
      onDismissRequest = { showAccountSwitcher = false },
      title = { Text("Switch Google Drive Account", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
      text = {
        Column {
          Text("Select active account for automatic daily cloud backups:", fontSize = 12.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(10.dp))
          driveSavedAccounts.forEach { acc ->
            val isCurrent = userEmail == acc.email
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrent) Color(0xFFEFF6FF) else Color.Transparent)
                .clickable {
                  viewModel.switchGoogleDriveAccount(acc.email, acc.displayName)
                  showAccountSwitcher = false
                  Toast.makeText(context, "Switched to ${acc.email}", Toast.LENGTH_SHORT).show()
                }
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(acc.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                Text(acc.email, fontSize = 11.sp, color = TextMuted)
              }
              if (isCurrent) {
                Icon(Icons.Default.Check, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showAccountSwitcher = false }) {
          Text("Done", fontWeight = FontWeight.Bold, color = RoyalNavy)
        }
      }
    )
  }
}

@Composable
fun SyncEntityCard(
  title: String,
  count: Int,
  textColor: Color,
  bgColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(10.dp)
  ) {
    Column {
      Text(title.uppercase(Locale.getDefault()), fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.8f))
      Spacer(modifier = Modifier.height(2.dp))
      Text("$count records", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
  }
}
