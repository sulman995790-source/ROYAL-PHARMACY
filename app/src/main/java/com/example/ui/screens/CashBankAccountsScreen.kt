package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.BankAccount
import com.example.data.model.UpiAccount
import com.example.ui.components.StoreUpiQrDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenLight
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashBankAccountsScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val bankAccounts by viewModel.allBankAccounts.collectAsState()
  val upiAccounts by viewModel.allUpiAccounts.collectAsState()
  val cashInHand by viewModel.cashInHandRegister.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()

  var selectedTab by remember { mutableStateOf("BANK") } // "BANK", "UPI", "CASH"
  var showAddBankDialog by remember { mutableStateOf(false) }
  var showAddUpiDialog by remember { mutableStateOf(false) }
  var showAdjustCashDialog by remember { mutableStateOf(false) }
  var showUpiQrModal by remember { mutableStateOf(false) }
  var previewUpiId by remember { mutableStateOf("") }

  val totalBankBalance = bankAccounts.sumOf { it.balance }
  val totalLiquidFunds = totalBankBalance + cashInHand

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.MORE) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Column {
            Text(
              text = "Cash, Bank & UPI Accounts",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Text(
              text = "Treasury • Merchant QR • Account Ledgers",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        IconButton(
          onClick = {
            if (selectedTab == "BANK") showAddBankDialog = true
            else if (selectedTab == "UPI") showAddUpiDialog = true
            else showAdjustCashDialog = true
          },
          modifier = Modifier.testTag("btn_add_account_entity")
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(RoyalMagentaLight),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = RoyalMagenta, modifier = Modifier.size(18.dp))
          }
        }
      }

      // 2. Navigation Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GrayBackground)
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          listOf(
            Triple("BANK", "Bank Accounts", Icons.Default.AccountBalance),
            Triple("UPI", "UPI & QR", Icons.Default.QrCode2),
            Triple("CASH", "Cash Register", Icons.Default.Payments)
          ).forEach { (tabId, label, icon) ->
            val isSel = selectedTab == tabId
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) RoyalNavy else Color.Transparent)
                .clickable { selectedTab = tabId }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = if (isSel) Color.White else TextDark, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSel) Color.White else TextDark
                )
              }
            }
          }
        }
      }

      // 3. Main Body
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
      ) {
        // Total Treasury Balance Banner
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Total Available Treasury", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = String.format(Locale.getDefault(), "₹%.2f", totalLiquidFunds),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RoyalNavy
              )

              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RoyalMagenta))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Bank Balances: ₹${String.format(Locale.getDefault(), "%.0f", totalBankBalance)}", fontSize = 11.sp, color = TextDark)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Cash in Drawer: ₹${String.format(Locale.getDefault(), "%.0f", cashInHand)}", fontSize = 11.sp, color = TextDark)
                }
              }
            }
          }
        }

        // TAB 1: BANK ACCOUNTS
        if (selectedTab == "BANK") {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("LINKED BANK ACCOUNTS (${bankAccounts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              TextButton(onClick = { showAddBankDialog = true }) {
                Text("+ Add Bank", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
              }
            }
          }

          items(bankAccounts, key = { it.id }) { acc ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth().testTag("bank_account_${acc.id}")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F2FE)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(acc.bankName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        if (acc.isPrimary) {
                          Spacer(modifier = Modifier.width(6.dp))
                          Box(
                            modifier = Modifier
                              .clip(RoundedCornerShape(4.dp))
                              .background(StatusGreenLight)
                              .padding(horizontal = 6.dp, vertical = 2.dp)
                          ) {
                            Text("PRIMARY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                          }
                        }
                      }
                      Text("${acc.accountType} • ${acc.accountNumber}", fontSize = 11.sp, color = TextMuted)
                    }
                  }

                  IconButton(
                    onClick = { viewModel.deleteBankAccount(acc.id) },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(16.dp))
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("IFSC: ${acc.ifscCode} (${acc.branchName})", fontSize = 10.5.sp, color = TextLight)
                  Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", acc.balance),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalNavy
                  )
                }
              }
            }
          }
        }

        // TAB 2: UPI & QR ACCOUNTS
        if (selectedTab == "UPI") {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("UPI VPAS & MERCHANT QR (${upiAccounts.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
              TextButton(onClick = { showAddUpiDialog = true }) {
                Text("+ Add UPI ID", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
              }
            }
          }

          items(upiAccounts, key = { it.id }) { upi ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth().testTag("upi_account_${upi.id}")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(upi.upiId, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        if (upi.isPrimary) {
                          Spacer(modifier = Modifier.width(6.dp))
                          Box(
                            modifier = Modifier
                              .clip(RoundedCornerShape(4.dp))
                              .background(Color(0xFFEDE9FE))
                              .padding(horizontal = 6.dp, vertical = 2.dp)
                          ) {
                            Text("DEFAULT QR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                          }
                        }
                      }
                      Text("${upi.providerName} • Linked: ${upi.linkedBank}", fontSize = 11.sp, color = TextMuted)
                    }
                  }

                  // Actions
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                      onClick = {
                        previewUpiId = upi.upiId
                        showUpiQrModal = true
                      }
                    ) {
                      Icon(Icons.Default.QrCode, contentDescription = "View QR", tint = RoyalNavy, modifier = Modifier.size(20.dp))
                    }

                    if (!upi.isPrimary) {
                      IconButton(onClick = { viewModel.setPrimaryUpi(upi.id) }) {
                        Icon(Icons.Default.Star, contentDescription = "Set Primary", tint = TextLight, modifier = Modifier.size(18.dp))
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // TAB 3: CASH REGISTER
        if (selectedTab == "CASH") {
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
              modifier = Modifier.fillMaxWidth()
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
                        .background(StatusGreenLight),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Payments, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text("Counter Cash Drawer", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text("Physical cash in retail register", fontSize = 11.sp, color = TextMuted)
                    }
                  }

                  Text(
                    text = String.format(Locale.getDefault(), "₹%.2f", cashInHand),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = StatusGreen
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Button(
                    onClick = { showAdjustCashDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Cash In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }

                  OutlinedButton(
                    onClick = { showAdjustCashDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                  ) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = StatusRed, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cash Drop / Out", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                  }
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(40.dp))
        }
      }
    }

    // Modal Dialog: Add Bank Account
    if (showAddBankDialog) {
      var bankName by remember { mutableStateOf("State Bank of India") }
      var accNumber by remember { mutableStateOf("") }
      var ifsc by remember { mutableStateOf("SBIN0001234") }
      var initialBal by remember { mutableStateOf("10000") }

      AlertDialog(
        onDismissRequest = { showAddBankDialog = false },
        title = {
          Text("Add Bank Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = bankName,
              onValueChange = { bankName = it },
              label = { Text("Bank Name*") },
              placeholder = { Text("e.g. HDFC Bank, SBI") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = accNumber,
              onValueChange = { accNumber = it },
              label = { Text("Account Number*") },
              placeholder = { Text("e.g. 123456789012") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = ifsc,
              onValueChange = { ifsc = it },
              label = { Text("IFSC Code") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = initialBal,
              onValueChange = { initialBal = it },
              label = { Text("Current Balance (₹)") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (bankName.isNotBlank() && accNumber.isNotBlank()) {
                viewModel.addBankAccount(
                  BankAccount(
                    bankName = bankName,
                    accountNumber = accNumber,
                    ifscCode = ifsc,
                    balance = initialBal.toDoubleOrNull() ?: 0.0
                  )
                )
                showAddBankDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
          ) {
            Text("Save Account")
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddBankDialog = false }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // Modal Dialog: Add UPI Account
    if (showAddUpiDialog) {
      var upiId by remember { mutableStateOf("") }
      var provider by remember { mutableStateOf("Google Pay") }
      var linkedBank by remember { mutableStateOf(bankAccounts.firstOrNull()?.bankName ?: "SBI") }

      AlertDialog(
        onDismissRequest = { showAddUpiDialog = false },
        title = {
          Text("Add UPI VPA & Merchant QR", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = upiId,
              onValueChange = { upiId = it },
              label = { Text("UPI ID / VPA*") },
              placeholder = { Text("e.g. royalpharmacy@okaxis") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = provider,
              onValueChange = { provider = it },
              label = { Text("Provider (GPay, PhonePe, Paytm, BHIM)") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = linkedBank,
              onValueChange = { linkedBank = it },
              label = { Text("Linked Bank Account") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (upiId.isNotBlank()) {
                viewModel.addUpiAccount(
                  UpiAccount(
                    upiId = upiId,
                    providerName = provider,
                    linkedBank = linkedBank,
                    isPrimary = upiAccounts.isEmpty()
                  )
                )
                showAddUpiDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
          ) {
            Text("Save UPI ID")
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddUpiDialog = false }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // Modal Dialog: Adjust Cash Drawer
    if (showAdjustCashDialog) {
      var amountText by remember { mutableStateOf("") }
      var reason by remember { mutableStateOf("Opening Float Addition") }
      var isAddition by remember { mutableStateOf(true) }

      AlertDialog(
        onDismissRequest = { showAdjustCashDialog = false },
        title = {
          Text("Adjust Cash in Hand", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { isAddition = true },
                colors = ButtonDefaults.buttonColors(containerColor = if (isAddition) StatusGreen else GrayBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Cash In (+)", color = if (isAddition) Color.White else TextDark, fontSize = 12.sp)
              }
              Button(
                onClick = { isAddition = false },
                colors = ButtonDefaults.buttonColors(containerColor = if (!isAddition) StatusRed else GrayBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Cash Out (-)", color = if (!isAddition) Color.White else TextDark, fontSize = 12.sp)
              }
            }

            OutlinedTextField(
              value = amountText,
              onValueChange = { amountText = it },
              label = { Text("Amount (₹)*") },
              placeholder = { Text("e.g. 1500") },
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = reason,
              onValueChange = { reason = it },
              label = { Text("Reason (Float, Bank Deposit, Petty Expense)") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val amt = amountText.toDoubleOrNull() ?: 0.0
              if (amt > 0) {
                viewModel.adjustCashInHand(if (isAddition) amt else -amt, reason)
                showAdjustCashDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isAddition) StatusGreen else StatusRed)
          ) {
            Text("Confirm Adjustment")
          }
        },
        dismissButton = {
          TextButton(onClick = { showAdjustCashDialog = false }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // Customer Payment QR Code Modal
    if (showUpiQrModal) {
      StoreUpiQrDialog(
        upiId = previewUpiId.ifBlank { upiAccounts.firstOrNull { it.isPrimary }?.upiId ?: "royalpharmacy@okaxis" },
        storeName = profile?.businessName ?: "ROYAL PHARMACY",
        onDismiss = { showUpiQrModal = false }
      )
    }
  }
}
