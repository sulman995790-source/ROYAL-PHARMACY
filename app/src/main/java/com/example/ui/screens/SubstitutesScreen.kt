package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiPharmacistService
import com.example.data.model.BrandMedicine
import com.example.data.model.MedicineItem
import com.example.ui.components.InAppSearchDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun SubstitutesScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentQuery by viewModel.substituteQuery.collectAsState()
  var searchQuery by remember(currentQuery) { mutableStateOf(currentQuery.ifBlank { "Paracetamol" }) }

  val localSubstitutes by viewModel.availableSubstitutes.collectAsState()
  val onlineSubstitutes by viewModel.onlineSubstitutes.collectAsState()
  val isSearchingOnline by viewModel.isSearchingOnlineSubstitutes.collectAsState()
  val cartItems by viewModel.distributorCart.collectAsState()
  var inAppSearchQuery by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    if (onlineSubstitutes.isEmpty()) {
      viewModel.searchSubstitutesWithGemini("Paracetamol 650mg")
    }
  }

  var selectedBrandFilter by remember { mutableStateOf("All") } // "All", "IPCA", "GSK", "Cipla", "SUN PHARMA", "ALKEM", "Generic"
  var selectedCategoryFilter by remember { mutableStateOf("All") } // "All", "Injectable", "Tablet", "Syrup", "Generic"

  val popularBrandFilters = listOf("All", "IPCA", "GSK", "Cipla", "SUN PHARMA", "ALKEM", "Jan Aushadhi")
  val categoryFilters = listOf("All", "Injectables", "Tablets", "Syrups", "Generics")

  val saltChips = listOf(
    "Paracetamol 650mg",
    "Amoxicillin + Clavulanic Acid",
    "Pantoprazole 40mg",
    "Azithromycin 500mg",
    "Ceftriaxone 1000mg",
    "Metoclopramide 5mg/ml",
    "Aceclofenac + Paracetamol"
  )

  // Filter online substitutes based on selected brand & category
  val filteredOnlineSubstitutes = remember(onlineSubstitutes, selectedBrandFilter, selectedCategoryFilter) {
    onlineSubstitutes.filter { item ->
      val matchesBrand = when (selectedBrandFilter) {
        "All" -> true
        "Jan Aushadhi" -> item.brandName.contains("Jan Aushadhi", ignoreCase = true) || item.name.contains("Generic", ignoreCase = true)
        else -> item.brandName.contains(selectedBrandFilter, ignoreCase = true) || item.name.contains(selectedBrandFilter, ignoreCase = true)
      }
      val matchesCategory = when (selectedCategoryFilter) {
        "All" -> true
        "Injectables" -> item.isInjectable || item.category.equals("Injectable", ignoreCase = true)
        "Generics" -> item.brandName.contains("Generic", ignoreCase = true) || item.brandName.contains("Jan Aushadhi", ignoreCase = true)
        else -> item.category.equals(selectedCategoryFilter.removeSuffix("s"), ignoreCase = true)
      }
      matchesBrand && matchesCategory
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A)) // Modern Dark Slate
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // 1. Top Header Bar with Back Button, Title, and Distributor Cart Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.HOME) },
            modifier = Modifier.size(36.dp).testTag("btn_back_substitutes")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "Smart Substitute Finder",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "GSK, IPCA, Cipla, Alkem, Injectables & Generics",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        // Cart Icon with Badge
        IconButton(
          onClick = { viewModel.navigateTo(Screen.CART) },
          modifier = Modifier.testTag("btn_cart_from_substitutes")
        ) {
          BadgedBox(
            badge = {
              if (cartItems.isNotEmpty()) {
                Badge(
                  containerColor = Color(0xFFF59E0B),
                  contentColor = Color.Black
                ) {
                  Text("${cartItems.size}", fontWeight = FontWeight.Bold)
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingCart,
              contentDescription = "Distributor Cart",
              tint = Color(0xFFFFD54F)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2. Banner with Popular Brands Catalog Quick Link
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF334155))
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(RoyalMagenta.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Popular Pharma Brand Catalogs",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "IPCA, GSK, Cipla, Alkem, Sun Pharma, Zydus...",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.BRAND_CATALOG) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_view_brand_catalog")
          ) {
            Text("Browse", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Search Bar for Salt / Brand / Injectable
      OutlinedTextField(
        value = searchQuery,
        onValueChange = {
          searchQuery = it
          viewModel.setSubstituteQuery(it)
          val trimmed = it.trim()
          if (trimmed.length >= 2) {
            viewModel.searchSubstitutesWithGemini(trimmed)
          } else if (trimmed.isEmpty()) {
            viewModel.searchSubstitutesWithGemini("Paracetamol 650mg")
          }
        },
        placeholder = {
          Text(
            "Search salt, generic, or brand (e.g. Paracetamol drops, Augmentin, IPCA)",
            color = Color(0xFF64748B),
            fontSize = 13.sp
          )
        },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2DD4BF))
        },
        trailingIcon = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = {
                searchQuery = ""
                viewModel.setSubstituteQuery("")
              }) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
              }
            }
            IconButton(
              onClick = {
                if (searchQuery.isNotBlank()) viewModel.searchSubstitutesWithGemini(searchQuery)
              },
              modifier = Modifier.testTag("btn_search_substitutes_inline")
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = "Find Substitutes", tint = Color(0xFF2DD4BF))
            }
          }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
          if (searchQuery.isNotBlank()) viewModel.searchSubstitutesWithGemini(searchQuery)
        }),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF1E293B),
          unfocusedContainerColor = Color(0xFF1E293B),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = Color(0xFF2DD4BF),
          unfocusedBorderColor = Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth().testTag("input_search_substitutes")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Quick Salt Molecule Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        saltChips.forEach { salt ->
          val isSelected = searchQuery.equals(salt, ignoreCase = true)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) Color(0xFF0F766E) else Color(0xFF1E293B))
              .border(1.dp, if (isSelected) Color(0xFF2DD4BF) else Color(0xFF334155), RoundedCornerShape(16.dp))
              .clickable {
                searchQuery = salt
                viewModel.setSubstituteQuery(salt)
                viewModel.searchSubstitutesWithGemini(salt)
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = salt,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Color.White else Color(0xFFCBD5E1)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 5. Dual Live Search Actions: Gemini AI & Google Search
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Gemini Search Button
        Button(
          onClick = {
            viewModel.searchSubstitutesWithGemini(searchQuery)
          },
          enabled = !isSearchingOnline,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)), // Violet Gemini
          modifier = Modifier.weight(1.3f).testTag("btn_search_gemini_substitutes"),
          contentPadding = PaddingValues(vertical = 10.dp)
        ) {
          if (isSearchingOnline) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Searching...", fontSize = 11.5.sp, color = Color.White)
          } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Search Gemini Online", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }

        // Google Search Button (In-App)
        OutlinedButton(
          onClick = {
            val queryText = if (searchQuery.isNotBlank()) searchQuery else "Paracetamol 650mg substitutes IPCA GSK Cipla"
            inAppSearchQuery = "$queryText medicine substitutes India IPCA GSK"
          },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
          modifier = Modifier.weight(1f).testTag("btn_search_google_substitutes"),
          contentPadding = PaddingValues(vertical = 10.dp)
        ) {
          Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Google Search", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 6. Brand Filters & Category Filters
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        popularBrandFilters.forEach { brand ->
          val selected = selectedBrandFilter == brand
          FilterChip(
            selected = selected,
            onClick = { selectedBrandFilter = brand },
            label = { Text(brand, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF0F766E),
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("chip_brand_$brand")
          )
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        categoryFilters.forEach { cat ->
          val selected = selectedCategoryFilter == cat
          FilterChip(
            selected = selected,
            onClick = { selectedCategoryFilter = cat },
            label = { Text(cat, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFD97706),
              selectedLabelColor = Color.White,
              containerColor = Color(0xFF1E293B),
              labelColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.testTag("chip_cat_$cat")
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 7. Header showing count
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Substitutes & Brand Equivalents (${filteredOnlineSubstitutes.size})",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = if (selectedBrandFilter != "All") "Filter: $selectedBrandFilter" else "All Brands",
          fontSize = 11.sp,
          color = Color(0xFF94A3B8)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 8. Results List (Rich Cards with Injectable tag, Generic savings, Add to Cart)
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Show Local in-stock substitutes first if matching
        if (localSubstitutes.isNotEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF064E3B))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "✓ Found in Local Pharmacy Stock (${localSubstitutes.size} items)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6EE7B7)
              )
            }
          }

          items(localSubstitutes, key = { "local_${it.id}" }) { med ->
            SubstituteMedicineCard(
              item = med,
              onAddToBill = {
                viewModel.addMedicineToCart(med, 1)
                viewModel.navigateTo(Screen.ADD_SALE)
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "🌐 Internet & Brand Equivalents (GSK, IPCA, Generics, Injectables)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
              )
            }
          }
        }

        // Online Gemini & Popular Brands Catalog results
        items(filteredOnlineSubstitutes, key = { it.id }) { brandMed ->
          OnlineSubstituteCard(
            brandMed = brandMed,
            onAddToCart = {
              viewModel.addBrandMedicineToCart(brandMed, 10)
              Toast.makeText(context, "Added ${brandMed.name} to Distributor Cart", Toast.LENGTH_SHORT).show()
            }
          )
        }

        if (filteredOnlineSubstitutes.isEmpty() && localSubstitutes.isEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
              modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "No direct substitutes matching filters for '$searchQuery'.",
                  color = Color(0xFF94A3B8),
                  fontSize = 13.sp,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                  onClick = {
                    selectedBrandFilter = "All"
                    selectedCategoryFilter = "All"
                    viewModel.searchSubstitutesWithGemini(searchQuery)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Reset Filters & Search All", fontSize = 11.5.sp, color = Color.White)
                }
              }
            }
          }
        }
      }
    }

    inAppSearchQuery?.let { q ->
      InAppSearchDialog(
        initialQuery = q,
        title = "Substitute Monograph Search",
        onDismiss = { inAppSearchQuery = null }
      )
    }
  }
}

