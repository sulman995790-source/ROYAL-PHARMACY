package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.model.BillItem
import com.example.data.model.Customer
import com.example.data.model.Patient
import com.example.data.model.SaleInvoice
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
fun CustomerHistoryScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customers by viewModel.allCustomers.collectAsState()
  val patients by viewModel.allPatients.collectAsState()
  val sales by viewModel.allSales.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
  var selectedPatient by remember { mutableStateOf<Patient?>(null) }
  var selectedTab by remember { mutableIntStateOf(0) } // 0: All Transactions, 1: Medication Frequency, 2: Udhar & Loyalty

  // Filtered customer candidates
  val filteredCustomers = customers.filter {
    searchQuery.isBlank() ||
      it.name.contains(searchQuery, ignoreCase = true) ||
      it.phone.contains(searchQuery)
  }

  // Active targeted customer identifier
  val currentTargetName = selectedCustomer?.name ?: selectedPatient?.name ?: searchQuery.takeIf { it.isNotBlank() }
  val currentTargetPhone = selectedCustomer?.phone ?: selectedPatient?.contactNumber ?: ""

  // Invoices for this customer/patient
  val customerInvoices = sales.filter { sale ->
    if (selectedCustomer != null) {
      sale.customerName.equals(selectedCustomer!!.name, ignoreCase = true) ||
        (selectedCustomer!!.phone.isNotBlank() && sale.customerPhone == selectedCustomer!!.phone)
    } else if (selectedPatient != null) {
      sale.customerName.equals(selectedPatient!!.name, ignoreCase = true) ||
        (selectedPatient!!.contactNumber.isNotBlank() && sale.customerPhone == selectedPatient!!.contactNumber)
    } else if (searchQuery.isNotBlank()) {
      sale.customerName.contains(searchQuery, ignoreCase = true) ||
        sale.customerPhone.contains(searchQuery)
    } else {
      true // All recent sales
    }
  }

  val totalSpent = customerInvoices.sumOf { it.grandTotal }
  val totalVisits = customerInvoices.size
  val avgOrderValue = if (totalVisits > 0) totalSpent / totalVisits else 0.0

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
          onClick = { viewModel.navigateTo(Screen.HOME) },
          modifier = Modifier.size(36.dp).testTag("btn_back_customer_history")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Customer & Patient History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("Prescription Timeline, Spend & 1-Tap Reorder", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Button(
        onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.testTag("btn_new_bill_from_history")
      ) {
        Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("New Bill", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
      }
    }

    // 2. Search & Filter Bar
    Card(
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by customer name, mobile or patient ID...", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
          trailingIcon = {
            if (searchQuery.isNotBlank() || selectedCustomer != null || selectedPatient != null) {
              Text(
                text = "Clear",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMagenta,
                modifier = Modifier
                  .clickable {
                    searchQuery = ""
                    selectedCustomer = null
                    selectedPatient = null
                  }
                  .padding(end = 12.dp)
              )
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = GrayBackground,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = CardBorder,
            focusedBorderColor = RoyalMagenta
          ),
          modifier = Modifier.fillMaxWidth().testTag("input_search_customer_history")
        )

        // Customer Quick Selection Pills
        if (customers.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            customers.take(6).forEach { cust ->
              val isSelected = selectedCustomer?.id == cust.id
              FilterChip(
                selected = isSelected,
                onClick = {
                  if (isSelected) {
                    selectedCustomer = null
                  } else {
                    selectedCustomer = cust
                    selectedPatient = null
                    searchQuery = cust.name
                  }
                },
                label = { Text(cust.name, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = RoyalNavy,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }
      }
    }

    // 3. Customer Profile Summary Card
    if (selectedCustomer != null || selectedPatient != null || currentTargetName != null) {
      val customerTitle = selectedCustomer?.name ?: selectedPatient?.name ?: currentTargetName ?: "Customer"
      val phoneDisplay = selectedCustomer?.phone?.takeIf { it.isNotBlank() }
        ?: selectedPatient?.contactNumber?.takeIf { it.isNotBlank() }
        ?: currentTargetPhone.takeIf { it.isNotBlank() } ?: "Mobile not registered"
      val points = selectedCustomer?.loyaltyPoints ?: selectedPatient?.loyaltyPoints ?: 0
      val udharBalance = selectedCustomer?.balanceReceivable ?: 0.0

      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
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
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(customerTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                Text(phoneDisplay, fontSize = 12.sp, color = TextMuted)
              }
            }

            if (phoneDisplay != "Mobile not registered") {
              IconButton(
                onClick = {
                  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneDisplay"))
                  context.startActivity(intent)
                },
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFDCFCE7))
              ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = Color(0xFFF1F5F9))
          Spacer(modifier = Modifier.height(10.dp))

          // Key metrics row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Lifetime Spend", fontSize = 10.sp, color = TextMuted)
              Text("₹${String.format(Locale.getDefault(), "%,.1f", totalSpent)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            }
            Column {
              Text("Total Bills", fontSize = 10.sp, color = TextMuted)
              Text("$totalVisits orders", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalNavy)
            }
            Column {
              Text("Loyalty Points", fontSize = 10.sp, color = TextMuted)
              Text("$points pts", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFD97706))
            }
            Column {
              Text("Udhar Balance", fontSize = 10.sp, color = TextMuted)
              Text(
                "₹${String.format(Locale.getDefault(), "%,.1f", udharBalance)}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (udharBalance > 0) StatusRed else StatusGreen
              )
            }
          }
        }
      }
    }

    // 4. Tab Selector
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
        text = { Text("Invoices (${customerInvoices.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_customer_invoices")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Prescribed Medicines", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_customer_medicines")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Spend & Ledger", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_customer_ledger")
      )
    }

    // 5. Invoices & Records List
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (customerInvoices.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
              Spacer(modifier = Modifier.height(10.dp))
              Text("No purchase records found", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
              Text("Sales made to this customer will automatically appear here.", fontSize = 12.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = {
                  viewModel.billingCustomerName.value = currentTargetName ?: ""
                  viewModel.billingCustomerPhone.value = currentTargetPhone
                  viewModel.navigateTo(Screen.ADD_SALE)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Create First Bill for Customer")
              }
            }
          }
        }
      } else {
        when (selectedTab) {
          0 -> {
            items(customerInvoices) { inv ->
              CustomerInvoiceCard(
                invoice = inv,
                onRepeatOrder = {
                  // Pre-fill billing with this customer and navigate to AddSaleScreen
                  viewModel.billingCustomerName.value = inv.customerName
                  viewModel.billingCustomerPhone.value = inv.customerPhone
                  viewModel.billingDoctorName.value = inv.doctorName
                  viewModel.navigateTo(Screen.ADD_SALE)
                  Toast.makeText(context, "Customer details loaded into new bill", Toast.LENGTH_SHORT).show()
                },
                onPrintInvoice = {
                  viewModel.openInvoicePrinter(inv)
                  viewModel.navigateTo(Screen.INVOICE_PRINTER)
                },
                onShareWhatsApp = {
                  val text = "🧾 *ROYAL PHARMACY INVOICE*\nInvoice No: ${inv.invoiceNumber}\nDate: ${inv.invoiceDate}\nCustomer: ${inv.customerName}\nItems: ${inv.itemsJson}\nGrand Total: ₹${inv.grandTotal}\nStatus: ${if (inv.isPaid) "PAID ✅" else "UNPAID ⚠️"}\nThank you for choosing Royal Pharmacy!"
                  val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                  }
                  context.startActivity(Intent.createChooser(intent, "Share Invoice via"))
                }
              )
            }
          }
          1 -> {
            // Prescribed Medicines Summary
            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(24.dp))
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("Medication Adherence & Frequency", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF166534))
                    Text("List of all pharmaceutical formulations dispensed to this patient.", fontSize = 11.sp, color = Color(0xFF15803D))
                  }
                }
              }
            }

            items(customerInvoices) { inv ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(inv.invoiceDate, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalNavy)
                    Text(inv.invoiceNumber, fontSize = 11.sp, color = TextMuted)
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(inv.itemsJson, fontSize = 13.sp, color = TextDark, lineHeight = 18.sp)
                  if (inv.doctorName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Prescribed by: ${inv.doctorName}", fontSize = 11.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.Medium)
                  }
                }
              }
            }
          }
          2 -> {
            // Spend & Ledger
            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text("Financial Ledger Overview", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Total Billed Amount", fontSize = 12.sp, color = TextMuted)
                    Text("₹${String.format(Locale.getDefault(), "%,.2f", totalSpent)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Average Order Value (AOV)", fontSize = 12.sp, color = TextMuted)
                    Text("₹${String.format(Locale.getDefault(), "%,.2f", avgOrderValue)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalNavy)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Total Orders Completed", fontSize = 12.sp, color = TextMuted)
                    Text("$totalVisits Transactions", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StatusGreen)
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
fun CustomerInvoiceCard(
  invoice: SaleInvoice,
  onRepeatOrder: () -> Unit,
  onPrintInvoice: () -> Unit,
  onShareWhatsApp: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

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
        Column {
          Text(invoice.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
          Text(invoice.invoiceDate, fontSize = 11.sp, color = TextMuted)
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            "₹${String.format(Locale.getDefault(), "%,.2f", invoice.grandTotal)}",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = RoyalNavy
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (invoice.isPaid) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              if (invoice.isPaid) "PAID (${invoice.paymentMode})" else "UNPAID / UDHAR",
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              color = if (invoice.isPaid) Color(0xFF15803D) else Color(0xFFB91C1C)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Items: ${invoice.itemsJson}",
        fontSize = 12.sp,
        color = TextDark,
        maxLines = if (isExpanded) Int.MAX_VALUE else 2
      )

      if (invoice.doctorName.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text("Doctor: ${invoice.doctorName}", fontSize = 11.sp, color = Color(0xFF6B21A8), fontWeight = FontWeight.Medium)
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = Color(0xFFF1F5F9))
      Spacer(modifier = Modifier.height(8.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onRepeatOrder,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
          modifier = Modifier.testTag("btn_repeat_order_${invoice.id}")
        ) {
          Icon(Icons.Default.Replay, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Repeat Order", fontSize = 11.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onShareWhatsApp,
            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFDCFCE7))
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
          }

          IconButton(
            onClick = onPrintInvoice,
            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFE0E7FF))
          ) {
            Icon(Icons.Default.Print, contentDescription = "Print", tint = RoyalNavy, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}
