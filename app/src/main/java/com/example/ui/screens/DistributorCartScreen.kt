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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun DistributorCartScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val cartItems by viewModel.distributorCart.collectAsState()
  val businessProfile by viewModel.businessProfile.collectAsState()

  var distributorName by remember { mutableStateOf("Darrang Pharma Wholesale Distributors") }
  var selectedTabFilter by remember { mutableStateOf("All") } // "All", "Reorder", "Short Expiry"

  val reorderItems = cartItems.filter { it.itemType != "EXPIRY_RETURN" }
  val expiryReturnItems = cartItems.filter { it.itemType == "EXPIRY_RETURN" }

  val filteredItems = when (selectedTabFilter) {
    "Reorder" -> reorderItems
    "Short Expiry" -> expiryReturnItems
    else -> cartItems
  }

  val totalReorderAmount = reorderItems.sumOf { it.totalAmount }
  val totalReturnAmount = expiryReturnItems.sumOf { it.totalAmount }

  val distributorSuggestions = listOf(
    "Darrang Pharma Wholesale Distributors",
    "Guwahati Medico Wholesalers Ltd",
    "Assam Lifesciences Agency",
    "National Drug House Tezpur"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalMagenta)
        .padding(horizontal = 8.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(Screen.HOME) },
        modifier = Modifier.testTag("btn_cart_back")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Distributor Cart & Export",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Text(
          text = "${cartItems.size} items • PO & Short Expiry Return Note",
          fontSize = 12.sp,
          color = Color.White.copy(alpha = 0.85f)
        )
      }

      if (cartItems.isNotEmpty()) {
        IconButton(
          onClick = { viewModel.clearCart() },
          modifier = Modifier.testTag("btn_clear_cart")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Clear Cart",
            tint = Color.White.copy(alpha = 0.9f)
          )
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
      // Distributor Selection & Target Info Card
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = RoyalMagenta,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Target Distributor / Wholesale Agency",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = distributorName,
              onValueChange = { distributorName = it },
              label = { Text("Distributor Name") },
              placeholder = { Text("e.g. Medico Pharma Wholesalers") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("input_distributor_name"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RoyalMagenta,
                unfocusedBorderColor = CardBorder
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Quick Presets:",
              fontSize = 11.sp,
              color = TextMuted
            )

            Row(
              modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              distributorSuggestions.take(2).forEach { name ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable { distributorName = name }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = name.take(24) + "...",
                    fontSize = 10.sp,
                    color = RoyalNavy,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // Summary & Export Actions Card (XLS / PDF)
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1))
          ),
          modifier = Modifier.fillMaxWidth().testTag("card_cart_summary")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Order & Debit Note Summary",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Text(
                  text = "Pharmacy: ${businessProfile.businessName} • DL: ${businessProfile.dlNumber}",
                  fontSize = 10.5.sp,
                  color = TextMuted
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(RoyalMagentaLight)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "${cartItems.size} Total Items",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMagenta
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Purchase Reorders:", fontSize = 11.sp, color = TextMuted)
                Text(
                  text = "${reorderItems.size} items • ₹${String.format(Locale.US, "%.2f", totalReorderAmount)}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalNavy
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("Short Expiry Return (<60d):", fontSize = 11.sp, color = TextMuted)
                Text(
                  text = "${expiryReturnItems.size} items • ₹${String.format(Locale.US, "%.2f", totalReturnAmount)}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFB45309)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two Primary Export Buttons: XLS and PDF
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  if (cartItems.isEmpty()) {
                    Toast.makeText(context, "Cart is empty! Add items first.", Toast.LENGTH_SHORT).show()
                  } else {
                    viewModel.exportCartToXls(context, distributorName)
                  }
                },
                modifier = Modifier.weight(1f).testTag("btn_export_xls"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E7E34)), // Excel Green
                contentPadding = PaddingValues(vertical = 10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.TableChart,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Export XLS/CSV",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }

              Button(
                onClick = {
                  if (cartItems.isEmpty()) {
                    Toast.makeText(context, "Cart is empty! Add items first.", Toast.LENGTH_SHORT).show()
                  } else {
                    viewModel.exportCartToPdf(context, distributorName)
                  }
                },
                modifier = Modifier.weight(1f).testTag("btn_export_pdf"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5221F)), // PDF Red
                contentPadding = PaddingValues(vertical = 10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.PictureAsPdf,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Export PDF",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }
        }
      }

      // Filter Chips (All, Purchase Reorders, Short Expiry Returns)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            "All" to "All Items (${cartItems.size})",
            "Reorder" to "Reorders (${reorderItems.size})",
            "Short Expiry" to "Expiry Returns (${expiryReturnItems.size})"
          ).forEach { (key, label) ->
            val selected = selectedTabFilter == key
            FilterChip(
              selected = selected,
              onClick = { selectedTabFilter = key },
              label = {
                Text(
                  label,
                  fontSize = 12.sp,
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = RoyalMagentaLight,
                selectedLabelColor = RoyalMagenta
              ),
              modifier = Modifier.testTag("filter_cart_$key")
            )
          }
        }
      }

      // Items List
      if (filteredItems.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(
              brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Cart is Empty",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = "Add medicines from Popular Brands (GSK, IPCA, Cipla) or Short Expiry (<60 days) to generate orders & debit notes.",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                  onClick = { viewModel.navigateTo(Screen.BRAND_CATALOG) },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Icon(Icons.Default.Business, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Popular Brands", fontSize = 11.5.sp, color = Color.White)
                }

                OutlinedButton(
                  onClick = { viewModel.navigateTo(Screen.SUBSTITUTES) },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text("Substitute Finder", fontSize = 11.5.sp, color = RoyalMagenta)
                }
              }
            }
          }
        }
      } else {
        items(filteredItems, key = { it.id }) { item ->
          CartItemCard(
            item = item,
            onUpdateQty = { newQty -> viewModel.updateCartItemQty(item.id, newQty) },
            onRemove = { viewModel.removeFromCart(item.id) }
          )
        }
      }
    }
  }
}

