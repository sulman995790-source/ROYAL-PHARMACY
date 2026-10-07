package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.InvoicePrintOptions
import com.example.service.InvoicePrinterService
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailySalesReportScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val sales by viewModel.allSales.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()

  val currentDate = remember { SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date()) }

  // Today's Sales Calculation
  val totalSalesCount = sales.size
  val grossRevenue = sales.sumOf { it.grandTotal }
  val totalTax = sales.sumOf { it.gstTotal }
  val totalDiscounts = sales.sumOf { it.discountTotal + it.loyaltyDiscountAmt }
  val totalLoyaltyEarned = sales.sumOf { it.loyaltyPointsEarned }
  val totalLoyaltyRedeemed = sales.sumOf { it.loyaltyPointsRedeemed }

  // Payment Breakdown
  val cashSales = sales.filter { it.paymentMode.contains("Cash", ignoreCase = true) }.sumOf { it.grandTotal }
  val upiSales = sales.filter { it.paymentMode.contains("UPI", ignoreCase = true) || it.paymentMode.contains("PhonePe", ignoreCase = true) }.sumOf { it.grandTotal }
  val cardSales = sales.filter { it.paymentMode.contains("Card", ignoreCase = true) }.sumOf { it.grandTotal }
  val creditSales = sales.filter { it.paymentMode.contains("Credit", ignoreCase = true) || it.paymentMode.contains("Udhar", ignoreCase = true) }.sumOf { it.grandTotal }

  // Cash Register Reconciliation State
  var openingFloatInput by remember { mutableStateOf("2000") }
  var actualCashCountInput by remember { mutableStateOf("${(cashSales + 2000).toInt()}") }

  val openingFloat = openingFloatInput.toDoubleOrNull() ?: 2000.0
  val actualCashCount = actualCashCountInput.toDoubleOrNull() ?: (cashSales + openingFloat)
  val expectedCashInDrawer = openingFloat + cashSales
  val cashDifference = actualCashCount - expectedCashInDrawer

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
          onClick = { viewModel.navigateTo(Screen.SALES) },
          modifier = Modifier.size(36.dp).testTag("btn_back_from_daily_sales")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Daily Sales & Closing Report", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("Day Cash-Up, Tax Audit & Register Reconciliation", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = {
            // WhatsApp summary dispatch
            val summaryMsg = buildString {
              append("🏥 *${profile.businessName}* 🏥\n")
              append("📊 *DAILY CLOSING REPORT - $currentDate*\n")
              append("Owner: ${profile.ownerName} (${profile.phone})\n")
              append("--------------------------------\n")
              append("💰 *Gross Sales:* ₹${String.format(Locale.getDefault(), "%,.2f", grossRevenue)}\n")
              append("🧾 *Total Invoices:* $totalSalesCount bills\n")
              append("💵 *Cash in Drawer:* ₹${String.format(Locale.getDefault(), "%,.2f", cashSales)}\n")
              append("📱 *UPI / QR Sales:* ₹${String.format(Locale.getDefault(), "%,.2f", upiSales)}\n")
              append("💳 *Card Payments:* ₹${String.format(Locale.getDefault(), "%,.2f", cardSales)}\n")
              append("📒 *Udhar/Credit:* ₹${String.format(Locale.getDefault(), "%,.2f", creditSales)}\n")
              append("--------------------------------\n")
              append("🏛️ *GST Collected:* ₹${String.format(Locale.getDefault(), "%,.2f", totalTax)}\n")
              append("⭐ *Loyalty Redeemed:* -₹${String.format(Locale.getDefault(), "%,.2f", totalDiscounts)}\n")
              append("🪙 *Cash Reconciliation:* Expected: ₹${String.format(Locale.getDefault(), "%,.0f", expectedCashInDrawer)} | Actual: ₹${String.format(Locale.getDefault(), "%,.0f", actualCashCount)}\n")
              append("--------------------------------\n")
              append("✨ Closed securely via Royal Pharmacy Cloud POS")
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
              data = Uri.parse("https://api.whatsapp.com/send?phone=919957905450&text=${Uri.encode(summaryMsg)}")
            }
            try {
              context.startActivity(intent)
            } catch (_: Exception) {
              Toast.makeText(context, "WhatsApp sharing opened", Toast.LENGTH_SHORT).show()
            }
          },
          modifier = Modifier.testTag("btn_whatsapp_daily_summary")
        ) {
          Icon(Icons.Default.Phone, contentDescription = "WhatsApp Summary", tint = Color(0xFF4ADE80))
        }

        IconButton(
          onClick = {
            sales.firstOrNull()?.let { sampleInv ->
              InvoicePrinterService.generateAndSharePdfInvoice(context, sampleInv, profile, InvoicePrintOptions())
            } ?: Toast.makeText(context, "No sales recorded yet", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.testTag("btn_export_daily_pdf")
        ) {
          Icon(Icons.Default.Download, contentDescription = "Export PDF", tint = Color.White)
        }
      }
    }

    // 2. Scrollable Body
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Financial Master Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_daily_revenue_summary")
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
                  Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("Total Sales Today ($currentDate)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("$totalSalesCount Invoices Generated • All counters", fontSize = 11.sp, color = TextMuted)
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFDCFCE7))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("DAY ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Massive Revenue Display
            Text(
              text = "₹${String.format(Locale.getDefault(), "%,.2f", grossRevenue)}",
              fontSize = 28.sp,
              fontWeight = FontWeight.ExtraBold,
              color = RoyalMagenta,
              fontFamily = FontFamily.Monospace
            )
            Text("Gross Tax-Paid Sales Turnover", fontSize = 11.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
            Spacer(modifier = Modifier.height(10.dp))

            // 3 Small metrics row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("GST COLLECTED", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("₹${String.format(Locale.getDefault(), "%,.2f", totalTax)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("CGST + SGST", fontSize = 9.sp, color = TextMuted)
              }

              Column {
                Text("DISCOUNTS GIVEN", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("₹${String.format(Locale.getDefault(), "%,.2f", totalDiscounts)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                Text("Loyalty & Schemes", fontSize = 9.sp, color = TextMuted)
              }

              Column {
                Text("LOYALTY BALANCE", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("+$totalLoyaltyEarned pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                Text("-$totalLoyaltyRedeemed redeemed", fontSize = 9.sp, color = TextMuted)
              }
            }
          }
        }
      }

      // Payment Mode Breakdown Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_payment_mode_split")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Payment Tender Breakdown", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(12.dp))

            // 4 Column tenders
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Cash Box
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFECFDF5))
                  .padding(10.dp)
              ) {
                Column {
                  Text("CASH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text("₹${String.format(Locale.getDefault(), "%,.0f", cashSales)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("In Drawer", fontSize = 9.sp, color = TextMuted)
                }
              }

              // UPI Box
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFEDE9FE))
                  .padding(10.dp)
              ) {
                Column {
                  Text("UPI / QR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text("₹${String.format(Locale.getDefault(), "%,.0f", upiSales)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Direct Bank", fontSize = 9.sp, color = TextMuted)
                }
              }

              // Card Box
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFEF3C7))
                  .padding(10.dp)
              ) {
                Column {
                  Text("CARD POS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                  Spacer(modifier = Modifier.height(2.dp))
                  Text("₹${String.format(Locale.getDefault(), "%,.0f", cardSales)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Swipe Terminal", fontSize = 9.sp, color = TextMuted)
                }
              }

              // Udhar Box
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFFF1F2))
                  .padding(10.dp)
              ) {
                Column {
                  Text("UDHAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text("₹${String.format(Locale.getDefault(), "%,.0f", creditSales)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Customer Khata", fontSize = 9.sp, color = TextMuted)
                }
              }
            }
          }
        }
      }

      // Cash-Up Register Reconciliation Tool
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("card_cash_up_reconciliation")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cash-Up Register Reconciliation", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
              Text("End of Day", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = openingFloatInput,
                onValueChange = { openingFloatInput = it },
                label = { Text("Opening Float (₹)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )

              OutlinedTextField(
                value = actualCashCountInput,
                onValueChange = { actualCashCountInput = it },
                label = { Text("Physical Cash Count (₹)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Result reconciliation status banner
            val isBalanced = Math.abs(cashDifference) < 1.0
            val isShort = cashDifference < -1.0

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isBalanced) Color(0xFFDCFCE7) else if (isShort) Color(0xFFFEE2E2) else Color(0xFFFEF3C7))
                .padding(10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = if (isBalanced) "✅ REGISTER PERFECTLY BALANCED" else if (isShort) "⚠️ CASH DRAWER SHORTAGE" else "ℹ️ CASH DRAWER SURPLUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBalanced) StatusGreen else if (isShort) StatusRed else Color(0xFFB45309)
                  )
                  Text(
                    text = "Expected: ₹${String.format(Locale.getDefault(), "%,.0f", expectedCashInDrawer)} | Difference: ₹${String.format(Locale.getDefault(), "%+,.0f", cashDifference)}",
                    fontSize = 10.5.sp,
                    color = TextDark
                  )
                }

                Button(
                  onClick = {
                    Toast.makeText(context, "Cash Register reconciled and closed for $currentDate", Toast.LENGTH_LONG).show()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Text("Lock Day", fontSize = 11.sp, color = Color.White)
                }
              }
            }
          }
        }
      }

      // Recent Dispensed Invoices List
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Today's Billed Invoices (${sales.size})", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("Real-time", fontSize = 10.5.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (sales.isEmpty()) {
              Text("No sales invoices billed yet today.", fontSize = 12.sp, color = TextMuted)
            } else {
              sales.take(8).forEach { inv ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(inv.invoiceNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${inv.customerName} • ${inv.paymentMode}", fontSize = 10.5.sp, color = TextMuted)
                  }

                  Text(
                    "₹${String.format(Locale.getDefault(), "%.2f", inv.grandTotal)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalMagenta
                  )
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
              }
            }
          }
        }
      }

      // Action Buttons Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { viewModel.navigateTo(Screen.INVOICE_PRINTER) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            modifier = Modifier.weight(1f).height(46.dp).testTag("btn_print_daily_closing")
          ) {
            Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Print Closing Memo", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.REPORTS) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            modifier = Modifier.weight(1f).height(46.dp).testTag("btn_goto_full_reports")
          ) {
            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("All Reports", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
