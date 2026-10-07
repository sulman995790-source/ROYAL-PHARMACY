package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InAppSearchDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen

@Composable
fun SearchAnythingScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var inAppSearchQuery by remember { mutableStateOf<String?>(null) }
  val recentSearches by viewModel.recentSearches.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()

  val searchResults = if (searchQuery.isNotBlank()) {
    medicines.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
        it.manufacturer.contains(searchQuery, ignoreCase = true) ||
        it.composition.contains(searchQuery, ignoreCase = true) ||
        it.barcode.contains(searchQuery, ignoreCase = true)
    }
  } else emptyList()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Header (Screenshot 19)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
        }
        Text(
          text = "Search Anything",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
      }

      // 2. Search Text Input (Screenshot 19)
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search medicine, invoice, customer...", fontSize = 13.sp, color = TextMuted) },
          trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = GrayBackground,
            focusedContainerColor = Color.White
          ),
          modifier = Modifier.fillMaxWidth().testTag("search_anything_input")
        )
      }

      // 3. Recently Searched or Live Search Results
      if (searchQuery.isBlank()) {
        Text(
          text = "Recently Searched",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextMuted,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(recentSearches) { term ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  searchQuery = term
                  viewModel.addRecentSearch(term)
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = term,
                fontSize = 14.sp,
                color = TextDark
              )
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextLight,
                modifier = Modifier.size(16.dp)
              )
            }
            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(CardBorder))
          }
        }
      } else {
        // Live search results
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (searchResults.isEmpty()) {
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GrayBackground),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
              ) {
                Column(
                  modifier = Modifier.fillMaxWidth().padding(20.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("No local pharmacy stock matching '$searchQuery'", fontSize = 13.sp, color = TextMuted)
                  Spacer(modifier = Modifier.height(10.dp))
                  Button(
                    onClick = { inAppSearchQuery = "$searchQuery medicine composition dosage India" },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Search on Google in App", fontSize = 12.sp, color = Color.White)
                  }
                }
              }
            }
          }

          items(searchResults) { med ->
            Card(
              onClick = {
                viewModel.addRecentSearch(med.name)
                viewModel.addMedicineToCart(med, 1)
                viewModel.navigateTo(Screen.ADD_SALE)
              },
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(containerColor = GrayBackground),
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
                  Text(med.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("${med.manufacturer} • ${med.stockPacks} packs in stock", fontSize = 11.sp, color = TextMuted)
                }
                Text("₹${med.saleRate}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
              }
            }
          }
        }
      }
    }

    // 4. Bottom Button Row
    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      if (searchQuery.isNotBlank()) {
        Button(
          onClick = { inAppSearchQuery = "$searchQuery medicine composition uses India" },
          shape = RoundedCornerShape(24.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
          modifier = Modifier.height(44.dp)
        ) {
          Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Google In-App", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }

      Button(
        onClick = { viewModel.navigateTo(Screen.QUICK_SCAN) },
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
        modifier = Modifier
          .height(44.dp)
          .testTag("btn_search_scan_barcode")
      ) {
        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Scan Barcode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }
    }

    inAppSearchQuery?.let { q ->
      InAppSearchDialog(
        initialQuery = q,
        title = "In-App Search: $searchQuery",
        onDismiss = { inAppSearchQuery = null }
      )
    }
  }
}
