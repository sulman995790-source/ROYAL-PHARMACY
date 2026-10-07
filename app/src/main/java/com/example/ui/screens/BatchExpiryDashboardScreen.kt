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
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.BatchExpiryItem
import com.example.viewmodel.BatchRiskTier
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun BatchExpiryDashboardScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allBatchItems by viewModel.batchExpiryItems.collectAsState()
  val distributorCart by viewModel.distributorCart.collectAsState()
  val feedbackMessage by viewModel.scanFeedbackMessage.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: All At-Risk, 1: Expired, 2: Critical (<30d), 3: Short Expiry (31-60d), 4: Upcoming (61-90d)
  var searchQuery by remember { mutableStateOf("") }

  // Risk categorization
  val expiredBatches = allBatchItems.filter { it.riskTier == BatchRiskTier.EXPIRED }
  val criticalBatches = allBatchItems.filter { it.riskTier == BatchRiskTier.CRITICAL_30 }
  val shortExpiryBatches = allBatchItems.filter { it.riskTier == BatchRiskTier.SHORT_60 }
  val upcomingBatches = allBatchItems.filter { it.riskTier == BatchRiskTier.UPCOMING_90 }

  val atRiskBatches = allBatchItems.filter { it.riskTier != BatchRiskTier.SAFE }

  val displayedBatches = when (selectedTab) {
    1 -> expiredBatches
    2 -> criticalBatches
    3 -> shortExpiryBatches
    4 -> upcomingBatches
    else -> atRiskBatches
  }.filter {
    searchQuery.isBlank() ||
      it.medicine.name.contains(searchQuery, ignoreCase = true) ||
      it.batchNumber.contains(searchQuery, ignoreCase = true) ||
      it.medicine.manufacturer.contains(searchQuery, ignoreCase = true)
  }

  val totalValueAtRisk = atRiskBatches.sumOf { it.valueAtRisk }
  val expiredValue = expiredBatches.sumOf { it.valueAtRisk }
  val criticalValue = criticalBatches.sumOf { it.valueAtRisk }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 90.dp)
    ) {
      // 1. Top Bar
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
                text = "Expiry Date Tracker",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "Loss Prevention, Short Expiry & Return Debit Notes",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Push notification trigger button
            IconButton(
              onClick = { viewModel.triggerTestExpiryPush() },
              modifier = Modifier.testTag("btn_trigger_test_expiry_push")
            ) {
              Icon(Icons.Default.NotificationsActive, contentDescription = "Test Push Notification", tint = Color(0xFFD97706))
            }

            // Cart icon
            IconButton(
              onClick = { viewModel.navigateTo(Screen.CART) },
              modifier = Modifier.testTag("btn_cart_from_expiry")
            ) {
              Box {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Distributor Cart", tint = RoyalNavy)
                if (distributorCart.isNotEmpty()) {
                  Box(
                    modifier = Modifier
                      .size(16.dp)
                      .align(Alignment.TopEnd)
                      .clip(CircleShape)
                      .background(RoyalMagenta),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("${distributorCart.size}", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }

      // 2. Feedback alert banner
      item {
        AnimatedVisibility(visible = feedbackMessage != null) {
          feedbackMessage?.let { msg ->
            Card(
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = StatusGreen),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = msg, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      }

      // 3. Financial Risk Overview Banner
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)), // Deep Wine Red
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("card_financial_expiry_risk")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Financial Loss at Expiry Risk",
                  fontSize = 11.5.sp,
                  color = Color(0xFFFCA5A5)
                )
                Text(
                  text = String.format(Locale.getDefault(), "₹%.2f", totalValueAtRisk),
                  fontSize = 22.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White
                )
              }

              Button(
                onClick = {
                  val highRisk = expiredBatches + criticalBatches
                  viewModel.addAllExpiringToReturnCart(highRisk)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("btn_return_all_critical")
              ) {
                Icon(Icons.AutoMirrored.Filled.AssignmentReturn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Return All Critical", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color.White.copy(alpha = 0.12f))
                  .padding(8.dp)
              ) {
                Column {
                  Text("Expired Value", fontSize = 9.5.sp, color = Color(0xFFFECACA))
                  Text("₹${expiredValue.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color.White.copy(alpha = 0.12f))
                  .padding(8.dp)
              ) {
                Column {
                  Text("<30d Critical", fontSize = 9.5.sp, color = Color(0xFFFED7AA))
                  Text("₹${criticalValue.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color.White.copy(alpha = 0.12f))
                  .padding(8.dp)
              ) {
                Column {
                  Text("Total At-Risk", fontSize = 9.5.sp, color = Color(0xFFE2E8F0))
                  Text("${atRiskBatches.size} Batches", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }
          }
        }
      }

      // 4. Risk Tiers Filter Tabs
      item {
        val tabTitles = listOf(
          "All (${atRiskBatches.size})",
          "Expired (${expiredBatches.size})",
          "<30d (${criticalBatches.size})",
          "31-60d (${shortExpiryBatches.size})",
          "61-90d (${upcomingBatches.size})"
        )

        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.White,
          contentColor = RoyalMagenta,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = when (selectedTab) {
                1 -> Color(0xFFDC2626)
                2 -> Color(0xFFEA580C)
                3 -> Color(0xFFD97706)
                else -> RoyalMagenta
              }
            )
          }
        ) {
          tabTitles.forEachIndexed { idx, title ->
            Tab(
              selected = selectedTab == idx,
              onClick = { selectedTab = idx },
              text = {
                Text(
                  text = title,
                  fontSize = 11.sp,
                  fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == idx) TextDark else TextMuted
                )
              },
              modifier = Modifier.testTag("tab_expiry_risk_$idx")
            )
          }
        }
      }

      // 5. Search Bar
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by medicine, batch or brand...", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = GrayBackground,
              unfocusedContainerColor = GrayBackground,
              focusedBorderColor = RoyalMagenta,
              unfocusedBorderColor = CardBorder
            ),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("input_search_expiry_batches")
          )
        }
      }

      // 6. Batch List Items
      if (displayedBatches.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Check, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(48.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("No batches found matching this criteria!", fontWeight = FontWeight.Bold, color = TextDark)
              Text("Your inventory in this tier is safe and up to date.", fontSize = 11.sp, color = TextMuted)
            }
          }
        }
      } else {
        items(displayedBatches) { batchItem ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(
                when (batchItem.riskTier) {
                  BatchRiskTier.EXPIRED -> Color(0xFFFCA5A5)
                  BatchRiskTier.CRITICAL_30 -> Color(0xFFFDBA74)
                  BatchRiskTier.SHORT_60 -> Color(0xFFFDE68A)
                  else -> CardBorder
                }
              )
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp)
              .testTag("batch_card_${batchItem.batchNumber}")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              // Row 1: Medicine Name & Expiry Pill
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = batchItem.medicine.name,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                  Text(
                    text = "${batchItem.medicine.manufacturer} • ${batchItem.rackLocation}",
                    fontSize = 11.sp,
                    color = TextMuted
                  )
                }

                // Days Countdown Pill
                val badgeColor = when (batchItem.riskTier) {
                  BatchRiskTier.EXPIRED -> Color(0xFFDC2626)
                  BatchRiskTier.CRITICAL_30 -> Color(0xFFEA580C)
                  BatchRiskTier.SHORT_60 -> Color(0xFFD97706)
                  BatchRiskTier.UPCOMING_90 -> Color(0xFF0F766E)
                  else -> StatusGreen
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = if (batchItem.daysRemaining <= 0) "Expired ${-batchItem.daysRemaining}d ago"
                           else "Expires in ${batchItem.daysRemaining} days",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Row 2: Monospace Batch Number, Expiry Date & Stock Value
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFF8FAFC))
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Batch No", fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = batchItem.batchNumber,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavy
                  )
                }

                Column {
                  Text("Expiry Date", fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = batchItem.expiryDate,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                }

                Column {
                  Text("Stock on Hand", fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = "${batchItem.stockPacks} Packs",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text("Value at Risk", fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", batchItem.valueAtRisk),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB91C1C)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Action Buttons Row
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                // Add to Return Cart (Debit Note)
                Button(
                  onClick = {
                    viewModel.addMedicineToCart(batchItem.medicine, qty = batchItem.stockPacks.coerceAtLeast(1), isReturn = true)
                    Toast.makeText(context, "Added ${batchItem.medicine.name} to Return Cart (Debit Note)", Toast.LENGTH_SHORT).show()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF581C87)),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1.3f).testTag("btn_return_cart_${batchItem.batchNumber}")
                ) {
                  Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Add to Return Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Send Expiry Push Alert
                Button(
                  onClick = {
                    viewModel.triggerBatchExpiryPush(context, batchItem)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f).testTag("btn_alert_push_${batchItem.batchNumber}")
                ) {
                  Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Push Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
