package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MobileOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import com.example.service.BackupFrequency
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.service.DriveBackupSnapshot
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

@Composable
fun CloudSyncScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) }

  // Google Drive state
  val isDriveConnected by viewModel.isDriveConnected.collectAsState()
  val driveUserEmail by viewModel.driveUserEmail.collectAsState()
  val driveUserName by viewModel.driveUserName.collectAsState()
  val isDriveBackingUp by viewModel.isDriveBackingUp.collectAsState()
  val isDriveRestoring by viewModel.isDriveRestoring.collectAsState()
  val driveLastBackupTime by viewModel.driveLastBackupTime.collectAsState()
  val autoDriveSync by viewModel.autoDriveSync.collectAsState()
  val syncOnMobileData by viewModel.driveSyncOnMobileData.collectAsState()
  val driveSavedAccounts by viewModel.driveSavedAccounts.collectAsState()
  val driveStatusMessage by viewModel.driveStatusMessage.collectAsState()
  val driveSnapshots by viewModel.driveSnapshots.collectAsState()
  val backupFrequency by viewModel.driveBackupFrequency.collectAsState()
  val storageUsage by viewModel.driveStorageUsage.collectAsState()
  val autoSyncOnBilling by viewModel.autoSyncOnBilling.collectAsState()
  val autoSyncOnLaunch by viewModel.autoSyncOnLaunch.collectAsState()
  val autoSyncPreferredHour by viewModel.autoSyncPreferredHour.collectAsState()
  val syncStatusInfo by viewModel.syncStatusInfo.collectAsState()

  // Firebase & Network state
  val isOnline by viewModel.isOnline.collectAsState()
  val connectionType by viewModel.connectionType.collectAsState()
  val isSimulationOffline by viewModel.isSimulationOffline.collectAsState()
  val isSyncing by viewModel.isSyncing.collectAsState()
  val syncStatusMessage by viewModel.syncStatusMessage.collectAsState()
  val lastSyncTime by viewModel.lastSyncTimestamp.collectAsState()
  val syncHistory by viewModel.syncHistory.collectAsState()
  val pendingCount by viewModel.pendingSyncCount.collectAsState()

  // Dialog states
  var showAccountDialog by remember { mutableStateOf(false) }
  var showFrequencyDialog by remember { mutableStateOf(false) }
  var snapshotToRestore by remember { mutableStateOf<DriveBackupSnapshot?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // 1. Top Header Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalMagenta)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(Screen.HOME) },
        modifier = Modifier.size(32.dp).testTag("btn_back_cloud_sync")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CloudSync,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Cloud Sync & Google Drive",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Text(
          text = if (selectedTab == 0) "Google Drive Backup & Account Sync" else "Room Local-First DB & Cloud Sync",
          fontSize = 12.sp,
          color = Color.White.copy(alpha = 0.85f)
        )
      }

      // Quick action button in header: Manual Sync
      if (selectedTab == 0) {
        Button(
          onClick = { viewModel.triggerManualSync(context) },
          enabled = !isDriveBackingUp && !isSyncing,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          modifier = Modifier.testTag("btn_manual_sync_header")
        ) {
          if (isDriveBackingUp || isSyncing) {
            CircularProgressIndicator(
              color = RoyalMagenta,
              modifier = Modifier.size(14.dp),
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              imageVector = Icons.Default.Sync,
              contentDescription = null,
              tint = RoyalMagenta,
              modifier = Modifier.size(14.dp)
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isDriveBackingUp || isSyncing) "Syncing..." else "Manual Sync",
            color = RoyalMagenta,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      } else {
        Button(
          onClick = { viewModel.triggerSyncNow() },
          enabled = !isSyncing && isOnline,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          modifier = Modifier.testTag("btn_sync_now_header")
        ) {
          if (isSyncing) {
            CircularProgressIndicator(
              color = RoyalMagenta,
              modifier = Modifier.size(14.dp),
              strokeWidth = 2.dp
            )
          } else {
            Icon(
              imageVector = Icons.Default.Sync,
              contentDescription = null,
              tint = RoyalMagenta,
              modifier = Modifier.size(14.dp)
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isSyncing) "Syncing..." else "Sync Now",
            color = RoyalMagenta,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 2. Navigation Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = RoyalMagenta,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = RoyalMagenta
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Google Drive Sync", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
          }
        },
        modifier = Modifier.testTag("tab_google_drive")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Cloud & Local Room", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
          }
        },
        modifier = Modifier.testTag("tab_cloud_sync")
      )
    }

    // 3. Tab Content
    if (selectedTab == 0) {
      // GOOGLE DRIVE SYNC TAB
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        // 0. Cloud Sync Status & Health Master Dashboard Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_cloud_sync_status_dashboard")
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
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(if (isOnline && isDriveConnected) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (isOnline && isDriveConnected) Icons.Default.CloudDone else Icons.Default.CloudSync,
                      contentDescription = null,
                      tint = if (isOnline && isDriveConnected) StatusGreen else Color(0xFFD97706),
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Cloud Synchronization Status",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                    Text(
                      text = if (isSyncing) "Sync in progress..." else "Continuous Cloud & SQLite Protection",
                      fontSize = 11.5.sp,
                      color = TextMuted
                    )
                  }
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isOnline && isDriveConnected) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = if (isOnline && isDriveConnected) "100% HEALTHY" else "ACTIVE",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOnline && isDriveConnected) StatusGreen else Color(0xFFB45309)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // 4-Quadrant Status Grid
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Quadrant 1: Google Drive
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (isDriveConnected) StatusGreen else StatusRed))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Google Drive", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (isDriveConnected) "Connected" else "Disconnected", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isDriveConnected) StatusGreen else StatusRed)
                    Text(driveUserEmail.substringBefore("@"), fontSize = 10.sp, color = TextMuted, maxLines = 1)
                  }
                }

                // Quadrant 2: Cloud Sync Server
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (isOnline) StatusGreen else Color(0xFFD97706)))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Cloud Storage", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (isOnline) "Real-time Online" else "Local SQLite", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isOnline) RoyalNavy else Color(0xFFB45309))
                    Text("$pendingCount pending sync", fontSize = 10.sp, color = TextMuted)
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Quadrant 3: Auto-Sync Engine
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (autoDriveSync) StatusGreen else TextMuted))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Auto-Sync Engine", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(backupFrequency.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = RoyalMagenta)
                    Text("Auto: ${if (autoDriveSync) "Enabled" else "Paused"}", fontSize = 10.sp, color = TextMuted)
                  }
                }

                // Quadrant 4: Network Channel
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (syncOnMobileData) StatusGreen else RoyalNavy))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Network Channel", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (syncOnMobileData) "Mobile + Wi-Fi" else "Wi-Fi Only", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text(connectionType, fontSize = 10.sp, color = TextMuted)
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Last synchronized: $driveLastBackupTime • Target: My Drive > Royal Pharmacy Backups",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }
        }

        // Google Account Login / Status Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_google_account")
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
                      .size(46.dp)
                      .clip(CircleShape)
                      .background(if (isDriveConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isDriveConnected) {
                      Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(24.dp)
                      )
                    } else {
                      Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = StatusRed,
                        modifier = Modifier.size(24.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Text(
                      text = if (isDriveConnected) driveUserName else "Google Drive Disconnected",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                    Text(
                      text = if (isDriveConnected) driveUserEmail else "Tap to connect your Google Account",
                      fontSize = 12.sp,
                      color = TextMuted
                    )
                  }
                }

                if (isDriveConnected) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFFDCFCE7))
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text("Connected", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
              Spacer(modifier = Modifier.height(10.dp))

              // Cloud Scope details
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "OAuth Scope: Google Drive (App Data & Backup Folder)",
                  fontSize = 11.sp,
                  color = RoyalNavy,
                  fontWeight = FontWeight.Medium
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Target Folder: My Drive > Royal Pharmacy Backups",
                fontSize = 11.sp,
                color = TextMuted
              )
              Text(
                text = "Last Google Drive sync: $driveLastBackupTime",
                fontSize = 11.sp,
                color = TextMuted
              )

              Spacer(modifier = Modifier.height(14.dp))

              // Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                if (isDriveConnected) {
                  OutlinedButton(
                    onClick = { viewModel.disconnectGoogleDrive() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                    modifier = Modifier.weight(1f).testTag("btn_disconnect_drive")
                  ) {
                    Text("Disconnect", fontSize = 12.sp, color = StatusRed)
                  }

                  Button(
                    onClick = { showAccountDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    modifier = Modifier.weight(1f).testTag("btn_switch_account")
                  ) {
                    Text("Switch Account", fontSize = 12.sp, color = Color.White)
                  }
                } else {
                  Button(
                    onClick = { viewModel.connectGoogleDrive("sulman995790@gmail.com", "Suleman Hoque") },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                    modifier = Modifier.fillMaxWidth().testTag("btn_connect_google")
                  ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign in with Google Account", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                  }
                }
              }
            }
          }
        }

        // 1. Manual Sync Master Action Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDDD6FE))
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_manual_sync_master")
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
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Sync,
                      contentDescription = null,
                      tint = RoyalNavy,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Manual Sync & Cloud Upload",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF4C1D95)
                    )
                    Text(
                      text = "Push latest local database state to Google Drive & Cloud",
                      fontSize = 11.5.sp,
                      color = TextMuted
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color.White, RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Last Synced: $driveLastBackupTime", fontSize = 11.5.sp, color = TextDark)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE0E7FF))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${storageUsage.totalRecordsCount} records",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3730A3)
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = { viewModel.triggerManualSync(context) },
                enabled = !isDriveBackingUp && !isSyncing,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier.fillMaxWidth().testTag("btn_manual_sync_action")
              ) {
                if (isDriveBackingUp || isSyncing) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Synchronizing Google Drive & Cloud...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Manual Sync Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // 2. Storage Usage & Quota Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_storage_usage")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Storage, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Storage Usage & Drive Quota", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text("15.0 GB Free Tier", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Storage bar
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "${storageUsage.formattedDriveUsed} of ${storageUsage.formattedDriveQuota} used",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                  )
                  Text(
                    text = "< 0.01% utilized",
                    fontSize = 11.5.sp,
                    color = TextMuted
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                  progress = { 0.005f },
                  modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                  color = RoyalMagenta,
                  trackColor = Color(0xFFE2E8F0)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Storage breakdown row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Card 1: Cloud Backups
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text("Drive Files", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(storageUsage.formattedDriveUsed, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                    Text("${storageUsage.totalSnapshotsCount} snapshots", fontSize = 10.sp, color = TextMuted)
                  }
                }

                // Card 2: Local SQLite DB
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text("Local SQLite", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(storageUsage.formattedLocalDbSize, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    Text("Room offline DB", fontSize = 10.sp, color = TextMuted)
                  }
                }

                // Card 3: Total Records
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text("Total Items", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${storageUsage.totalRecordsCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                    Text("Medicines & Bills", fontSize = 10.sp, color = TextMuted)
                  }
                }
              }
            }
          }
        }

        // 3. Backup Frequency Schedule Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_backup_frequency")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Schedule, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Backup Frequency", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEDE9FE))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(backupFrequency.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              // Next scheduled run banner
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text("Next Auto-Backup Schedule:", fontSize = 11.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.SemiBold)
                  Text(backupFrequency.nextScheduled, fontSize = 12.sp, color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              Text("Choose Auto-Backup Interval:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
              Spacer(modifier = Modifier.height(8.dp))

              // Interactive Frequency options list
              val frequencyOptions = listOf(
                BackupFrequency.REAL_TIME,
                BackupFrequency.HOURLY,
                BackupFrequency.DAILY,
                BackupFrequency.WEEKLY,
                BackupFrequency.MANUAL_ONLY
              )

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                frequencyOptions.forEach { freq ->
                  val isSelected = backupFrequency == freq
                  Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                      containerColor = if (isSelected) Color(0xFFF3E8FF) else Color(0xFFF8FAFC)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                      brush = androidx.compose.ui.graphics.SolidColor(
                        if (isSelected) RoyalMagenta else CardBorder
                      )
                    ),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable {
                        viewModel.setBackupFrequency(freq)
                        Toast.makeText(context, "Backup frequency set to: ${freq.title}", Toast.LENGTH_SHORT).show()
                      }
                      .testTag("freq_option_${freq.name.lowercase()}")
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      RadioButton(
                        selected = isSelected,
                        onClick = {
                          viewModel.setBackupFrequency(freq)
                          Toast.makeText(context, "Backup frequency set to: ${freq.title}", Toast.LENGTH_SHORT).show()
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = freq.title,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                          fontSize = 12.5.sp,
                          color = TextDark
                        )
                        Text(
                          text = freq.subtitle,
                          fontSize = 11.sp,
                          color = TextMuted
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // Auto-Backup & Schedule Engine Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_auto_sync_schedule_engine")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              // Master Auto-Sync Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Automated Google Drive Sync",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Automatically create cloud snapshots in Google Drive without interrupting pharmacy sales.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                  checked = autoDriveSync,
                  onCheckedChange = { viewModel.toggleAutoDriveSync(it) },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = RoyalMagenta
                  ),
                  modifier = Modifier.testTag("switch_auto_drive_sync")
                )
              }

              AnimatedVisibility(visible = autoDriveSync) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                  Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                  Spacer(modifier = Modifier.height(12.dp))

                  Text("Auto-Sync Event Triggers & Scheduling:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Spacer(modifier = Modifier.height(8.dp))

                  // Trigger 1: On Billing
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text("Auto-Sync on Billing Complete", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                      Text("Takes an immediate snapshot upon completing checkout", fontSize = 11.sp, color = TextMuted)
                    }
                    Switch(
                      checked = autoSyncOnBilling,
                      onCheckedChange = { viewModel.toggleAutoSyncOnBilling(it) },
                      colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = StatusGreen),
                      modifier = Modifier.testTag("switch_auto_sync_billing")
                    )
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  // Trigger 2: On App Startup
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text("Auto-Sync on App Startup", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                      Text("Backs up store database whenever app opens", fontSize = 11.sp, color = TextMuted)
                    }
                    Switch(
                      checked = autoSyncOnLaunch,
                      onCheckedChange = { viewModel.toggleAutoSyncOnLaunch(it) },
                      colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = StatusGreen),
                      modifier = Modifier.testTag("switch_auto_sync_launch")
                    )
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Schedule Hour Chips
                  Text("Scheduled Daily Closing Sync Time:", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = TextDark)
                  Spacer(modifier = Modifier.height(6.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    listOf("09:00 PM", "10:30 PM", "11:59 PM").forEach { hour ->
                      val isSelected = autoSyncPreferredHour == hour
                      Box(
                        modifier = Modifier
                          .weight(1f)
                          .clip(RoundedCornerShape(6.dp))
                          .background(if (isSelected) RoyalNavy else Color(0xFFF1F5F9))
                          .clickable {
                            viewModel.setAutoSyncPreferredHour(hour)
                            Toast.makeText(context, "Scheduled daily sync set to: $hour", Toast.LENGTH_SHORT).show()
                          }
                          .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = hour,
                          fontSize = 11.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                          color = if (isSelected) Color.White else TextDark
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // Mobile Data Sync (Cellular 4G/5G) Toggle Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_mobile_data_sync")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = if (syncOnMobileData) StatusGreen else RoyalNavy,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Sync during Mobile Data On",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = if (syncOnMobileData)
                    "Enabled: Google Drive sync will proceed over Mobile Data (Cellular 4G/5G) as well as Wi-Fi."
                  else
                    "Disabled: Sync will pause on Cellular Mobile Data and only run when connected to Wi-Fi.",
                  fontSize = 11.sp,
                  color = TextMuted,
                  lineHeight = 15.sp
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Switch(
                checked = syncOnMobileData,
                onCheckedChange = { viewModel.toggleDriveSyncOnMobileData(it) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = StatusGreen
                ),
                modifier = Modifier.testTag("switch_sync_on_mobile_data")
              )
            }
          }
        }

        // Create New Backup Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBF7D0))
            )
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Live Backup to Google Drive",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF166534)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Backs up all 6 Room tables: Catalog Medicines, Tax Invoices, Udhar Ledger, Customer profiles, Supplier accounts, and Patient CRM histories.",
                fontSize = 11.sp,
                color = Color(0xFF14532D),
                lineHeight = 15.sp
              )
              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = { viewModel.backupToGoogleDrive(context) },
                enabled = !isDriveBackingUp && isDriveConnected,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                modifier = Modifier.fillMaxWidth().testTag("btn_trigger_drive_backup")
              ) {
                if (isDriveBackingUp) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Uploading Snapshot to Drive...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Backup Database to Google Drive Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // Google Drive Snapshots List & Restore
        item {
          Text(
            text = "Google Drive Backups (${driveSnapshots.size} Cloud Files)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        }

        items(driveSnapshots, key = { it.id }) { snapshot ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("card_snapshot_${snapshot.id}")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = snapshot.fileName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavy
                  )
                  Text(
                    text = "Created: ${snapshot.timestamp} • Size: ${snapshot.formattedSize}",
                    fontSize = 11.sp,
                    color = TextMuted
                  )
                  Text(
                    text = "Contains: ${snapshot.totalRecords} records across all database tables",
                    fontSize = 11.sp,
                    color = StatusGreen,
                    fontWeight = FontWeight.Medium
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE0F2FE))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("Drive Synced", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = { viewModel.shareDriveSnapshot(context, snapshot) },
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.weight(1f).height(36.dp)
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = RoyalNavy)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Share JSON", fontSize = 11.sp, color = RoyalNavy)
                }

                Button(
                  onClick = { snapshotToRestore = snapshot },
                  shape = RoundedCornerShape(6.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  modifier = Modifier.weight(1f).height(36.dp).testTag("btn_restore_${snapshot.id}")
                ) {
                  Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Restore to App", fontSize = 11.sp, color = Color.White)
                }
              }
            }
          }
        }
      }
    } else {
      // LOCAL-FIRST ROOM DB & CLOUD SYNC TAB
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        // Local-First Guarantee Banner
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE))
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Storage,
                  contentDescription = null,
                  tint = Color(0xFF1D4ED8),
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Local-First Architecture Active",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E3A8A)
                )
                Text(
                  text = "All billing, inventory adjustments, and patient ledgers are written immediately to your local SQLite Room database. The app operates with 100% functionality offline, and synchronizes automatically once connected.",
                  fontSize = 11.sp,
                  color = Color(0xFF1E40AF),
                  lineHeight = 15.sp
                )
              }
            }
          }
        }

        // Connectivity & Offline Simulation Card
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            )
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
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(if (isOnline) StatusGreen else StatusRed)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (isOnline) "Network Connected" else "Device Offline",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                      contentDescription = null,
                      tint = if (isOnline) StatusGreen else StatusRed,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = connectionType,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isOnline) StatusGreen else StatusRed
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Simulation Mode Switch
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF8FAFC))
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.MobileOff,
                    contentDescription = null,
                    tint = if (isSimulationOffline) Color(0xFFD97706) else TextMuted,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Simulate Offline Mode",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = TextDark
                    )
                    Text(
                      text = "Test offline billing & local Room persistence without disabling Wi-Fi.",
                      fontSize = 11.sp,
                      color = TextMuted,
                      lineHeight = 14.sp
                    )
                  }
                }

                Switch(
                  checked = isSimulationOffline,
                  onCheckedChange = { viewModel.toggleSimulationOffline(it) },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFD97706)
                  ),
                  modifier = Modifier.testTag("switch_simulate_offline")
                )
              }
            }
          }
        }

        // Synchronization Status
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            )
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Cloud Database & Storage Synchronization",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "Status: $syncStatusMessage",
                fontSize = 12.sp,
                color = if (isSyncing) RoyalMagenta else TextMuted
              )
              Text(
                text = "Last synced: $lastSyncTime",
                fontSize = 11.sp,
                color = TextMuted
              )

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { viewModel.triggerSyncNow() },
                enabled = !isSyncing && isOnline,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                modifier = Modifier.fillMaxWidth().testTag("btn_trigger_cloud_sync")
              ) {
                if (isSyncing) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Syncing Changes...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Sync Pending Changes Now ($pendingCount pending)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // Synchronized Entities
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            )
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Synchronized Data Entities",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Spacer(modifier = Modifier.height(10.dp))

              listOf(
                "pharmacy_inventory" to "Medicines, batches & safety thresholds",
                "pharmacy_sales" to "Tax invoices & loyalty points records",
                "pharmacy_udhar_transactions" to "Customer credit ledger & balances",
                "pharmacy_customers" to "Customer accounts & loyalty tiers",
                "pharmacy_patients" to "Patient records, clinical CRM & refill dates",
                "pharmacy_purchase_orders" to "Automated reorders & supplier POs"
              ).forEach { (col, desc) ->
                Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(col, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    Text(desc, fontSize = 10.5.sp, color = TextMuted)
                  }
                }
              }
            }
          }
        }

        // Audit Trail
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            )
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Sync Audit Trail",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Spacer(modifier = Modifier.height(10.dp))

              syncHistory.forEach { log ->
                Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .padding(top = 4.dp)
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(RoyalMagenta)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(log, fontSize = 11.sp, color = TextDark, lineHeight = 15.sp)
                }
              }
            }
          }
        }
      }
    }

    // Account Switch / Login Dialog
    if (showAccountDialog) {
      var selectedAccountEmail by remember { mutableStateOf(driveUserEmail) }
      var isCustomAccount by remember { mutableStateOf(false) }
      var customEmailInput by remember { mutableStateOf("") }
      var customNameInput by remember { mutableStateOf("") }

      AlertDialog(
        onDismissRequest = { showAccountDialog = false },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Switch Google Drive Account", fontWeight = FontWeight.Bold, fontSize = 17.sp)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              "Select or enter the Google account to synchronize backups, ledgers, and inventory with Google Drive:",
              fontSize = 12.sp,
              color = TextMuted,
              lineHeight = 16.sp
            )

            // Saved Accounts List
            driveSavedAccounts.forEach { acc ->
              val isSelected = !isCustomAccount && selectedAccountEmail.equals(acc.email, ignoreCase = true)
              Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) Color(0xFFF3E8FF) else Color(0xFFF8FAFC)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(
                    if (isSelected) RoyalMagenta else CardBorder
                  )
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    isCustomAccount = false
                    selectedAccountEmail = acc.email
                  }
                  .testTag("account_item_${acc.email.replace("@", "_").replace(".", "_")}")
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = isSelected,
                    onClick = {
                      isCustomAccount = false
                      selectedAccountEmail = acc.email
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(acc.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                      if (acc.email.equals(driveUserEmail, ignoreCase = true)) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                          Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        }
                      }
                    }
                    Text(acc.email, fontSize = 11.5.sp, color = TextMuted)
                  }

                  IconButton(
                    onClick = {
                      viewModel.removeGoogleDriveAccount(acc.email)
                      Toast.makeText(context, "Removed account ${acc.email}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Remove account",
                      tint = StatusRed,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }

            // Option: Custom Google Account
            Card(
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isCustomAccount) Color(0xFFF3E8FF) else Color(0xFFF8FAFC)
              ),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                  if (isCustomAccount) RoyalMagenta else CardBorder
                )
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { isCustomAccount = true }
                .testTag("account_item_custom")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isCustomAccount,
                  onClick = { isCustomAccount = true },
                  colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add / Use Another Google Account", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
              }
            }

            AnimatedVisibility(visible = isCustomAccount) {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                OutlinedTextField(
                  value = customEmailInput,
                  onValueChange = { customEmailInput = it },
                  label = { Text("Google Email (e.g. sulman995790@gmail.com)") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth().testTag("input_google_account_email")
                )
                OutlinedTextField(
                  value = customNameInput,
                  onValueChange = { customNameInput = it },
                  label = { Text("Display / Owner Name") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth().testTag("input_google_account_name")
                )
              }
            }
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (isCustomAccount) {
                if (customEmailInput.isNotBlank()) {
                  val name = customNameInput.ifBlank { customEmailInput.substringBefore("@") }
                  viewModel.switchGoogleDriveAccount(customEmailInput.trim(), name.trim())
                  Toast.makeText(context, "Switched to: ${customEmailInput.trim()}", Toast.LENGTH_SHORT).show()
                }
              } else {
                val matched = driveSavedAccounts.find { it.email.equals(selectedAccountEmail, ignoreCase = true) }
                if (matched != null) {
                  viewModel.switchGoogleDriveAccount(matched.email, matched.displayName)
                  Toast.makeText(context, "Active Google Drive account: ${matched.email}", Toast.LENGTH_SHORT).show()
                }
              }
              showAccountDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            modifier = Modifier.testTag("btn_confirm_switch_account")
          ) {
            Text("Switch Account")
          }
        },
        dismissButton = {
          TextButton(onClick = { showAccountDialog = false }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // Restore Confirmation Dialog
    snapshotToRestore?.let { snap ->
      AlertDialog(
        onDismissRequest = { snapshotToRestore = null },
        title = { Text("Restore from Google Drive?", fontWeight = FontWeight.Bold) },
        text = {
          Text(
            text = "Are you sure you want to restore '${snap.fileName}'?\n\nThis will synchronize ${snap.totalRecords} medicines, sales, customer ledgers, and patient profiles into your local Room database.",
            fontSize = 13.sp,
            color = TextDark,
            lineHeight = 18.sp
          )
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.restoreFromGoogleDrive(context, snap)
              snapshotToRestore = null
              Toast.makeText(context, "Restored snapshot from Google Drive!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
          ) {
            Text("Confirm Restore")
          }
        },
        dismissButton = {
          TextButton(onClick = { snapshotToRestore = null }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }
  }
}
