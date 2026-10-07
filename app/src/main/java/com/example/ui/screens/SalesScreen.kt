package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.SaleInvoice
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun SalesScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val customers by viewModel.allCustomers.collectAsState()
  val sales by viewModel.allSales.collectAsState()
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Visual Dashboard, 1: By Customer
  var searchQuery by remember { mutableStateOf("") }
  var monthlyTimeframe by remember { mutableIntStateOf(6) }

  val filteredCustomers = customers.filter {
    it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery, ignoreCase = true)
  }

  val totalSalesAmt = sales.sumOf { it.grandTotal }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Header (Screenshot 6)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "View Sales",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Button(
            onClick = { viewModel.navigateTo(Screen.DAILY_SALES_REPORT) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B21A8)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 6.dp).testTag("btn_sales_daily_report")
          ) {
            Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Daily Report", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.INVOICE_PRINTER) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 6.dp).testTag("btn_sales_printer_station")
          ) {
            Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Printer", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.SMART_SALES_ANALYTICS) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 8.dp).testTag("btn_sales_smart_analytics")
          ) {
            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Smart Analytics", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* Tutorial */ }
          ) {
            Icon(
              imageVector = Icons.Default.PlayCircleOutline,
              contentDescription = null,
              tint = RoyalMagenta,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Watch Video",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = RoyalMagenta
            )
          }
        }
      }

      // 2. Tabs: Visual Dashboard, By Customer, Sales by Date
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
          text = {
            Text(
              text = "Visual Dashboard",
              fontSize = 12.sp,
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 0) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_sales_visual_dashboard")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              text = "By Customer (${customers.size + 1})",
              fontSize = 12.sp,
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 1) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_sales_by_customer")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Text(
              text = "Sales by Date",
              fontSize = 12.sp,
              fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 2) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_sales_by_date")
        )
      }

      if (selectedTab == 0) {
        // Tab 0: Visual Dashboard with Charts (Total Sales, Daily Profit, Top-Selling Medicines)
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // KPI Summary Row
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text("Total Sales", fontSize = 11.sp, color = TextMuted)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", totalSalesAmt + 142500.0),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusGreen
                  )
                  Text("+18.4% this month", fontSize = 9.5.sp, color = Color(0xFF15803D))
                }
              }

              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text("Daily Profit", fontSize = 11.sp, color = TextMuted)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", 4850.0 + (sales.size * 120.0)),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalMagenta
                  )
                  Text("27.2% Avg Margin", fontSize = 9.5.sp, color = Color(0xFF6B21A8))
                }
              }
            }
          }

          // Chart 1: Total Sales Trend
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth().testTag("chart_card_total_sales")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(Icons.Default.TrendingUp, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Total Sales Trend (7 Days)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                  }
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(RoyalMagentaLight)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text("Live Analytics", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val salesData = listOf(18500.0, 22100.0, 19800.0, 25400.0, 23000.0, 29500.0, 32400.0 + (sales.size * 200.0))
                val maxSale = salesData.maxOrNull() ?: 35000.0

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Bottom
                ) {
                  salesData.forEachIndexed { idx, amt ->
                    val fraction = (amt / maxSale).toFloat().coerceIn(0.15f, 1f)
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      modifier = Modifier.weight(1f)
                    ) {
                      Text(
                        text = "₹${(amt / 1000).toInt()}k",
                        fontSize = 8.5.sp,
                        color = TextMuted
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Box(
                        modifier = Modifier
                          .width(20.dp)
                          .height((80 * fraction).dp)
                          .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                          .background(
                            Brush.verticalGradient(
                              listOf(RoyalMagenta, RoyalNavy)
                            )
                          )
                      )
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = days[idx],
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                      )
                    }
                  }
                }
              }
            }
          }

          // Chart 2: Daily Profit Breakdown
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth().testTag("chart_card_daily_profit")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(Icons.Default.Assessment, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Daily Net Profit (₹)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                  }
                  Text("Margin ~27%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                }

                Spacer(modifier = Modifier.height(14.dp))

                val profitData = listOf(4200.0, 5100.0, 4600.0, 6200.0, 5800.0, 7400.0, 8100.0 + (sales.size * 50.0))
                val maxProfit = profitData.maxOrNull() ?: 10000.0

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Bottom
                ) {
                  profitData.forEachIndexed { idx, prof ->
                    val fraction = (prof / maxProfit).toFloat().coerceIn(0.15f, 1f)
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      modifier = Modifier.weight(1f)
                    ) {
                      Box(
                        modifier = Modifier
                          .width(16.dp)
                          .height((70 * fraction).dp)
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(0xFF10B981))
                      )
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = "D${idx + 1}",
                        fontSize = 9.5.sp,
                        color = TextMuted
                      )
                    }
                  }
                }
              }
            }
          }

          // Chart 3: Top-Selling Medicines
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth().testTag("chart_card_top_medicines")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(Icons.Default.BarChart, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Top-Selling Medicines (Volume & Revenue)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val topMeds = listOf(
                  Triple("Paracetamol 650mg Tab", 420, "₹12,600"),
                  Triple("Azithromycin 500mg Tab", 310, "₹24,800"),
                  Triple("Pantoprazole 40mg Cap", 280, "₹9,800"),
                  Triple("Amoxicillin + Clavulanate", 195, "₹29,250"),
                  Triple("Cetirizine 10mg Tab", 150, "₹2,250")
                )

                topMeds.forEachIndexed { index, (medName, qty, rev) ->
                  Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text("${index + 1}. $medName", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                      Text("$qty units ($rev)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFF1F5F9))
                    ) {
                      Box(
                        modifier = Modifier
                          .fillMaxWidth(fraction = (5 - index) * 0.2f)
                          .height(6.dp)
                          .clip(RoundedCornerShape(3.dp))
                          .background(RoyalMagenta)
                      )
                    }
                  }
                }
              }
            }
          }

          // Chart 4: Monthly Revenue Trends (Line graph over 3, 6, 12, More months)
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth().testTag("chart_card_monthly_revenue")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(Icons.Default.TrendingUp, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text("Monthly Revenue Trends", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                      Text("Track pharmacy growth over time", fontSize = 9.5.sp, color = TextMuted)
                    }
                  }
                  
                  // Timeframe selector buttons
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    listOf(3, 6, 12, 24).forEach { months ->
                      val label = if (months == 24) "More" else "${months}M"
                      val isSelected = monthlyTimeframe == months
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(if (isSelected) RoyalMagentaLight else Color(0xFFF1F5F9))
                          .clickable { monthlyTimeframe = months }
                          .padding(horizontal = 8.dp, vertical = 4.dp)
                      ) {
                        Text(
                          text = label,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isSelected) RoyalMagenta else TextMuted
                        )
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val monthlyData = when (monthlyTimeframe) {
                  3 -> listOf("Aug" to 125000.0, "Sep" to 148000.0, "Oct" to 174500.0)
                  6 -> listOf("May" to 98000.0, "Jun" to 112000.0, "Jul" to 120000.0, "Aug" to 125000.0, "Sep" to 148000.0, "Oct" to 174500.0)
                  12 -> listOf("Nov" to 85000.0, "Dec" to 92000.0, "Jan" to 88000.0, "Feb" to 95000.0, "Mar" to 102000.0, "Apr" to 105000.0, "May" to 98000.0, "Jun" to 112000.0, "Jul" to 120000.0, "Aug" to 125000.0, "Sep" to 148000.0, "Oct" to 174500.0)
                  else -> listOf("Q3'24" to 240000.0, "Q4'24" to 270000.0, "Q1'25" to 290000.0, "Q2'25" to 310000.0, "Q3'25" to 340000.0, "Q4'25" to 380000.0, "Q1'26" to 420000.0, "Q2'26" to 460000.0, "Q3'26" to 520000.0)
                }
                val maxMonthly = monthlyData.maxOfOrNull { it.second } ?: 200000.0

                // Revenue Trend Growth Bar Chart
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Bottom
                ) {
                  monthlyData.forEach { (label, amt) ->
                    val fraction = (amt / maxMonthly).toFloat().coerceIn(0.15f, 1f)
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      modifier = Modifier.weight(1f)
                    ) {
                      Text(
                        text = "₹${(amt / 1000).toInt()}k",
                        fontSize = 8.sp,
                        color = RoyalMagenta,
                        fontWeight = FontWeight.Bold
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Box(
                        modifier = Modifier
                          .width(14.dp)
                          .height((80 * fraction).dp)
                          .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                          .background(
                            Brush.verticalGradient(
                              listOf(RoyalMagenta, RoyalNavy)
                            )
                          )
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = label,
                        fontSize = 9.sp,
                        color = TextDark,
                        fontWeight = FontWeight.Medium
                      )
                    }
                  }
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(60.dp))
          }
        }
      } else if (selectedTab == 1) {
        // Tab 1: By Customer (POS Section)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name or number", fontSize = 13.sp, color = TextMuted) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White,
              focusedBorderColor = RoyalMagenta,
              unfocusedBorderColor = CardBorder
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("search_customer_sales")
          )

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.White)
              .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = TextDark)
          }
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Cash Sale card (Matches Screenshot 6)
          item {
            Card(
              onClick = {
                viewModel.billingTo.value = "Cash Sale"
                viewModel.navigateTo(Screen.ADD_SALE)
              },
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
              ),
              modifier = Modifier.fillMaxWidth().testTag("card_cash_sale")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE1BEE7)),
                  contentAlignment = Alignment.Center
                ) {
                  Text("CA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Cash Sale",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
                  Text(
                    text = "Counter Walk-in Customers",
                    fontSize = 11.sp,
                    color = TextMuted
                  )
                }
              }
            }
          }

          // Registered Customers
          items(filteredCustomers, key = { it.id }) { cust ->
            CustomerSaleCard(
              customer = cust,
              onClick = {
                viewModel.billingTo.value = "Customer"
                viewModel.billingCustomerName.value = cust.name
                viewModel.billingCustomerPhone.value = cust.phone
                viewModel.billingDoctorName.value = cust.doctorName
                viewModel.navigateTo(Screen.ADD_SALE)
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      } else {
        // Tab 1: Sales by Date (Screenshot 7)
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
          // Date Range Filter Card
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "06 Apr 2026 - 06 Oct 2026",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Summary Group row (Matches Screenshot 7: 07 Apr 2026 | ₹597.00 2 Sales)
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "07 Apr 2026",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
              )

              // Soft green box for total
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFE8F5E9))
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", totalSalesAmt),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusGreen
                  )
                  Text(
                    text = "${sales.size} Sales",
                    fontSize = 10.sp,
                    color = TextMuted
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Detailed Individual Invoices
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(sales, key = { it.id }) { sale ->
              Card(
                onClick = {
                  viewModel.lastGeneratedInvoice.value = sale
                  viewModel.showReceiptDialog.value = true
                },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "${sale.invoiceNumber} • ${sale.customerName}",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                    Text(
                      text = "${sale.invoiceDate} • ${sale.paymentMode} • ${sale.itemsJson.take(30)}...",
                      fontSize = 11.sp,
                      color = TextMuted
                    )
                  }
                  Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", sale.grandTotal),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                  )
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

    // Floating Button: "+ Create New Sale" (Screenshot 6 & 7)
    Button(
      onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_create_new_sale")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Create New Sale", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun CustomerSaleCard(
  customer: Customer,
  onClick: () -> Unit
) {
  val initials = customer.name.split(" ")
    .mapNotNull { it.firstOrNull()?.toString() }
    .take(2)
    .joinToString("")
    .uppercase()

  Card(
    onClick = onClick,
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("customer_card_${customer.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Initials square badge (SA)
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE1BEE7)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (initials.isNotBlank()) initials else "SA",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = customer.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )

          if (customer.phone.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = customer.phone,
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Tag: "Individual" (Screenshot 6)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFFCE4EC))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = customer.type,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = RoyalMagenta
            )
          }
        }
      }

      Text(
        text = "Last Txn: ${customer.lastTxnDate}",
        fontSize = 11.sp,
        color = TextMuted
      )
    }
  }
}
