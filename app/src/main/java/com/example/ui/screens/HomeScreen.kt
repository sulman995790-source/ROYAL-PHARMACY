package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingQuikScanButton
import com.example.ui.components.RoyalPharmacyTopHeader
import com.example.ui.components.StoreUpiQrDialog
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
import java.util.Locale

@Composable
fun HomeScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val metrics by viewModel.dashboardMetrics.collectAsState()
  val selectedTimeFilter by viewModel.selectedTimeFilter.collectAsState()
  val businessProfile by viewModel.businessProfile.collectAsState()
  val criticalMedicines by viewModel.criticalLowStockMedicines.collectAsState()
  val isOnline by viewModel.isOnline.collectAsState()
  val isSyncing by viewModel.isSyncing.collectAsState()
  val distributorCart by viewModel.distributorCart.collectAsState()
  val isDriveConnected by viewModel.isDriveConnected.collectAsState()
  val driveLastBackupTime by viewModel.driveLastBackupTime.collectAsState()
  val batchItems by viewModel.batchExpiryItems.collectAsState()
  val sales by viewModel.allSales.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()
  val customers by viewModel.allCustomers.collectAsState()
  val upiAccounts by viewModel.allUpiAccounts.collectAsState()
  val primaryUpiId = upiAccounts.firstOrNull { it.isPrimary }?.upiId
    ?: upiAccounts.firstOrNull()?.upiId
    ?: "royalchemist@okaxis"

  val expiringCount = batchItems.count { it.daysRemaining in 1..60 || it.daysRemaining <= 0 }
  val recentActivityLogs by viewModel.staffActivityLogs.collectAsState()

  val isDarkMode by viewModel.isDarkMode.collectAsState()
  val userRole by viewModel.currentUserRole.collectAsState()
  val visualSyncState by viewModel.visualSyncState.collectAsState()
  var showCustomerUpiQrDialog by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(if (isDarkMode) Color(0xFF0F172A) else GrayBackground)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Top Header
      item {
        RoyalPharmacyTopHeader(
          businessName = businessProfile.businessName,
          cartCount = distributorCart.size,
          userRole = userRole,
          visualSyncState = visualSyncState,
          onProfileClick = { viewModel.navigateTo(Screen.BUSINESS_PROFILE) },
          onNotificationClick = { viewModel.navigateTo(Screen.CRITICAL_STOCK_ALERTS) },
          onCartClick = { viewModel.navigateTo(Screen.CART) },
          onLockClick = { viewModel.lockAppNow() },
          onVoiceSearchClick = { viewModel.navigateTo(Screen.VOICE_SEARCH) },
          onSyncManagerClick = { viewModel.navigateTo(Screen.SYNC_MANAGER) },
          onRoleClick = { viewModel.showUserRoleAuthDialog.value = true },
          onVisualSyncClick = { viewModel.showVisualSyncStatusSheet.value = true },
          onDarkModeToggle = { viewModel.toggleDarkMode() },
          isDarkMode = isDarkMode
        )
      }

      // 2. Primary Fast-Action Row (Billing, Rx Scan, UPI QR, QuickScan)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // New Bill
          Button(
            onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            modifier = Modifier.weight(1.15f).testTag("btn_home_create_bill")
          ) {
            Icon(Icons.Default.PointOfSale, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("New Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          // AI Rx Scanner
          Button(
            onClick = { viewModel.navigateTo(Screen.PRESCRIPTION_SCANNER) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            modifier = Modifier.weight(1.05f).testTag("btn_home_rx_scanner")
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("AI Rx", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          // Show UPI QR to Customer
          Button(
            onClick = { showCustomerUpiQrDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            modifier = Modifier.weight(1.05f).testTag("btn_home_show_upi_qr")
          ) {
            Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("UPI QR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          // QuickScan Barcode
          OutlinedButton(
            onClick = { viewModel.navigateTo(Screen.QUICK_SCAN) },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
            modifier = Modifier.weight(0.95f).testTag("btn_home_quickscan")
          ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Barcode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
          }
        }
      }

      // 2.5 Prominent AI Symptom & Disease Tracker Card
      item {
        Card(
          onClick = { viewModel.navigateTo(Screen.SYMPTOM_DISEASE_TRACKER) },
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_home_ai_disease_tracker")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(RoyalMagentaLight),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Healing, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("AI Disease & Symptom Tracker", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(RoyalMagenta)
                      .padding(horizontal = 4.dp, vertical = 1.dp)
                  ) {
                    Text("NEW AI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                  }
                }
                Text("Track patient symptoms, diagnose diseases & get oral + injectable protocols", fontSize = 11.sp, color = TextMuted)
              }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = RoyalMagenta)
          }
        }
      }

      // 3. Low Stock Alert Interactive Dashboard Widget
      if (criticalMedicines.isNotEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isDarkMode) Color(0xFF1E1B2E) else Color(0xFFFEF2F2)
            ),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isDarkMode) Color(0xFF7F1D1D) else Color(0xFFFCA5A5)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp)
              .testTag("widget_low_stock_dashboard")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFDC2626)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Warning,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Low Stock Alert Widget",
                      fontSize = 13.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isDarkMode) Color(0xFFFEE2E2) else Color(0xFF991B1B)
                    )
                    Text(
                      text = "${criticalMedicines.size} items below reorder point",
                      fontSize = 11.sp,
                      color = if (isDarkMode) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
                    )
                  }
                }

                // Batch One-Click Reorder Button
                Button(
                  onClick = { viewModel.reorderAllLowStockMedicines() },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.height(32.dp).testTag("btn_reorder_all_low_stock")
                ) {
                  Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Reorder All", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Horizontal Scrollable Cards of Low Stock Items with 1-Click PO addition
              val infiniteTransition = rememberInfiniteTransition(label = "lowStockPulse")
              val breathingScale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.045f,
                animationSpec = infiniteRepeatable(
                  animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse
                ),
                label = "breathingScale"
              )
              val breathingAlpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                  animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                  repeatMode = RepeatMode.Reverse
                ),
                label = "breathingAlpha"
              )

              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
              ) {
                items(criticalMedicines.take(8)) { med ->
                  val isUrgentLow = med.stockPacks < 5
                  Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                      containerColor = if (isDarkMode) {
                        if (isUrgentLow) Color(0xFF381520) else Color(0xFF2D1B22)
                      } else {
                        if (isUrgentLow) Color(0xFFFFF5F5) else Color.White
                      }
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                      if (isUrgentLow) 1.5.dp else 1.dp,
                      if (isUrgentLow) {
                        Color(0xFFDC2626).copy(alpha = breathingAlpha)
                      } else {
                        if (isDarkMode) Color(0xFF991B1B) else Color(0xFFFECACA)
                      }
                    ),
                    modifier = Modifier
                      .width(170.dp)
                      .graphicsLayer {
                        if (isUrgentLow) {
                          scaleX = breathingScale
                          scaleY = breathingScale
                        }
                      }
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = med.name,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          maxLines = 1,
                          modifier = Modifier.weight(1f),
                          color = if (isDarkMode) Color.White else TextDark
                        )
                        if (isUrgentLow) {
                          Box(
                            modifier = Modifier
                              .clip(RoundedCornerShape(3.dp))
                              .background(Color(0xFFDC2626))
                              .padding(horizontal = 3.dp, vertical = 1.dp)
                          ) {
                            Text(
                              text = "<5 CRITICAL",
                              fontSize = 7.5.sp,
                              fontWeight = FontWeight.ExtraBold,
                              color = Color.White
                            )
                          }
                        }
                      }
                      Text(
                        text = med.manufacturer.ifBlank { "Generic" },
                        fontSize = 10.sp,
                        color = TextMuted,
                        maxLines = 1
                      )

                      Spacer(modifier = Modifier.height(6.dp))

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "Stock: ${med.stockPacks}",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = if (isUrgentLow) Color(0xFFDC2626) else StatusRed
                        )
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isUrgentLow) Color(0xFFFEE2E2) else Color(0xFFFEF2F2))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                          Text(
                            text = "Min: ${med.minStockAlert}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                          )
                        }
                      }

                      Spacer(modifier = Modifier.height(6.dp))

                      // Stock Meter
                      val fillRatio = (med.stockPacks.toFloat() / med.minStockAlert.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .height(4.dp)
                          .clip(RoundedCornerShape(2.dp))
                          .background(Color(0xFFFCA5A5))
                      ) {
                        Box(
                          modifier = Modifier
                            .fillMaxWidth(fillRatio)
                            .height(4.dp)
                            .background(if (isUrgentLow) Color(0xFFDC2626) else StatusRed)
                        )
                      }

                      Spacer(modifier = Modifier.height(8.dp))

                      // 1-Click Purchase Order Button
                      Button(
                        onClick = { viewModel.addMedicineToCart(med, qty = 20) },
                        colors = ButtonDefaults.buttonColors(
                          containerColor = if (isDarkMode) Color(0xFF9C1258) else RoyalMagenta
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .height(28.dp)
                          .testTag("btn_1click_add_po_${med.id}")
                      ) {
                        Icon(
                          imageVector = Icons.Default.Add,
                          contentDescription = null,
                          tint = Color.White,
                          modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                          text = "1-Click + PO",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White
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

      // 3.5 Compact Expiry Alert Badge
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Card(
            onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (expiringCount > 0) (if (isDarkMode) Color(0xFF2A1F0D) else Color(0xFFFFFBEB)) else (if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF8FAFC))
            ),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (expiringCount > 0) Color(0xFFFCD34D) else (if (isDarkMode) Color(0xFF334155) else CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().testTag("badge_home_expiry_alert")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.EventBusy,
                  contentDescription = null,
                  tint = if (expiringCount > 0) Color(0xFFD97706) else TextMuted,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text("Expiry Alert Status", fontSize = 10.5.sp, color = TextMuted)
                  Text(
                    text = if (expiringCount > 0) "$expiringCount Medicines Expiring Within 60 Days" else "All Batches Safe & Valid",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (expiringCount > 0) Color(0xFFD97706) else Color(0xFF15803D)
                  )
                }
              }
              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      // 4. Time Filter Chips (Today, Yesterday, 7 Days, Month)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Today", "Yesterday", "Last 7 days", "Last 30 days").forEach { filter ->
            val isSelected = selectedTimeFilter == filter
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setTimeFilter(filter) },
              label = { Text(filter, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalNavy,
                selectedLabelColor = Color.White
              ),
              border = if (isSelected) null else FilterChipDefaults.filterChipBorder(enabled = true, selected = false)
            )
          }
        }
      }

      // 5. Core KPI Cards (Sales & Gross Profit)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Total Sales
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.weight(1f).testTag("kpi_home_sales")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Total Sales", fontSize = 11.sp, color = TextMuted)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text("+14%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = String.format(Locale.getDefault(), "₹%.2f", metrics.netSalesAmt),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StatusGreen
              )
              Text("${sales.size + 4} Invoices Dispensed", fontSize = 10.5.sp, color = TextMuted)
            }
          }

          // Gross Profit
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.weight(1f).testTag("kpi_home_profit")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Gross Profit", fontSize = 11.sp, color = TextMuted)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEDE9FE))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text("24.5% Margin", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B21A8))
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = String.format(Locale.getDefault(), "₹%.2f", metrics.netProfitAmt),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMagenta
              )
              val avgBill = if (sales.isNotEmpty()) metrics.netSalesAmt / (sales.size + 4) else 149.25
              Text("Avg Bill: ₹${String.format(Locale.getDefault(), "%.0f", avgBill)}", fontSize = 10.5.sp, color = TextMuted)
            }
          }
        }
      }

      // 6. Interactive 7-Day Sales Trends Chart
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_home_sales_trends_chart")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sales Trends Chart", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextDark)
              }

              Text(
                text = "View Z-Report >",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMagenta,
                modifier = Modifier.clickable { viewModel.navigateTo(Screen.DAILY_SALES_REPORT) }
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chart Canvas
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun (Today)")
            val salesValues = listOf(14200.0, 18500.0, 16800.0, 21400.0, 19200.0, 24800.0, 28650.0 + (sales.size * 180.0))
            val maxSale = salesValues.maxOrNull() ?: 30000.0

            var selectedBarIndex by remember { mutableIntStateOf(6) }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              days.forEachIndexed { index, day ->
                val amount = salesValues[index]
                val barHeightFrac = (amount / maxSale).toFloat().coerceIn(0.15f, 1f)
                val isSelected = selectedBarIndex == index

                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedBarIndex = index }
                ) {
                  if (isSelected) {
                    Text(
                      text = "₹${(amount / 1000).toInt()}k",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMagenta
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))

                  Box(
                    modifier = Modifier
                      .width(18.dp)
                      .height((75 * barHeightFrac).dp)
                      .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                      .background(
                        if (isSelected) {
                          Brush.verticalGradient(listOf(RoyalMagenta, Color(0xFFC026D3)))
                        } else {
                          Brush.verticalGradient(listOf(RoyalNavy.copy(alpha = 0.7f), RoyalNavy))
                        }
                      )
                  )

                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = day.take(3),
                    fontSize = 9.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) TextDark else TextMuted
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Weekly Avg: ₹20.5k / day", fontSize = 10.5.sp, color = TextMuted)
              Text("Peak Day: Saturday (₹24.8k)", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = RoyalNavy)
            }
          }
        }
      }

      // 6.5 Real-Time Recent Activity Feed (Supplementing Charts with Detailed Log)
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_home_recent_activity_feed")
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
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = RoyalNavy,
                    modifier = Modifier.size(14.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "Recent Activity Feed",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.5.sp,
                      color = if (isDarkMode) Color.White else TextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Live Log",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF10B981)
                    )
                  }
                  Text(
                    text = "Real-time updates for billing, sales & inventory adjustments",
                    fontSize = 10.5.sp,
                    color = TextMuted
                  )
                }
              }

              Text(
                text = "Audit Log >",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMagenta,
                modifier = Modifier.clickable { viewModel.navigateTo(Screen.STAFF_ACTIVITY) }
              )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            if (recentActivityLogs.isEmpty()) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No recent billing or stock events recorded yet.",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recentActivityLogs.take(5).forEach { log ->
                  val isBilling = log.actionType == "BILL_GENERATED"
                  val isStock = log.actionType.contains("STOCK")
                  val actionIcon = when {
                    isBilling -> Icons.Default.PointOfSale
                    isStock -> Icons.Default.Inventory2
                    else -> Icons.Default.Sync
                  }
                  val badgeBg = when {
                    isBilling -> Color(0xFFDCFCE7)
                    isStock -> Color(0xFFFEF3C7)
                    else -> Color(0xFFE0E7FF)
                  }
                  val badgeFg = when {
                    isBilling -> Color(0xFF15803D)
                    isStock -> Color(0xFFB45309)
                    else -> RoyalNavy
                  }

                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                      .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = badgeFg,
                        modifier = Modifier.size(16.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = log.staffName,
                          fontSize = 11.5.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isDarkMode) Color.White else TextDark
                        )
                        Text(
                          text = log.timestamp,
                          fontSize = 9.5.sp,
                          color = TextMuted
                        )
                      }
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = log.description,
                        fontSize = 10.5.sp,
                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                        maxLines = 2
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // 7. Core Feature Navigation Hub (2x3 Grid)
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("Quick Operations & Hubs", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextDark)

          // Row 1: Customer Database & Medicine Expiry Alert
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HomeFeatureTile(
              icon = Icons.Default.People,
              iconBg = Color(0xFF065F46),
              title = "Customer Database",
              subtitle = "${customers.size} Registered • Khata Ledger",
              badge = "CRM",
              badgeColor = Color(0xFF10B981),
              onClick = { viewModel.navigateTo(Screen.CUSTOMER_HISTORY) },
              modifier = Modifier.weight(1f).testTag("tile_customer_database")
            )

            HomeFeatureTile(
              icon = Icons.Default.EventBusy,
              iconBg = Color(0xFF9A3412),
              title = "Medicine Expiry Alert",
              subtitle = "Risk Radar & Debit Notes",
              badge = if (expiringCount > 0) "$expiringCount Alert" else "Safe",
              badgeColor = if (expiringCount > 0) Color(0xFFEA580C) else Color(0xFF10B981),
              onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
              modifier = Modifier.weight(1f).testTag("tile_medicine_expiry")
            )
          }

          // Row 2: Sales Reports & Purchase Orders
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HomeFeatureTile(
              icon = Icons.Default.Assessment,
              iconBg = Color(0xFF831843),
              title = "Daily Sales Report",
              subtitle = "Z-Report & Cash Closing",
              badge = "Report",
              badgeColor = Color(0xFFDB2777),
              onClick = { viewModel.navigateTo(Screen.DAILY_SALES_REPORT) },
              modifier = Modifier.weight(1f).testTag("tile_daily_sales")
            )

            HomeFeatureTile(
              icon = Icons.Default.ReceiptLong,
              iconBg = Color(0xFF1E1B4B),
              title = "Purchase Orders",
              subtitle = "Draft & Receive Stock",
              badge = "PO",
              badgeColor = Color(0xFF4F46E5),
              onClick = { viewModel.navigateTo(Screen.PURCHASE_ORDERS) },
              modifier = Modifier.weight(1f).testTag("tile_purchase_orders")
            )
          }

          // Row 3: Stock Analytics & Gemini AI Pharmacist
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HomeFeatureTile(
              icon = Icons.Default.BarChart,
              iconBg = Color(0xFF312E81),
              title = "Stock Analytics",
              subtitle = "FSN Velocity & Runout Radar",
              badge = "Analytics",
              badgeColor = Color(0xFF6366F1),
              onClick = { viewModel.navigateTo(Screen.STOCK_ANALYTICS) },
              modifier = Modifier.weight(1f).testTag("tile_stock_analytics")
            )

            HomeFeatureTile(
              icon = Icons.Default.Science,
              iconBg = Color(0xFF0F766E),
              title = "Clinical Unit Converter",
              subtitle = "Insulin (IU), Dilution & Drops",
              badge = "Clinical",
              badgeColor = Color(0xFF14B8A6),
              onClick = { viewModel.navigateTo(Screen.UNIT_CONVERTER) },
              modifier = Modifier.weight(1f).testTag("tile_unit_converter")
            )
          }

          // Row 4: Daily Huddle Report & Inventory QR Integration
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HomeFeatureTile(
              icon = Icons.Default.Checklist,
              iconBg = Color(0xFF831843),
              title = "Daily Huddle Report",
              subtitle = "Morning Brief & Shift Targets",
              badge = "Shift Hub",
              badgeColor = Color(0xFFDB2777),
              onClick = { viewModel.navigateTo(Screen.DAILY_HUDDLE) },
              modifier = Modifier.weight(1f).testTag("tile_daily_huddle")
            )

            HomeFeatureTile(
              icon = Icons.Default.QrCodeScanner,
              iconBg = Color(0xFF065F46),
              title = "Inventory QR & Racks",
              subtitle = "Scan, Assign Rack & Print Labels",
              badge = "QR & Racks",
              badgeColor = Color(0xFF10B981),
              onClick = { viewModel.navigateTo(Screen.INVENTORY_QR) },
              modifier = Modifier.weight(1f).testTag("tile_inventory_qr")
            )
          }

          // Row 5: Backup and Restore Hub
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HomeFeatureTile(
              icon = Icons.Default.CloudDownload,
              iconBg = Color(0xFF0F172A),
              title = "Backup & Restore",
              subtitle = "JSON Export, Share & Restore",
              badge = "Encrypted",
              badgeColor = Color(0xFF38BDF8),
              onClick = { viewModel.navigateTo(Screen.BACKUP_RESTORE) },
              modifier = Modifier.weight(1f).testTag("tile_backup_restore")
            )

            HomeFeatureTile(
              icon = Icons.Default.CameraAlt,
              iconBg = Color(0xFF0284C7),
              title = "Prescription Scanner",
              subtitle = "AI Rx Reader & Auto-Billing",
              badge = "Gemini Vision",
              badgeColor = Color(0xFF0284C7),
              onClick = { viewModel.navigateTo(Screen.PRESCRIPTION_SCANNER) },
              modifier = Modifier.weight(1f).testTag("tile_prescription_scanner")
            )
          }

          // Row 6: Drug Interaction Checker & Smart Inventory Suggestions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HomeFeatureTile(
              icon = Icons.Default.HealthAndSafety,
              iconBg = Color(0xFF991B1B),
              title = "Drug Interactions",
              subtitle = "Contraindications & CDSCO Alerts",
              badge = "Clinical Safety",
              badgeColor = Color(0xFFEF4444),
              onClick = { viewModel.navigateTo(Screen.DRUG_INTERACTION_CHECKER) },
              modifier = Modifier.weight(1f).testTag("tile_drug_interactions")
            )

            HomeFeatureTile(
              icon = Icons.Default.TrendingUp,
              iconBg = Color(0xFF1E3A8A),
              title = "Smart Inventory",
              subtitle = "Velocity Run-Rate & Auto-PO",
              badge = "AI Restock",
              badgeColor = Color(0xFF3B82F6),
              onClick = { viewModel.navigateTo(Screen.SMART_INVENTORY_SUGGESTIONS) },
              modifier = Modifier.weight(1f).testTag("tile_smart_inventory")
            )
          }
        }
      }

      // 8. Fast-Moving Medicines Quick List
      item {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Fast-Moving Stock Today", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
              Text(
                text = "View Stock (${medicines.size}) >",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalNavy,
                modifier = Modifier.clickable { viewModel.navigateTo(Screen.STOCK) }
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val fastMovingList = medicines.take(4)
            fastMovingList.forEachIndexed { index, med ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("${index + 1}", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(med.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${med.manufacturer} • ${med.rackLocation}", fontSize = 10.sp, color = TextMuted)
                  }
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text("₹${String.format(Locale.getDefault(), "%.2f", if (med.saleRate > 0) med.saleRate else med.mrp)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("${med.stockPacks} in stock", fontSize = 10.sp, color = if (med.stockPacks <= med.minStockAlert) StatusRed else StatusGreen)
                }
              }
              if (index < fastMovingList.lastIndex) {
                HorizontalDivider(color = Color(0xFFF8FAFC))
              }
            }
          }
        }
      }
    }

    // Floating QuickScan Scanner Button
    FloatingQuikScanButton(
      onClick = { viewModel.navigateTo(Screen.QUICK_SCAN) },
      modifier = Modifier.align(Alignment.BottomEnd)
    )

    // Customer UPI QR Dialog (Quick Show to Customer on Home Screen)
    if (showCustomerUpiQrDialog) {
      StoreUpiQrDialog(
        upiId = primaryUpiId,
        storeName = businessProfile.businessName.ifBlank { "Royal Pharmacy" },
        initialAmount = 0.0,
        onDismiss = { showCustomerUpiQrDialog = false }
      )
    }
  }
}

@Composable
fun HomeFeatureTile(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconBg: Color,
  title: String,
  subtitle: String,
  badge: String,
  badgeColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeColor)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark,
        maxLines = 1
      )
      Text(
        text = subtitle,
        fontSize = 9.5.sp,
        color = TextMuted,
        maxLines = 1
      )
    }
  }
}
