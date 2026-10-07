package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.CsvExportService
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
import java.util.Locale
import androidx.compose.material.icons.filled.Download
import androidx.compose.ui.platform.LocalContext

@Composable
fun ReportsScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val analytics by viewModel.analyticsData.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()
  val sales by viewModel.allSales.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Financial Trends, 1: 90-Day Expiry Radar, 2: Low Stock & Fast Moving

  val expiringWithin90Days = medicines.filter { it.expiryDate.contains("26") || it.isExpired }
  val lowStockMeds = medicines.filter { it.stockPacks <= it.minStockAlert }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Text(
            text = "Reports & Analytics",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              if (selectedTab == 0) CsvExportService.exportSalesToCsv(context, sales)
              else CsvExportService.exportInventoryToCsv(context, medicines)
            },
            modifier = Modifier.testTag("btn_export_csv_reports")
          ) {
            Icon(Icons.Default.Download, contentDescription = "Export CSV", tint = RoyalNavy)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.SMART_SALES_ANALYTICS) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(end = 6.dp).testTag("btn_smart_sales_reports")
          ) {
            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Smart Analytics", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.AI_CHATBOT) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B21B6)),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(end = 8.dp).testTag("btn_ask_ai_reports")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ask AI", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = RoyalMagenta,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = RoyalMagenta
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Revenue Trends", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
          modifier = Modifier.testTag("tab_rep_revenue")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("90-Day Expiry Radar", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
          modifier = Modifier.testTag("tab_rep_expiry")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Inventory Alerts", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
          modifier = Modifier.testTag("tab_rep_inventory")
        )
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        when (selectedTab) {
          0 -> {
            // Tab 0: Revenue & Sales Analytics
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text("Daily Sales (Today)", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = String.format(Locale.getDefault(), "₹%.2f", analytics.todaySales),
                      fontSize = 18.sp,
                      fontWeight = FontWeight.Bold,
                      color = StatusGreen
                    )
                    Text("Counter Invoices: ${sales.size}", fontSize = 10.sp, color = TextMuted)
                  }
                }

                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                  ),
                  modifier = Modifier.weight(1f)
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text("Monthly Revenue", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = String.format(Locale.getDefault(), "₹%.2f", analytics.monthlyRevenue),
                      fontSize = 18.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMagenta
                    )
                    Text("Est. Margin: 22%", fontSize = 10.sp, color = TextMuted)
                  }
                }
              }
            }

            // Visual Data Chart: 7-Day Daily Sales Trend (Canvas Drawing)
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier.fillMaxWidth().testTag("chart_daily_sales")
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "7-Day Sales Trend (₹)",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(RoyalMagenta))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Daily Gross", fontSize = 10.sp, color = TextMuted)
                    }
                  }

                  Spacer(modifier = Modifier.height(16.dp))

                  // Interactive Bar Chart Canvas
                  val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                  val values = listOf(340f, 520f, 410f, 690f, 580f, 920f, 597f)
                  val maxValue = 1000f

                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(140.dp)
                  ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                      val barWidth = size.width / (days.size * 2f)
                      val chartHeight = size.height - 24.dp.toPx()

                      // Draw subtle grid lines
                      for (i in 1..3) {
                        val y = chartHeight * (i / 4f)
                        drawLine(
                          color = Color(0xFFE2E8F0),
                          start = Offset(0f, y),
                          end = Offset(size.width, y),
                          strokeWidth = 1.dp.toPx()
                        )
                      }

                      // Draw bars
                      values.forEachIndexed { index, value ->
                        val barHeight = (value / maxValue) * chartHeight
                        val x = index * (size.width / days.size) + barWidth / 2
                        val y = chartHeight - barHeight

                        drawRoundRect(
                          color = if (index == days.lastIndex) Color(0xFF9C1258) else Color(0xFFE1BEE7),
                          topLeft = Offset(x, y),
                          size = Size(barWidth, barHeight),
                          cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                      }
                    }

                    // X-axis day labels
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                      horizontalArrangement = Arrangement.SpaceAround
                    ) {
                      days.forEach { day ->
                        Text(day, fontSize = 10.sp, color = TextMuted)
                      }
                    }
                  }
                }
              }
            }

            // Top Dispensed Drugs Ranking
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier.fillMaxWidth().testTag("ranking_top_dispensed")
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Most Frequently Dispensed Drugs",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                  Spacer(modifier = Modifier.height(10.dp))

                  analytics.topDispensed.forEachIndexed { index, pair ->
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = "#${index + 1}",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = RoyalMagenta,
                          modifier = Modifier.width(26.dp)
                        )
                        Text(pair.first, fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium)
                      }
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(0xFFEDE9FE))
                          .padding(horizontal = 8.dp, vertical = 2.dp)
                      ) {
                        Text("${pair.second} packs dispensed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B21A8))
                      }
                    }
                    if (index < analytics.topDispensed.lastIndex) {
                      Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(CardBorder))
                    }
                  }
                }
              }
            }
          }

          1 -> {
            // Tab 1: 90-Day Expiry Radar Alert System
            item {
              Card(
                onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCA5A5))
                ),
                modifier = Modifier.fillMaxWidth().testTag("banner_reports_open_expiry_dashboard")
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = "Batch Expiry Dashboard",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusRed
                      )
                      Text(
                        text = "${expiringWithin90Days.size} batches at risk. Tap for full Risk Matrix & Debit Notes >",
                        fontSize = 11.sp,
                        color = Color(0xFF7F1D1D)
                      )
                    }
                  }
                  Button(
                    onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                  ) {
                    Text("Open Dashboard", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            items(expiringWithin90Days, key = { it.id }) { med ->
              Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(med.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Batch: ${med.batchNumber} • ${med.manufacturer}", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (med.isExpired) Color(0xFFFEE2E2) else Color(0xFFFFFBEB))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = if (med.isExpired) "EXPIRED (${med.expiryDate})" else "Expiring Soon (${med.expiryDate})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (med.isExpired) StatusRed else Color(0xFFB45309)
                      )
                    }
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text("${med.stockPacks} Packs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Val: ₹${med.stockPacks * med.mrp}", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                      onClick = { viewModel.reduceMedicineQuantity(med.id, med.stockPacks, "Expired Return") },
                      shape = RoundedCornerShape(6.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                      Text("Write-Off", fontSize = 10.sp, color = Color.White)
                    }
                  }
                }
              }
            }
          }

          2 -> {
            // Tab 2: Low Stock Items (< 5 packs)
            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCD34D))
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = "Low Stock Replenishment List",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFB45309)
                    )
                    Text(
                      text = "${lowStockMeds.size} medicines are at or below minimum threshold (5 packs).",
                      fontSize = 11.sp,
                      color = Color(0xFF78350F)
                    )
                  }
                }
              }
            }

            items(lowStockMeds, key = { it.id }) { med ->
              Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(med.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(med.manufacturer, fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "Current Stock: ${med.stockPacks} pack(s)",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (med.stockPacks == 0) StatusRed else Color(0xFFD97706)
                    )
                  }

                  Button(
                    onClick = {
                      viewModel.createPurchaseOrder(
                        supplierId = 1,
                        supplierName = "Sun Pharma Hub",
                        itemsJson = "${med.name} (x50 packs)",
                        totalAmount = med.purchaseRate * 50,
                        expectedDeliveryDate = "12-10-2026"
                      )
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Text("Reorder 50", fontSize = 11.sp, color = Color.White)
                  }
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
  }
}