@Composable
fun CartItemCard(
  item: CartItem,
  onUpdateQty: (Int) -> Unit,
  onRemove: () -> Unit
) {
  val isExpiryReturn = item.itemType == "EXPIRY_RETURN"

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(
        if (isExpiryReturn) Color(0xFFFCD34D) else CardBorder
      )
    ),
    modifier = Modifier.fillMaxWidth().testTag("cart_item_${item.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          // Badge: Expiry Return vs Purchase Order
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (isExpiryReturn) Color(0xFFFEF3C7) else RoyalMagentaLight)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isExpiryReturn) Icons.Default.HourglassBottom else Icons.Default.Business,
                contentDescription = null,
                tint = if (isExpiryReturn) Color(0xFFB45309) else RoyalMagenta,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isExpiryReturn) "EXPIRY RETURN (<60 DAYS)" else "PURCHASE REORDER",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isExpiryReturn) Color(0xFFB45309) else RoyalMagenta
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = item.medicineName,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )

          if (item.composition.isNotBlank()) {
            Text(
              text = item.composition,
              fontSize = 11.5.sp,
              color = TextMuted
            )
          }

          Text(
            text = "${item.manufacturer} • ${item.category}",
            fontSize = 11.sp,
            color = TextMuted
          )

          if (item.batchNumber.isNotBlank() || item.expiryDate.isNotBlank()) {
            Text(
              text = "Batch: ${item.batchNumber.ifBlank { "N/A" }} • Expiry: ${item.expiryDate.ifBlank { "N/A" }}",
              fontSize = 11.sp,
              fontWeight = if (isExpiryReturn) FontWeight.Bold else FontWeight.Normal,
              color = if (isExpiryReturn) Color(0xFFB45309) else TextDark
            )
          }
        }

        // Remove button
        IconButton(
          onClick = onRemove,
          modifier = Modifier.size(32.dp).testTag("btn_remove_${item.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove",
            tint = StatusRed.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(CardBorder))

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Quantity Controls (- Qty +)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
              .clickable { onUpdateQty(item.quantity - 1) },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextDark, modifier = Modifier.size(16.dp))
          }

          Text(
            text = "${item.quantity} packs",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 10.dp)
          )

          Box(
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
              .clickable { onUpdateQty(item.quantity + 1) },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextDark, modifier = Modifier.size(16.dp))
          }
        }

        // Total Price for this item
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "₹${String.format(Locale.US, "%.2f", item.totalAmount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isExpiryReturn) Color(0xFFB45309) else RoyalNavy
          )
          Text(
            text = "Rate: ₹${String.format(Locale.US, "%.2f", item.unitRate)}/pack",
            fontSize = 10.5.sp,
            color = TextMuted
          )
        }
      }
    }
  }
}
