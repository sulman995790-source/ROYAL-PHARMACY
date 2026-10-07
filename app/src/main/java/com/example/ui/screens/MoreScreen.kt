package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun MoreScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val profile by viewModel.businessProfile.collectAsState()
  val isDarkMode by viewModel.isDarkMode.collectAsState()
  val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
  val cashInHand by viewModel.cashInHandRegister.collectAsState()
  val bankAccounts by viewModel.allBankAccounts.collectAsState()
  val totalBankBal = bankAccounts.sumOf { it.balance }

  var showSecurityDialog by remember { mutableStateOf(false) }
  var pinInput by remember { mutableStateOf(viewModel.savedPin.value) }

  // Security Lock Setup Dialog
  if (showSecurityDialog) {
    AlertDialog(
      onDismissRequest = { showSecurityDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Fingerprint, contentDescription = null, tint = RoyalMagenta)
          Spacer(modifier = Modifier.width(8.dp))
          Text("App Security & PIN Lock", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Protect pharmacy sales, Udhar ledger, and confidential stock margins with Biometric or 4-digit PIN lock.",
            fontSize = 12.sp,
            color = TextMuted
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Enable Security Lock", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Switch(
              checked = isAppLockEnabled,
              onCheckedChange = { viewModel.toggleAppLock(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
            )
          }

          OutlinedTextField(
            value = pinInput,
            onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) pinInput = it },
            label = { Text("Set 4-Digit PIN") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Button(
            onClick = {
              showSecurityDialog = false
              viewModel.lockAppNow()
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Lock App Now")
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (pinInput.length == 4) {
              viewModel.setAppPin(pinInput)
            }
            showSecurityDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSecurityDialog = false }) {
          Text("Cancel", color = TextMuted)
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White),
    contentPadding = PaddingValues(bottom = 100.dp)
  ) {
    // 1. Profile Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(GrayBackground)
            .border(1.dp, CardBorder, CircleShape)
            .clickable { viewModel.navigateTo(Screen.EDIT_PROFILE) },
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextMuted, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = profile.businessName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = "${profile.ownerName} • ${profile.phone}",
            fontSize = 12.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Edit Business Profile",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = RoyalMagenta,
            modifier = Modifier.clickable { viewModel.navigateTo(Screen.EDIT_PROFILE) }
          )
        }
      }
    }

    // 2. Cash & Treasury Summary Ribbon
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(RoyalNavy)
          .clickable { viewModel.navigateTo(Screen.CASH_BANK_ACCOUNTS) }
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Cash: ₹${String.format(Locale.getDefault(), "%.0f", cashInHand)} | Bank: ₹${String.format(Locale.getDefault(), "%.0f", totalBankBal)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Text("Manage >", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
      }
    }

    // SECTION 1: FINANCE & BANKING
    item {
      SectionHeader("Finance & Accounts")
      MoreMenuRow(
        icon = Icons.Default.AccountBalance,
        title = "Cash, Bank & UPI Accounts",
        subtitle = "Manage bank ledgers, UPI IDs, and counter cash float",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.CASH_BANK_ACCOUNTS) }
      )
      MoreMenuRow(
        icon = Icons.Default.AccountBalanceWallet,
        title = "Udhar Khata (Customer Credit)",
        subtitle = "Customer balance tracking & WhatsApp reminders",
        onClick = { viewModel.navigateTo(Screen.UDHAR_KHATA) }
      )
      MoreMenuRow(
        icon = Icons.Default.Calculate,
        title = "Automated GST Tax Calculator",
        subtitle = "HSN summary, CGST/SGST split, tax reports",
        onClick = { viewModel.navigateTo(Screen.AUTOMATED_TAX_CALC) }
      )
    }

    // SECTION 2: CLINICAL & PHARMACY TOOLS
    item {
      SectionHeader("Clinical & Smart Tools")
      MoreMenuRow(
        icon = Icons.Default.CameraAlt,
        title = "AI Prescription Scanner",
        subtitle = "Gemini Vision OCR with auto-billing",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.PRESCRIPTION_SCANNER) }
      )
      MoreMenuRow(
        icon = Icons.Default.History,
        title = "Prescription History",
        subtitle = "Archive of patient prescriptions & doctor notes",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.PRESCRIPTION_HISTORY) }
      )
      MoreMenuRow(
        icon = Icons.Default.ChildCare,
        title = "Smart Dosage & Reminders",
        subtitle = "Neonatal (0-1y), Child (1-12y) & Adult weight dosing",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.SMART_DOSAGE_CALCULATOR) }
      )
      MoreMenuRow(
        icon = Icons.Default.Science,
        title = "Clinical Unit Converter",
        subtitle = "Liquid ml/drops, mass mg/mcg, % solutions, insulin",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.UNIT_CONVERTER) }
      )
      MoreMenuRow(
        icon = Icons.Default.AutoAwesome,
        title = "Gemini AI Pharmacist Copilot",
        subtitle = "Clinical Q&A, contraindications & CDSCO search",
        hasGemTag = true,
        onClick = { viewModel.navigateTo(Screen.AI_CHATBOT) }
      )
      MoreMenuRow(
        icon = Icons.Default.HealthAndSafety,
        title = "Drug Interaction Checker",
        subtitle = "Multi-drug contraindications, risk severity & food warnings",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.DRUG_INTERACTION_CHECKER) }
      )
      MoreMenuRow(
        icon = Icons.Default.FindReplace,
        title = "Generic Substitute Finder",
        subtitle = "Match salt molecules and cost-saving brands",
        onClick = { viewModel.navigateTo(Screen.SUBSTITUTES) }
      )
    }

    // SECTION 3: INVENTORY & LOGISTICS
    item {
      SectionHeader("Inventory & Master")
      MoreMenuRow(
        icon = Icons.Default.TrendingUp,
        title = "Smart Inventory Suggestions",
        subtitle = "Velocity run-rate, stockout predictor & 1-tap PO draft",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.SMART_INVENTORY_SUGGESTIONS) }
      )
      MoreMenuRow(
        icon = Icons.Default.QrCodeScanner,
        title = "Inventory QR & Rack Management",
        subtitle = "Scan shelf QR, edit rack location, print stickers",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.INVENTORY_QR) }
      )
      MoreMenuRow(
        icon = Icons.Default.EventBusy,
        title = "Expiry Date Tracker",
        subtitle = "Early risk radar, short expiry & distributor returns",
        onClick = { viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD) }
      )
      MoreMenuRow(
        icon = Icons.Default.Inventory2,
        title = "Batch Master & Tracking",
        subtitle = "FIFO batch tracking & physical shelf allocation",
        onClick = { viewModel.navigateTo(Screen.BATCH_TRACKING) }
      )
      MoreMenuRow(
        icon = Icons.Default.ReceiptLong,
        title = "Purchase Orders",
        subtitle = "PO creation, distributor order tracking",
        onClick = { viewModel.navigateTo(Screen.PURCHASE_ORDERS) }
      )
      MoreMenuRow(
        icon = Icons.Default.LocalShipping,
        title = "Supplier Management",
        subtitle = "Supplier directory, credit terms & ledger",
        onClick = { viewModel.navigateTo(Screen.SUPPLIERS) }
      )
      MoreMenuRow(
        icon = Icons.Default.NotificationsActive,
        title = "Critical Low Stock Alerts",
        subtitle = "Essential life-saving drug thresholds",
        onClick = { viewModel.navigateTo(Screen.CRITICAL_STOCK_ALERTS) }
      )
    }

    // SECTION 4: SHIFT & SALES REPORTS
    item {
      SectionHeader("Reports & Shift")
      MoreMenuRow(
        icon = Icons.Default.Checklist,
        title = "Daily Huddle Report",
        subtitle = "Morning shift targets, tasks & WhatsApp brief",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.DAILY_HUDDLE) }
      )
      MoreMenuRow(
        icon = Icons.Default.Assessment,
        title = "Daily Sales Report (Z-Report)",
        subtitle = "Shift sales, payment modes & cash closing",
        onClick = { viewModel.navigateTo(Screen.DAILY_SALES_REPORT) }
      )
      MoreMenuRow(
        icon = Icons.Default.BarChart,
        title = "Smart Sales & Peak Hour Analytics",
        subtitle = "Hourly velocity, revenue trends, top doctors",
        onClick = { viewModel.navigateTo(Screen.SMART_SALES_ANALYTICS) }
      )
      MoreMenuRow(
        icon = Icons.Default.Print,
        title = "Smart Invoice Printer Station",
        subtitle = "ESC/POS Thermal & A4/A5 PDF generation",
        onClick = { viewModel.navigateTo(Screen.INVOICE_PRINTER) }
      )
    }

    // SECTION 5: SYSTEM & PREFERENCES
    item {
      SectionHeader("System & Security")
      MoreMenuRow(
        icon = Icons.Default.CloudDownload,
        title = "Database Backup & Restore",
        subtitle = "Full JSON system snapshots & offline restore",
        hasNewTag = true,
        onClick = { viewModel.navigateTo(Screen.BACKUP_RESTORE) }
      )
      MoreMenuRow(
        icon = Icons.Default.CloudSync,
        title = "Cloud & Google Drive Sync",
        subtitle = "Real-time sync queue & automated cloud backups",
        onClick = { viewModel.navigateTo(Screen.SYNC_MANAGER) }
      )
      MoreMenuRow(
        icon = Icons.Default.Fingerprint,
        title = "Security & PIN Lock",
        subtitle = if (isAppLockEnabled) "Enabled (4-digit PIN)" else "Disabled",
        onClick = { showSecurityDialog = true }
      )

      // Dark Mode Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.toggleDarkMode(!isDarkMode) }
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = null,
            tint = if (isDarkMode) RoyalMagenta else TextDark,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text("Dark Theme", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(if (isDarkMode) "Enabled" else "Light Mode", fontSize = 11.sp, color = TextMuted)
          }
        }
        Switch(
          checked = isDarkMode,
          onCheckedChange = { viewModel.toggleDarkMode(it) },
          colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalMagenta)
        )
      }
    }
  }
}

@Composable
fun SectionHeader(title: String) {
  Text(
    text = title.uppercase(),
    fontSize = 11.sp,
    fontWeight = FontWeight.ExtraBold,
    color = TextMuted,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
  )
}

@Composable
fun MoreMenuRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String = "",
  hasNewTag: Boolean = false,
  hasGemTag: Boolean = false,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 11.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = TextDark,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
          )
          if (hasNewTag) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(RoyalMagentaLight)
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text("NEW", fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, color = RoyalMagenta)
            }
          }
          if (hasGemTag) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFEF3C7))
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text("AI", fontSize = 8.5.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
            }
          }
        }
        if (subtitle.isNotBlank()) {
          Text(
            text = subtitle,
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
      contentDescription = null,
      tint = TextLight,
      modifier = Modifier.size(13.dp)
    )
  }
}
