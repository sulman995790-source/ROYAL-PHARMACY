package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.Customer
import com.example.ui.theme.StatusGreen
import com.example.viewmodel.PharmacyViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UdharKhataScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customers by viewModel.allCustomers.collectAsState()
  val totalOutstanding by viewModel.totalUdharOutstanding.collectAsState()
  var searchQuery by remember { mutableStateOf("") }

  var selectedCustomerForLedger by remember { mutableStateOf<Customer?>(null) }
  var showNewCustomerDialog by remember { mutableStateOf(false) }

  val filteredCustomers = customers.filter {
    it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery, ignoreCase = true)
  }

  val customersWithPending = customers.count { it.balanceReceivable > 0 }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF11171E)) // Dark mode matching Screenshot 3
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(14.dp))

      // 1. Total Outstanding (Udhar) Summary Card (Matches Screenshot 3)
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2630)),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E3846))
        ),
        modifier = Modifier.fillMaxWidth().testTag("udhar_total_card")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "TOTAL OUTSTANDING (UDHAR)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = String.format(Locale.getDefault(), "₹%.2f", totalOutstanding),
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFFFB923C) // Vivid Orange as shown in Screenshot 3
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "$customersWithPending customers with pending balance",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }

          // Round Ledger Book Icon
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color(0xFF334155)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = "Ledger",
              tint = Color(0xFFFBBF24),
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Search Bar (Screenshot 3: Search customer name or phone number...)
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search customer name or phone number...", fontSize = 13.sp, color = Color(0xFF64748B)) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2DD4BF))
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.LightGray)
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF1E2630),
          unfocusedContainerColor = Color(0xFF1E2630),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = Color(0xFF2DD4BF),
          unfocusedBorderColor = Color(0xFF2E3846)
        ),
        modifier = Modifier.fillMaxWidth().testTag("udhar_search_input")
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Section Title (Screenshot 3: Customer Ledgers (5))
      Text(
        text = "Customer Ledgers (${filteredCustomers.size})",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Customer Ledgers List (Screenshot 3)
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredCustomers, key = { it.id }) { cust ->
          CustomerUdharCard(
            customer = cust,
            onClick = { selectedCustomerForLedger = cust },
            onSendReminder = {
              val message = "Dear ${cust.name}, this is a gentle reminder from ROYAL PHARMACY regarding your pending balance of ₹${cust.balanceReceivable}. Kindly clear at your earliest convenience. Thank you!"
              val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=${cust.phone.replace("+", "").replace(" ", "")}&text=${Uri.encode(message)}")
              }
              try {
                context.startActivity(intent)
              } catch (e: Exception) {
                // Handle no WhatsApp installed
              }
            }
          )
        }
      }
    }

    // 5. Floating Button: "+ New Customer" (Screenshot 3: Teal/Green pill)
    Button(
      onClick = { showNewCustomerDialog = true },
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_new_udhar_customer")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("New Customer", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }

    // 6. Ledger Details Bottom Sheet
    selectedCustomerForLedger?.let { customer ->
      val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
      ModalBottomSheet(
        onDismissRequest = { selectedCustomerForLedger = null },
        sheetState = sheetState,
        containerColor = Color(0xFF1E2630),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
      ) {
        CustomerLedgerSheetContent(
          customer = customer,
          onRecordPayment = { amount, note ->
            viewModel.recordUdharPayment(customer, amount, note)
            selectedCustomerForLedger = null
          },
          onGiveCredit = { amount, note ->
            viewModel.recordUdharCredit(customer, amount, note)
            selectedCustomerForLedger = null
          },
          onClose = { selectedCustomerForLedger = null }
        )
      }
    }

    // 7. Add New Customer Dialog
    if (showNewCustomerDialog) {
      var name by remember { mutableStateOf("") }
      var phone by remember { mutableStateOf("") }
      var doctor by remember { mutableStateOf("") }

      AlertDialog(
        onDismissRequest = { showNewCustomerDialog = false },
        title = { Text("Add Customer to Udhar Khata", fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Customer / Clinic Name*") },
              modifier = Modifier.fillMaxWidth().testTag("input_new_khata_name")
            )
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Phone Number*") },
              modifier = Modifier.fillMaxWidth().testTag("input_new_khata_phone")
            )
            OutlinedTextField(
              value = doctor,
              onValueChange = { doctor = it },
              label = { Text("Consulting Doctor (Optional)") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (name.isNotBlank()) {
                viewModel.addNewCustomer(name, phone, "", doctor)
                showNewCustomerDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
          ) {
            Text("Create Khata")
          }
        },
        dismissButton = {
          TextButton(onClick = { showNewCustomerDialog = false }) {
            Text("Cancel", color = Color.Gray)
          }
        }
      )
    }
  }
}

