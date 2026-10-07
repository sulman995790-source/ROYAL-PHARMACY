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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Distributor
import com.example.data.model.PurchaseInvoice
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.viewmodel.Screen
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import java.util.Locale

@Composable
fun PurchasesScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val distributors by viewModel.allDistributors.collectAsState()
  val purchases by viewModel.allPurchases.collectAsState()
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Distributor (N), 1: Purchases by Date
  var searchQuery by remember { mutableStateOf("") }
  var showAddPurchaseDialog by remember { mutableStateOf(false) }

  val filteredDistributors = distributors.filter {
    it.name.contains(searchQuery, ignoreCase = true) || it.gstin.contains(searchQuery, ignoreCase = true)
  }

  val filteredPurchases = purchases.filter {
    it.distributorName.contains(searchQuery, ignoreCase = true) || it.invoiceNumber.contains(searchQuery, ignoreCase = true)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Header (Screenshot 4)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "View Purchases",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Button(
            onClick = { viewModel.navigateTo(Screen.PURCHASE_ORDERS) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 4.dp).testTag("btn_goto_purchase_orders")
          ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Orders (PO)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.SUPPLIERS) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("btn_goto_suppliers")
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Suppliers", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 2. Tabs: Distributor (N) vs Purchases by Date
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
              text = "Distributor (${distributors.size})",
              fontSize = 13.sp,
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 0) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_distributors")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              text = "Purchases by Date",
              fontSize = 13.sp,
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 1) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_purchases_date")
        )
      }

      // 3. Search Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              if (selectedTab == 0) "Search Distributors" else "Search Invoices",
              fontSize = 13.sp,
              color = TextMuted
            )
          },
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
            .testTag("purchase_search_bar")
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

      // 4. Content List
      if (selectedTab == 0) {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredDistributors, key = { it.id }) { dist ->
            DistributorCard(dist)
          }

          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredPurchases, key = { it.id }) { purchase ->
            PurchaseInvoiceCard(purchase)
          }

          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      }
    }

    // Floating Button: "+ Add Purchase" (Screenshot 4)
    Button(
      onClick = { showAddPurchaseDialog = true },
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_add_purchase")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Add Purchase", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }

    // Add Purchase Dialog
    if (showAddPurchaseDialog) {
      AddPurchaseDialog(
        distributors = distributors,
        onDismiss = { showAddPurchaseDialog = false },
        onSave = { name, gstin, amount, count, invNo ->
          viewModel.addNewPurchase(name, gstin, amount, count, invNo)
          showAddPurchaseDialog = false
        }
      )
    }
  }
}

@Composable
fun DistributorCard(dist: Distributor) {
  val initials = dist.name.split(" ")
    .mapNotNull { it.firstOrNull()?.toString() }
    .take(2)
    .joinToString("")
    .uppercase()

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("distributor_card_${dist.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Initials square badge (SA)
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFFFCDD2)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (initials.isNotBlank()) initials else "SA",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFC62828)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = dist.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified",
                tint = StatusGreen,
                modifier = Modifier.size(16.dp)
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // GST Registered badge
            if (dist.isGstRegistered) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFFE0F7FA))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "GST Registered",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF00838F)
                )
              }
            }
          }
        }

        // Amount payable (Screenshot 4)
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = String.format(Locale.getDefault(), "₹%.2f", dist.balancePayable),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = StatusRed
          )
          Text(
            text = "You'll pay",
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Last Txn: ${dist.lastTxnDate}",
          fontSize = 11.sp,
          color = TextMuted
        )
        Text(
          text = dist.gstin,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = TextDark
        )
      }
    }
  }
}

@Composable
fun PurchaseInvoiceCard(purchase: PurchaseInvoice) {
  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = purchase.distributorName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = "${purchase.invoiceNumber} • ${purchase.invoiceDate}",
            fontSize = 11.sp,
            color = TextMuted
          )
        }
        Text(
          text = String.format(Locale.getDefault(), "₹%.2f", purchase.totalAmount),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "${purchase.itemsCount} Items purchased",
          fontSize = 11.sp,
          color = TextMuted
        )
        Text(
          text = purchase.status,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (purchase.status == "Paid") StatusGreen else StatusRed
        )
      }
    }
  }
}

@Composable
fun AddPurchaseDialog(
  distributors: List<Distributor>,
  onDismiss: () -> Unit,
  onSave: (String, String, Double, Int, String) -> Unit
) {
  var distributorName by remember { mutableStateOf(distributors.firstOrNull()?.name ?: "Sample Distributor") }
  var gstin by remember { mutableStateOf(distributors.firstOrNull()?.gstin ?: "24ABCDE1234F1ZK") }
  var invoiceNo by remember { mutableStateOf("PUR-${System.currentTimeMillis() % 10000}") }
  var totalAmount by remember { mutableStateOf("957.00") }
  var itemsCount by remember { mutableStateOf("3") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Record New Purchase", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = distributorName,
          onValueChange = { distributorName = it },
          label = { Text("Distributor Name*") },
          modifier = Modifier.fillMaxWidth().testTag("input_purchase_dist_name")
        )
        OutlinedTextField(
          value = gstin,
          onValueChange = { gstin = it },
          label = { Text("Distributor GSTIN") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = invoiceNo,
          onValueChange = { invoiceNo = it },
          label = { Text("Purchase Invoice No") },
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = totalAmount,
            onValueChange = { totalAmount = it },
            label = { Text("Amount (₹)*") },
            modifier = Modifier.weight(1f).testTag("input_purchase_amount")
          )
          OutlinedTextField(
            value = itemsCount,
            onValueChange = { itemsCount = it },
            label = { Text("Items Count") },
            modifier = Modifier.weight(1f)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = totalAmount.toDoubleOrNull() ?: 0.0
          val cnt = itemsCount.toIntOrNull() ?: 1
          onSave(distributorName, gstin, amt, cnt, invoiceNo)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        modifier = Modifier.testTag("btn_save_purchase")
      ) {
        Text("Save Purchase")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}
