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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun AutomatedTaxCalculatorScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val profile by viewModel.businessProfile.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()
  val sales by viewModel.allSales.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Calculator & Simulator, 1: HSN Master & Slabs, 2: GST Sales Tax Audit

  // Calculator Inputs
  var grossAmountInput by remember { mutableStateOf("1000") }
  var quantityInput by remember { mutableStateOf("1") }
  var discountPercentInput by remember { mutableStateOf("0") }
  var selectedGstRate by remember { mutableDoubleStateOf(12.0) } // 0, 5, 12, 18, 28
  var isTaxInclusive by remember { mutableStateOf(true) } // MRP (Inclusive) vs Base (Exclusive)
  var isInterState by remember { mutableStateOf(false) } // Intra (CGST+SGST) vs Inter (IGST)

  val grossAmount = grossAmountInput.toDoubleOrNull() ?: 1000.0
  val quantity = quantityInput.toIntOrNull() ?: 1
  val discountPercent = discountPercentInput.toDoubleOrNull() ?: 0.0

  val totalRaw = grossAmount * quantity
  val discountAmount = totalRaw * (discountPercent / 100.0)
  val effectiveAmount = (totalRaw - discountAmount).coerceAtLeast(0.0)

  // Tax calculations
  val taxableBase: Double
  val totalGst: Double

  if (isTaxInclusive) {
    // Reverse calculation: Base = Effective / (1 + Rate/100)
    taxableBase = effectiveAmount / (1.0 + (selectedGstRate / 100.0))
    totalGst = effectiveAmount - taxableBase
  } else {
    // Forward calculation: Tax = Effective * (Rate/100)
    taxableBase = effectiveAmount
    totalGst = effectiveAmount * (selectedGstRate / 100.0)
  }

  val finalGrandTotal = taxableBase + totalGst
  val halfRate = selectedGstRate / 2.0
  val cgstAmount = if (isInterState) 0.0 else totalGst / 2.0
  val sgstAmount = if (isInterState) 0.0 else totalGst / 2.0
  val igstAmount = if (isInterState) totalGst else 0.0

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
          modifier = Modifier.size(36.dp).testTag("btn_back_tax_calc")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Automated GST Tax Calculator", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("HSN Rates, CGST/SGST Split & Reverse Tax Base", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      Button(
        onClick = { viewModel.navigateTo(Screen.ADD_SALE) },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.testTag("btn_goto_billing_from_tax")
      ) {
        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Billing POS", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
      }
    }

    // 2. GSTIN Banner
    Card(
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Slate 800
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF67E8F9), modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(profile?.businessName ?: "ROYAL PHARMACY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            Text("GSTIN: ${profile?.gstin ?: "18AABCR1234M1Z5"} (${if (profile?.isCompositionScheme == true) "Composition" else "Regular Taxpayer"})", color = Color(0xFF94A3B8), fontSize = 10.5.sp)
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0284C7))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("State: Assam (18)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }

    // 3. Tab Selector
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
        text = { Text("Tax Engine", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_tax_simulator")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("HSN Slabs (${medicines.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_tax_hsn")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("GST Audit & Filing", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
        modifier = Modifier.testTag("tab_tax_audit")
      )
    }

    // 4. Tab Body
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      when (selectedTab) {
        0 -> {
          // Calculator Inputs
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("Real-Time Tax Calculation Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(modifier = Modifier.height(10.dp))

                // Mode Selector: Inclusive vs Exclusive
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  FilterChip(
                    selected = isTaxInclusive,
                    onClick = { isTaxInclusive = true },
                    label = { Text("MRP Inclusive (Standard Retail)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = RoyalNavy,
                      selectedLabelColor = Color.White
                    )
                  )
                  FilterChip(
                    selected = !isTaxInclusive,
                    onClick = { isTaxInclusive = false },
                    label = { Text("Base Exclusive (B2B)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = RoyalNavy,
                      selectedLabelColor = Color.White
                    )
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rate Selector Chips
                Text("Select GST Rate (HSN Standard)", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf(0.0, 5.0, 12.0, 18.0, 28.0).forEach { rate ->
                    val isSelected = selectedGstRate == rate
                    FilterChip(
                      selected = isSelected,
                      onClick = { selectedGstRate = rate },
                      label = { Text("${rate.toInt()}% GST", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                      colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF6B21A8),
                        selectedLabelColor = Color.White
                      )
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount and Quantity Row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  OutlinedTextField(
                    value = grossAmountInput,
                    onValueChange = { grossAmountInput = it },
                    label = { Text(if (isTaxInclusive) "MRP Rate (₹)" else "Base Rate (₹)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("input_tax_amount")
                  )

                  OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it },
                    label = { Text("Qty (Packs)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.7f).testTag("input_tax_qty")
                  )

                  OutlinedTextField(
                    value = discountPercentInput,
                    onValueChange = { discountPercentInput = it },
                    label = { Text("Disc %", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.7f).testTag("input_tax_discount")
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Intra-state vs Inter-state
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = !isInterState,
                    onClick = { isInterState = false },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalNavy)
                  )
                  Text("Intra-State (CGST + SGST)", fontSize = 12.sp, color = TextDark)
                  Spacer(modifier = Modifier.width(16.dp))
                  RadioButton(
                    selected = isInterState,
                    onClick = { isInterState = true },
                    colors = RadioButtonDefaults.colors(selectedColor = RoyalNavy)
                  )
                  Text("Inter-State (IGST)", fontSize = 12.sp, color = TextDark)
                }
              }
            }
          }

          // Calculation Result Breakdown Card
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("TAX INVOICE BREAKDOWN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalNavy)
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0xFFDCFCE7))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text("${selectedGstRate.toInt()}% GST Applied", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown rows
                TaxBreakdownRow("Gross Billed Amount", "₹${String.format(Locale.getDefault(), "%.2f", totalRaw)}")
                if (discountAmount > 0) {
                  TaxBreakdownRow("Discount ($discountPercent%)", "- ₹${String.format(Locale.getDefault(), "%.2f", discountAmount)}", color = StatusRed)
                }
                TaxBreakdownRow("Net Taxable Value (Base)", "₹${String.format(Locale.getDefault(), "%.2f", taxableBase)}", isBold = true)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                if (isInterState) {
                  TaxBreakdownRow("IGST (${selectedGstRate.toInt()}%)", "₹${String.format(Locale.getDefault(), "%.2f", igstAmount)}", color = Color(0xFF7C3AED))
                } else {
                  TaxBreakdownRow("CGST (${String.format(Locale.getDefault(), "%.1f", halfRate)}%)", "₹${String.format(Locale.getDefault(), "%.2f", cgstAmount)}", color = Color(0xFF0369A1))
                  TaxBreakdownRow("SGST (${String.format(Locale.getDefault(), "%.1f", halfRate)}%)", "₹${String.format(Locale.getDefault(), "%.2f", sgstAmount)}", color = Color(0xFF0369A1))
                }

                TaxBreakdownRow("Total GST Tax Collected", "₹${String.format(Locale.getDefault(), "%.2f", totalGst)}", isBold = true, color = RoyalMagenta)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFCBD5E1))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("FINAL PAYABLE (ROUNDED)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                  Text(
                    "₹${String.format(Locale.getDefault(), "%,.2f", finalGrandTotal)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = RoyalNavy
                  )
                }
              }
            }
          }
        }
        1 -> {
          // HSN Slabs & Medicine Mapping
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Percent, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("Pharmacy HSN & GST Tariff Codes", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RoyalNavy)
                  Text("HSN 3004 (Medicaments 12%), HSN 3006 (Diagnostics 18%), HSN 9018 (Surgicals 12%)", fontSize = 11.sp, color = Color(0xFF1E40AF))
                }
              }
            }
          }

          items(medicines) { med ->
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(med.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                  Text("HSN: ${med.hsnCode} • Salt: ${med.saltMolecule}", fontSize = 11.sp, color = TextMuted)
                  Text("MRP: ₹${med.mrp} (Base: ₹${String.format(Locale.getDefault(), "%.2f", med.mrp / (1.0 + med.gstPercent / 100.0))})", fontSize = 11.sp, color = RoyalNavy)
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                      when (med.gstPercent) {
                        5.0 -> Color(0xFFFEF3C7)
                        12.0 -> Color(0xFFDCFCE7)
                        18.0 -> Color(0xFFE0E7FF)
                        else -> Color(0xFFFEE2E2)
                      }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    "${med.gstPercent.toInt()}% GST",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = when (med.gstPercent) {
                      5.0 -> Color(0xFFB45309)
                      12.0 -> Color(0xFF15803D)
                      18.0 -> Color(0xFF4338CA)
                      else -> StatusRed
                    }
                  )
                }
              }
            }
          }
        }
        2 -> {
          // GST Audit & Filing Summary
          val totalSalesGst = sales.sumOf { it.gstTotal }
          val totalGrossRevenue = sales.sumOf { it.grandTotal }
          val totalTaxableTurnover = totalGrossRevenue - totalSalesGst

          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("GSTR-1 & 3B Monthly Sales Tax Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(modifier = Modifier.height(10.dp))

                TaxBreakdownRow("Total Invoices Generated", "${sales.size} Tax Invoices")
                TaxBreakdownRow("Gross Aggregate Turnover", "₹${String.format(Locale.getDefault(), "%,.2f", totalGrossRevenue)}")
                TaxBreakdownRow("Net Taxable Turnover (Base)", "₹${String.format(Locale.getDefault(), "%,.2f", totalTaxableTurnover)}", isBold = true)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                TaxBreakdownRow("CGST Output Tax (6%)", "₹${String.format(Locale.getDefault(), "%,.2f", totalSalesGst / 2.0)}", color = Color(0xFF0369A1))
                TaxBreakdownRow("SGST Output Tax (6%)", "₹${String.format(Locale.getDefault(), "%,.2f", totalSalesGst / 2.0)}", color = Color(0xFF0369A1))
                TaxBreakdownRow("Total Output GST Liability", "₹${String.format(Locale.getDefault(), "%,.2f", totalSalesGst)}", isBold = true, color = RoyalMagenta)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun TaxBreakdownRow(
  label: String,
  value: String,
  isBold: Boolean = false,
  color: Color = TextDark
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, fontSize = 12.sp, color = TextMuted, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    Text(value, fontSize = 12.sp, color = color, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
  }
}
