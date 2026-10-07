package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.BrandCatalogProvider
import com.example.data.ai.GeminiPharmacistService
import com.example.data.model.BrandMedicine
import com.example.data.model.MedicineItem
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen

@Composable
fun BrandCatalogScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val selectedBrand by viewModel.selectedBrandForCatalog.collectAsState()
  val medicines by viewModel.brandCatalogMedicines.collectAsState()
  val isLoading by viewModel.isBrandLoading.collectAsState()
  val cartItems by viewModel.distributorCart.collectAsState()
  val localInventory by viewModel.allMedicines.collectAsState()

  var selectedCategoryFilter by remember { mutableStateOf("All") } // "All", "Injectable", "Tablet", "Syrup"
  var searchQuery by remember { mutableStateOf("") }

  val filteredMedicines = remember(medicines, selectedCategoryFilter, searchQuery) {
    medicines.filter { item ->
      val matchesCat = when (selectedCategoryFilter) {
        "All" -> true
        "Injectable" -> item.isInjectable || item.category.equals("Injectable", ignoreCase = true)
        "Tablet" -> item.category.equals("Tablet", ignoreCase = true) || item.category.equals("Capsule", ignoreCase = true)
        "Syrup" -> item.category.equals("Syrup", ignoreCase = true) || item.category.equals("Liquid", ignoreCase = true)
        else -> true
      }
      val matchesQuery = searchQuery.isBlank() ||
        item.name.contains(searchQuery, ignoreCase = true) ||
        item.saltComposition.contains(searchQuery, ignoreCase = true)
      matchesCat && matchesQuery
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // 1. Top Header Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalMagenta)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(Screen.HOME) },
        modifier = Modifier.size(32.dp).testTag("btn_back_brand_catalog")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "$selectedBrand Products & Injectables",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Text(
          text = "Pharma Catalog & Internet Drug Search",
          fontSize = 11.sp,
          color = Color.White.copy(alpha = 0.85f)
        )
      }

      // Cart Button with badge
      IconButton(
        onClick = { viewModel.navigateTo(Screen.CART) },
        modifier = Modifier.testTag("btn_cart_from_brand")
      ) {
        BadgedBox(
          badge = {
            if (cartItems.isNotEmpty()) {
              Badge(containerColor = Color.White) {
                Text("${cartItems.size}", color = RoyalMagenta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Cart",
            tint = Color.White
          )
        }
      }
    }

    // 2. Horizontal Brands Selector Chips (Cipla, SUN, Mankind, ALKEM, IPCA, GSK, etc.)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      BrandCatalogProvider.POPULAR_BRANDS.forEach { brandInfo ->
        val selected = selectedBrand.equals(brandInfo.name, ignoreCase = true)
        FilterChip(
          selected = selected,
          onClick = {
            viewModel.openBrandCatalog(brandInfo.name)
          },
          label = {
            Text(
              text = brandInfo.name,
              fontSize = 12.sp,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RoyalMagentaLight,
            selectedLabelColor = RoyalMagenta
          ),
          modifier = Modifier.testTag("brand_chip_${brandInfo.name.lowercase().replace(" ", "_")}")
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 3. Online Search & Gemini Controls Card
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            val brandDesc = BrandCatalogProvider.POPULAR_BRANDS.firstOrNull { it.name.equals(selectedBrand, ignoreCase = true) }?.description
              ?: "Leading pharmaceutical manufacturer in India"

            Text(
              text = selectedBrand,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalNavy
            )
            Text(
              text = brandDesc,
              fontSize = 11.sp,
              color = TextMuted,
              lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Google Search Button
              OutlinedButton(
                onClick = {
                  GeminiPharmacistService().openGoogleSearch(context, "$selectedBrand medicines injectables price list 1mg India")
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("btn_google_search_brand")
              ) {
                Icon(
                  imageVector = Icons.Default.OpenInBrowser,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = RoyalNavy
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Google Search", fontSize = 11.sp, color = RoyalNavy, fontWeight = FontWeight.Bold)
              }

              // Gemini AI Fetch Button
              Button(
                onClick = {
                  viewModel.searchBrandOnlineWithGemini(selectedBrand)
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("btn_gemini_fetch_brand")
              ) {
                if (isLoading) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                } else {
                  Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Gemini Search", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      // 4. Formulation Filters (All, Injectables, Tablets, Syrups)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            "All" to "All (${medicines.size})",
            "Injectable" to "Injectables (${medicines.count { it.isInjectable }})",
            "Tablet" to "Tablets",
            "Syrup" to "Syrups"
          ).forEach { (key, label) ->
            val isSelected = selectedCategoryFilter == key
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategoryFilter = key },
              label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (key == "Injectable") Color(0xFFF3E8FF) else RoyalMagentaLight,
                selectedLabelColor = if (key == "Injectable") Color(0xFF7E22CE) else RoyalMagenta
              ),
              modifier = Modifier.testTag("filter_cat_$key")
            )
          }
        }
      }

      // 5. Medicines & Injectables List
      if (filteredMedicines.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("No matching items found for category", fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Bold)
              Text("Tap 'Gemini Search' above to fetch online listings.", fontSize = 11.sp, color = TextMuted)
            }
          }
        }
      } else {
        items(filteredMedicines) { item ->
          val localMatch = localInventory.firstOrNull { it.name.contains(item.name.take(6), ignoreCase = true) }

          BrandMedicineCard(
            item = item,
            localMatch = localMatch,
            onAddToCart = {
              viewModel.addBrandMedicineToCart(item, 10)
            },
            onAddToInventory = {
              viewModel.addNewMedicine(
                MedicineItem(
                  name = item.name,
                  manufacturer = item.brandName,
                  composition = item.saltComposition,
                  saltMolecule = item.saltComposition,
                  category = item.category,
                  mrp = item.mrp,
                  purchaseRate = item.mrp * 0.8,
                  saleRate = item.mrp,
                  stockPacks = 15,
                  minStockAlert = 5,
                  isEssential = item.isEssential,
                  isLifeSaving = item.isInjectable
                )
              )
              viewModel.scanFeedbackMessage.value = "Imported ${item.name} into Pharmacy Stock!"
            }
          )
        }
      }
    }
  }
}

