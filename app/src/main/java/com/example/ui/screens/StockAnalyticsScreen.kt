package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.MedicineItem
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

@Composable
fun StockAnalyticsScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val medicines by viewModel.allMedicines.collectAsState()
  val sales by viewModel.allSales.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: FSN Velocity, 1: Stock Runout & Reorder, 2: Ageing & Dead Stock, 3: Category Margins
  var searchQuery by remember { mutableStateOf("") }

  // Key Valuation Metrics
  val totalSkus = medicines.size
  val totalStockPacks = medicines.sumOf { it.stockPacks }
  val totalCostValuation = medicines.sumOf { it.stockPacks * it.purchaseRate }
  val totalMrpValuation = medicines.sumOf { it.stockPacks * it.mrp }
  val totalPotentialProfit = (totalMrpValuation - totalCostValuation).coerceAtLeast(0.0)
  val overallMarginPercent = if (totalMrpValuation > 0) (totalPotentialProfit / totalMrpValuation) * 100.0 else 0.0

  // FSN Matrix Classification
  val fastMoving = medicines.filter { it.dispenseCount >= 40 }
  val slowMoving = medicines.filter { it.dispenseCount in 10..39 }
  val nonMovingDeadStock = medicines.filter { it.dispenseCount < 10 }

  val deadStockCostLocked = nonMovingDeadStock.sumOf { it.stockPacks * it.purchaseRate }
  val criticalLowStockCount = medicines.count { it.stockPacks <= it.minStockAlert }

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
          onClick = { viewModel.navigateTo(Screen.STOCK) },
          modifier = Modifier.size(36.dp).testTag("btn_back_stock_analytics")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Stock & Inventory Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("FSN Matrix, Runout Estimator & Dead Stock", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Button(
        onClick = { viewModel.navigateTo(Screen.PURCHASE_ORDERS) },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.testTag("btn_goto_pos_from_analytics")
      ) {
        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Create PO", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
      }
    }

    // 2. Executive Stock Valuation Card
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Slate 900
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("TOTAL STOCK VALUE (PURCHASE COST)", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
            Text(
              "₹${String.format(Locale.getDefault(), "%,.1f", totalCostValuation)}",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF10B981).copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              "${String.format(Locale.getDefault(), "%.1f", overallMarginPercent)}% Margin",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF34D399)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFF334155))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Retail MRP Value", fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text("₹${String.format(Locale.getDefault(), "%,.1f", totalMrpValuation)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFF1F5F9))
          }
          Column {
            Text("Total SKUs / Units", fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text("$totalSkus SKUs (${totalStockPacks} packs)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF38BDF8))
          }
          Column {
            Text("Dead Stock Locked", fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text("₹${String.format(Locale.getDefault(), "%,.1f", deadStockCostLocked)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFF87171))
          }
        }
      }
    }

    // 3. Navigation Tabs
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
        text = { Text("FSN Velocity", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_analytics_fsn")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Runout & Reorder", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_analytics_runout")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Dead Stock", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_analytics_deadstock")
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = { Text("Category Margins", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_analytics_margins")
      )
    }

    // 4. Tab Content
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      when (selectedTab) {
        0 -> {
          // FSN Velocity Tab
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("FSN Velocity Classification", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Text("Categorization by sales frequency & dispensing turns", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  FsnSummaryChip(
                    title = "Fast Moving (F)",
                    count = fastMoving.size,
                    color = Color(0xFF047857),
                    bgColor = Color(0xFFD1FAE5),
                    modifier = Modifier.weight(1f)
                  )
                  FsnSummaryChip(
                    title = "Slow Moving (S)",
                    count = slowMoving.size,
                    color = Color(0xFFD97706),
                    bgColor = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f)
                  )
                  FsnSummaryChip(
                    title = "Non-Moving (N)",
                    count = nonMovingDeadStock.size,
                    color = Color(0xFFDC2626),
                    bgColor = Color(0xFFFEE2E2),
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }
          }

          item {
            Text("Top Fast-Moving Medicines (High Turnover)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
          }

          items(fastMoving) { med ->
            MedicineVelocityCard(
              medicine = med,
              tier = "FAST",
              onAddToCart = {
                viewModel.addToCart(
                  CartItem(
                    medicineName = med.name,
                    manufacturer = med.manufacturer,
                    composition = med.composition,
                    quantity = 20,
                    unitRate = med.purchaseRate,
                    mrp = med.mrp,
                    itemType = "PURCHASE_ORDER",
                    note = "Fast-Moving Reorder"
                  )
                )
                Toast.makeText(context, "Added ${med.name} to Distributor Cart", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
        1 -> {
          // Stock Runout & Reorder Estimator
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("Stockout Risk & Runout Predictor", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RoyalNavy)
                  Text("Estimated days before stock depletes based on daily dispensing velocity.", fontSize = 11.sp, color = Color(0xFF1E40AF))
                }
              }
            }
          }

          val sortedByUrgency = medicines.sortedBy { med ->
            val dailyVelocity = (med.dispenseCount / 30.0).coerceAtLeast(0.1)
            med.stockPacks / dailyVelocity
          }

          items(sortedByUrgency) { med ->
            val dailyVelocity = (med.dispenseCount / 30.0).coerceAtLeast(0.1)
            val estimatedDaysRemaining = (med.stockPacks / dailyVelocity).toInt()

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
                  Column(modifier = Modifier.weight(1f)) {
                    Text(med.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Text("${med.manufacturer} • ${med.composition}", fontSize = 11.sp, color = TextMuted)
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(
                        when {
                          estimatedDaysRemaining <= 5 -> Color(0xFFFEE2E2)
                          estimatedDaysRemaining <= 15 -> Color(0xFFFEF3C7)
                          else -> Color(0xFFDCFCE7)
                        }
                      )
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(
                      if (estimatedDaysRemaining <= 0) "OUT OF STOCK" else "$estimatedDaysRemaining Days Left",
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = when {
                        estimatedDaysRemaining <= 5 -> StatusRed
                        estimatedDaysRemaining <= 15 -> Color(0xFFD97706)
                        else -> StatusGreen
                      }
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Current Stock: ${med.stockPacks} packs (Alert: ${med.minStockAlert})", fontSize = 11.5.sp, color = TextDark)
                  OutlinedButton(
                    onClick = {
                      viewModel.addToCart(
                        CartItem(
                          medicineName = med.name,
                          manufacturer = med.manufacturer,
                          composition = med.composition,
                          quantity = 30,
                          unitRate = med.purchaseRate,
                          mrp = med.mrp,
                          itemType = "PURCHASE_ORDER",
                          note = "Predicted Stockout Reorder"
                        )
                      )
                      Toast.makeText(context, "Added 30 packs of ${med.name} to Cart", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto Reorder", fontSize = 10.5.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
        2 -> {
          // Dead Stock & Capital Trapped
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(22.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Dead Stock & Locked Capital", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF9F1239))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Medicines with less than 10 dispensing turns. Recommend supplier return or special discount clearance.", fontSize = 11.5.sp, color = Color(0xFFBE123C))
              }
            }
          }

          items(nonMovingDeadStock) { med ->
            MedicineVelocityCard(
              medicine = med,
              tier = "DEAD",
              onAddToCart = {
                viewModel.addToCart(
                  CartItem(
                    medicineName = med.name,
                    manufacturer = med.manufacturer,
                    composition = med.composition,
                    quantity = med.stockPacks.coerceAtLeast(1),
                    unitRate = med.purchaseRate,
                    mrp = med.mrp,
                    itemType = "EXPIRY_RETURN",
                    note = "Non-Moving Stock Return"
                  )
                )
                Toast.makeText(context, "Added ${med.name} to Expiry/Return Cart", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
        3 -> {
          // Category Margins Breakdown
          val categories = medicines.groupBy { it.category }

          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("Category Profit Margin Analysis", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Text("Gross margin spread by dosage and pharmacological form", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(12.dp))

                categories.forEach { (category, items) ->
                  val cost = items.sumOf { it.stockPacks * it.purchaseRate }
                  val mrp = items.sumOf { it.stockPacks * it.mrp }
                  val profit = (mrp - cost).coerceAtLeast(0.0)
                  val marginPct = if (mrp > 0) (profit / mrp) * 100.0 else 0.0

                  Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text("$category (${items.size} SKUs)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                      Text("${String.format(Locale.getDefault(), "%.1f", marginPct)}% Margin", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StatusGreen)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                      progress = { (marginPct / 100.0).toFloat().coerceIn(0f, 1f) },
                      modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                      color = if (marginPct > 20.0) StatusGreen else RoyalNavy,
                      trackColor = GrayBackground
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
fun FsnSummaryChip(
  title: String,
  count: Int,
  color: Color,
  bgColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(8.dp)
  ) {
    Column {
      Text(title, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
      Text("$count SKUs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
  }
}

@Composable
fun MedicineVelocityCard(
  medicine: MedicineItem,
  tier: String,
  onAddToCart: () -> Unit
) {
  val margin = ((medicine.mrp - medicine.purchaseRate) / medicine.mrp * 100.0).coerceAtLeast(0.0)

  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
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
        Text(medicine.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
        Text("${medicine.manufacturer} • Stock: ${medicine.stockPacks} packs", fontSize = 11.5.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          "MRP: ₹${medicine.mrp} | Cost: ₹${medicine.purchaseRate} (${String.format(Locale.getDefault(), "%.1f", margin)}% Margin)",
          fontSize = 11.sp,
          color = RoyalNavy,
          fontWeight = FontWeight.Medium
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (tier == "FAST") Color(0xFFD1FAE5) else Color(0xFFFEE2E2))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            "${medicine.dispenseCount} Sold",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (tier == "FAST") Color(0xFF047857) else Color(0xFFDC2626)
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        IconButton(
          onClick = onAddToCart,
          modifier = Modifier.size(28.dp).clip(CircleShape).background(GrayBackground)
        ) {
          Icon(Icons.Default.AddShoppingCart, contentDescription = "Action", tint = RoyalNavy, modifier = Modifier.size(14.dp))
        }
      }
    }
  }
}
