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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillItem
import com.example.data.model.MedicineItem
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val billingTo by viewModel.billingTo.collectAsState()
  val customerName by viewModel.billingCustomerName.collectAsState()
  val customerPhone by viewModel.billingCustomerPhone.collectAsState()
  val doctorName by viewModel.billingDoctorName.collectAsState()
  val saleType by viewModel.billingSaleType.collectAsState()
  val invoiceNumber by viewModel.billingInvoiceNumber.collectAsState()
  val cartItems by viewModel.billingCartItems.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()
  val criticalMedicines by viewModel.criticalLowStockMedicines.collectAsState()
  val isAudioAlertsEnabled by viewModel.isAudioAlertsEnabled.collectAsState()

  androidx.compose.runtime.LaunchedEffect(Unit) {
    if (criticalMedicines.isNotEmpty() && isAudioAlertsEnabled) {
      viewModel.playLowStockAlert(context)
    }
  }

  val allCustomers by viewModel.allCustomers.collectAsState()
  val allPatients by viewModel.allPatients.collectAsState()
  val redeemLoyalty by viewModel.billingRedeemLoyaltyPoints.collectAsState()
  val loyaltyPointsToRedeem by viewModel.billingLoyaltyPointsToRedeem.collectAsState()

  // Match customer or patient to find their loyalty points
  val matchingCustomer = allCustomers.firstOrNull {
    it.name.equals(customerName, ignoreCase = true) || (it.phone.isNotBlank() && it.phone == customerPhone)
  }
  val matchingPatient = allPatients.firstOrNull {
    it.name.equals(customerName, ignoreCase = true) || (it.contactNumber.isNotBlank() && it.contactNumber == customerPhone)
  }
  val availableLoyaltyPoints = matchingCustomer?.loyaltyPoints ?: matchingPatient?.loyaltyPoints ?: 0

  val currentDate = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) }
  var showItemSearchSheet by remember { mutableStateOf(false) }
  var showBatchQrScannerDialog by remember { mutableStateOf(false) }
  var paymentMode by remember { mutableStateOf("Cash") }

  val userRole by viewModel.currentUserRole.collectAsState()
  var showProfitDetails by remember { mutableStateOf(false) }

  val subtotal = cartItems.sumOf { it.packQty * it.rate }
  val totalCost = cartItems.sumOf { it.packQty * it.purchaseRate }
  val itemDiscount = cartItems.sumOf { (it.packQty * it.rate) * (it.discountPercent / 100.0) }
  val taxableBeforeLoyalty = subtotal - itemDiscount
  val loyaltyDiscount = if (redeemLoyalty) loyaltyPointsToRedeem.toDouble().coerceAtMost(taxableBeforeLoyalty) else 0.0
  val taxable = (taxableBeforeLoyalty - loyaltyDiscount).coerceAtLeast(0.0)
  val gst = taxable * 0.12 // 12% standard medicine GST
  val grandTotal = taxable + gst
  val pointsToEarn = (taxable / 100.0).toInt().coerceAtLeast(0)
  val totalProfit = (taxable - totalCost).coerceAtLeast(0.0)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Header (Screenshot 8)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.SALES) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Text(
            text = "Add Sale",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(end = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          OutlinedButton(
            onClick = { viewModel.clearBillingCart() },
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
            modifier = Modifier.testTag("btn_header_clear_bill")
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRed, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Clear", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { viewModel.navigateTo(Screen.CUSTOMER_HISTORY) },
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.testTag("btn_header_customer_history")
          ) {
            Icon(Icons.Default.History, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("History", fontSize = 10.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { viewModel.navigateTo(Screen.AUTOMATED_TAX_CALC) },
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            modifier = Modifier.testTag("btn_header_tax_calc")
          ) {
            Icon(Icons.Default.Calculate, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("GST", fontSize = 10.sp, color = RoyalMagenta, fontWeight = FontWeight.Bold)
          }
        }
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Text(
            text = "Fill the details below to add",
            fontSize = 12.sp,
            color = TextMuted
          )
        }

        // 2. Billing To: Cash Sale vs Customer (Screenshot 8)
        item {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Billing to",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Radio Cash Sale
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .clickable { viewModel.billingTo.value = "Cash Sale" }
                    .testTag("radio_cash_sale")
                ) {
                  RadioButton(
                    selected = billingTo == "Cash Sale",
                    onClick = { viewModel.billingTo.value = "Cash Sale" },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                  )
                  Text(
                    text = "Cash Sale",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0xFFFFEBEE))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "Fast",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMagenta
                    )
                  }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Radio Customer
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .clickable { viewModel.billingTo.value = "Customer" }
                    .testTag("radio_customer")
                ) {
                  RadioButton(
                    selected = billingTo == "Customer",
                    onClick = { viewModel.billingTo.value = "Customer" },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                  )
                  Text(
                    text = "Customer",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                  )
                }
              }

              // If Customer chosen, show Name, Phone, Doctor
              if (billingTo == "Customer") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                  value = customerName,
                  onValueChange = { viewModel.billingCustomerName.value = it },
                  label = { Text("Customer Name*", fontSize = 12.sp) },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth().testTag("input_customer_name")
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { viewModel.billingCustomerPhone.value = it },
                    label = { Text("Phone Number", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_customer_phone")
                  )
                  OutlinedTextField(
                    value = doctorName,
                    onValueChange = { viewModel.billingDoctorName.value = it },
                    label = { Text("Doctor Name", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("input_doctor_name")
                  )
                }
              }
            }
          }
        }

        // 3. Sale Type: Invoice vs Delivery Challan (Screenshot 8)
        item {
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Sale Type",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextMuted
                )
                Text(
                  text = "what's this?",
                  fontSize = 11.sp,
                  color = RoyalMagenta,
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable { viewModel.billingSaleType.value = "Invoice" }
                ) {
                  RadioButton(
                    selected = saleType == "Invoice",
                    onClick = { viewModel.billingSaleType.value = "Invoice" },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                  )
                  Text("Invoice", fontSize = 14.sp, color = TextDark)
                }

                Spacer(modifier = Modifier.width(24.dp))

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable { viewModel.billingSaleType.value = "Delivery Challan" }
                ) {
                  RadioButton(
                    selected = saleType == "Delivery Challan",
                    onClick = { viewModel.billingSaleType.value = "Delivery Challan" },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalMagenta)
                  )
                  Text("Delivery Challan", fontSize = 14.sp, color = TextDark)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Invoice Number & Invoice Date (Screenshot 8)
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedTextField(
                  value = invoiceNumber,
                  onValueChange = { viewModel.billingInvoiceNumber.value = it },
                  label = { Text("Invoice Number", fontSize = 11.sp) },
                  singleLine = true,
                  shape = RoundedCornerShape(8.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = GrayBackground,
                    focusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f).testTag("input_invoice_number")
                )

                OutlinedTextField(
                  value = currentDate,
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("Invoice Date", fontSize = 11.sp) },
                  trailingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                  },
                  singleLine = true,
                  shape = RoundedCornerShape(8.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = GrayBackground,
                    focusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        // 4. Products Section (Screenshot 8)
        item {
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = "Products",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Two buttons: [Scan to Add] and [Search to Add]
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Scan to Add button
              OutlinedButton(
                onClick = { showBatchQrScannerDialog = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("btn_scan_to_add")
              ) {
                Icon(
                  imageVector = Icons.Default.QrCodeScanner,
                  contentDescription = null,
                  tint = TextDark,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Scan to Add",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextDark
                )
              }

              // Search to Add button
              Button(
                onClick = { showItemSearchSheet = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("btn_search_to_add")
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Search to Add",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color.White
                )
              }
            }
          }
        }

        // 5. Cart Items Table
        if (cartItems.isEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No medicines added yet.\nUse Scan to Add or Search to Add above.",
                  fontSize = 13.sp,
                  color = TextMuted,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
        } else {
          itemsIndexed(cartItems) { index, item ->
            CartItemRow(
              item = item,
              showCostPrice = showProfitDetails && userRole == com.example.viewmodel.UserRole.OWNER,
              onQtyChange = { newQty -> viewModel.updateCartItemQty(index, newQty) },
              onRemove = { viewModel.removeCartItem(index) },
              onTogglePaid = { viewModel.toggleBillItemPaidStatus(index) }
            )
          }

          // 6. Summary Breakdown
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "Bill Summary",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Loyalty Points Redemption Banner if available
                if (availableLoyaltyPoints > 0) {
                  Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A))),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("card_loyalty_redemption")
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth().padding(10.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text(
                            text = "Loyalty: $availableLoyaltyPoints pts available",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                          )
                        }
                        Text(
                          text = "Redeem for ₹${availableLoyaltyPoints}.00 discount on this bill",
                          fontSize = 11.sp,
                          color = Color(0xFFB45309)
                        )
                      }
                      Switch(
                        checked = redeemLoyalty,
                        onCheckedChange = { checked ->
                          viewModel.toggleRedeemLoyalty(checked, availableLoyaltyPoints)
                        },
                        colors = SwitchDefaults.colors(
                          checkedThumbColor = Color.White,
                          checkedTrackColor = Color(0xFFD97706)
                        ),
                        modifier = Modifier.testTag("switch_redeem_loyalty")
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Subtotal (${cartItems.size} items)", fontSize = 12.sp, color = TextMuted)
                  Text(String.format(Locale.getDefault(), "₹%.2f", subtotal), fontSize = 12.sp, color = TextDark)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Item Discount", fontSize = 12.sp, color = TextMuted)
                  Text(String.format(Locale.getDefault(), "-₹%.2f", itemDiscount), fontSize = 12.sp, color = StatusGreen)
                }

                if (loyaltyDiscount > 0) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Loyalty Discount ($loyaltyPointsToRedeem pts)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                    }
                    Text(String.format(Locale.getDefault(), "-₹%.2f", loyaltyDiscount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("GST (CGST 6% + SGST 6%)", fontSize = 12.sp, color = TextMuted)
                  Text(String.format(Locale.getDefault(), "₹%.2f", gst), fontSize = 12.sp, color = TextDark)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Grand Total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text(
                    String.format(Locale.getDefault(), "₹%.2f", grandTotal),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalMagenta
                  )
                }

                if (userRole == com.example.viewmodel.UserRole.OWNER) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text("Show Profit & Cost", fontSize = 12.sp, color = TextMuted)
                      Spacer(modifier = Modifier.width(4.dp))
                      Switch(
                        checked = showProfitDetails,
                        onCheckedChange = { showProfitDetails = it },
                        modifier = Modifier.scale(0.7f).testTag("switch_show_profit")
                      )
                    }
                    if (showProfitDetails) {
                      Column(horizontalAlignment = Alignment.End) {
                        Text("Total Profit Margin", fontSize = 11.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
                        Text(
                          String.format(Locale.getDefault(), "₹%.2f", totalProfit),
                          fontSize = 13.sp,
                          fontWeight = FontWeight.Bold,
                          color = StatusGreen
                        )
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Loyalty Points Earn Indicator
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFECFDF5))
                    .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "You will earn +$pointsToEarn Loyalty Points on this bill!",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF047857)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment mode selector chips
                Text("Payment Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  listOf("Cash", "UPI", "Card", "Credit").forEach { mode ->
                    val isSel = paymentMode == mode
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) RoyalMagenta else GrayBackground)
                        .border(1.dp, if (isSel) RoyalMagenta else CardBorder, RoundedCornerShape(6.dp))
                        .clickable { paymentMode = mode }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Text(
                        text = mode,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) Color.White else TextDark
                      )
                    }
                  }
                }
              }
            }
          }

          // 7. Save & Print Action Buttons
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = {
                  viewModel.completeSale(paymentMode)
                  viewModel.navigateTo(Screen.SALES)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(48.dp).testTag("btn_save_bill")
              ) {
                Text("Save Bill", fontWeight = FontWeight.Bold, color = RoyalNavy)
              }

              Button(
                onClick = {
                  viewModel.completeSale(paymentMode)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                modifier = Modifier.weight(1f).height(48.dp).testTag("btn_save_print_bill")
              ) {
                Text("Save & Print", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(30.dp))
        }
      }
    }

    // 8. Search Items Bottom Sheet (Screenshot 9)
    if (showItemSearchSheet) {
      val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
      ModalBottomSheet(
        onDismissRequest = { showItemSearchSheet = false },
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
      ) {
        SearchItemForBillingContent(
          medicines = medicines,
          showCostPrice = showProfitDetails && userRole == com.example.viewmodel.UserRole.OWNER,
          onItemSelect = { med ->
            viewModel.addMedicineToCart(med, 1)
            showItemSearchSheet = false
          },
          onScanClick = {
            showItemSearchSheet = false
            viewModel.navigateTo(Screen.QUICK_SCAN)
          }
        )
      }
    }

    // Batch QR Scanner Dialog
    if (showBatchQrScannerDialog) {
      val context = LocalContext.current
      PosBatchScannerDialog(
        medicines = medicines,
        onDismiss = { showBatchQrScannerDialog = false },
        onScan = { med ->
          viewModel.addMedicineToCart(med, 1)
          showBatchQrScannerDialog = false
          android.widget.Toast.makeText(context, "Scanned & Added: ${med.name} (Batch: ${med.batchNumber})", android.widget.Toast.LENGTH_SHORT).show()
        }
      )
    }
  }
}