@Composable
fun OnlineSubstituteCard(
  brandMed: BrandMedicine,
  onAddToCart: () -> Unit
) {
  val isGeneric = brandMed.brandName.contains("Generic", ignoreCase = true) || brandMed.brandName.contains("Jan Aushadhi", ignoreCase = true)
  val isInjectable = brandMed.isInjectable || brandMed.category.equals("Injectable", ignoreCase = true)

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(
        if (isInjectable) Color(0xFF38BDF8) else if (isGeneric) Color(0xFF10B981) else Color(0xFF334155)
      )
    ),
    modifier = Modifier.fillMaxWidth().testTag("online_substitute_${brandMed.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Brand badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  when {
                    brandMed.brandName.contains("IPCA", ignoreCase = true) -> Color(0xFF00695C)
                    brandMed.brandName.contains("GSK", ignoreCase = true) -> Color(0xFFE65100)
                    brandMed.brandName.contains("Cipla", ignoreCase = true) -> Color(0xFF1565C0)
                    isGeneric -> Color(0xFF047857)
                    else -> Color(0xFF374151)
                  }
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = brandMed.brandName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            // Injectable tag
            if (isInjectable) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF0284C7))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Vaccines, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("Injectable (IV/IM)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }

            // Generic tag
            if (isGeneric) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF065F46))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("Subsidized Generic", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA7F3D0))
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = brandMed.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Text(
            text = brandMed.saltComposition,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )

          Text(
            text = "${brandMed.category} • ${brandMed.packaging}",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )

          if (brandMed.description.isNotBlank()) {
            Text(
              text = brandMed.description,
              fontSize = 10.5.sp,
              color = Color(0xFFCBD5E1),
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }

        // Pricing column
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = String.format(Locale.getDefault(), "₹%.1f", brandMed.mrp),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isGeneric) Color(0xFF34D399) else Color(0xFF38BDF8)
          )
          Text(
            text = "MRP per pack",
            fontSize = 9.5.sp,
            color = Color(0xFF64748B)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isInjectable) "Hospital / Clinical Restock" else "Retail Pharmacy Substitute",
          fontSize = 10.sp,
          color = Color(0xFF64748B)
        )

        // [+ Add to Distributor Cart]
        Button(
          onClick = onAddToCart,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("btn_cart_brand_${brandMed.id}")
        ) {
          Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add to Cart", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

@Composable
fun SubstituteMedicineCard(
  item: MedicineItem,
  onAddToBill: () -> Unit
) {
  val isBestValue = item.isGeneric || item.saleRate <= 25.0
  val discountPercent = if (item.mrp > item.saleRate) {
    (((item.mrp - item.saleRate) / item.mrp) * 100).toInt()
  } else 0

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isBestValue) Color(0xFF1E3A3A) else Color(0xFF1E293B)
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(
        if (isBestValue) Color(0xFF10B981) else Color(0xFF334155)
      )
    ),
    modifier = Modifier.fillMaxWidth().testTag("substitute_item_${item.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          if (isBestValue) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF065F46))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "Best Value",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA7F3D0)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
          }

          Text(
            text = item.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = item.composition,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )
          Text(
            text = "${item.manufacturer} • ${item.rackLocation}",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }

        // Pricing column
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = String.format(Locale.getDefault(), "₹%.1f", item.saleRate),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2DD4BF)
          )
          if (item.mrp > item.saleRate) {
            Text(
              text = "MRP ₹${item.mrp} (${discountPercent}% OFF)",
              fontSize = 10.sp,
              textDecoration = TextDecoration.LineThrough,
              color = Color(0xFF94A3B8)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Stock count and Expiry
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (item.stockPacks > 0) Color(0xFF064E3B) else Color(0xFF7F1D1D))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${item.stockPacks} in stock",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (item.stockPacks > 0) Color(0xFF6EE7B7) else Color(0xFFFCA5A5)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Exp: ${item.expiryDate}",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
          )
        }

        // [+ Add to Bill] Button
        Button(
          onClick = onAddToBill,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0D9488)
          ),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier.testTag("btn_add_substitute_${item.id}")
        ) {
          Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add to Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}
