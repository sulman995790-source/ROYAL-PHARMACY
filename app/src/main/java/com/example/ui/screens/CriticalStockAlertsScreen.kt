package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import com.example.service.StockAlertNotificationService
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.core.content.ContextCompat
import com.example.data.model.MedicineItem
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
fun CriticalStockAlertsScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val criticalMedicines by viewModel.criticalLowStockMedicines.collectAsState()
  val allMedicines by viewModel.allMedicines.collectAsState()
  val autoAlertServiceEnabled by viewModel.autoAlertServiceEnabled.collectAsState()
  val globalThreshold by viewModel.globalSafetyThreshold.collectAsState()
  val notificationLogs by viewModel.alertNotificationLog.collectAsState()
  val lastAlertTime by viewModel.lastAlertDispatchedTime.collectAsState()
  val isAudioAlertsEnabled by viewModel.isAudioAlertsEnabled.collectAsState()
  val alertSoundStyle by viewModel.alertSoundStyle.collectAsState()

  androidx.compose.runtime.LaunchedEffect(criticalMedicines) {
    if (criticalMedicines.isNotEmpty() && isAudioAlertsEnabled) {
      viewModel.playLowStockAlert(context)
    }
  }

  // Android 13+ Notification permission state
  var hasNotificationPermission by remember {
    mutableStateOf(
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
      } else {
        true
      }
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasNotificationPermission = isGranted
    if (isGranted) {
      viewModel.scanFeedbackMessage.value = "Push notifications permission granted!"
    }
  }

  val shortExpiryMedicines = remember(allMedicines) {
    allMedicines.mapNotNull { med ->
      val days = viewModel.calculateDaysToExpiry(med.expiryDate)
      if (days in 1..60 || med.isExpired) {
        med to days
      } else null
    }.sortedBy { it.second }
  }

  var selectedTab by remember { mutableStateOf("Critical Items") } // "Critical Items", "Short Expiry", "Threshold Settings", "Alert Logs"

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // 1. Top Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalMagenta)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(Screen.HOME) },
        modifier = Modifier.size(32.dp).testTag("btn_back_critical_alerts")
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
            imageVector = Icons.Default.Emergency,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Essential Medicine Alerts",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Text(
          text = "Automated Push Notification Service",
          fontSize = 12.sp,
          color = Color.White.copy(alpha = 0.85f)
        )
      }

      // Quick Test Button
      Button(
        onClick = {
          if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          } else {
            viewModel.triggerTestPushNotification()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.testTag("btn_test_notification")
      ) {
        Icon(
          imageVector = Icons.Default.NotificationsActive,
          contentDescription = null,
          tint = RoyalMagenta,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Test Alert", color = RoyalMagenta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    // 2. Permission Banner if not granted
    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFFEF3C7))
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Enable notifications to receive urgent low-stock push alerts",
            fontSize = 12.sp,
            color = Color(0xFF92400E)
          )
        }
        Button(
          onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.testTag("btn_enable_notifications")
        ) {
          Text("Allow", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 3. Tab Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(
        "Critical Items (${criticalMedicines.size})" to "Critical Items",
        "Short Expiry 60d (${shortExpiryMedicines.size})" to "Short Expiry",
        "Threshold Config" to "Threshold Settings",
        "Alert Log (${notificationLogs.size})" to "Alert Logs"
      ).forEach { (label, key) ->
        val selected = selectedTab == key
        FilterChip(
          selected = selected,
          onClick = { selectedTab = key },
          label = { Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RoyalMagentaLight,
            selectedLabelColor = RoyalMagenta
          ),
          modifier = Modifier.testTag("chip_tab_$key")
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 4. Service Status Card
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(if (autoAlertServiceEnabled) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (autoAlertServiceEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                  contentDescription = null,
                  tint = if (autoAlertServiceEnabled) StatusGreen else StatusRed,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = "Push Alert Service",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Text(
                  text = if (autoAlertServiceEnabled) "Active • Monitoring Room Inventory" else "Paused by Administrator",
                  fontSize = 12.sp,
                  color = if (autoAlertServiceEnabled) StatusGreen else StatusRed,
                  fontWeight = FontWeight.SemiBold
                )
                if (lastAlertTime != null) {
                  Text(
                    text = "Last alert triggered at: $lastAlertTime",
                    fontSize = 10.sp,
                    color = TextMuted
                  )
                }
              }
            }

            Switch(
              checked = autoAlertServiceEnabled,
              onCheckedChange = { viewModel.toggleAutoAlertService(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = RoyalMagenta
              ),
              modifier = Modifier.testTag("switch_auto_alert_service")
            )
          }
        }
      }

      when (selectedTab) {
        "Critical Items" -> {
          // Summary Banner
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = if (criticalMedicines.isEmpty()) Color(0xFFECFDF5) else Color(0xFFFEF2F2)),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(if (criticalMedicines.isEmpty()) Color(0xFFA7F3D0) else Color(0xFFFECACA))
              )
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (criticalMedicines.isEmpty()) Icons.Default.CheckCircle else Icons.Default.Warning,
                  contentDescription = null,
                  tint = if (criticalMedicines.isEmpty()) StatusGreen else StatusRed,
                  modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = if (criticalMedicines.isEmpty()) "All Essential Stock Levels Safe" else "${criticalMedicines.size} Critical Drugs Below Safety Level!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (criticalMedicines.isEmpty()) Color(0xFF065F46) else Color(0xFF991B1B)
                  )
                  Text(
                    text = if (criticalMedicines.isEmpty()) "Every essential & life-saving drug meets or exceeds the safety threshold." else "Push notifications dispatched. Immediate restock order recommended to avoid shortage.",
                    fontSize = 11.sp,
                    color = if (criticalMedicines.isEmpty()) Color(0xFF047857) else Color(0xFFB91C1C),
                    lineHeight = 15.sp
                  )
                }
              }
            }
          }

          if (criticalMedicines.isNotEmpty()) {
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE))
                ),
                modifier = Modifier.fillMaxWidth().testTag("card_automated_reorder_engine")
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Automated Reorder Engine Active", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Automatically drafts official Purchase Orders (PO) for understocked medicines and calculates optimal batch restock levels.",
                    fontSize = 11.5.sp,
                    color = Color(0xFF1E40AF),
                    lineHeight = 15.sp
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Button(
                    onClick = {
                      viewModel.autoGenerateReorderPOs(context)
                      Toast.makeText(context, "Automated Purchase Order generated for critical items!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                    modifier = Modifier.fillMaxWidth().testTag("btn_auto_generate_po")
                  ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto-Generate PO for All Understocked Drugs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          if (criticalMedicines.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusGreen,
                    modifier = Modifier.size(48.dp)
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Inventory is fully stocked!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                  Text(
                    text = "No life-saving medications below safety thresholds.",
                    fontSize = 12.sp,
                    color = TextMuted
                  )
                }
              }
            }
          } else {
            items(criticalMedicines) { med ->
              CriticalMedicineCard(
                medicine = med,
                onPushAlert = { viewModel.sendImmediateCriticalAlertForMedicine(med) },
                onCreateOrder = {
                  viewModel.navigateTo(Screen.SUPPLIERS)
                }
              )
            }
          }
        }

        "Threshold Settings" -> {
          // Audible Low Stock Alert Settings Card
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = "Audible Low-Stock Alerts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                      )
                      Text(
                        text = "Play sound alert for Billing Counter & Replenishment Dashboard on critical stock-outs",
                        fontSize = 11.5.sp,
                        color = TextMuted
                      )
                    }
                  }
                  Switch(
                    checked = isAudioAlertsEnabled,
                    onCheckedChange = { viewModel.toggleAudioAlerts(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta),
                    modifier = Modifier.testTag("switch_audio_alerts")
                  )
                }

                if (isAudioAlertsEnabled) {
                  Spacer(modifier = Modifier.height(14.dp))
                  Text("Alert Sound Style", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    listOf("Beep", "Chime", "Alarm", "Siren").forEach { style ->
                      FilterChip(
                        selected = alertSoundStyle == style,
                        onClick = {
                          viewModel.setAlertSoundStyle(style)
                          viewModel.playLowStockAlert(context)
                        },
                        label = { Text(style, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                          selectedContainerColor = RoyalMagentaLight,
                          selectedLabelColor = RoyalMagenta
                        ),
                        modifier = Modifier.testTag("chip_sound_$style")
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(10.dp))
                  OutlinedButton(
                    onClick = { viewModel.playLowStockAlert(context) },
                    modifier = Modifier.fillMaxWidth().testTag("btn_test_sound")
                  ) {
                    Text("Test Current Alert Sound", fontSize = 12.sp, color = RoyalMagenta, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          // Global Threshold Config Card
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
                  text = "Global Safety Threshold",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Text(
                  text = "Minimum pack quantity that triggers an automated push notification for essential & emergency medicines.",
                  fontSize = 12.sp,
                  color = TextMuted,
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Safety Minimum:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                      onClick = {
                        if (globalThreshold > 1) {
                          viewModel.updateGlobalSafetyThreshold(globalThreshold - 1)
                        }
                      },
                      modifier = Modifier.size(32.dp).testTag("btn_threshold_minus")
                    ) {
                      Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = RoyalMagenta)
                    }

                    Text(
                      text = "$globalThreshold packs",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMagenta,
                      modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                      onClick = {
                        viewModel.updateGlobalSafetyThreshold(globalThreshold + 1)
                      },
                      modifier = Modifier.size(32.dp).testTag("btn_threshold_plus")
                    ) {
                      Icon(Icons.Default.Add, contentDescription = "Increase", tint = RoyalMagenta)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  listOf(2, 5, 8, 10, 15).forEach { thresholdValue ->
                    FilterChip(
                      selected = globalThreshold == thresholdValue,
                      onClick = { viewModel.updateGlobalSafetyThreshold(thresholdValue) },
                      label = { Text("$thresholdValue packs", fontSize = 11.sp) },
                      colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalMagentaLight,
                        selectedLabelColor = RoyalMagenta
                      )
                    )
                  }
                }
              }
            }
          }

          item {
            Text(
              text = "Catalog Essential & Life-Saving Status",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          }

          items(allMedicines) { med ->
            MedicineThresholdRow(
              medicine = med,
              onUpdate = { isEss, isLife, thresh ->
                viewModel.updateMedicineSafetyThreshold(med.id, isEss, isLife, thresh)
              }
            )
          }
        }

        "Short Expiry" -> {
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCD34D))
              ),
              modifier = Modifier.fillMaxWidth().testTag("banner_short_expiry_push")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                      imageVector = Icons.Default.HourglassBottom,
                      contentDescription = null,
                      tint = Color(0xFFB45309),
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = "60-Day Expiry Push Alert Service",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                      )
                      Text(
                        text = "${shortExpiryMedicines.size} medicine batches expiring within 60 days. Dispatch return debit notes to distributors.",
                        fontSize = 11.5.sp,
                        color = Color(0xFFB45309)
                      )
                    }
                  }

                  Button(
                    onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_critical_alerts_open_expiry_dashboard")
                  ) {
                    Text("Full Matrix >", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  // Button 1: Send Test Expiry Push Alert
                  Button(
                    onClick = {
                      if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                      } else {
                        viewModel.triggerTestExpiryPush()
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("btn_trigger_expiry_push"),
                    contentPadding = PaddingValues(vertical = 8.dp)
                  ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Push Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }

                  // Button 2: Add All to Return Cart
                  Button(
                    onClick = {
                      shortExpiryMedicines.forEach { (med, _) ->
                        viewModel.addMedicineToCart(med, qty = med.stockPacks, isReturn = true)
                      }
                      Toast.makeText(context, "Added ${shortExpiryMedicines.size} expiring items to Return Cart", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.2f).testTag("btn_add_all_expiry_cart"),
                    contentPadding = PaddingValues(vertical = 8.dp)
                  ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add All to Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }

                  // Button 3: View Return Cart
                  OutlinedButton(
                    onClick = { viewModel.navigateTo(Screen.CART) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.9f).testTag("btn_view_cart_from_expiry"),
                    contentPadding = PaddingValues(vertical = 8.dp)
                  ) {
                    Text("View Cart", fontSize = 11.sp, color = RoyalMagenta)
                  }
                }
              }
            }
          }

          if (shortExpiryMedicines.isEmpty()) {
            item {
              Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No medicines expiring within 60 days!\nInventory is fresh and within safe shelf life.",
                  color = TextMuted,
                  fontSize = 13.sp,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          } else {
            items(shortExpiryMedicines) { (med, days) ->
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCD34D))
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(if (med.isExpired) Color(0xFFFEE2E2) else Color(0xFFFEF3C7))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = if (med.isExpired) "EXPIRED" else "EXPIRES IN $days DAYS (<60d)",
                          fontSize = 9.5.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (med.isExpired) StatusRed else Color(0xFFB45309)
                        )
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(med.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text(med.composition.ifBlank { med.saltMolecule }, fontSize = 11.sp, color = TextMuted)
                      Text(
                        text = "Batch: ${med.batchNumber.ifBlank { "N/A" }} • Expiry: ${med.expiryDate} • ${med.manufacturer}",
                        fontSize = 11.sp,
                        color = Color(0xFFB45309),
                        fontWeight = FontWeight.Medium
                      )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text("${med.stockPacks} Packs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                      Text("₹${String.format(Locale.US, "%.2f", med.stockPacks * med.mrp)}", fontSize = 11.sp, color = TextMuted)
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("Rack: ${med.rackLocation}", fontSize = 11.sp, color = TextMuted)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      // Push alert for this specific medicine
                      OutlinedButton(
                        onClick = {
                          StockAlertNotificationService.sendShortExpiryPushNotification(context, med, days)
                          Toast.makeText(context, "Dispatched 60d push alert for ${med.name}", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                      ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Push Alert", fontSize = 10.5.sp, color = RoyalMagenta)
                      }

                      // Add to Cart as Return Debit Note
                      Button(
                        onClick = {
                          viewModel.addMedicineToCart(med, qty = med.stockPacks, isReturn = true)
                          Toast.makeText(context, "Added to Return Cart (Debit Note)", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Cart", fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        "Alert Logs" -> {
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
                  text = "Push Notification Audit Trail",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Text(
                  text = "Chronological record of push alerts dispatched to device status bar.",
                  fontSize = 11.sp,
                  color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                notificationLogs.forEach { log ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(RoyalMagenta)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = log,
                      fontSize = 12.sp,
                      color = TextDark,
                      lineHeight = 16.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CriticalMedicineCard(
  medicine: MedicineItem,
  onPushAlert: () -> Unit,
  onCreateOrder: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (medicine.isLifeSaving) Color(0xFFFEE2E2) else Color(0xFFFEF3C7))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = if (medicine.isLifeSaving) "LIFE-SAVING" else "ESSENTIAL",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (medicine.isLifeSaving) StatusRed else Color(0xFFD97706)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = medicine.category,
              fontSize = 10.sp,
              color = TextMuted
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = medicine.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )

          Text(
            text = medicine.composition.ifBlank { medicine.saltMolecule },
            fontSize = 11.sp,
            color = TextMuted
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${medicine.stockPacks} Left",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = StatusRed
          )
          Text(
            text = "Threshold: ${medicine.minStockAlert}",
            fontSize = 10.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Visual Progress Bar
      val progress = (medicine.stockPacks.toFloat() / medicine.minStockAlert.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = StatusRed,
        trackColor = Color(0xFFFEE2E2)
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Loc: ${medicine.rackLocation} • Mfr: ${medicine.manufacturer}",
          fontSize = 10.sp,
          color = TextMuted
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Push Alert button
          OutlinedButton(
            onClick = onPushAlert,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_push_alert_${medicine.id}")
          ) {
            Icon(Icons.Default.Send, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Push Alert", fontSize = 11.sp, color = RoyalMagenta, fontWeight = FontWeight.Bold)
          }

          // Create PO Button
          Button(
            onClick = onCreateOrder,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("btn_order_po_${medicine.id}")
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Order PO", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun MedicineThresholdRow(
  medicine: MedicineItem,
  onUpdate: (isEssential: Boolean, isLifeSaving: Boolean, threshold: Int) -> Unit
) {
  var isEssential by remember(medicine.isEssential) { mutableStateOf(medicine.isEssential) }
  var isLifeSaving by remember(medicine.isLifeSaving) { mutableStateOf(medicine.isLifeSaving) }
  var threshold by remember(medicine.minStockAlert) { mutableStateOf(medicine.minStockAlert) }

  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = medicine.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = "${medicine.manufacturer} • In Stock: ${medicine.stockPacks}",
            fontSize = 11.sp,
            color = TextMuted
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              if (threshold > 1) {
                threshold--
                onUpdate(isEssential, isLifeSaving, threshold)
              }
            },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
          }
          Text(
            text = "$threshold",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 4.dp)
          )
          IconButton(
            onClick = {
              threshold++
              onUpdate(isEssential, isLifeSaving, threshold)
            },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = isEssential,
          onClick = {
            isEssential = !isEssential
            onUpdate(isEssential, isLifeSaving, threshold)
          },
          label = { Text("Essential", fontSize = 10.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFFEF3C7),
            selectedLabelColor = Color(0xFFB45309)
          )
        )

        FilterChip(
          selected = isLifeSaving,
          onClick = {
            isLifeSaving = !isLifeSaving
            onUpdate(isEssential, isLifeSaving, threshold)
          },
          label = { Text("Life-Saving", fontSize = 10.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFFEE2E2),
            selectedLabelColor = StatusRed
          )
        )
      }
    }
  }
}