@Composable
fun CartItemRow(
  item: BillItem,
  showCostPrice: Boolean = false,
  onQtyChange: (Int) -> Unit,
  onRemove: () -> Unit,
  onTogglePaid: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.medicineName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Batch: ${item.batchNumber} • Exp: ${item.expiryDate} • ₹${item.rate}/u",
            fontSize = 10.5.sp,
            color = TextMuted
          )
        }
        IconButton(onClick = onRemove, modifier = Modifier.size(26.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(15.dp))
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Compact Quantity Stepper
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(GrayBackground)
              .border(1.dp, CardBorder, CircleShape)
              .clickable { onQtyChange(item.packQty - 1) },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextDark, modifier = Modifier.size(12.dp))
          }

          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${item.packQty}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Spacer(modifier = Modifier.width(6.dp))

          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(RoyalMagentaLight)
              .clickable { onQtyChange(item.packQty + 1) },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", tint = RoyalMagenta, modifier = Modifier.size(12.dp))
          }
        }

        // Real-time Payable vs Paid Toggle Switch
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = if (item.isPaid) "Paid" else "Payable",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.isPaid) StatusGreen else Color(0xFFD97706)
          )
          Switch(
            checked = item.isPaid,
            onCheckedChange = { onTogglePaid() },
            modifier = Modifier.scale(0.7f).testTag("switch_item_paid_${item.medicineName}")
          )
        }

        // Total
        Text(
          text = String.format(Locale.getDefault(), "₹%.2f", item.total),
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalNavy
        )
      }
    }
  }
}

