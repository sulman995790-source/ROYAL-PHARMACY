package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun SmartSalesAnalyticsScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedPeriod by remember { mutableStateOf("Last 7 days") }
  val salesSummary = remember(selectedPeriod) { viewModel.getSmartSalesSummary(selectedPeriod) }
  val allSales by viewModel.allSales.collectAsState()
  val businessProfile by viewModel.businessProfile.collectAsState()

  val periods = listOf("Today", "Yesterday", "Last 7 days", "Last 30 days", "Quarterly")

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 90.dp)
    ) {
      // 1. Top Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Column {
              Text(
                text = "Smart Sales Analytics",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "${businessProfile.businessName} • AI Insights & Margin Radar",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          IconButton(
            onClick = {
              Toast.makeText(context, "Exported Analytics Summary Report (CSV)", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.testTag("btn_export_analytics")
          ) {
            Icon(Icons.Default.FileDownload, contentDescription = "Export Report", tint = RoyalNavy)
          }
        }
      }

      // 2. Period Filter Chips
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          periods.forEach { period ->
            val isSelected = selectedPeriod == period
            FilterChip(
              selected = isSelected,
              onClick = { selectedPeriod = period },
              label = {
                Text(
                  text = period,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              shape = RoundedCornerShape(18.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalMagenta.copy(alpha = 0.12f),
                selectedLabelColor = RoyalMagenta,
                containerColor = Color(0xFFF1F5F9),
                labelColor = TextMuted
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = if (isSelected) RoyalMagenta else CardBorder
              ),
              modifier = Modifier.testTag("period_chip_${period.replace(" ", "_").lowercase()}")
            )
          }
        }
      }

      // 3. AI Executive Insights Banner
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)), // Dark Indigo
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("card_ai_sales_insights")
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
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6).copy(alpha = 0.3f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFC4B5FD),
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Executive AI Pharmacist Intelligence",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF10B981).copy(alpha = 0.25f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("Live Radar", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            salesSummary.aiInsights.forEach { insight ->
              Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text("💡 ", fontSize = 11.sp)
                Text(
                  text = insight,
                  fontSize = 11.5.sp,
                  color = Color(0xFFE2E8F0),
                  lineHeight = 16.sp
                )
              }
            }
          }
        }
      }

      // 4. Primary KPI Summary Grid
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Row 1: Gross Sales & Net Profit
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
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Total Revenue", fontSize = 11.sp, color = TextMuted)
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(StatusGreen.copy(alpha = 0.15f))
                      .padding(horizontal = 5.dp, vertical = 2.dp)
                  ) {
                    Text("+14.2% ↑", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = String.format(Locale.getDefault(), "₹%.2f", salesSummary.totalRevenue),
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Text(
                  text = "${salesSummary.totalOrders} total invoices",
                  fontSize = 10.sp,
                  color = TextMuted
                )
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
                      .background(RoyalMagenta.copy(alpha = 0.12f))
                      .padding(horizontal = 5.dp, vertical = 2.dp)
                  ) {
                    Text("${salesSummary.grossProfitMarginPercent}% Margin", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = String.format(Locale.getDefault(), "₹%.2f", salesSummary.grossProfit),
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalNavy
                )
                Text(
                  text = "AOV: ₹${String.format(Locale.getDefault(), "%.1f", salesSummary.averageOrderValue)}",
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }
          }

          // Row 2: Peak Hours & Digital Realization
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
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Peak Dispensary Slot", fontSize = 11.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = salesSummary.peakHourSlot,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Text(
                  text = "Generates 46% of footfall",
                  fontSize = 10.sp,
                  color = Color(0xFFD97706)
                )
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
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Payments, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Payment Mix", fontSize = 11.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "84% Realized",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = StatusGreen
                )
                Text(
                  text = "Udhar: 12% • UPI: 36%",
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }
          }
        }
      }

      // 5. Visual Revenue & Margin Trend Chart (Canvas Drawn)
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("card_sales_trend_chart")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Daily Sales & Margin Trajectory", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
              Text("Values in ₹", fontSize = 10.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Bar Chart
            val dayPoints = listOf(
              Triple("Mon", 2400f, 610f),
              Triple("Tue", 3100f, 790f),
              Triple("Wed", 2800f, 720f),
              Triple("Thu", 3600f, 920f),
              Triple("Fri", 4200f, 1080f),
              Triple("Sat", 4800f, 1220f),
              Triple("Sun", 3900f, 980f)
            )

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height - 24.dp.toPx()
                val maxVal = 5200f
                val barWidth = 14.dp.toPx()
                val step = canvasW / dayPoints.size

                dayPoints.forEachIndexed { idx, point ->
                  val x = idx * step + (step - barWidth) / 2
                  val barH = (point.second / maxVal) * canvasH
                  val profitH = (point.third / maxVal) * canvasH

                  // Revenue Bar
                  drawRoundRect(
                    color = Color(0xFFE2E8F0),
                    topLeft = Offset(x, canvasH - barH),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                  )

                  // Profit Fill (Inner Bar)
                  drawRoundRect(
                    brush = Brush.verticalGradient(
                      colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0))
                    ),
                    topLeft = Offset(x, canvasH - profitH),
                    size = Size(barWidth, profitH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                  )
                }
              }

              // Day labels row below bars
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .align(Alignment.BottomCenter)
                  .padding(top = 110.dp),
                horizontalArrangement = Arrangement.SpaceAround
              ) {
                dayPoints.forEach { point ->
                  Text(point.first, fontSize = 9.sp, color = TextMuted)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFCBD5E1)))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Gross Sale", fontSize = 10.sp, color = TextMuted)
              Spacer(modifier = Modifier.width(16.dp))
              Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RoyalMagenta))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Gross Profit (24.5%)", fontSize = 10.sp, color = TextDark, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 6. Category Revenue Contribution
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Category Revenue Contribution",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Distribution across formulation classes",
              fontSize = 10.5.sp,
              color = TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            salesSummary.topCategories.forEach { (category, amt) ->
              val pct = if (salesSummary.totalRevenue > 0) (amt / salesSummary.totalRevenue).toFloat() else 0.2f
              Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(category, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = TextDark)
                  Text("₹${String.format(Locale.getDefault(), "%.1f", amt)} (${(pct * 100).toInt()}%)", fontSize = 11.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                  progress = { pct },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = when {
                    category.contains("Injection") -> Color(0xFFD97706)
                    category.contains("Syrup") -> Color(0xFF0F766E)
                    category.contains("Tablet") -> RoyalMagenta
                    else -> Color(0xFF4338CA)
                  },
                  trackColor = Color(0xFFF1F5F9),
                )
              }
            }
          }
        }
      }

      // 7. Top-Selling Medicines Leaderboard
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("card_top_selling_medicines")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Fast-Moving Medicines",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text("Ranked by units", fontSize = 10.5.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(10.dp))

            salesSummary.topSellingMedicines.forEachIndexed { index, med ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (index < 3) RoyalMagenta.copy(alpha = 0.12f) else Color(0xFFF1F5F9)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${index + 1}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (index < 3) RoyalMagenta else TextMuted
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(med.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("${med.unitsSold} units sold • Margin: ${med.marginPercent}%", fontSize = 10.sp, color = TextMuted)
                }

                Text(
                  text = "₹${med.revenue.toInt()}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = StatusGreen
                )
              }
            }
          }
        }
      }

      // 8. Top Prescribing Doctors
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Doctor Prescription Share", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            salesSummary.topDoctors.forEach { doc ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(doc.doctorName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text(doc.clinicName, fontSize = 10.sp, color = TextMuted)
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text("₹${doc.totalValue.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                  Text("${doc.prescriptionCount} Rx filled", fontSize = 10.sp, color = TextMuted)
                }
              }
            }
          }
        }
      }

      // 9. Quick Actions Footer
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
            modifier = Modifier.weight(1f)
          ) {
            Text("Expiry Dashboard >", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            modifier = Modifier.weight(1f)
          ) {
            Text("New Sale Bill >", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
