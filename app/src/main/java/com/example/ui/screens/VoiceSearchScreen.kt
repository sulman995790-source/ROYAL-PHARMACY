package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineItem
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
fun VoiceSearchScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val medicines by viewModel.allMedicines.collectAsState()
  val customers by viewModel.allCustomers.collectAsState()
  val patients by viewModel.allPatients.collectAsState()

  var spokenQuery by remember { mutableStateOf("") }
  var isListening by remember { mutableStateOf(false) }
  var detectedIntent by remember { mutableStateOf<String?>(null) } // "SEARCH", "BILL", "SUBSTITUTE", "EXPIRY", "STOCK", "NAVIGATE"

  // Speech Recognition Intent Launcher
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    isListening = false
    if (result.resultCode == Activity.RESULT_OK) {
      val data = result.data
      val spokenMatches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      if (!spokenMatches.isNullOrEmpty()) {
        val recognizedText = spokenMatches[0]
        spokenQuery = recognizedText
        processVoiceCommand(recognizedText, viewModel) { intentTag ->
          detectedIntent = intentTag
        }
      }
    }
  }

  fun startVoiceRecognition() {
    isListening = true
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
      putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak medicine name, salt, or command (e.g. 'Search Dolo 650' or 'Check expiry')...")
    }
    try {
      speechLauncher.launch(intent)
    } catch (e: Exception) {
      isListening = false
      Toast.makeText(context, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show()
    }
  }

  // Auto-launch mic on entering Voice Search screen
  LaunchedEffect(Unit) {
    if (spokenQuery.isBlank()) {
      startVoiceRecognition()
    }
  }

  // Animation for listening pulse
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  // Filtered matching results
  val matchingMedicines = medicines.filter { med ->
    spokenQuery.isNotBlank() && (
      med.name.contains(spokenQuery, ignoreCase = true) ||
      med.composition.contains(spokenQuery, ignoreCase = true) ||
      med.saltMolecule.contains(spokenQuery, ignoreCase = true) ||
      med.manufacturer.contains(spokenQuery, ignoreCase = true)
    )
  }

  val matchingCustomers = customers.filter { cust ->
    spokenQuery.isNotBlank() && (
      cust.name.contains(spokenQuery, ignoreCase = true) ||
      cust.phone.contains(spokenQuery)
    )
  }

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
          modifier = Modifier.size(36.dp).testTag("btn_back_voice_search")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("Smart Voice Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
          Text("Speech Recognition & Fast Pharma Navigation", color = Color(0xFF93C5FD), fontSize = 11.sp)
        }
      }

      IconButton(
        onClick = { startVoiceRecognition() },
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(RoyalMagenta)
      ) {
        Icon(Icons.Default.Mic, contentDescription = "Listen", tint = Color.White, modifier = Modifier.size(20.dp))
      }
    }

    // 2. Interactive Voice Microphone Pulse Hub
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(90.dp)
            .scale(if (isListening) pulseScale else 1f)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = if (isListening) listOf(Color(0xFFE11D48), Color(0xFF9F1239))
                else listOf(RoyalNavy, Color(0xFF1E3A8A))
              )
            )
            .clickable { startVoiceRecognition() }
            .testTag("btn_voice_mic_pulse")
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Microphone",
            tint = Color.White,
            modifier = Modifier.size(42.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (isListening) "Listening... Speak now" else if (spokenQuery.isNotBlank()) "\"$spokenQuery\"" else "Tap microphone to speak",
          fontSize = if (spokenQuery.isNotBlank()) 16.sp else 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (isListening) Color(0xFFE11D48) else TextDark
        )

        if (detectedIntent != null) {
          Spacer(modifier = Modifier.height(4.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFDCFCE7))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              "Intent Detected: $detectedIntent",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Voice Command Pills
        Text("Try saying:", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            "Dolo 650",
            "Substitute for Pan 40",
            "Paracetamol 650",
            "Check expiry",
            "Create bill for Ramesh",
            "Daily sales report",
            "Sync Google Drive"
          ).forEach { sample ->
            FilterChip(
              selected = false,
              onClick = {
                spokenQuery = sample
                processVoiceCommand(sample, viewModel) { tag ->
                  detectedIntent = tag
                }
              },
              label = { Text(sample, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                containerColor = GrayBackground,
                labelColor = RoyalNavy
              )
            )
          }
        }
      }
    }

    // 3. Search Results & Matched Records
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (matchingMedicines.isNotEmpty()) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Matched Medicines (${matchingMedicines.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
            Text("Tap to action", fontSize = 11.sp, color = TextMuted)
          }
        }

        items(matchingMedicines) { med ->
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
                Column(modifier = Modifier.weight(1f)) {
                  Text(med.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                  Text("${med.manufacturer} • Salt: ${med.saltMolecule}", fontSize = 11.5.sp, color = TextMuted)
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (med.stockPacks > 10) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    "${med.stockPacks} in stock",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (med.stockPacks > 10) Color(0xFF15803D) else StatusRed
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text("MRP: ₹${med.mrp} | Sale Rate: ₹${med.saleRate} | Rack: ${med.rackLocation}", fontSize = 11.5.sp, color = RoyalNavy)

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = Color(0xFFF1F5F9))
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    viewModel.billingCustomerName.value = "Cash Sale"
                    viewModel.navigateTo(Screen.ADD_SALE)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Add to Bill", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                  onClick = {
                    viewModel.globalSearchQuery.value = med.saltMolecule
                    viewModel.navigateTo(Screen.SUBSTITUTES)
                  },
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.FindReplace, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Substitutes", fontSize = 11.sp, color = RoyalMagenta, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      if (matchingCustomers.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(6.dp))
          Text("Matched Customers (${matchingCustomers.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
        }

        items(matchingCustomers) { cust ->
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
              Column {
                Text(cust.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                Text(cust.phone, fontSize = 11.sp, color = TextMuted)
                Text("Loyalty: ${cust.loyaltyPoints} pts • Udhar: ₹${cust.balanceReceivable}", fontSize = 11.sp, color = RoyalNavy)
              }

              Button(
                onClick = {
                  viewModel.billingCustomerName.value = cust.name
                  viewModel.billingCustomerPhone.value = cust.phone
                  viewModel.navigateTo(Screen.ADD_SALE)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text("Bill Customer", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
  }
}

private fun processVoiceCommand(
  query: String,
  viewModel: PharmacyViewModel,
  onIntentDetected: (String) -> Unit
) {
  val clean = query.lowercase(Locale.getDefault())

  when {
    clean.contains("substitute") || clean.contains("generic") -> {
      onIntentDetected("Find Generic Substitutes")
      val saltOrBrand = clean.replace("substitute", "").replace("for", "").replace("generic", "").trim()
      if (saltOrBrand.isNotBlank()) {
        viewModel.globalSearchQuery.value = saltOrBrand
      }
      viewModel.navigateTo(Screen.SUBSTITUTES)
    }
    clean.contains("bill") || clean.contains("invoice") || clean.contains("sale") -> {
      onIntentDetected("New Billing Invoice")
      viewModel.navigateTo(Screen.ADD_SALE)
    }
    clean.contains("expiry") || clean.contains("expired") || clean.contains("risk") -> {
      onIntentDetected("Check Expiry Tracker")
      viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD)
    }
    clean.contains("daily sales") || clean.contains("day closing") || clean.contains("z report") -> {
      onIntentDetected("Daily Sales Report")
      viewModel.navigateTo(Screen.DAILY_SALES_REPORT)
    }
    clean.contains("stock") || clean.contains("inventory") || clean.contains("valuation") -> {
      onIntentDetected("Stock Analytics")
      viewModel.navigateTo(Screen.STOCK_ANALYTICS)
    }
    clean.contains("sync") || clean.contains("google drive") || clean.contains("backup") -> {
      onIntentDetected("Google Drive Sync")
      viewModel.navigateTo(Screen.SYNC_MANAGER)
    }
    clean.contains("purchase") || clean.contains("supplier") || clean.contains("po") -> {
      onIntentDetected("Purchase Orders Hub")
      viewModel.navigateTo(Screen.PURCHASE_ORDERS)
    }
    clean.contains("tax") || clean.contains("gst") || clean.contains("hsn") -> {
      onIntentDetected("Automated GST Tax Calculator")
      viewModel.navigateTo(Screen.AUTOMATED_TAX_CALC)
    }
    clean.contains("history") || clean.contains("patient") || clean.contains("prescription") -> {
      onIntentDetected("Customer Purchase History")
      viewModel.navigateTo(Screen.CUSTOMER_HISTORY)
    }
    else -> {
      onIntentDetected("Medicine & Salt Lookup")
    }
  }
}
