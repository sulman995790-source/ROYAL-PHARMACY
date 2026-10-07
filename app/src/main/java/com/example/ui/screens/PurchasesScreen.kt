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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun PurchasesScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val distributors by viewModel.allDistributorsWithPurchases.collectAsState()
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
      // 1. Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.HOME) },
            modifier = Modifier.size(32.dp).padding(end = 4.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Text(
            text = "View Purchases",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        }

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
        if (filteredDistributors.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("No distributors recorded yet", fontSize = 14.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = { showAddPurchaseDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
              ) {
                Text("Record Purchase & Add Distributor")
              }
            }
          }
        } else {
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
        }
      } else {
        if (filteredPurchases.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("No purchases recorded yet", fontSize = 14.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = { showAddPurchaseDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
              ) {
                Text("+ Add New Purchase")
              }
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(filteredPurchases, key = { it.id }) { purchase ->
              PurchaseInvoiceCard(
                purchase = purchase,
                onToggleStatus = { viewModel.togglePurchaseStatus(purchase) },
                onDelete = { viewModel.deletePurchaseInvoice(purchase) }
              )
            }

            item {
              Spacer(modifier = Modifier.height(80.dp))
            }
          }
        }
      }
    }

    // Floating Button: "+ Add Purchase"
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
        onSave = { name, gstin, amount, count, invNo, status, payMode ->
          viewModel.addNewPurchase(
            distributorName = name,
            gstin = gstin,
            amount = amount,
            itemCount = count,
            invoiceNo = invNo,
            status = status,
            paymentMode = payMode
          )
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
          // Initials square badge
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
            if (dist.isGstRegistered || dist.gstin.isNotBlank()) {
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

        // Amount payable
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = String.format(Locale.getDefault(), "₹%.2f", dist.balancePayable),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (dist.balancePayable > 0) StatusRed else StatusGreen
          )
          Text(
            text = if (dist.balancePayable > 0) "You'll pay" else "All Settled",
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
          text = dist.gstin.ifBlank { "GST: Unregistered" },
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = TextDark
        )
      }
    }
  }
}

@Composable
fun PurchaseInvoiceCard(
  purchase: PurchaseInvoice,
  onToggleStatus: () -> Unit,
  onDelete: () -> Unit
) {
  val isPaid = purchase.status.equals("Paid", ignoreCase = true)

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
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${purchase.itemsCount} Items purchased",
          fontSize = 11.sp,
          color = TextMuted
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Interactive Paid / Unpaid Toggle Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
              .border(1.dp, if (isPaid) Color(0xFFA5D6A7) else Color(0xFFFFCDD2), RoundedCornerShape(6.dp))
              .clickable { onToggleStatus() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Toggle status",
                tint = if (isPaid) StatusGreen else StatusRed,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = if (isPaid) "PAID" else "UNPAID",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPaid) StatusGreen else StatusRed
              )
            }
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(15.dp))
          }
        }
      }
    }
  }
}

@Composable
fun AddPurchaseDialog(
  distributors: List<Distributor>,
  onDismiss: () -> Unit,
  onSave: (String, String, Double, Int, String, String, String) -> Unit
) {
  var distributorName by remember { mutableStateOf(distributors.firstOrNull()?.name ?: "Sun Pharma Distribution Hub") }
  var gstin by remember { mutableStateOf(distributors.firstOrNull()?.gstin ?: "18AABCS9988K1Z3") }
  var invoiceNo by remember { mutableStateOf("PUR-${(1000..9999).random()}") }
  var totalAmount by remember { mutableStateOf("957.00") }
  var itemsCount by remember { mutableStateOf("3") }
  var paymentStatus by remember { mutableStateOf("Paid") } // "Paid" or "Unpaid"
  var paymentMode by remember { mutableStateOf("Cash") } // "Cash", "UPI", "Bank Transfer"

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
        // Quick distributor preset selector
        if (distributors.isNotEmpty()) {
          Text("Select Known Distributor:", fontSize = 11.sp, color = TextMuted)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            distributors.take(3).forEach { dist ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (distributorName == dist.name) RoyalMagenta.copy(alpha = 0.1f) else Color(0xFFF1F5F9))
                  .border(1.dp, if (distributorName == dist.name) RoyalMagenta else CardBorder, RoundedCornerShape(6.dp))
                  .clickable {
                    distributorName = dist.name
                    gstin = dist.gstin
                  }
                  .padding(horizontal = 6.dp, vertical = 3.dp)
              ) {
                Text(
                  text = dist.name.take(12) + if (dist.name.length > 12) "..." else "",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (distributorName == dist.name) RoyalMagenta else TextDark
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = distributorName,
          onValueChange = { distributorName = it },
          label = { Text("Distributor / Supplier Name*") },
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

        // Payment Status Selection
        Text("Payment Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (paymentStatus == "Paid") Color(0xFFE8F5E9) else Color(0xFFF8FAFC))
              .border(
                1.5.dp,
                if (paymentStatus == "Paid") StatusGreen else CardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { paymentStatus = "Paid" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "✓ Paid (Cash/UPI)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (paymentStatus == "Paid") StatusGreen else TextDark
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (paymentStatus == "Unpaid") Color(0xFFFFEBEE) else Color(0xFFF8FAFC))
              .border(
                1.5.dp,
                if (paymentStatus == "Unpaid") StatusRed else CardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { paymentStatus = "Unpaid" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "⚠️ Unpaid (Credit)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (paymentStatus == "Unpaid") StatusRed else TextDark
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = totalAmount.toDoubleOrNull() ?: 0.0
          val cnt = itemsCount.toIntOrNull() ?: 1
          onSave(distributorName, gstin, amt, cnt, invoiceNo, paymentStatus, paymentMode)
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