@Composable
fun BrandMedicineCard(
  item: BrandMedicine,
  localMatch: MedicineItem?,
  onAddToCart: () -> Unit,
  onAddToInventory: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(if (item.isInjectable) Color(0xFFE9D5FF) else CardBorder)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Category Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (item.isInjectable) Color(0xFFF3E8FF) else Color(0xFFE0F2FE))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.isInjectable) {
                  Icon(
                    imageVector = Icons.Default.Vaccines,
                    contentDescription = null,
                    tint = Color(0xFF7E22CE),
                    modifier = Modifier.size(10.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                  text = item.category.uppercase(),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (item.isInjectable) Color(0xFF7E22CE) else Color(0xFF0369A1)
                )
              }
            }

            if (item.isEssential) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFFFEF3C7))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("ESSENTIAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = item.saltComposition,
            fontSize = 11.sp,
            color = TextMuted
          )
          if (item.description.isNotBlank()) {
            Text(
              text = item.description,
              fontSize = 10.sp,
              color = Color(0xFF475569),
              lineHeight = 13.sp,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }

        // Pricing column
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "₹${item.mrp}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalNavy
          )
          Text(
            text = item.packaging,
            fontSize = 10.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom bar with Stock Status & Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Stock Status Pill
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (localMatch != null && localMatch.stockPacks > 0) StatusGreen else Color(0xFF94A3B8))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (localMatch != null && localMatch.stockPacks > 0) "In Stock (${localMatch.stockPacks} pk)" else "Out of Stock (Local)",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (localMatch != null && localMatch.stockPacks > 0) StatusGreen else Color(0xFF64748B)
          )
        }

        // Action Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (localMatch == null) {
            OutlinedButton(
              onClick = onAddToInventory,
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(30.dp).testTag("btn_add_to_inv_${item.name.take(6)}")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Stock", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Button(
            onClick = onAddToCart,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(30.dp).testTag("btn_add_cart_${item.name.take(6)}")
          ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Order", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