@Composable
fun SearchItemForBillingContent(
  medicines: List<MedicineItem>,
  showCostPrice: Boolean = false,
  onItemSelect: (MedicineItem) -> Unit,
  onScanClick: () -> Unit
) {
  var query by remember { mutableStateOf("") }
  var showQrDialogForMed by remember { mutableStateOf<MedicineItem?>(null) }

  val filtered = medicines.filter {
    it.name.contains(query, ignoreCase = true) ||
      it.manufacturer.contains(query, ignoreCase = true) ||
      it.composition.contains(query, ignoreCase = true)
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .padding(bottom = 24.dp)
  ) {
    Text(
      text = "Search Item",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = TextDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Search Box (Screenshot 9: Eg. Dolo Tab 650mg + barcode button)
    OutlinedTextField(
      value = query,
      onValueChange = { query = it },
      placeholder = { Text("Eg. Dolo Tab 650mg", fontSize = 13.sp, color = TextMuted) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
      },
      trailingIcon = {
        IconButton(onClick = onScanClick) {
          Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = RoyalMagenta)
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.fillMaxWidth().testTag("search_medicine_billing_input")
    )

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(vertical = 4.dp)
    ) {
      @Suppress("DEPRECATION")
    Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(14.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text("Item search methods (Name, Salt, Batch or Barcode)", fontSize = 11.sp, color = RoyalMagenta)
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      modifier = Modifier.height(380.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(filtered) { med ->
        Card(
          onClick = { onItemSelect(med) },
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = GrayBackground),
          modifier = Modifier.fillMaxWidth().testTag("select_med_${med.id}")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = med.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "${med.manufacturer} • ${med.stockPacks} in stock",
                fontSize = 11.sp,
                color = if (med.stockPacks > 0) StatusGreen else StatusRed
              )
              if (med.composition.isNotBlank()) {
                Text(
                  text = med.composition,
                  fontSize = 10.sp,
                  color = TextMuted,
                  maxLines = 1
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(3.dp))
                  .background(Color(0xFFFEF3C7))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "📍 ${med.rackLocation.ifBlank { "Rack A-1" }}",
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF78350F)
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              if (showCostPrice) {
                Text(
                  text = "Cost: ₹${med.purchaseRate}",
                  fontSize = 11.sp,
                  color = TextMuted,
                  fontWeight = FontWeight.Medium
                )
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { showQrDialogForMed = med },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(Icons.Default.QrCode, contentDescription = "Batch QR", tint = RoyalNavy, modifier = Modifier.size(16.dp))
                }
                
                Text(
                  text = "₹${med.saleRate}",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
              }
              
              Text(
                text = "+ Add",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMagenta
              )
            }
          }
        }
      }
    }

    if (showQrDialogForMed != null) {
      val med = showQrDialogForMed!!
      val qrPayload = remember(med) { com.example.util.MedicineQrPayload.fromMedicine(med).toJsonString() }
      val qrBitmap = remember(med) { com.example.util.QrCodeGeneratorUtil.generateQrBitmap(qrPayload, 320) }

      AlertDialog(
        onDismissRequest = { showQrDialogForMed = null },
        title = {
          Text("Batch QR Code", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            androidx.compose.foundation.Image(
              bitmap = qrBitmap.asImageBitmap(),
              contentDescription = "Medicine QR Code",
              modifier = Modifier.size(200.dp).border(1.dp, Color.LightGray)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(med.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("Batch: ${med.batchNumber} | Exp: ${med.expiryDate}", fontSize = 12.sp, color = TextMuted)
            Text("Price: ₹${med.saleRate}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              "Scan this code at the POS to automatically fill medicine, batch, expiry and price details.",
              fontSize = 10.sp,
              color = TextMuted,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        },
        confirmButton = {
          TextButton(onClick = { showQrDialogForMed = null }) {
            Text("Close", color = RoyalNavy, fontWeight = FontWeight.Bold)
          }
        }
      )
    }
  }
}

@Composable
fun PosBatchScannerDialog(
  medicines: List<MedicineItem>,
  onDismiss: () -> Unit,
  onScan: (MedicineItem) -> Unit
) {
  var barcodeInput by remember { mutableStateOf("") }
  val filteredMeds = medicines.filter {
    barcodeInput.isBlank() ||
    it.name.contains(barcodeInput, ignoreCase = true) ||
    it.batchNumber.contains(barcodeInput, ignoreCase = true) ||
    it.barcode.contains(barcodeInput, ignoreCase = true)
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = RoyalMagenta)
        Spacer(modifier = Modifier.width(8.dp))
        Text("POS Batch QR / Barcode Scanner", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth().height(340.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A)),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Align Batch QR Label in Viewfinder", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
            Text("Simulated Live Laser Scanning Active", fontSize = 9.5.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
          }
        }

        OutlinedTextField(
          value = barcodeInput,
          onValueChange = { barcodeInput = it },
          label = { Text("Search by Medicine or Batch/Barcode") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_batch_qr_search")
        )

        Text("Select Scanned Batch:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)

        LazyColumn(
          modifier = Modifier.fillMaxWidth().height(180.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(filteredMeds.take(20), key = { it.id }) { med ->
            Card(
              onClick = { onScan(med) },
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
              border = CardDefaults.outlinedCardBorder(),
              modifier = Modifier.fillMaxWidth().testTag("scanned_batch_item_${med.id}")
            ) {
              Row(
                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(med.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Batch: ${med.batchNumber} • Exp: ${med.expiryDate}", fontSize = 10.5.sp, color = TextMuted)
                }
                Text("₹${med.saleRate}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}
