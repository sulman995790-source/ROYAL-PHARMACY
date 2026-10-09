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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.MedicineItem
import com.example.service.DistributorExportService
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.BatchRiskTier
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun InventoryDashboardScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val medicines by viewModel.allMedicines.collectAsState()
  val criticalMedicines by viewModel.criticalLowStockMedicines.collectAsState()
  val batchItems by viewModel.batchExpiryItems.collectAsState()
  val suppliers by viewModel.allSuppliers.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()
  val isAudioAlertsEnabled by viewModel.isAudioAlertsEnabled.collectAsState()

  androidx.compose.runtime.LaunchedEffect(criticalMedicines) {
    if (criticalMedicines.isNotEmpty() && isAudioAlertsEnabled) {
      viewModel.playLowStockAlert(context)
    }
  }

  var selectedFilterTab by remember { mutableIntStateOf(0) }

  // Stock Valuation Metrics
  val totalSkus = medicines.size
  val totalUnits = medicines.sumOf { it.stockPacks }
  val totalCostValue = medicines.sumOf { it.stockPacks * it.purchaseRate }
  val totalMrpValue = medicines.sumOf { it.stockPacks * it.mrp }
  val grossProfitMarginValue = totalMrpValue - totalCostValue
  val grossProfitMarginPercent = if (totalMrpValue > 0) (grossProfitMarginValue / totalMrpValue) * 100 else 0.0

  // ABC and Velocity Classification
  val outOfStockCount = medicines.count { it.stockPacks <= 0 }
  val lowStockCount = medicines.count { it.stockPacks in 1..it.minStockAlert }
  val fastMovingCount = medicines.count { it.stockPacks > 50 || it.dispenseCount > 10 }
  val slowMovingCount = medicines.count { it.stockPacks in 11..30 && it.dispenseCount <= 2 }

  // Expiry Risk
  val expiredBatches = batchItems.filter { it.riskTier == BatchRiskTier.EXPIRED }
  val nearExpiry30Batches = batchItems.filter { it.riskTier == BatchRiskTier.CRITICAL_30 }
  val totalExpiryRiskValue = (expiredBatches + nearExpiry30Batches).sumOf { it.valueAtRisk }

  // Category breakdown
  val categoryMap = medicines.groupBy { it.category }.mapValues { entry ->
    val units = entry.value.sumOf { it.stockPacks }
    val cost = entry.value.sumOf { it.stockPacks * it.purchaseRate }
    Pair(units, cost)
  }

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
          modifier = Modifier.size(36.dp).testTag("btn_back_from_inv_dashboard")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Inventory Executive Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("Valuation, ABC Velocity & Stock Health", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = {
            val exportItems = medicines.map { m ->
              CartItem(
                medicineName = m.name,
                manufacturer = m.manufacturer,
                category = m.category,
                batchNumber = m.batchNumber,
                expiryDate = m.expiryDate,
                quantity = m.stockPacks,
                unitRate = m.purchaseRate,
                mrp = m.mrp,
                itemType = "STOCK_VALUATION"
              )
            }
            DistributorExportService.generateAndShareXls(
              context = context,
              cartItems = exportItems,
              distributorName = "Master Stock Audit",
              pharmacyName = profile.businessName
            )
          },
          modifier = Modifier.testTag("btn_export_inv_csv")
        ) {
          Icon(Icons.Default.Download, contentDescription = "Export Excel", tint = Color.White)
        }

        IconButton(
          onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
          modifier = Modifier.testTag("btn_goto_expiry")
        ) {
          Icon(Icons.Default.EventBusy, contentDescription = "Expiry Tracker", tint = Color(0xFFFCA5A5))
        }
      }
    }

    // 2. Scrollable Body
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Stock Valuation Master Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_stock_valuation")
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
                    .background(Color(0xFFEDE9FE)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("Total Stock Valuation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("$totalSkus Product Lines • $totalUnits Total Units", fontSize = 11.sp, color = TextMuted)
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFDCFCE7))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "+${String.format(Locale.getDefault(), "%.1f", grossProfitMarginPercent)}% Margin",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = StatusGreen
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two main valuation blocks
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Purchase Cost Valuation
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFF8FAFC))
                  .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                  .padding(12.dp)
              ) {
                Column {
                  Text("COST VALUATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "₹${String.format(Locale.getDefault(), "%,.0f", totalCostValue)}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalNavy
                  )
                  Text("At Purchase Cost Rate", fontSize = 9.5.sp, color = TextMuted)
                }
              }

              // Retail MRP Valuation
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFFAF5FF))
                  .border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(10.dp))
                  .padding(12.dp)
              ) {
                Column {
                  Text("RETAIL MRP VALUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "₹${String.format(Locale.getDefault(), "%,.0f", totalMrpValue)}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalMagenta
                  )
                  Text("Expected Sales Revenue", fontSize = 9.5.sp, color = TextMuted)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expected Profit summary
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Expected Gross Profit Potential:", fontSize = 11.5.sp, color = TextDark)
              Text(
                "₹${String.format(Locale.getDefault(), "%,.0f", grossProfitMarginValue)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = StatusGreen
              )
            }
          }
        }
      }

      // Stock Health & ABC Velocity Matrix
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_stock_health_velocity")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Inventory Velocity & Health", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("ABC Classification", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = RoyalMagenta)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Grid status tiles
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Fast Moving
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFECFDF5))
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fast Moving", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text("$fastMovingCount SKUs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("High Turnover", fontSize = 9.sp, color = TextMuted)
                }
              }

              // Slow Moving
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFEF3C7))
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Slow Moving", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text("$slowMovingCount SKUs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text(">60d Idle", fontSize = 9.sp, color = TextMuted)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Critical Low Stock
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFFF1F2))
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Critical Low", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text("$lowStockCount SKUs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Reorder Required", fontSize = 9.sp, color = TextMuted)
                }
              }

              // Out of stock
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF1F5F9))
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Inventory, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Out of Stock", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text("$outOfStockCount SKUs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Zero Inventory", fontSize = 9.sp, color = TextMuted)
                }
              }
            }
          }
        }
      }

      // Expiry Loss Exposure Widget
      item {
        Card(
          onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA))
          ),
          modifier = Modifier.fillMaxWidth().testTag("card_expiry_risk_banner")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.EventBusy, contentDescription = null, tint = StatusRed, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Expiry Date Loss Exposure", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                Text(
                  text = "₹${String.format(Locale.getDefault(), "%,.0f", totalExpiryRiskValue)} at risk (${expiredBatches.size} expired, ${nearExpiry30Batches.size} near expiry)",
                  fontSize = 11.sp,
                  color = Color(0xFF991B1B)
                )
              }
            }

            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Open Expiry", tint = StatusRed, modifier = Modifier.size(16.dp))
          }
        }
      }

      // Category-wise Breakdown with Progress bars
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Category-wise Inventory Distribution", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(10.dp))

            categoryMap.forEach { (catName, pair) ->
              val units = pair.first
              val cost = pair.second
              val pct = if (totalUnits > 0) units.toFloat() / totalUnits.toFloat() else 0f

              Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(catName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                  Text("$units units • ₹${String.format(Locale.getDefault(), "%,.0f", cost)}", fontSize = 11.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                  progress = { pct },
                  modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                  color = RoyalNavy,
                  trackColor = Color(0xFFE2E8F0)
                )
              }
            }
          }
        }
      }

      // Action Buttons
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { viewModel.navigateTo(Screen.CRITICAL_STOCK_ALERTS) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            modifier = Modifier.weight(1f).height(46.dp).testTag("btn_reorder_po")
          ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Smart Reorder PO", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            modifier = Modifier.weight(1f).height(46.dp).testTag("btn_open_expiry_tracker")
          ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Expiry Tracker", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
