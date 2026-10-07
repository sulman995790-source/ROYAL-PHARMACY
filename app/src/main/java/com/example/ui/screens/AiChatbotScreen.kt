package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ChatMessage
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun AiChatbotScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val messages by viewModel.chatMessages.collectAsState()
  val isThinking by viewModel.isAiThinking.collectAsState()
  var inputQuery by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Voice speech recognizer
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val query = spokenList?.firstOrNull()?.trim()
      if (!query.isNullOrBlank()) {
        inputQuery = query
        viewModel.sendChatMessage(query)
      }
    }
  }

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.lastIndex)
    }
  }

  val quickPrompts = listOf(
    "Check Dolo 650 vs Pacimol substitutes",
    "Warfarin + Aspirin interaction alert",
    "Directions to Sun Pharma Distributor Hub",
    "Latest CDSCO banned FDC drug list",
    "Summarize Udhar Khata credit dues",
    "Dosage for Augmentin 625 Duo in adults",
    "Paracetamol pediatric dose calculation"
  )

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
          .background(RoyalNavy)
          .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Gemini Pharmacist AI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFFF59E0B))
                  .padding(horizontal = 5.dp, vertical = 1.5.dp)
              ) {
                Text("Gemini 3.5 Flash", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              }
            }
            Text("Google Search & Maps Grounded • Ask Anything", fontSize = 11.sp, color = Color(0xFFE2E8F0))
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask Gemini Pharmacist AI...")
              }
              try {
                speechLauncher.launch(intent)
              } catch (_: Exception) {
                Toast.makeText(context, "Speech recognition not available on device", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.testTag("btn_voice_input_ai_chat")
          ) {
            Icon(Icons.Default.Mic, contentDescription = "Voice Ask", tint = Color(0xFFFFE082), modifier = Modifier.size(22.dp))
          }
        }
      }

      // 2. Quick Suggestion Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        quickPrompts.forEach { prompt ->
          FilterChip(
            selected = false,
            onClick = {
              viewModel.sendChatMessage(prompt)
            },
            label = { Text(prompt, fontSize = 11.sp) },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
              containerColor = Color(0xFFF1F5F9),
              labelColor = TextDark
            ),
            modifier = Modifier.testTag("quick_prompt_${prompt.take(10)}")
          )
        }
      }

      // 3. Chat Messages List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(messages, key = { it.id }) { msg ->
          ChatBubble(
            message = msg,
            onOpenMaps = { query ->
              val geoUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
              context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
            },
            onOpenSearch = { query ->
              val searchUri = Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
              context.startActivity(Intent(Intent.ACTION_VIEW, searchUri))
            },
            onCopy = { text ->
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("AI Pharmacist Answer", text)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Copied response to clipboard", Toast.LENGTH_SHORT).show()
            },
            onShare = { text ->
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_SUBJECT, "Royal Pharmacy AI Insight")
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share Clinical Insight"))
            }
          )
        }

        if (isThinking) {
          item {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 8.dp)
            ) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = RoyalMagenta)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Gemini 3.5 Flash is analyzing clinical data & searching...", fontSize = 11.5.sp, color = TextMuted)
            }
          }
        }
      }

      // 4. Input Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputQuery,
          onValueChange = { inputQuery = it },
          placeholder = { Text("Ask clinical question, dosage, substitutes...", fontSize = 13.sp, color = TextMuted) },
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = GrayBackground,
            focusedContainerColor = Color.White,
            focusedBorderColor = RoyalMagenta
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field")
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (inputQuery.isNotBlank()) {
              val text = inputQuery
              inputQuery = ""
              viewModel.sendChatMessage(text)
            }
          },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(RoyalNavy)
            .testTag("btn_send_chat")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun ChatBubble(
  message: ChatMessage,
  onOpenMaps: (String) -> Unit,
  onOpenSearch: (String) -> Unit,
  onCopy: (String) -> Unit,
  onShare: (String) -> Unit
) {
  val isUser = message.sender == "user"

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(Color(0xFFEDE9FE)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Card(
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (isUser) 14.dp else 2.dp,
        bottomEnd = if (isUser) 2.dp else 14.dp
      ),
      colors = CardDefaults.cardColors(
        containerColor = if (isUser) RoyalNavy else Color.White
      ),
      border = if (!isUser) CardDefaults.outlinedCardBorder().copy(
        brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
      ) else null,
      modifier = Modifier.widthIn(max = 300.dp)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = message.text,
          fontSize = 13.sp,
          color = if (isUser) Color.White else TextDark,
          lineHeight = 18.sp
        )

        // Action links for Maps and Search
        if (!isUser) {
          if (message.text.contains("Maps") || message.text.contains("Route") || message.text.contains("km")) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE0F2FE))
                .clickable { onOpenMaps("Sun Pharma Guwahati Logistics") }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.Directions, contentDescription = null, tint = Color(0xFF0369A1), modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Open in Google Maps", fontSize = 11.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.Bold)
            }
          }

          if (message.text.contains("CDSCO") || message.text.contains("Gazette") || message.text.contains("Search") || message.text.contains("Substitute") || message.text.contains("Interaction")) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF1F5F9))
                .clickable { onOpenSearch(message.text.take(60)) }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.Search, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Verify on Google Search", fontSize = 11.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            IconButton(
              onClick = { onCopy(message.text) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
              onClick = { onShare(message.text) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.Share, contentDescription = "Share", tint = TextMuted, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }
  }
}