@Composable
fun CustomerUdharCard(
  customer: Customer,
  onClick: () -> Unit,
  onSendReminder: () -> Unit
) {
  val initial = customer.name.firstOrNull()?.toString()?.uppercase() ?: "C"

  Card(
    onClick = onClick,
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2630)),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E3846))
    ),
    modifier = Modifier.fillMaxWidth().testTag("khata_customer_${customer.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Initial Avatar circle with green background (Screenshot 3)
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(0xFF065F46)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = initial,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6EE7B7)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = customer.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = customer.phone,
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
            if (customer.loyaltyPoints > 0) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• ⭐ ${customer.loyaltyPoints} pts (${customer.loyaltyTier})",
                fontSize = 10.sp,
                color = Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Outstanding Amount & WhatsApp Reminder Button (Screenshot 3)
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = String.format(Locale.getDefault(), "₹%.0f", customer.balanceReceivable),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFB923C) // Vivid Orange (Screenshot 3)
          )
          Text(
            text = "Due",
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // WhatsApp / Send reminder icon button
        IconButton(
          onClick = onSendReminder,
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF2E3846))
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send Reminder",
            tint = Color(0xFF2DD4BF),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun CustomerLedgerSheetContent(
  customer: Customer,
  onRecordPayment: (Double, String) -> Unit,
  onGiveCredit: (Double, String) -> Unit,
  onClose: () -> Unit
) {
  var amountText by remember { mutableStateOf("") }
  var noteText by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 18.dp, vertical = 12.dp)
      .padding(bottom = 32.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(customer.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(customer.phone, fontSize = 12.sp, color = Color(0xFF94A3B8))
      }
      IconButton(onClick = onClose) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Balance Banner
    Card(
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3846)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Current Pending Balance:", fontSize = 13.sp, color = Color(0xFFCBD5E1))
        Text(
          String.format(Locale.getDefault(), "₹%.2f", customer.balanceReceivable),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFFB923C)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = amountText,
      onValueChange = { amountText = it },
      label = { Text("Transaction Amount (₹)", color = Color.LightGray) },
      placeholder = { Text("e.g. 500", color = Color.Gray) },
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
      ),
      modifier = Modifier.fillMaxWidth().testTag("input_ledger_amount")
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = noteText,
      onValueChange = { noteText = it },
      label = { Text("Note / Bill Reference", color = Color.LightGray) },
      placeholder = { Text("e.g. Cash payment / Bill INV-102", color = Color.Gray) },
      colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(18.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // [Got Payment] Button (Green)
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull() ?: 0.0
          if (amt > 0) {
            onRecordPayment(amt, noteText)
          }
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
        modifier = Modifier.weight(1f).height(46.dp).testTag("btn_got_payment")
      ) {
        Text("Got Payment", fontWeight = FontWeight.Bold, color = Color.White)
      }

      // [Give Credit] Button (Red/Orange)
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull() ?: 0.0
          if (amt > 0) {
            onGiveCredit(amt, noteText)
          }
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
        modifier = Modifier.weight(1f).height(46.dp).testTag("btn_give_credit")
      ) {
        Text("Give Credit", fontWeight = FontWeight.Bold, color = Color.White)
      }
    }
  }
}
